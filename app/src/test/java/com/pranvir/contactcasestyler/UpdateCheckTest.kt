package com.pranvir.contactcasestyler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCheckTest {

    @Test fun newer_patch() {
        assertTrue(UpdateCheck.isNewer("v0.2.1", "0.2.0"))
    }

    @Test fun newer_minor() {
        assertTrue(UpdateCheck.isNewer("0.3.0", "v0.2.9"))
    }

    @Test fun newer_numericNotLexical() {
        assertTrue(UpdateCheck.isNewer("v0.10.0", "v0.9.0"))
        assertFalse(UpdateCheck.isNewer("v0.9.0", "v0.10.0"))
    }

    @Test fun same_isNotNewer() {
        assertFalse(UpdateCheck.isNewer("v0.2.0", "0.2.0"))
    }

    @Test fun older_isNotNewer() {
        assertFalse(UpdateCheck.isNewer("v0.1.0", "v0.2.0"))
    }

    @Test fun parseTag_readsField() {
        val json = """{"url":"x","tag_name":"v0.3.0","name":"v0.3.0"}"""
        assertEquals("v0.3.0", UpdateCheck.parseTag(json))
    }

    @Test fun parseTag_missingIsNull() {
        assertNull(UpdateCheck.parseTag("""{"name":"nope"}"""))
        assertNull(UpdateCheck.parseTag("not json at all"))
        assertNull(UpdateCheck.parseTag("""{"tag_name":""}"""))
    }
}
