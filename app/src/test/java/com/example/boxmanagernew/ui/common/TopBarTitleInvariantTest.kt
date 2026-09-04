package com.example.boxmanagernew.ui.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Titolo topbar in-app = sempre "BoxManager" (1.2).
 * Launcher flavor sviluppo: stesso nome prodotto BoxManager
 * (flavor Gradle `famiglia` resta solo variante di build).
 */
class TopBarTitleInvariantTest {

    @Test
    fun inAppTopBarTitle_isBoxManagerInBothLocales() {
        assertEquals("BoxManager", stringValue("values/strings.xml", "topbar_app_title"))
        assertEquals("BoxManager", stringValue("values-en/strings.xml", "topbar_app_title"))
    }

    @Test
    fun topBarLayoutAndUtils_doNotUseLauncherAppName() {
        val layout = read("app/src/main/res/layout/layout_global_topbar.xml")
        assertTrue(layout.contains("@string/topbar_app_title"))
        assertFalse(layout.contains("@string/app_name"))

        val utils = read(
            "app/src/main/java/com/example/boxmanagernew/ui/common/TopBarUtils.kt"
        )
        assertTrue(utils.contains("R.string.topbar_app_title"))
        assertFalse(utils.contains("R.string.app_name"))
    }

    @Test
    fun famigliaLauncherName_isBoxManager() {
        assertEquals(
            "BoxManager",
            stringValue("famiglia/res/values/strings.xml", "app_name")
        )
        assertEquals(
            "BoxManager",
            stringValue("famiglia/res/values-en/strings.xml", "app_name")
        )
    }

    private fun read(relativeFromRepo: String): String {
        val candidates = listOf(
            File(relativeFromRepo),
            File(relativeFromRepo.removePrefix("app/"))
        )
        return candidates.first { it.isFile }.readText()
    }

    private fun stringValue(relativeUnderResOrFlavor: String, name: String): String {
        val candidates = listOf(
            File("app/src/main/res/$relativeUnderResOrFlavor"),
            File("src/main/res/$relativeUnderResOrFlavor"),
            File("app/src/$relativeUnderResOrFlavor"),
            File("src/$relativeUnderResOrFlavor")
        )
        val text = candidates.first { it.isFile }.readText()
        val match = Regex(
            "<string name=\"$name\"[^>]*>([^<]*)</string>"
        ).find(text)
        return match?.groupValues?.get(1)?.trim().orEmpty()
    }
}
