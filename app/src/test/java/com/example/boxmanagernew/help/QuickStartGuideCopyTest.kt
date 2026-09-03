package com.example.boxmanagernew.help

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Guida: contenuti IT/EN in strings.xml; struttura e wiring verificati qui.
 */
class QuickStartGuideCopyTest {

    @Test
    fun headerStrings_existInItalianAndEnglish() {
        assertEquals("Guida rapida", stringIt("guide_page_title"))
        assertEquals("Quick guide", stringEn("guide_page_title"))
        assertTrue(stringIt("guide_page_subtitle").contains("BoxManager"))
        assertTrue(stringEn("guide_page_subtitle").contains("BoxManager"))
        assertEquals(
            "Come usare BoxManager in tre mosse",
            stringIt("guide_workflow_title")
        )
        assertEquals(
            "How to use BoxManager in three steps",
            stringEn("guide_workflow_title")
        )
        assertEquals("Configura", stringIt("guide_phase_config"))
        assertEquals("Set up", stringEn("guide_phase_config"))
        assertEquals("Censisci", stringIt("guide_phase_census"))
        assertEquals("Catalog", stringEn("guide_phase_census"))
        assertEquals("Utilizza", stringIt("guide_phase_usage"))
        assertEquals("Use", stringEn("guide_phase_usage"))
    }

    @Test
    fun activity_usesContextAwareGuideCopy() {
        val source = kotlinSource("ui/help/QuickStartGuideActivity.kt")
        assertTrue(source.contains("QuickStartGuideCopy.pageTitle(this)"))
        assertTrue(source.contains("QuickStartGuideCopy.sectionsFor(this,"))
        assertTrue(source.contains("QuickStartGuideCopy.csvFootnote(this)"))
        assertFalse(source.contains("PAGE_TITLE"))
        assertFalse(source.contains("CSV_FOOTNOTE"))
    }

    @Test
    fun copyObject_buildsSectionsFromResources() {
        val source = kotlinSource("domain/help/QuickStartGuideCopy.kt")
        assertTrue(source.contains("R.string.guide_section_settings_title"))
        assertTrue(source.contains("R.string.guide_utility_family_share"))
        assertTrue(source.contains("fun sectionsFor("))
        assertEquals(6, Regex("number = [1-6]").findAll(source).count())
    }

    @Test
    fun footer_mentionsPremiumRestrictionsInBothLocales() {
        assertTrue(stringIt("guide_footer_note").contains("premium"))
        assertTrue(stringIt("guide_footer_note").contains("restrizioni temporali"))
        assertTrue(stringEn("guide_footer_note").contains("premium"))
        assertTrue(stringEn("guide_footer_note").contains("time limits"))
    }

    @Test
    fun csvFootnoteBuilder_usesImportAndExportConfig() {
        val source = kotlinSource("domain/help/QuickStartGuideCopy.kt")
        assertTrue(source.contains("ImportConfiguration.SEPARATOR"))
        assertTrue(source.contains("ImportConfiguration.FILE_NAME"))
        assertTrue(source.contains("ImportConfiguration.FORMAT_NAME"))
        assertTrue(source.contains("ImportConfiguration.SECTION_BOXES"))
        assertTrue(source.contains("ImportConfiguration.SECTION_OBJECTS"))
        assertTrue(source.contains("ImportConfiguration.PRE_IMPORT_PREFIX"))
        assertTrue(source.contains("ViewOutputConfiguration.EXPORT_FILE_PREFIX"))
        assertTrue(stringIt("guide_csv_export_same_schema").contains("Importa dati"))
        assertTrue(stringEn("guide_csv_export_same_schema").contains("Import data"))
        assertEquals(";", ImportConfiguration.SEPARATOR)
        assertTrue(ViewOutputConfiguration.EXPORT_FILE_PREFIX.isNotBlank())
    }

    @Test
    fun settingsSection_mentionsPrivacyAndSharedArchive() {
        assertEquals("Impostazioni", stringIt("guide_section_settings_title"))
        assertEquals("Settings", stringEn("guide_section_settings_title"))
        assertTrue(stringIt("guide_section_settings_b1").contains("Archivio Condiviso"))
        assertTrue(stringEn("guide_section_settings_b1").contains("Shared Archive"))
        assertTrue(stringIt("guide_section_settings_b3").contains("Privacy"))
        assertTrue(stringEn("guide_section_settings_b3").contains("Privacy"))
    }

    @Test
    fun contextualToolsSection_referencesCsvFootnote() {
        assertEquals("Strumenti contestuali", stringIt("guide_section_tools_title"))
        assertEquals("Contextual tools", stringEn("guide_section_tools_title"))
        assertTrue(stringIt("guide_section_tools_b2").contains("(*)"))
        assertTrue(stringEn("guide_section_tools_b2").contains("(*)"))
        assertTrue(stringIt("guide_section_tools_closing").contains("etichetta QR"))
        assertTrue(stringEn("guide_section_tools_closing").contains("QR label"))
    }

    @Test
    fun familyBetaUtilityStrings_differFromPlayUtility() {
        assertTrue(stringIt("guide_utility_family_share").contains("Condividi Archivio"))
        assertTrue(stringEn("guide_utility_family_share").contains("Share Archive"))
        assertTrue(stringIt("guide_utility_family_merge").contains("non Ripristino"))
        assertTrue(stringEn("guide_utility_family_merge").contains("not Restore"))
        val source = kotlinSource("domain/help/QuickStartGuideCopy.kt")
        assertTrue(source.contains("if (includeFamilyBeta)"))
    }

    private fun kotlinSource(relativeUnderJava: String): String {
        val path = "com/example/boxmanagernew/$relativeUnderJava"
        return File("app/src/main/java/$path")
            .takeIf { it.isFile }
            ?.readText()
            ?: File("src/main/java/$path").readText()
    }

    private fun stringIt(name: String): String =
        stringValue("values/strings.xml", name)

    private fun stringEn(name: String): String =
        stringValue("values-en/strings.xml", name)

    private fun stringValue(relative: String, name: String): String {
        val text = File("app/src/main/res/$relative")
            .takeIf { it.isFile }
            ?.readText()
            ?: File("src/main/res/$relative").readText()
        val match = Regex("""name="$name">(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
            .find(text)
            ?: error("Missing string $name in $relative")
        return match.groupValues[1]
            .replace("\\'", "'")
            .replace("\\\"", "\"")
            .replace("\\n", "\n")
    }
}
