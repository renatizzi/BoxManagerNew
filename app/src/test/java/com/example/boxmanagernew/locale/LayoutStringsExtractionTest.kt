package com.example.boxmanagernew.locale

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        assertEquals("Object list", stringEn("box_detail_title"))
        assertEquals("Lista Oggetti", stringIt("box_detail_title"))
        assertEquals("Share Archive", stringEn("family_page_title"))
        assertEquals("Condivisione Archivio", stringIt("family_page_title"))
        assertEquals("Full archive (trial)", stringEn("premium_settings_unlock_title"))
        assertEquals("Archivio completo (prova)", stringIt("premium_settings_unlock_title"))
        assertEquals("Edit", stringEn("menu_edit"))
        assertEquals("Modifica", stringIt("menu_edit"))
        assertEquals("View QR label", stringEn("menu_view_qr_label"))
        assertEquals("Move", stringEn("menu_move"))
        assertEquals("Quick guide", stringEn("guide_page_title"))
        assertEquals("Guida rapida", stringIt("guide_page_title"))
        assertEquals("Advanced feature", stringEn("premium_page_subtitle"))
        assertEquals("Funzione avanzata", stringIt("premium_page_subtitle"))
        assertEquals("SHARE", stringEn("premium_button_share"))
        assertEquals("CONDIVIDI", stringIt("premium_button_share"))
    }

    @Test
    fun contextualMenus_useStringResourcesNotHardcodedItalian() {
        val adapters = listOf(
            "ui/main/BoxAdapter.kt",
            "ui/boxdetail/ObjectAdapter.kt",
            "ui/categories/CategoryAdapter.kt",
            "ui/settings/LocationAdapter.kt"
        )
        for (relative in adapters) {
            val source = kotlinSource(relative)
            assertTrue(
                "$relative deve usare R.string.menu_edit",
                source.contains("R.string.menu_edit")
            )
            assertTrue(
                "$relative deve usare R.string.menu_delete",
                source.contains("R.string.menu_delete")
            )
            assertFalse(
                "$relative non deve matchare sul titolo italiano",
                source.contains("\"Modifica\"")
            )
            assertFalse(
                "$relative non deve matchare sul titolo italiano",
                source.contains("\"Elimina\"")
            )
        }
        val boxDetail = kotlinSource("ui/BoxDetailActivity.kt")
        assertTrue(boxDetail.contains("R.string.box_detail_title"))
        assertTrue(boxDetail.contains("R.string.box_detail_subtitle_named"))
        assertFalse(boxDetail.contains("\"Lista Oggetti\""))

        val family = kotlinSource("ui/family/FamilyCatalogActivity.kt")
        assertTrue(family.contains("R.string.family_page_title"))
        assertFalse(family.contains("FamilyMergeCopy."))

        val settings = kotlinSource("ui/settings/SettingsActivity.kt")
        assertTrue(settings.contains("R.string.premium_settings_unlock_title"))
        assertFalse(settings.contains("ArchivioCompletoCopy.SETTINGS_"))
    }

    private fun kotlinSource(relativeUnderJava: String): String {
        val path = "com/example/boxmanagernew/$relativeUnderJava"
        return File("app/src/main/java/$path")
            .takeIf { it.isFile }
            ?.readText()
            ?: File("src/main/java/$path").readText()
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
