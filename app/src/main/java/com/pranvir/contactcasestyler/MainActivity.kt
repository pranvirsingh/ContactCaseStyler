package com.pranvir.contactcasestyler

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pranvir.contactcasestyler.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var helper: ContactsHelper
    private var contacts: List<DeviceContact> = emptyList()
    private var style: CaseStyles.Style = CaseStyles.Style.CAMEL

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.all { it }) loadContacts()
        else toast(getString(R.string.permission_needed))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = ContactsHelper(this)

        binding.styleSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            CaseStyles.Style.entries.map {
                it.label + "  ·  " + it.example + if (it.safe) "" else "  ·  fun"
            },
        )
        binding.styleSpinner.setSelection(0)
        binding.styleSpinner.onItemSelectedListener = SimpleSelectedListener {
            style = CaseStyles.Style.entries[binding.styleSpinner.selectedItemPosition]
            renderPreview()
        }

        binding.grantButton.setOnClickListener { askPermissions() }
        binding.applyButton.setOnClickListener { apply() }
        binding.backupButton.setOnClickListener { backup() }
        binding.restoreButton.setOnClickListener { restore() }
        binding.hideIconSwitch.isChecked = isIconHidden()
        binding.hideIconSwitch.setOnCheckedChangeListener { _, hide -> setIconHidden(hide) }

        askPermissions()
    }

    private fun askPermissions() {
        permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS))
    }

    private fun loadContacts() {
        lifecycleScope.launch {
            contacts = withContext(Dispatchers.IO) { helper.loadAll() }
            binding.countText.text = getString(R.string.count, contacts.size)
            binding.grantButton.isEnabled = false
            renderPreview()
        }
    }

    private fun renderPreview() {
        if (contacts.isEmpty()) {
            binding.previewText.text = getString(R.string.preview_empty)
            return
        }
        binding.previewText.text = contacts.take(5).joinToString("\n") {
            "${it.displayName}  →  ${CaseStyles.apply(it.displayName, style)}"
        }
    }

    private fun apply() {
        if (contacts.isEmpty()) {
            toast(getString(R.string.load_first))
            return
        }
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                helper.backup(contacts)
                if (!style.safe) {
                    helper.ensureNicknames(contacts.associate { it.id to it.displayName })
                }
                helper.applyStyles(contacts, style)
            }
            toast(getString(R.string.applied, result.updated, result.failed))
            loadContacts()
        }
    }

    private fun backup() {
        lifecycleScope.launch {
            val saved = withContext(Dispatchers.IO) {
                val all = helper.loadAll()
                helper.backup(all)
                all.size
            }
            contacts = withContext(Dispatchers.IO) { helper.loadAll() }
            renderPreview()
            toast(getString(R.string.backed_up, saved))
        }
    }

    private fun restore() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { helper.restore() }
            toast(getString(R.string.restored, result.updated, result.failed))
            loadContacts()
        }
    }

    private fun launcherAlias(): ComponentName =
        ComponentName(this, "com.pranvir.contactcasestyler.Launcher")

    private fun isIconHidden(): Boolean =
        packageManager.getComponentEnabledSetting(launcherAlias()) ==
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    private fun setIconHidden(hide: Boolean) {
        packageManager.setComponentEnabledSetting(
            launcherAlias(),
            if (hide) PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
        toast(getString(if (hide) R.string.icon_hidden else R.string.icon_shown))
    }

    private fun toast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
