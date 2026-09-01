package com.example.boxmanagernew.family

import com.example.boxmanagernew.family.catalog.FamilyCatalogMerger
import com.example.boxmanagernew.family.catalog.FamilyCatalogReader
import com.example.boxmanagernew.family.catalog.FamilyCatalogWriter
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyCatalogRoundTripTest {

    @Test
    fun writer_reader_roundTrip_preservesEntries() {
        val snapshot = FamilyCatalogSnapshot(
            categories = listOf(
                FamilyCatalogCategory("Attrezzi", "outline_browse_24"),
                FamilyCatalogCategory("Cucina", "outline_browse_24")
            ),
            locations = listOf(
                FamilyCatalogLocation("Garage"),
                FamilyCatalogLocation("Dispensa")
            )
        )

        val csv = FamilyCatalogWriter.toCsvLines(snapshot).joinToString("\n")
        val parsed = FamilyCatalogReader().parse(csv)

        assertTrue(parsed is FamilyCatalogReader.Result.Ok)
        val ok = parsed as FamilyCatalogReader.Result.Ok
        assertEquals(2, ok.snapshot.categories.size)
        assertEquals("Attrezzi", ok.snapshot.categories[0].name)
        assertEquals(2, ok.snapshot.locations.size)
        assertEquals("Dispensa", ok.snapshot.locations[1].name)
        assertTrue(csv.contains(FamilyCatalogConfiguration.FORMAT_NAME))
    }

    @Test
    fun reader_preservesLocationNameWithSemicolon() {
        val csv = buildString {
            append("formato;")
            append(FamilyCatalogConfiguration.FORMAT_NAME)
            append(";1\n")
            append("sezione;POSIZIONI\n")
            append("nome\n")
            append("Garage\n")
            append("sezione;CONTENITORI\n")
        }

        val parsed = FamilyCatalogReader().parse(csv)
        assertTrue(parsed is FamilyCatalogReader.Result.Ok)
        val ok = parsed as FamilyCatalogReader.Result.Ok
        assertEquals(2, ok.snapshot.locations.size)
        assertEquals("sezione;CONTENITORI", ok.snapshot.locations[1].name)
    }

    @Test
    fun merger_addsOnlyMissingNames() {
        val incoming = FamilyCatalogSnapshot(
            categories = listOf(
                FamilyCatalogCategory("Attrezzi", "outline_browse_24"),
                FamilyCatalogCategory("Hobby", "outline_browse_24")
            ),
            locations = listOf(
                FamilyCatalogLocation("Garage"),
                FamilyCatalogLocation("Soffitta")
            )
        )

        val plan = FamilyCatalogMerger().plan(
            incoming = incoming,
            existingCategoryNames = listOf("attrezzi"),
            existingLocationNames = listOf("Garage")
        )

        assertEquals(1, plan.categoriesToInsert.size)
        assertEquals("Hobby", plan.categoriesToInsert[0].name)
        assertEquals(1, plan.locationsToInsert.size)
        assertEquals("Soffitta", plan.locationsToInsert[0].name)
        assertEquals(1, plan.ignoredCategories)
        assertEquals(1, plan.ignoredLocations)
    }

    @Test
    fun reader_rejectsWrongFormat() {
        val result = FamilyCatalogReader().parse(
            "formato;BoxManager_Import;1\n"
        )
        assertTrue(result is FamilyCatalogReader.Result.Error)
    }
}
