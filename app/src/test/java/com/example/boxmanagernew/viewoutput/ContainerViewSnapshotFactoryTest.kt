package com.example.boxmanagernew.viewoutput

import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.domain.model.SearchResult
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshotFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContainerViewSnapshotFactoryTest {

    @Test
    fun fromBoxContents_keepsVisibleObjectsOnly() {

        val snapshot =
            ContainerViewSnapshotFactory.fromBoxContents(
                box = box(1, "Box A", 10, "Cantina"),
                categoryName = "Utensili",
                categoryIconRes = 7,
                objects = listOf(
                    objectWithType(1, "Viti", "M6", 20),
                    objectWithType(1, "Dadi", null, null)
                )
            )

        assertEquals(1, snapshot.boxes.size)
        assertEquals("Box A", snapshot.boxes[0].name)
        assertEquals("Utensili", snapshot.boxes[0].category)
        assertEquals("Cantina", snapshot.boxes[0].position)
        assertEquals(7, snapshot.boxes[0].categoryIconRes)
        assertEquals(2, snapshot.objectCount)
        assertEquals("Viti", snapshot.boxes[0].objects[0].name)
        assertEquals("M6", snapshot.boxes[0].objects[0].description)
        assertEquals("20", snapshot.boxes[0].objects[0].quantity)
        assertEquals("Dadi", snapshot.boxes[0].objects[1].name)
        assertEquals("", snapshot.boxes[0].objects[1].description)
        assertEquals("", snapshot.boxes[0].objects[1].quantity)
    }

    @Test
    fun fromSearchResults_groupsMatchingObjectsAndSkipsOtherBoxes() {

        val snapshot =
            ContainerViewSnapshotFactory.fromSearchResults(
                results = listOf(
                    SearchResult(
                        objectName = "Trapano",
                        description = null,
                        quantity = 1,
                        boxId = 2,
                        boxName = "Box B",
                        boxPosition = "Mansarda",
                        categoryName = "Utensili"
                    ),
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
            ) { name ->
                if (name == "Alimenti") 3 else 4
            }

        assertEquals(2, snapshot.boxes.size)
        assertEquals("Box A", snapshot.boxes[0].name)
        assertEquals("Box B", snapshot.boxes[1].name)
        assertEquals(2, snapshot.objectCount)
        assertEquals(1, snapshot.boxes[0].objects.size)
        assertEquals("Viti", snapshot.boxes[0].objects[0].name)
        assertEquals(3, snapshot.boxes[0].categoryIconRes)
        assertEquals("Trapano", snapshot.boxes[1].objects[0].name)
    }

    @Test
    fun fromSearchResults_empty_hasNoBoxes() {

        val snapshot =
            ContainerViewSnapshotFactory.fromSearchResults(
                emptyList()
            ) { 0 }

        assertTrue(snapshot.boxes.isEmpty())
        assertEquals(0, snapshot.objectCount)
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

    private fun objectWithType(
        boxId: Int,
        name: String,
        description: String?,
        quantity: Int?
    ): ObjectWithType {

        return ObjectWithType(
            obj = Object(
                id = 0,
                typeObjectId = 0,
                boxId = boxId,
                description = description,
                quantity = quantity
            ),
            typeName = name
        )
    }
}
