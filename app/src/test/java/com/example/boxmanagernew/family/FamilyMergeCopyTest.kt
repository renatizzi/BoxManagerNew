package com.example.boxmanagernew.family

import com.example.boxmanagernew.domain.family.FamilyMergeCopy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyMergeCopyTest {

    @Test
    fun utilityCard_usesEmojiSingleLine() {
        assertEquals("🤝 Condivisione Archivio", FamilyMergeCopy.UTILITY_CARD_LABEL)
        assertFalse(FamilyMergeCopy.UTILITY_CARD_LABEL.contains("\n"))
    }

    @Test
    fun buttons_useInviaRiceviArchivio() {
        assertEquals("📤 Invia Archivio", FamilyMergeCopy.BUTTON_SEND)
        assertEquals("📥 Ricevi Archivio", FamilyMergeCopy.BUTTON_RECEIVE)
        assertTrue(FamilyMergeCopy.BUTTON_SEND.contains("Invia"))
        assertTrue(FamilyMergeCopy.BUTTON_RECEIVE.contains("Ricevi"))
    }

    @Test
    fun accentCardTexts_includeAllFamilyLabels() {
        val accents = FamilyMergeCopy.accentCardTexts()
        assertTrue(accents.contains(FamilyMergeCopy.UTILITY_CARD_LABEL))
        assertTrue(accents.contains(FamilyMergeCopy.BUTTON_SEND))
        assertTrue(accents.contains(FamilyMergeCopy.BUTTON_RECEIVE))
    }
}
