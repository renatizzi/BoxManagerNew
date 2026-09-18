package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.export.ExportSelectionFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class ExportSelectionFilterTest {

    data class Box(val id: Int, val name: String)
    data class Obj(val boxId: Int, val name: String)

    @Test
    fun nullSelection_keepsAll() {
        val boxes = listOf(Box(1, "A"), Box(2, "B"))
        val objects = listOf(Obj(1, "x"), Obj(2, "y"))
        assertEquals(boxes, ExportSelectionFilter.filterBoxes(boxes, Box::id, null))
        assertEquals(
            objects,
            ExportSelectionFilter.filterObjects(objects, Obj::boxId, null)
        )
    }

    @Test
    fun selection_filtersBoxesAndObjects() {
        val boxes = listOf(Box(1, "A"), Box(2, "B"), Box(3, "C"))
        val objects = listOf(Obj(1, "x"), Obj(2, "y"), Obj(1, "z"), Obj(3, "w"))
        val selected = setOf(1, 3)
        assertEquals(
            listOf(Box(1, "A"), Box(3, "C")),
            ExportSelectionFilter.filterBoxes(boxes, Box::id, selected)
        )
        assertEquals(
            listOf(Obj(1, "x"), Obj(1, "z"), Obj(3, "w")),
            ExportSelectionFilter.filterObjects(objects, Obj::boxId, selected)
        )
    }

    @Test
    fun emptySelection_returnsEmpty() {
        val boxes = listOf(Box(1, "A"))
        assertEquals(
            emptyList<Box>(),
            ExportSelectionFilter.filterBoxes(boxes, Box::id, emptySet())
        )
    }
}
