package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ImportPreBackupNameTest {

    @Test
    fun preImportFileName_usesDedicatedPrefixAndTimestamp() {
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.AUGUST, 22, 8, 15, 0)

        val name = ImportConfiguration.preImportFileName(calendar.time)

        assertTrue(name.startsWith(ImportConfiguration.PRE_IMPORT_PREFIX))
        assertEquals(
            ImportConfiguration.PRE_IMPORT_PREFIX + "220826_0815",
            name
        )
    }

    @Test
    fun templateFileName_staysOfficialUnlessEdited() {
        assertEquals(
            ImportConfiguration.FILE_NAME,
            ImportConfiguration.templateFileName("")
        )
        assertEquals(
            ImportConfiguration.FILE_NAME,
            ImportConfiguration.templateFileName("  ")
        )
        assertEquals(
            "Modello_Importazione.csv",
            ImportConfiguration.templateFileName("Modello_Importazione")
        )
        assertEquals(
            "Copia_modello.csv",
            ImportConfiguration.templateFileName("Copia_modello")
        )
        assertEquals(
            "Modello_Importazione",
            ImportConfiguration.templateStem(ImportConfiguration.FILE_NAME)
        )
    }

    @Test
    fun generaModello_reusesBackupFolderKey() {
        assertEquals(
            StorageFolderConfiguration.KEY_BACKUP,
            ImportConfiguration.TEMPLATE_FOLDER_KEY
        )
        assertNotEquals(
            StorageFolderConfiguration.KEY_IMPORT_EXPORT,
            ImportConfiguration.TEMPLATE_FOLDER_KEY
        )
    }

    @Test
    fun importOpenMimeTypes_areCsvNotZip() {
        val types = ImportConfiguration.IMPORT_OPEN_MIME_TYPES.toList()
        assertTrue(types.contains(ImportConfiguration.CSV_MIME_TYPE))
        assertTrue(types.none { it.contains("zip", ignoreCase = true) })
        assertTrue(types.none { it == "*/*" })
    }

    @Test
    fun officialFormatLine_acceptsTrailingEmptyAndIgnoresFormatoCase() {
        assertTrue(
            ImportConfiguration.isOfficialFormatLine(
                listOf("formato", "BoxManager_Import", "1")
            )
        )
        assertTrue(
            ImportConfiguration.isOfficialFormatLine(
                listOf("Formato", "BoxManager_Import", "1", "")
            )
        )
        assertTrue(
            !ImportConfiguration.isOfficialFormatLine(
                listOf("formato", "BoxManager_FamilyCatalog", "1")
            )
        )
    }
}
