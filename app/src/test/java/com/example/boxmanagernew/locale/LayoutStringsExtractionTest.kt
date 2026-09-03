package com.example.boxmanagernew.locale

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * M1b: i layout core non devono avere testi utente hardcoded (solo @string).
 */
class LayoutStringsExtractionTest {

    @Test
    fun coreLayouts_useStringResourcesForUserText() {
        val layouts = listOf(
            "layout_bottom_nav.xml",
            "layout_global_topbar.xml",
            "activity_dashboard.xml",
            "activity_utility.xml",
            "activity_backup.xml",
            "activity_restore.xml",
            "activity_import.xml",
            "activity_main.xml",
            "activity_categories.xml",
            "activity_box_detail.xml",
            "activity_settings.xml"
        )

        val pattern =
            Regex("android:(text|hint|contentDescription)=\"([^@][^\"]*)\"")

        val offenders = mutableListOf<String>()
        for (name in layouts) {
            val text = layout(name)
            pattern.findAll(text).forEach { match ->
                val value = match.groupValues[2]
                if (value.isNotBlank()) {
                    offenders.add("$name: ${match.value}")
                }
            }
        }

        assertTrue(
            "Layout con testo hardcoded: $offenders",
            offenders.isEmpty()
        )
    }

    @Test
    fun englishStrings_coverItalianLayoutKeys() {
        val itKeys = stringKeys("values/strings.xml")
        val enKeys = stringKeys("values-en/strings.xml")
        val missing = itKeys - enKeys
        assertTrue(
            "Chiavi EN mancanti: $missing",
            missing.isEmpty()
        )
    }

    @Test
    fun englishNavLabels_differFromItalian() {
        assertEquals("Containers", stringEn("nav_boxes"))
        assertEquals("Contenitori", stringIt("nav_boxes"))
        assertEquals("📤 Share Archive", stringEn("utility_family_catalog"))
        assertEquals("📤 Condividi Archivio", stringIt("utility_family_catalog"))
        assertEquals("Settings", stringEn("page_settings_title"))
        assertEquals("Impostazioni", stringIt("page_settings_title"))
        assertEquals("Yes", stringEn("common_yes"))
        assertEquals("SI", stringIt("common_yes"))
    }

    private fun layout(name: String): String {
        return File("app/src/main/res/layout/$name")
            .takeIf { it.isFile }
            ?.readText()
            ?: File("src/main/res/layout/$name").readText()
    }

    private fun stringKeys(relative: String): Set<String> {
        val text = File("app/src/main/res/$relative")
            .takeIf { it.isFile }
            ?.readText()
            ?: File("src/main/res/$relative").readText()
        return Regex("name=\"([^\"]+)\"")
            .findAll(text)
            .map { it.groupValues[1] }
            .toSet()
    }

    private fun stringIt(name: String): String {
        return stringValue("values/strings.xml", name)
    }

    private fun stringEn(name: String): String {
        return stringValue("values-en/strings.xml", name)
    }

    private fun stringValue(relative: String, name: String): String {
        val text = File("app/src/main/res/$relative")
            .takeIf { it.isFile }
            ?.readText()
            ?: File("src/main/res/$relative").readText()
        val match =
            Regex("<string name=\"$name\"[^>]*>([^<]*)</string>")
                .find(text)
                ?: Regex(
                    "<string name=\"$name\">\\s*([^<]+?)\\s*</string>",
                    RegexOption.DOT_MATCHES_ALL
                ).find(text)
        return match?.groupValues?.get(1)?.trim().orEmpty()
    }
}
