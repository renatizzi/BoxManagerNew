package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import com.example.boxmanagernew.importdata.template.ImportTemplateBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportFileInspectorTest {

    private val inspector = ImportFileInspector()

    @Test
    fun officialEmptyTemplate_isReadyWithZeroRecords() {
        val result = inspector.inspect(ImportTemplateBuilder().build())

        assertTrue(result is ImportFileInspector.Result.Ready)
        val ready = result as ImportFileInspector.Result.Ready
        assertEquals(0, ready.recordsRead)
        assertEquals(2, ready.formatVersion)
    }

    @Test
    fun filledTemplate_countsBoxAndObjectRows() {
        val result = inspector.inspect(
            csv(
                "formato;BoxManager_Import;1",
                "sezione;CONTENITORI",
                "nome;categoria;posizione",
                "Scatola pasta;Alimenti;Cucina",
                "sezione;OGGETTI",
                "nome;contenitore;descrizione;quantita",
                "Viti;Scatola pasta;4mm;100"
            )
        )

        assertTrue(result is ImportFileInspector.Result.Ready)
        val ready = result as ImportFileInspector.Result.Ready
        assertEquals(2, ready.recordsRead)
        assertEquals("Scatola pasta", ready.boxes.single().name)
        assertEquals("Viti", ready.objects.single().name)
    }

    @Test
    fun nullBytes_failsFileExists() {
        val result = inspector.inspect(null)
        assertEquals(
            ImportConfiguration.CHECK_FILE_EXISTS,
            (result as ImportFileInspector.Result.Failed).check
        )
    }

    @Test
    fun missingFormatLine_failsFormat() {
        val result = inspector.inspect("not a template".toByteArray())
        assertEquals(
            ImportConfiguration.CHECK_FORMAT,
            (result as ImportFileInspector.Result.Failed).check
        )
    }

    @Test
    fun trailingEmptyFormatField_isOfficial() {
        val result = inspector.inspect(
            csv(
                "formato;BoxManager_Import;1;",
                "sezione;CONTENITORI",
                "nome;categoria;posizione",
                "sezione;OGGETTI",
                "nome;contenitore;descrizione;quantita"
            )
        )

        assertTrue(result is ImportFileInspector.Result.Ready)
    }

    @Test
    fun zipBytes_failFormatCheck() {
        val zipHeader = byteArrayOf(0x50, 0x4B, 0x03, 0x04) +
            "not-a-csv".toByteArray()
        val result = inspector.inspect(zipHeader)
        assertEquals(
            ImportConfiguration.CHECK_FORMAT,
            (result as ImportFileInspector.Result.Failed).check
        )
    }

    @Test
    fun zipWithV2CsvAndPhotos_isReady() {
        val csv = csv(
            "formato;BoxManager_Import;2",
            "sezione;CONTENITORI",
            "nome;categoria;posizione;permanentId",
            "Scatola;Alimenti;Cucina;box-1",
            "sezione;OGGETTI",
            "nome;contenitore;descrizione;quantita;objectPermanentId",
            "Viti;Scatola;4mm;100;obj-1"
        )
        val photos = mapOf(
            "photos/objects/obj-1.jpg" to byteArrayOf(1, 2),
            "photos/objects/obj-1_thumb.jpg" to byteArrayOf(3)
        )
        val zip = com.example.boxmanagernew.importdata.zip.ImportDataZip.pack(
            csv,
            photos
        )
        val result = inspector.inspect(zip)
        assertTrue(result is ImportFileInspector.Result.Ready)
        val ready = result as ImportFileInspector.Result.Ready
        assertEquals(2, ready.formatVersion)
        assertEquals("box-1", ready.boxes.single().permanentId)
        assertEquals("obj-1", ready.objects.single().objectPermanentId)
        assertEquals(2, ready.photoEntries.size)
    }

    @Test
    fun wrongBoxHeader_failsStructure() {
        val result = inspector.inspect(
            csv(
                "formato;BoxManager_Import;1",
                "sezione;CONTENITORI",
                "nome;categoria",
                "sezione;OGGETTI",
                "nome;contenitore;descrizione;quantita"
            )
        )
        assertEquals(
            ImportConfiguration.CHECK_STRUCTURE,
            (result as ImportFileInspector.Result.Failed).check
        )
    }

    @Test
    fun boxMissingPosition_failsRequired() {
        val result = inspector.inspect(
            csv(
                "formato;BoxManager_Import;1",
                "sezione;CONTENITORI",
                "nome;categoria;posizione",
                "Scatola pasta;Alimenti;",
                "sezione;OGGETTI",
                "nome;contenitore;descrizione;quantita"
            )
        )
        assertEquals(
            ImportConfiguration.CHECK_REQUIRED,
            (result as ImportFileInspector.Result.Failed).check
        )
    }

    @Test
    fun excelQuotedHeadersAndBlankLines_areAccepted() {
        val result = inspector.inspect(
            csv(
                "formato;BoxManager_Import;1",
                "",
                "\"sezione\";\"CONTENITORI\"",
                "\"nome\";\"categoria\";\"posizione\"",
                "sezione;OGGETTI",
                "nome;contenitore;descrizione;quantita"
            )
        )

        assertTrue(result is ImportFileInspector.Result.Ready)
    }

    private fun csv(vararg lines: String): ByteArray {
        val body = lines.joinToString(separator = "\r\n", postfix = "\r\n")
        return ImportConfiguration.UTF8_BOM + body.toByteArray(Charsets.UTF_8)
    }
}
