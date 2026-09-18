package com.example.boxmanagernew.importdata.export

/**
 * Filtro selezione Esporta dati (B–C R2): contenitori scelti + relativi oggetti.
 */
object ExportSelectionFilter {

    fun <B> filterBoxes(
        boxes: List<B>,
        boxId: (B) -> Int,
        selectedBoxIds: Set<Int>?
    ): List<B> {
        if (selectedBoxIds == null) return boxes
        return boxes.filter { boxId(it) in selectedBoxIds }
    }

    fun <O> filterObjects(
        objects: List<O>,
        objectBoxId: (O) -> Int,
        selectedBoxIds: Set<Int>?
    ): List<O> {
        if (selectedBoxIds == null) return objects
        return objects.filter { objectBoxId(it) in selectedBoxIds }
    }
}
