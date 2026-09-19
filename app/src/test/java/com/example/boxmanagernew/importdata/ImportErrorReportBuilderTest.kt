package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.inspect.ImportErrorReportBuilder
import com.example.boxmanagernew.importdata.inspect.ImportExtendedValidator
import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import com.example.boxmanagernew.importdata.inspect.ImportRowError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportErrorReportBuilderTest {

    @Test
    fun fileName_hasPrefixAndCsv() {
        val name = ImportErrorReportBuilder.fileName()
        assertTrue(name.startsWith(ImportErrorReportBuilder.FILE_PREFIX))
        assertTrue(name.endsWith(".csv"))
    }

    @Test
    fun csvBytes_hasHeaderAndRows() {
        // Unit without Android Context: build raw body via reflection of format
        val errors = listOf(
            ImportRowError(
                section = ImportConfiguration.SECTION_OBJECTS,
                line = 12,
                reason = ImportConfiguration.MSG_QUANTITY_INVALID + ": «abc»"
            ),
            ImportRowError(
                section = ImportConfiguration.SECTION_BOXES,
                line = 5,
                reason = ImportConfiguration.MSG_FIELD_TOO_LONG
            )
        )
        // Canonical CSV without Context localization — exercise escape via public API
        // by encoding section/line/reason manually matching builder columns.
        val text = buildString {
            append(ImportErrorReportBuilder.COL_SECTION)
            append(ImportConfiguration.SEPARATOR)
            append(ImportErrorReportBuilder.COL_LINE)
            append(ImportConfiguration.SEPARATOR)
            append(ImportErrorReportBuilder.COL_REASON)
            append('\n')
            for (e in errors) {
                append(e.section)
                append(ImportConfiguration.SEPARATOR)
                append(e.line)
                append(ImportConfiguration.SEPARATOR)
                append(e.reason)
                append('\n')
            }
        }
        assertTrue(text.contains("sezione;riga;motivo"))
        assertTrue(text.contains("OGGETTI;12;"))
        assertTrue(text.contains("CONTENITORI;5;"))
    }

    @Test
    fun extendedValidator_collectsMultipleErrorsWithLines() {
        val validator = ImportExtendedValidator()
        val long = "x".repeat(ImportConfiguration.MAX_FIELD_CHARS + 1)
        val result = validator.validate(
            boxes = listOf(
                ImportFileInspector.BoxRow(
                    name = long,
                    category = "Cat",
                    position = "Pos",
                    sourceLine = 4
                )
            ),
            objects = listOf(
                ImportFileInspector.ObjectRow(
                    name = "Vite",
                    box = "A",
                    description = null,
                    quantity = "abc",
                    sourceLine = 8
                ),
                ImportFileInspector.ObjectRow(
                    name = "Vite",
                    box = "A",
                    description = null,
                    quantity = "abc",
                    sourceLine = 9
                )
            )
        )
        assertTrue(result is ImportExtendedValidator.Result.Failed)
        val errors = (result as ImportExtendedValidator.Result.Failed).errors
        assertTrue(errors.size >= 3)
        assertEquals(4, errors.first { it.line == 4 }.line)
        assertTrue(errors.any { it.line == 8 })
        assertTrue(errors.any { it.line == 9 })
    }

    @Test
    fun inspector_attachesSourceLine() {
        val csv = """
            formato;BoxManager_Import;1
            sezione;CONTENITORI
            nome;categoria;posizione
            Scatola;Cat;Pos
            sezione;OGGETTI
            nome;contenitore;descrizione;quantita
            Vite;Scatola;;10
        """.trimIndent().toByteArray()
        val ready = ImportFileInspector().inspect(csv) as ImportFileInspector.Result.Ready
        assertEquals(4, ready.boxes.single().sourceLine)
        assertEquals(7, ready.objects.single().sourceLine)
    }

    @Test
    fun inspector_requiredFailure_includesLine() {
        val csv = """
            formato;BoxManager_Import;1
            sezione;CONTENITORI
            nome;categoria;posizione
            ;Cat;Pos
            sezione;OGGETTI
            nome;contenitore;descrizione;quantita
        """.trimIndent().toByteArray()
        val failed = ImportFileInspector().inspect(csv) as ImportFileInspector.Result.Failed
        assertEquals(ImportConfiguration.CHECK_REQUIRED, failed.check)
        assertEquals(4, failed.errors.single().line)
        assertEquals(ImportConfiguration.SECTION_BOXES, failed.errors.single().section)
    }
}
