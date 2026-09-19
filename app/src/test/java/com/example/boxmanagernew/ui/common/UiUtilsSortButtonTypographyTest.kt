package com.example.boxmanagernew.ui.common

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * ORDINA deve ereditare BoxManager.Button (14sp bold allCaps) come SPOSTA/ELIMINA:
 * vietato forzare textSize/typeface in [UiUtils.updateSortButton].
 */
class UiUtilsSortButtonTypographyTest {

    @Test
    fun updateSortButton_doesNotOverrideTextSizeOrTypeface() {
        val src = File("src/main/java/com/example/boxmanagernew/ui/common/UiUtils.kt")
        assertTrue(src.exists())
        val text = src.readText()
        val start = text.indexOf("fun updateSortButton")
        assertTrue(start >= 0)
        val end = text.indexOf("fun setupSearchAndSort", start)
        assertTrue(end > start)
        val body = text.substring(start, end)
        assertFalse(body.contains("textSize"))
        assertFalse(body.contains("setTypeface"))
        assertFalse(body.contains("SORT_TEXT_SIZE"))
    }
}
