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
        assertFalse(
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT.contains(
                "operazione rischiosa",
                ignoreCase = true
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT.contains(
                "esportare"
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT.contains(
                "tasto \"Invia\""
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT.contains(
                "tasto \"Ricevi\""
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_ARCHIVE_HINT.contains(
                "esportare"
            )
        )
        assertTrue(
            FamilyMergeCopy.SECTION_ARCHIVE_HINT.contains(
                "riconoscendo gli stessi contenitori e oggetti"
            )
        )
        assertFalse(
            FamilyMergeCopy.SECTION_ARCHIVE_HINT.contains(
                "intero archivio",
                ignoreCase = true
            )
        )
        assertTrue(
            FamilyMergeCopy.INTRO.contains("Importa ed Esporta Dati")
        )
    }

    @Test
    fun buildExportSummary_usesReadableFolderPath() {
        val summary = FamilyMergeCopy.buildExportSummary(
            folderName = "Download/Boxmanager_Famiglia",
            fileName = "Tabelle_Condivise_300826_1200.csv"
        )
        assertTrue(summary.contains("Salvataggio completato."))
        assertTrue(summary.contains("Cartella: Download/Boxmanager_Famiglia"))
        assertTrue(summary.contains("Nome file: Tabelle_Condivise_300826_1200.csv"))
        assertFalse(summary.contains("content://"))
    }

    @Test
    fun copy_avoidsStrutturaWording() {
        val texts = listOf(
            FamilyMergeCopy.INTRO,
            FamilyMergeCopy.SECTION_SHARED_TABLES,
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT,
            FamilyMergeCopy.SECTION_ARCHIVE,
            FamilyMergeCopy.SECTION_ARCHIVE_HINT
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
