package com.example.boxmanagernew.family

import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import com.example.boxmanagernew.family.shared.SharedTablesMerger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SharedTablesMergerTest {

    @Test
    fun plan_insertsMissingCategoriesAndLocations() {
        val incoming = FamilyCatalogSnapshot(
            categories = listOf(
                FamilyCatalogCategory("Hobby", "outline_browse_24"),
                FamilyCatalogCategory("Attrezzi", "outline_browse_24")
            ),
            locations = listOf(
                FamilyCatalogLocation("Garage"),
                FamilyCatalogLocation("Soffitta")
            )
        )

        val plan = SharedTablesMerger().plan(
            incoming = incoming,
            localCategories = listOf(
                CategoryEntity(id = 1, name = "Hobby", icon = "outline_browse_24")
            ),
            localLocations = listOf(
                LocationEntity(id = 1, name = "Garage")
            ),
            categoryBoxCounts = emptyMap(),
            locationBoxCounts = emptyMap()
        )

        assertTrue(plan.blockingErrors.isEmpty())
        assertTrue(plan.canApply)
        assertEquals(1, plan.categoriesToInsert.size)
        assertEquals("Attrezzi", plan.categoriesToInsert[0].name)
        assertEquals(1, plan.locationsToInsert.size)
        assertEquals("Soffitta", plan.locationsToInsert[0].name)
    }

    @Test
    fun plan_blocksRemovalWhenCategoryStillUsed() {
        val incoming = FamilyCatalogSnapshot(
            categories = emptyList(),
            locations = emptyList()
        )

        val plan = SharedTablesMerger().plan(
            incoming = incoming,
            localCategories = listOf(
                CategoryEntity(id = 1, name = "Hobby", icon = "outline_browse_24")
            ),
            localLocations = emptyList(),
            categoryBoxCounts = mapOf(1 to 2),
            locationBoxCounts = emptyMap()
        )

        assertFalse(plan.canApply)
        assertEquals(1, plan.blockingErrors.size)
        assertTrue(plan.blockingErrors[0].contains("Hobby"))
    }

    @Test
    fun plan_removesUnusedLocalEntries() {
        val incoming = FamilyCatalogSnapshot(
            categories = listOf(
                FamilyCatalogCategory("Hobby", "outline_browse_24")
            ),
            locations = listOf(
                FamilyCatalogLocation("Garage")
            )
        )

        val obsoleteCategory =
            CategoryEntity(id = 2, name = "Vecchia", icon = "outline_browse_24")
        val obsoleteLocation = LocationEntity(id = 2, name = "Cantina")

        val plan = SharedTablesMerger().plan(
            incoming = incoming,
            localCategories = listOf(
                CategoryEntity(id = 1, name = "Hobby", icon = "outline_browse_24"),
                obsoleteCategory
            ),
            localLocations = listOf(
                LocationEntity(id = 1, name = "Garage"),
                obsoleteLocation
            ),
            categoryBoxCounts = mapOf(1 to 0, 2 to 0),
            locationBoxCounts = mapOf(1 to 0, 2 to 0)
        )

        assertTrue(plan.blockingErrors.isEmpty())
        assertTrue(plan.canApply)
        assertEquals(1, plan.categoriesToRemove.size)
        assertEquals(obsoleteCategory, plan.categoriesToRemove[0].entity)
        assertEquals(1, plan.locationsToRemove.size)
        assertEquals(obsoleteLocation, plan.locationsToRemove[0].entity)
    }

    @Test
    fun plan_updatesCategoryIconWhenDifferent() {
        val incoming = FamilyCatalogSnapshot(
            categories = listOf(
                FamilyCatalogCategory("Hobby", "outline_home_24")
            ),
            locations = emptyList()
        )

        val local = CategoryEntity(id = 1, name = "Hobby", icon = "outline_browse_24")

        val plan = SharedTablesMerger().plan(
            incoming = incoming,
            localCategories = listOf(local),
            localLocations = emptyList(),
            categoryBoxCounts = emptyMap(),
            locationBoxCounts = emptyMap()
        )

        assertTrue(plan.blockingErrors.isEmpty())
        assertTrue(plan.canApply)
        assertEquals(1, plan.categoriesToUpdate.size)
        assertEquals(local, plan.categoriesToUpdate[0].entity)
        assertEquals("outline_home_24", plan.categoriesToUpdate[0].incoming.icon)
    }
}
