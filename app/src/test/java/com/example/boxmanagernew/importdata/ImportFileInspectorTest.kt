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
