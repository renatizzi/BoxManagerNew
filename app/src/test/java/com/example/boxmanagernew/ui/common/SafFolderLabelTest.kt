package com.example.boxmanagernew.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class SafFolderLabelTest {

    @Test
    fun primaryDownloadSubfolder_isReadablePath() {
        assertEquals(
            "Download/Boxmanager_Bck",
            SafFolderLabel.fromDocumentId(
                "primary:Download/Boxmanager_Bck",
                "Cartella selezionata"
            )
        )
    }

    @Test
    fun importCsvFolder_isReadablePath() {
        assertEquals(
            "Download/Bck_prova",
            SafFolderLabel.fromDocumentId(
                "primary:Download/Bck_prova",
                "Cartella selezionata"
            )
        )
    }

    @Test
    fun encodedDownloadSubfolder_isReadablePath() {
        assertEquals(
            "Download/Boxmanager_Famiglia",
            SafFolderLabel.fromDocumentId(
                "primary:Download%2FBoxmanager_Famiglia",
                "Cartella selezionata"
            )
        )
    }

    @Test
    fun blankAfterVolume_usesFallback() {
        assertEquals(
            "Cartella selezionata",
            SafFolderLabel.fromDocumentId(
                "primary:",
                "Cartella selezionata"
            )
        )
    }
}
