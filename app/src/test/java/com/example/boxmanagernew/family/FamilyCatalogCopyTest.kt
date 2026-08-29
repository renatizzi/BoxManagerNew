package com.example.boxmanagernew.family

import com.example.boxmanagernew.domain.family.FamilyCatalogCopy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FamilyCatalogCopyTest {

    @Test
    fun utilityCard_usesEmojiSingleLine() {
        assertEquals("📋 Catalogo famiglia", FamilyCatalogCopy.UTILITY_CARD_LABEL)
        assertFalse(FamilyCatalogCopy.UTILITY_CARD_LABEL.contains("\n"))
    }

    @Test
    fun pageTitle_matchesImportStyleCapitalization() {
        assertEquals("Catalogo Famiglia", FamilyCatalogCopy.PAGE_TITLE)
    }
}
