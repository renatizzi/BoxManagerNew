package com.example.boxmanagernew.locale

import com.example.boxmanagernew.domain.locale.LocalePreference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalePreferenceTest {

    @Test
    fun resolve_defaultsToItalian() {
        assertEquals(LocalePreference.IT, LocalePreference.resolve(null))
        assertEquals(LocalePreference.IT, LocalePreference.resolve(""))
        assertEquals(LocalePreference.IT, LocalePreference.resolve("fr"))
        assertEquals(LocalePreference.IT, LocalePreference.resolve("IT"))
        assertEquals(LocalePreference.IT, LocalePreference.resolve("it-IT"))
    }

    @Test
    fun resolve_acceptsEnglishTags() {
        assertEquals(LocalePreference.EN, LocalePreference.resolve("en"))
        assertEquals(LocalePreference.EN, LocalePreference.resolve("EN"))
        assertEquals(LocalePreference.EN, LocalePreference.resolve("en-US"))
        assertEquals(LocalePreference.EN, LocalePreference.resolve("en_GB"))
    }

    @Test
    fun isEnglish_onlyForEnglishTags() {
        assertTrue(LocalePreference.isEnglish("en-US"))
        assertFalse(LocalePreference.isEnglish("it"))
        assertFalse(LocalePreference.isEnglish(null))
    }
}
