package com.example.boxmanagernew.viewoutput

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Esporta vista (strumento contestuale): nel box nome file
 * deve comparire la cartella e il pulsante Cartella, come Genera Modello.
 */
class ViewExportFolderDialogPolicyTest {

    @Test
    fun viewOutputController_showsFolderAndBrowseInNameBox() {
        val body = askExportFileNameBody(
            "app/src/main/java/com/example/boxmanagernew/viewoutput/ui/ViewOutputController.kt"
        )
        assertTrue(body.contains("onBrowseFolder"))
        assertTrue(body.contains("launchFolderPicker()"))
        assertTrue(body.contains("folderName"))
        assertTrue(body.contains("folderDisplayName"))
    }

    @Test
    fun mainActivity_showsFolderAndBrowseInNameBox() {
        val body = askExportFileNameBody(
            "app/src/main/java/com/example/boxmanagernew/MainActivity.kt"
        )
        assertTrue(body.contains("onBrowseFolder"))
        assertTrue(body.contains("exportFolderPicker.launch"))
        assertTrue(body.contains("folderName"))
        assertTrue(body.contains("folderDisplayName"))
    }

    private fun askExportFileNameBody(relative: String): String {
        val source = source(relative)
        val fromDialog = source.substringAfter("DialogUtils.showExportFileName")
        return fromDialog.substringBefore("private fun writeExport")
    }

    private fun source(relative: String): String {
        val candidates = listOf(
            File(relative.removePrefix("app/")),
            File(relative)
        )
        return candidates.first { it.isFile }.readText()
    }
}
