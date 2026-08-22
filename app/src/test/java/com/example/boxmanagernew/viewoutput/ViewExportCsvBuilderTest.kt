package com.example.boxmanagernew.viewoutput

import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.SearchResult
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import com.example.boxmanagernew.viewoutput.csv.ViewExportCsvBuilder
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshotFactory
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewExportCsvBuilderTest {

    @Test
    fun build_boxesAndObjects_officialTrackRoundTrip() {

        val snapshot =
            ContainerViewSnapshotFactory.from(
                boxes = listOf(
                    box(1, "Box A", 10, "Cantina"),
                    box(2, "Box B", 11, "Mansarda")
                ),
                categoryNameOf = { id ->
                    if (id == 10) "Alimenti" else "Utensili"
                },
                categoryIconOf = { 0 },
                objects = listOf(
                    SearchResult(
                        objectName = "Viti",
                        description = "M6",
                        quantity = 20,
                        boxId = 1,
                        boxName = "Box A",
                        boxPosition = "Cantina",
                        categoryName = "Alimenti"
                    ),
                    SearchResult(
                        objectName = "Trapano",
                        description = null,
                        quantity = null,
                        boxId = 2,
                        boxName = "Box B",
                        boxPosition = "Mansarda",
                        categoryName = "Utensili"
                    )
                )
            )

        val bytes = ViewExportCsvBuilder().build(snapshot)

        assertArrayEquals(
            ImportConfiguration.UTF8_BOM,
            bytes.copyOfRange(0, ImportConfiguration.UTF8_BOM.size)
        )

        val inspected = ImportFileInspector().inspect(bytes)
        val ready = inspected as ImportFileInspector.Result.Ready

        assertEquals(2, ready.boxes.size)
        assertEquals("Box A", ready.boxes[0].name)
        assertEquals("Alimenti", ready.boxes[0].category)
        assertEquals("Cantina", ready.boxes[0].position)
        assertEquals("Box B", ready.boxes[1].name)
        assertEquals(2, ready.objects.size)
        assertEquals("Viti", ready.objects[0].name)
        assertEquals("Box A", ready.objects[0].box)
        assertEquals("M6", ready.objects[0].description)
        assertEquals("20", ready.objects[0].quantity)
        assertEquals("Trapano", ready.objects[1].name)
        assertEquals("Box B", ready.objects[1].box)
        assertEquals(null, ready.objects[1].description)
        assertEquals(null, ready.objects[1].quantity)
        assertEquals(4, ready.recordsRead)
    }

    @Test
    fun build_objectHits_onlyMatchingRows_noTotalLine() {

        val snapshot =
            ContainerViewSnapshotFactory.fromSearchResults(
                results = listOf(
                    SearchResult(
                        objectName = "Viti",
                        description = "M6",
                        quantity = 20,
                        boxId = 1,
                        boxName = "Box A",
                        boxPosition = "Cantina",
                        categoryName = "Alimenti"
                    )
                )
            ) { 0 }

        val bytes = ViewExportCsvBuilder().build(snapshot)
        val text = String(
            bytes.copyOfRange(
                ImportConfiguration.UTF8_BOM.size,
                bytes.size
            ),
            Charsets.UTF_8
        )

        assertTrue(!text.contains("N. Oggetti"))
        assertTrue(!text.contains("N. Contenitori"))

        val inspected = ImportFileInspector().inspect(bytes)
        val ready = inspected as ImportFileInspector.Result.Ready
        assertEquals(1, ready.boxes.size)
        assertEquals("Box A", ready.boxes[0].name)
        assertEquals(1, ready.objects.size)
        assertEquals("Viti", ready.objects[0].name)
    }

    @Test
    fun build_emptySnapshot_headersOnlyStillValid() {

        val bytes =
            ViewExportCsvBuilder().build(
                ContainerViewSnapshot(emptyList())
            )

        val inspected = ImportFileInspector().inspect(bytes)
        val ready = inspected as ImportFileInspector.Result.Ready
        assertTrue(ready.boxes.isEmpty())
        assertTrue(ready.objects.isEmpty())
        assertEquals(0, ready.recordsRead)
    }

    @Test
    fun build_semicolonInName_quotedAndReimported() {

        val snapshot =
            ContainerViewSnapshotFactory.from(
                boxes = listOf(
                    box(1, "Box; prova", 10, "Garage")
                ),
                categoryNameOf = { "Generico" },
                categoryIconOf = { 0 },
                objects = emptyList()
            )

        val bytes = ViewExportCsvBuilder().build(snapshot)
        val inspected = ImportFileInspector().inspect(bytes)
        val ready = inspected as ImportFileInspector.Result.Ready
        assertEquals("Box; prova", ready.boxes[0].name)
        assertTrue(ready.objects.isEmpty())
    }

    private fun box(
        id: Int,
        name: String,
        categoryId: Int,
        position: String
    ): Box {

        return Box(
            id = id,
            name = name,
            categoryId = categoryId,
            position = position,
            lastModified = 0L
        )
    }
}
