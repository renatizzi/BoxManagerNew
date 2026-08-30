package com.example.boxmanagernew.family

import com.example.boxmanagernew.domain.family.FamilyMergeCopy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyMergeCopyTest {

    @Test
    fun utilityCard_usesEmojiSingleLine() {
        assertEquals("📤 Condividi Archivio", FamilyMergeCopy.UTILITY_CARD_LABEL)
        assertFalse(FamilyMergeCopy.UTILITY_CARD_LABEL.contains("\n"))
    }

    @Test
    fun buttons_useInviaRiceviArchivio() {
        assertEquals("📤 Invia Archivio", FamilyMergeCopy.BUTTON_SEND)
        assertEquals("📥 Ricevi Archivio", FamilyMergeCopy.BUTTON_RECEIVE)
        assertEquals(
            "📤 Invia tabelle condivise",
            FamilyMergeCopy.BUTTON_SEND_SHARED_TABLES
        )
        assertEquals(
            "📥 Ricevi tabelle condivise",
            FamilyMergeCopy.BUTTON_RECEIVE_SHARED_TABLES
        )
        assertTrue(FamilyMergeCopy.BUTTON_SEND.contains("Invia"))
        assertTrue(FamilyMergeCopy.BUTTON_RECEIVE.contains("Ricevi"))
    }

    @Test
    fun sectionHints_useRequestedWording() {
        assertTrue(
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT.contains(
                "Definisci e personalizza le tabelle"
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT.contains(
                "\"Tabelle condivise\""
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_ARCHIVE_HINT.contains(
                "\"Archivio\""
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_ARCHIVE_HINT.contains(
                "tutti i dati dell'Archivio"
            )
        )
    }

    @Test
    fun folderCopy_isDefined() {
        assertEquals("Cartella condivisa", FamilyMergeCopy.FOLDER_TITLE)
        assertEquals(
            "Nessuna cartella selezionata",
            FamilyMergeCopy.FOLDER_NONE
        )
        assertTrue(FamilyMergeCopy.FOLDER_HINT.contains("Invia e Ricevi"))
    }

    @Test
    fun copy_avoidsStrutturaWording() {
        val texts = listOf(
            FamilyMergeCopy.INTRO,
            FamilyMergeCopy.SECTION_SHARED_TABLES,
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT,
            FamilyMergeCopy.SECTION_ARCHIVE,
            FamilyMergeCopy.SECTION_ARCHIVE_HINT,
            FamilyMergeCopy.FOLDER_HINT
        )
        for (text in texts) {
            assertFalse(
                "Testo non deve contenere «struttura»: $text",
                text.contains("struttura", ignoreCase = true)
            )
        }
        assertTrue(
            FamilyMergeCopy.SECTION_SHARED_TABLES.contains("tabelle condivise", true)
        )
    }

    @Test
    fun accentCardTexts_includeAllFamilyLabels() {
        val accents = FamilyMergeCopy.accentCardTexts()
        assertTrue(accents.contains(FamilyMergeCopy.UTILITY_CARD_LABEL))
        assertTrue(accents.contains(FamilyMergeCopy.BUTTON_SEND_SHARED_TABLES))
        assertTrue(accents.contains(FamilyMergeCopy.BUTTON_RECEIVE_SHARED_TABLES))
        assertTrue(accents.contains(FamilyMergeCopy.BUTTON_SEND))
        assertTrue(accents.contains(FamilyMergeCopy.BUTTON_RECEIVE))
    }
}
