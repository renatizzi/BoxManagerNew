package com.example.boxmanagernew.family

import com.example.boxmanagernew.family.catalog.FamilyCatalogReader
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.merge.FamilyMergeMerger
import com.example.boxmanagernew.family.merge.FamilyMergeReader
import com.example.boxmanagernew.family.merge.FamilyMergeWriter
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyMergeRoundTripTest {

    @Test
    fun writer_reader_roundTrip_preservesUnifiedSnapshot() {
        val snapshot = FamilyMergeSnapshot(
            catalog = FamilyCatalogSnapshot(
                categories = listOf(
                    FamilyCatalogCategory("Attrezzi", "outline_browse_24")
                ),
                locations = listOf(
                    FamilyCatalogLocation("Garage")
                )
            ),
            inventory = FamilyInventorySnapshot(
                boxes = listOf(
                    FamilyInventoryBox(
                        permanentId = "box-1",
                        name = "Scatola A",
                        category = "Attrezzi",
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
        )

        val csv = FamilyMergeWriter.toCsvLines(snapshot).joinToString("\n")
        val parsed = FamilyMergeReader().parse(csv)

        assertTrue(parsed is FamilyMergeReader.Result.Ok)
        val ok = parsed as FamilyMergeReader.Result.Ok
        assertEquals(1, ok.snapshot.catalog.categories.size)
        assertEquals("Attrezzi", ok.snapshot.catalog.categories[0].name)
        assertEquals(1, ok.snapshot.catalog.locations.size)
        assertEquals(1, ok.snapshot.inventory.boxes.size)
        assertEquals("Scatola A", ok.snapshot.inventory.boxes[0].name)
        assertEquals(1, ok.snapshot.inventory.objects.size)
        assertTrue(csv.contains(FamilyMergeConfiguration.FORMAT_NAME))
    }

    @Test
    fun reader_acceptsLegacyCatalogFile() {
        val legacy = buildString {
            append("formato;")
            append(FamilyCatalogConfiguration.FORMAT_NAME)
            append(";1\n")
            append("sezione;CATEGORIE\n")
            append("nome;icona\n")
            append("Hobby;outline_browse_24\n")
            append("sezione;POSIZIONI\n")
            append("nome\n")
            append("Soffitta\n")
        }

        val parsed = FamilyMergeReader().parse(legacy)
        assertTrue(parsed is FamilyMergeReader.Result.Ok)
        val ok = parsed as FamilyMergeReader.Result.Ok
        assertEquals(1, ok.snapshot.catalog.categories.size)
        assertTrue(ok.snapshot.inventory.boxes.isEmpty())
    }

    @Test
    fun reader_acceptsLegacyInventoryFile() {
        val legacy = buildString {
            append("formato;")
            append(FamilyInventoryConfiguration.FORMAT_NAME)
            append(";1\n")
            append("sezione;CONTENITORI\n")
            append("permanentId;nome;categoria;posizione;lastModified\n")
            append("box-1;Scatola;Hobby;Garage;1000\n")
            append("sezione;OGGETTI\n")
            append("objectPermanentId;boxPermanentId;tipo;descrizione;quantita;lastModified\n")
        }

        val parsed = FamilyMergeReader().parse(legacy)
        assertTrue(parsed is FamilyMergeReader.Result.Ok)
        val ok = parsed as FamilyMergeReader.Result.Ok
        assertTrue(ok.snapshot.catalog.categories.isEmpty())
        assertEquals(1, ok.snapshot.inventory.boxes.size)
    }

    @Test
    fun reader_bytesRoundTrip_withLocationContainingInventoryMarker() {
        val snapshot = FamilyMergeSnapshot(
            catalog = FamilyCatalogSnapshot(
                categories = listOf(
                    FamilyCatalogCategory("Hobby", "outline_browse_24")
                ),
                locations = listOf(
                    FamilyCatalogLocation("Garage"),
                    FamilyCatalogLocation("sezione;CONTENITORI")
                )
            ),
            inventory = FamilyInventorySnapshot(
                boxes = listOf(
                    FamilyInventoryBox(
                        permanentId = "box-1",
                        name = "Scatola",
                        category = "Hobby",
                        position = "Garage",
                        lastModified = 1000L
                    )
                ),
                objects = listOf(
                    FamilyInventoryObject(
                        objectPermanentId = "obj-1",
                        boxPermanentId = "box-1",
                        typeName = "",
                        description = "Rosso",
                        quantity = null,
                        lastModified = 2000L
                    )
                )
            )
        )

        val bytes = FamilyMergeWriter.toCsvBytes(snapshot)
        val text = String(bytes, Charsets.UTF_8)
        val parsed = FamilyMergeReader().parse(text)

        if (parsed is FamilyMergeReader.Result.Error) {
            org.junit.Assert.fail(parsed.message)
        }
        assertTrue(parsed is FamilyMergeReader.Result.Ok)
        val ok = parsed as FamilyMergeReader.Result.Ok
        assertEquals(2, ok.snapshot.catalog.locations.size)
        assertEquals("Garage", ok.snapshot.catalog.locations[0].name)
        assertEquals("sezione;CONTENITORI", ok.snapshot.catalog.locations[1].name)
        assertEquals(1, ok.snapshot.inventory.boxes.size)
        assertEquals(1, ok.snapshot.inventory.objects.size)
        assertEquals("Oggetto", ok.snapshot.inventory.objects[0].typeName)
    }

    @Test
    fun merger_healsMissingStructureFromBoxes() {
        val incoming = FamilyMergeSnapshot(
            catalog = FamilyCatalogSnapshot(
                categories = emptyList(),
                locations = emptyList()
            ),
            inventory = FamilyInventorySnapshot(
                boxes = listOf(
                    FamilyInventoryBox(
                        permanentId = "box-1",
                        name = "Scatola",
                        category = "Hobby cancellata",
                        position = "Soffitta cancellata",
                        lastModified = 1000L
                    )
                ),
                objects = emptyList()
            )
        )

        val plan = FamilyMergeMerger().plan(
            incoming = incoming,
            localBoxes = emptyList(),
            localObjects = emptyList(),
            existingCategoryNames = emptyList(),
            existingLocationNames = emptyList(),
            objectTypeNames = emptyMap()
        )

        assertEquals(1, plan.healedCategories.size)
        assertEquals("Hobby cancellata", plan.healedCategories[0].name)
        assertEquals(1, plan.healedLocations.size)
        assertEquals("Soffitta cancellata", plan.healedLocations[0].name)
        assertTrue(plan.inventoryPlan.blockingErrors.isEmpty())
        assertEquals(1, plan.inventoryPlan.boxesToInsert.size)
    }
}
