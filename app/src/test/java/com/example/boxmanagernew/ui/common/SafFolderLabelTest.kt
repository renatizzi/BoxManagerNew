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

    @Test
    fun encodedAccDocPattern_isReadablePath() {
        assertEquals(
            "Download/Boxmanager_Bck",
            SafFolderLabel.fromDocumentId(
                "acc=1;doc=encoded=primary%3ADownload%2FBoxmanager_Bck",
                "Cartella selezionata"
            )
        )
    }

    @Test
    fun rawAccDocFallback_rejectedForReadableName() {
        assertEquals(
            SafFolderLabel.DEFAULT_LABEL,
            SafFolderLabel.fromDocumentId(
                "acc=1;doc=raw",
                "acc=1;doc=encoded=broken"
            )
        )
    }

    @Test
    fun opaqueBase64DocumentId_usesFallbackNotRawId() {
        assertEquals(
            SafFolderLabel.DEFAULT_LABEL,
            SafFolderLabel.fromDocumentId(
                "3TJfDST5QTeo6YbjhYAIVY_5SYPkZDf19qvzoDxkSiN4XGZhgy6Rhe3h6g3o7dH0_g==",
                SafFolderLabel.DEFAULT_LABEL
            )
        )
    }

    @Test
    fun opaqueTreeName_isNotReadableLabel() {
        assertEquals(
            false,
            SafFolderLabel.isReadableLabel(
                "3TJfDST5QTeo6YbjhYAIVY_5SYPkZDf19qvzoDxkSiN4XGZhgy6Rhe3h6g3o7dH0_g=="
            )
        )
    }
}
