package com.example.boxmanagernew.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvFileNamesTest {

    @Test
    fun force_addsCsvWhenMissing() {
        assertEquals(
            "Lista_Cantina.csv",
            CsvFileNames.force("Lista_Cantina", "export")
        )
    }

    @Test
    fun force_keepsSingleCsvAndNormalizesCase() {
        assertEquals(
            "Lista.csv",
            CsvFileNames.force("Lista.CSV", "export")
        )
        assertEquals(
            "Lista.csv",
            CsvFileNames.force("Lista.csv", "export")
        )
    }

    @Test
    fun force_collapsesRepeatedCsv() {
        assertEquals(
            "Esporta.csv",
            CsvFileNames.force("Esporta.csv.csv", "export")
        )
    }

    @Test
    fun force_replacesUserEditedExtension() {
        assertEquals(
            "report.csv",
            CsvFileNames.force("report.txt", "export")
        )
        assertEquals(
            "report.csv",
            CsvFileNames.force("report.TXT", "export")
        )
    }

    @Test
    fun force_blankUsesDefault() {
        assertEquals(
            "Modello_Importazione.csv",
            CsvFileNames.force("  ", "Modello_Importazione.csv")
        )
    }

    @Test
    fun isLikelyCsv_acceptsNameOrMimeWithoutWrongExtension() {
        assertTrue(CsvFileNames.isLikelyCsv("dati.csv", null))
        assertTrue(
            CsvFileNames.isLikelyCsv(
                "ESPORTA_070926_1200",
                "text/csv"
            )
        )
        assertFalse(
            CsvFileNames.isLikelyCsv(
                "backup.zip",
                "text/csv"
            )
        )
        assertFalse(CsvFileNames.isLikelyCsv("note.txt", null))
    }
}
