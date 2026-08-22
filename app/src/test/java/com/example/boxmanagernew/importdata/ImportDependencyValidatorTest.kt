package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.inspect.ImportDependencyValidator
import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportDependencyValidatorTest {

    private val validator = ImportDependencyValidator()

    @Test
    fun existingCategoryPositionAndArchiveBox_ok() {
        val result = validator.validate(
            boxes = listOf(box("Scatola pasta", "Alimenti", "Cucina")),
            objects = listOf(obj("Viti", "Box attrezzi")),
            categoryNames = listOf("Alimenti"),
            locationNames = listOf("Cucina"),
            archiveBoxNames = listOf("Box attrezzi")
        )

        assertTrue(result is ImportDependencyValidator.Result.Ok)
    }

    @Test
    fun objectCanUseBoxFromSameFile() {
        val result = validator.validate(
            boxes = listOf(box("Scatola pasta", "Alimenti", "Cucina")),
            objects = listOf(obj("Viti", "Scatola pasta")),
            categoryNames = listOf("Alimenti"),
            locationNames = listOf("Cucina"),
            archiveBoxNames = emptyList()
        )

        assertTrue(result is ImportDependencyValidator.Result.Ok)
    }

    @Test
    fun missingCategory_failsBoxDependency() {
        val result = validator.validate(
            boxes = listOf(box("Scatola pasta", "Alimentari", "Cucina")),
            objects = emptyList(),
            categoryNames = listOf("Alimenti"),
            locationNames = listOf("Cucina"),
            archiveBoxNames = emptyList()
        )

        assertEquals(
            ImportConfiguration.MSG_BOX_DEPENDENCY,
            (result as ImportDependencyValidator.Result.Failed).message
        )
    }

    @Test
    fun missingPosition_failsBoxDependency() {
        val result = validator.validate(
            boxes = listOf(box("Scatola pasta", "Alimenti", "Mansarda")),
            objects = emptyList(),
            categoryNames = listOf("Alimenti"),
            locationNames = listOf("Cucina"),
            archiveBoxNames = emptyList()
        )

        assertEquals(
            ImportConfiguration.MSG_BOX_DEPENDENCY,
            (result as ImportDependencyValidator.Result.Failed).message
        )
    }

    @Test
    fun missingBox_failsObjectDependency() {
        val result = validator.validate(
            boxes = emptyList(),
            objects = listOf(obj("Viti", "Inesistente")),
            categoryNames = emptyList(),
            locationNames = emptyList(),
            archiveBoxNames = listOf("Box attrezzi")
        )

        assertEquals(
            ImportConfiguration.MSG_OBJECT_DEPENDENCY,
            (result as ImportDependencyValidator.Result.Failed).message
        )
    }

    @Test
    fun namesMatchIgnoringCase() {
        val result = validator.validate(
            boxes = listOf(box("Scatola pasta", "alimenti", "cucina")),
            objects = listOf(obj("Viti", "scatola pasta")),
            categoryNames = listOf("Alimenti"),
            locationNames = listOf("Cucina"),
            archiveBoxNames = emptyList()
        )

        assertTrue(result is ImportDependencyValidator.Result.Ok)
    }

    private fun box(
        name: String,
        category: String,
        position: String
    ) = ImportFileInspector.BoxRow(name, category, position)

    private fun obj(
        name: String,
        box: String
    ) = ImportFileInspector.ObjectRow(name, box, null, null)
}
