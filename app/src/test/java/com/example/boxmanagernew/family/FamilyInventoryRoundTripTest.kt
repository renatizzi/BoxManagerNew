package com.example.boxmanagernew.family

import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.inventory.FamilyInventoryMerger
import com.example.boxmanagernew.family.inventory.FamilyInventoryReader
import com.example.boxmanagernew.family.inventory.FamilyInventoryWriter
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyInventoryRoundTripTest {

    @Test
    fun writer_reader_roundTrip_preservesEntries() {
        val snapshot = FamilyInventorySnapshot(
            boxes = listOf(
                FamilyInventoryBox(
                    permanentId = "box-1",
                    name = "Scatola A",
                    category = "Hobby",
                    position = "Garage",
                    lastModified = 1000L
                )
            ),
            objects = listOf(
                FamilyInventoryObject(
                    objectPermanentId = "obj-1",
                    boxPermanentId = "box-1",
                    typeName = "Trapano",
                    description = "Rosso",
                    quantity = 1,
                    lastModified = 2000L
                )
            )
        )

        val csv = FamilyInventoryWriter.toCsvLines(snapshot).joinToString("\n")
        val parsed = FamilyInventoryReader().parse(csv)

        assertTrue(parsed is FamilyInventoryReader.Result.Ok)
        val ok = parsed as FamilyInventoryReader.Result.Ok
        assertEquals(1, ok.snapshot.boxes.size)
        assertEquals("Scatola A", ok.snapshot.boxes[0].name)
        assertEquals(1, ok.snapshot.objects.size)
        assertEquals("Trapano", ok.snapshot.objects[0].typeName)
        assertTrue(csv.contains(FamilyInventoryConfiguration.FORMAT_NAME))
    }

    @Test
    fun merger_insertsNewAndUpdatesWhenRemoteIsNewer() {
        val incoming = FamilyInventorySnapshot(
            boxes = listOf(
                FamilyInventoryBox(
                    permanentId = "box-1",
                    name = "Scatola A",
                    category = "Hobby",
                    position = "Garage",
                    lastModified = 3000L
                ),
                FamilyInventoryBox(
                    permanentId = "box-2",
                    name = "Scatola B",
                    category = "Hobby",
                    position = "Garage",
                    lastModified = 1000L
                )
            ),
            objects = emptyList()
        )

        val plan = FamilyInventoryMerger().plan(
            incoming = incoming,
            localBoxes = listOf(
                BoxEntity(
                    id = 10,
                    name = "Vecchio nome",
                    categoryId = 1,
                    position = "Garage",
                    lastModified = 1000L,
                    permanentId = "box-1"
                )
            ),
            localObjects = emptyList(),
            categoryNames = mapOf(1 to "Hobby"),
            objectTypeNames = emptyMap(),
            locationNames = listOf("Garage")
        )

        assertEquals(1, plan.boxesToInsert.size)
        assertEquals("box-2", plan.boxesToInsert[0].permanentId)
        assertEquals(1, plan.boxesToUpdate.size)
        assertEquals(10, plan.boxesToUpdate[0].localId)
        assertTrue(plan.blockingErrors.isEmpty())
    }

    @Test
    fun merger_flagsConflictWhenRemoteIsOlder() {
        val incoming = FamilyInventorySnapshot(
            boxes = listOf(
                FamilyInventoryBox(
                    permanentId = "box-1",
                    name = "Nome remoto",
                    category = "Hobby",
                    position = "Garage",
                    lastModified = 500L
                )
            ),
            objects = emptyList()
        )

        val plan = FamilyInventoryMerger().plan(
            incoming = incoming,
            localBoxes = listOf(
                BoxEntity(
                    id = 10,
                    name = "Nome locale",
                    categoryId = 1,
                    position = "Garage",
                    lastModified = 1000L,
                    permanentId = "box-1"
                )
            ),
            localObjects = emptyList(),
            categoryNames = mapOf(1 to "Hobby"),
            objectTypeNames = emptyMap(),
            locationNames = listOf("Garage")
        )

        assertEquals(1, plan.boxConflicts.size)
        assertTrue(plan.boxesToUpdate.isEmpty())
    }

    @Test
    fun reader_rejectsWrongFormat() {
        val result = FamilyInventoryReader().parse(
            "formato;BoxManager_FamilyCatalog;1\n"
        )
        assertTrue(result is FamilyInventoryReader.Result.Error)
    }
}
