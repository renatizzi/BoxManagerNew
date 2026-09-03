package com.example.boxmanagernew.locale

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SettingsLanguageLayoutTest {

    @Test
    fun settings_hasLanguageChoiceCard() {
        val xml = layoutSource()
        assertTrue(xml.contains("android:id=\"@+id/cardLanguage\""))
        assertTrue(xml.contains("@string/settings_language_hint"))
        val languageIndex = xml.indexOf("android:id=\"@+id/cardLanguage\"")
        val appearanceIndex = xml.indexOf("@string/settings_appearance")
        assertTrue(languageIndex > 0)
        assertTrue(appearanceIndex > languageIndex)
        assertTrue(xml.contains("android:id=\"@+id/optionItalian\""))
        assertTrue(xml.contains("android:id=\"@+id/optionEnglish\""))
        assertTrue(xml.contains("@string/settings_language_title"))
        assertTrue(xml.contains("@string/settings_language_italian"))
        assertTrue(xml.contains("@string/settings_language_english"))
    }

    @Test
    fun englishStrings_includeLanguageLabels() {
        val en = resource("app/src/main/res/values-en/strings.xml")
        assertTrue(en.contains("name=\"settings_language_title\""))
        assertTrue(en.contains(">Language<"))
        val it = resource("app/src/main/res/values/strings.xml")
        assertTrue(it.contains(">Scelta lingua<"))
    }

    @Test
    fun application_appliesStoredLocale() {
        val source = resource(
            "app/src/main/java/com/example/boxmanagernew/BoxManagerApplication.kt"
        )
        assertTrue(source.contains("LocaleManager.applyStored"))
    }

    @Test
    fun settingsActivity_wiresLanguageSelector() {
        val source = resource(
            "app/src/main/java/com/example/boxmanagernew/ui/settings/SettingsActivity.kt"
        )
        assertTrue(source.contains("setupLanguageSelector()"))
        assertTrue(source.contains("LocaleManager.setLanguage"))
        assertTrue(source.contains("LocalePreference.EN"))
        assertTrue(source.contains("LocalePreference.IT"))
    }

    private fun layoutSource(): String {
        return resource("app/src/main/res/layout/activity_settings.xml")
    }

    private fun resource(relative: String): String {
        val candidates = listOf(
            File(relative.removePrefix("app/")),
            File(relative)
        )
        return candidates.first { it.isFile }.readText()
    }
}
