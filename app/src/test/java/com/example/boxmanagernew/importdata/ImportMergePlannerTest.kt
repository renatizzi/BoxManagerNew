package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import com.example.boxmanagernew.importdata.merge.ImportMergePlanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportMergePlannerTest {

    private val planner = ImportMergePlanner()

    @Test
    fun newBoxAndObject_areImported() {
        val plan = planner.plan(
            fileBoxes = listOf(box("NomeTest", "Generico", "Cucina")),
            fileObjects = listOf(obj("Viti", "NomeTest", null, "100")),
            archiveBoxes = emptyList(),
            archiveObjects = emptyList()
        )

        assertTrue(plan.canApply)
        assertEquals(2, plan.recordsRead)
        assertEquals(2, plan.imported)
        assertEquals(0, plan.ignoredDuplicates)
        assertEquals(1, plan.boxesToInsert.size)
        assertEquals(1, plan.objectsToInsert.size)
        assertEquals(100, plan.objectsToInsert.single().quantity)
    }

    @Test
    fun sameBoxAlreadyInArchive_isIgnoredDuplicate() {
        val plan = planner.plan(
            fileBoxes = listOf(box("NomeTest", "Generico", "Cucina")),
            fileObjects = emptyList(),
            archiveBoxes = listOf(
                ImportMergePlanner.ArchiveBox("NomeTest", "Generico", "Cucina")
            ),
            archiveObjects = emptyList()
        )

        assertEquals(0, plan.imported)
        assertEquals(1, plan.ignoredDuplicates)
        assertTrue(plan.boxesToInsert.isEmpty())
    }

    @Test
    fun objectToExistingBox_isImported() {
        val plan = planner.plan(
            fileBoxes = emptyList(),
            fileObjects = listOf(obj("Viti", "Box attrezzi", "4mm", null)),
            archiveBoxes = listOf(
                ImportMergePlanner.ArchiveBox("Box attrezzi", "Generico", "Cucina")
            ),
            archiveObjects = emptyList()
        )

        assertEquals(1, plan.imported)
        assertEquals("Box attrezzi", plan.objectsToInsert.single().box)
    }

    @Test
    fun invalidQuantity_isDiscardedAndBlocksApply() {
        val plan = planner.plan(
            fileBoxes = emptyList(),
            fileObjects = listOf(obj("Viti", "Box attrezzi", null, "dieci")),
            archiveBoxes = emptyList(),
            archiveObjects = emptyList()
        )

        assertFalse(plan.canApply)
        assertEquals(1, plan.discardedErrors)
        assertEquals(0, plan.imported)
    }

    @Test
    fun reimportSameObject_isIgnoredDuplicate() {
        val plan = planner.plan(
            fileBoxes = emptyList(),
            fileObjects = listOf(obj("Viti", "Box attrezzi", "4mm", "10")),
            archiveBoxes = emptyList(),
            archiveObjects = listOf(
                ImportMergePlanner.ArchiveObject("Viti", "Box attrezzi", "4mm", 10)
            )
        )

        assertEquals(1, plan.ignoredDuplicates)
        assertTrue(plan.objectsToInsert.isEmpty())
    }

    private fun box(
        name: String,
        category: String,
        position: String
    ) = ImportFileInspector.BoxRow(name, category, position)

    private fun obj(
        name: String,
        box: String,
        description: String?,
        quantity: String?
    ) = ImportFileInspector.ObjectRow(name, box, description, quantity)
}
