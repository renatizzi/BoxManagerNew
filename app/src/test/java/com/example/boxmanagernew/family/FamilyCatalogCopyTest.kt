package com.example.boxmanagernew.family

import com.example.boxmanagernew.domain.family.FamilyCatalogCopy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyCatalogCopyTest {

    @Test
    fun utilityCard_usesEmojiSingleLine() {
        assertEquals("📋 Catalogo Famiglia", FamilyCatalogCopy.UTILITY_CARD_LABEL)
        assertFalse(FamilyCatalogCopy.UTILITY_CARD_LABEL.contains("\n"))
    }

    @Test
    fun buttons_useInviaRicevi_notImportExport() {
        assertEquals("📤 Invia Catalogo", FamilyCatalogCopy.BUTTON_SEND)
        assertEquals("📥 Ricevi Catalogo", FamilyCatalogCopy.BUTTON_RECEIVE)
        assertEquals("📤 Invia Inventario", FamilyCatalogCopy.BUTTON_SEND_INVENTORY)
        assertEquals("📥 Ricevi Inventario", FamilyCatalogCopy.BUTTON_RECEIVE_INVENTORY)
        assertTrue(
            FamilyCatalogCopy.BUTTON_SEND.contains("Invia")
        )
        assertTrue(
            FamilyCatalogCopy.BUTTON_RECEIVE.contains("Ricevi")
        )
    }

    @Test
    fun accentCardTexts_includeAllFamilyLabels() {
        val accents = FamilyCatalogCopy.accentCardTexts()
        assertTrue(accents.contains(FamilyCatalogCopy.UTILITY_CARD_LABEL))
        assertTrue(accents.contains(FamilyCatalogCopy.BUTTON_SEND))
        assertTrue(accents.contains(FamilyCatalogCopy.BUTTON_RECEIVE))
        assertTrue(accents.contains(FamilyCatalogCopy.BUTTON_SEND_INVENTORY))
        assertTrue(accents.contains(FamilyCatalogCopy.BUTTON_RECEIVE_INVENTORY))
    }
}
