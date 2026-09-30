package com.pranvir.contactcasestyler

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class DeviceContact(
    val id: Long,
    val lookupKey: String,
    val displayName: String,
)

/** All device-contacts reads, writes, backups. UI stays out of here. */
class ContactsHelper(private val context: Context) {

    private val resolver: ContentResolver get() = context.contentResolver

    fun loadAll(): List<DeviceContact> {
        val out = mutableListOf<DeviceContact>()
        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ),
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} NOT NULL",
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC",
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val keyCol = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)
            val nameCol = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            while (cursor.moveToNext()) {
                out += DeviceContact(
                    id = cursor.getLong(idCol),
                    lookupKey = cursor.getString(keyCol).orEmpty(),
                    displayName = cursor.getString(nameCol).orEmpty(),
                )
            }
        }
        return out
    }

    data class ApplyResult(val updated: Int, val failed: Int)

    /**
     * Renames every contact by rewriting its StructuredName rows.
     * Some read-only/synced accounts reject the write — those count as failed.
     */
    fun applyStyles(
        contacts: List<DeviceContact>,
        style: CaseStyles.Style,
    ): ApplyResult {
        var updated = 0
        var failed = 0
        contacts.chunked(300).forEach { chunk ->
            val ops = ArrayList<ContentProviderOperation>(chunk.size)
            for (c in chunk) {
                val styled = CaseStyles.apply(c.displayName, style)
                if (styled == c.displayName) continue
                ops += ContentProviderOperation
                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(
                        "${ContactsContract.Data.CONTACT_ID}=? AND " +
                            "${ContactsContract.Data.MIMETYPE}=?",
                        arrayOf(
                            c.id.toString(),
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                        ),
                    )
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, styled)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, null)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, null)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.PREFIX, null)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, null)
                    .build()
            }
            if (ops.isEmpty()) return@forEach
            try {
                val results = resolver.applyBatch(ContactsContract.AUTHORITY, ops)
                updated += results.count { (it.count ?: 0) > 0 }
            } catch (_: Exception) {
                failed += ops.size
            }
        }
        return ApplyResult(updated, failed)
    }

    /**
     * Saves each original name as the contact's nickname. Most dialers also
     * search nicknames, so styled contacts stay findable. Best-effort:
     * read-only accounts may reject it — those are silently skipped.
     * Nicknames are intentionally left in place by restore(), they only help.
     */
    fun ensureNicknames(originals: Map<Long, String>) {
        if (originals.isEmpty()) return
        val ids = originals.keys.toList()

        // contactId -> one rawContactId (first wins).
        val rawByContact = mutableMapOf<Long, Long>()
        ids.chunked(400).forEach { chunk ->
            val where = "${ContactsContract.RawContacts.CONTACT_ID} IN (${chunk.joinToString(",") { "?" }})"
            resolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                arrayOf(ContactsContract.RawContacts._ID, ContactsContract.RawContacts.CONTACT_ID),
                where,
                chunk.map { it.toString() }.toTypedArray(),
                null,
            )?.use { cursor ->
                val rawCol = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts._ID)
                val contactCol = cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.CONTACT_ID)
                while (cursor.moveToNext()) {
                    rawByContact.putIfAbsent(cursor.getLong(contactCol), cursor.getLong(rawCol))
                }
            }
        }

        // Contacts that already have a nickname row.
        val nicknamed = mutableSetOf<Long>()
        ids.chunked(400).forEach { chunk ->
            val where = "${ContactsContract.Data.CONTACT_ID} IN (${chunk.joinToString(",") { "?" }})" +
                " AND ${ContactsContract.Data.MIMETYPE}=?"
            resolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.Data.CONTACT_ID),
                where,
                chunk.map { it.toString() } + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE,
                null,
            )?.use { cursor ->
                while (cursor.moveToNext()) nicknamed += cursor.getLong(0)
            }
        }

        val nicknameType = ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE
        val ops = ArrayList<ContentProviderOperation>()
        fun flush() {
            if (ops.isEmpty()) return
            try {
                resolver.applyBatch(ContactsContract.AUTHORITY, ArrayList(ops))
            } catch (_: Exception) {
                // Best-effort by design.
            }
            ops.clear()
        }
        for ((contactId, original) in originals) {
            if (contactId in nicknamed) {
                ops += ContentProviderOperation
                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(
                        "${ContactsContract.Data.CONTACT_ID}=? AND ${ContactsContract.Data.MIMETYPE}=?",
                        arrayOf(contactId.toString(), nicknameType),
                    )
                    .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, original)
                    .build()
            } else {
                val rawId = rawByContact[contactId] ?: continue
                ops += ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                    .withValue(ContactsContract.Data.MIMETYPE, nicknameType)
                    .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, original)
                    .build()
            }
            if (ops.size >= 300) flush()
        }
        flush()
    }

    fun backup(contacts: List<DeviceContact>): File {        val json = JSONArray()
        for (c in contacts) {
            json.put(
                JSONObject()
                    .put("id", c.id)
                    .put("key", c.lookupKey)
                    .put("name", c.displayName),
            )
        }
        val file = File(context.filesDir, "contact-backup.json")
        file.writeText(json.toString())
        return file
    }

    fun readBackup(): List<DeviceContact> {
        val file = File(context.filesDir, "contact-backup.json")
        if (!file.exists()) return emptyList()
        val json = JSONArray(file.readText())
        return List(json.length()) { i ->
            val o = json.getJSONObject(i)
            DeviceContact(o.getLong("id"), o.getString("key"), o.getString("name"))
        }
    }

    fun restore(): ApplyResult {
        val saved = readBackup()
        var updated = 0
        var failed = 0
        saved.chunked(300).forEach { chunk ->
            val ops = ArrayList<ContentProviderOperation>(chunk.size)
            for (c in chunk) {
                ops += ContentProviderOperation
                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(
                        "${ContactsContract.Data.CONTACT_ID}=? AND " +
                            "${ContactsContract.Data.MIMETYPE}=?",
                        arrayOf(
                            c.id.toString(),
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                        ),
                    )
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, c.displayName)
                    .build()
            }
            try {
                val results = resolver.applyBatch(ContactsContract.AUTHORITY, ops)
                updated += results.count { (it.count ?: 0) > 0 }
            } catch (_: Exception) {
                failed += ops.size
            }
        }
        return ApplyResult(updated, failed)
    }
}
