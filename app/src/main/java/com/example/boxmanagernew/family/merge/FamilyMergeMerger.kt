package com.example.boxmanagernew.family.merge

import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.family.catalog.FamilyCatalogMerger
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.inventory.FamilyInventoryMerger
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot
import java.util.Locale

/**
 * Pianifica l'unione famiglia: catalogo additivo, guarigione struttura dai
 * contenitori in arrivo, poi inventario per ID stabili.
 */
class FamilyMergeMerger(
    private val catalogMerger: FamilyCatalogMerger = FamilyCatalogMerger(),
    private val inventoryMerger: FamilyInventoryMerger = FamilyInventoryMerger()
) {

    data class Plan(
        val catalogPlan: FamilyCatalogMerger.Plan,
        val healedCategories: List<FamilyCatalogCategory>,
        val healedLocations: List<FamilyCatalogLocation>,
        val inventoryPlan: FamilyInventoryMerger.Plan
    ) {
        val categoriesToInsert: List<FamilyCatalogCategory>
            get() = catalogPlan.categoriesToInsert + healedCategories

        val locationsToInsert: List<FamilyCatalogLocation>
            get() = catalogPlan.locationsToInsert + healedLocations

        val canApply: Boolean
            get() = inventoryPlan.canApply ||
                categoriesToInsert.isNotEmpty() ||
                locationsToInsert.isNotEmpty()

        val hasConflicts: Boolean
            get() = inventoryPlan.hasConflicts

        val structureInserted: Int
            get() = categoriesToInsert.size + locationsToInsert.size
    }

    fun plan(
        incoming: FamilyMergeSnapshot,
        localBoxes: List<BoxEntity>,
        localObjects: List<ObjectEntity>,
        existingCategoryNames: Collection<String>,
        existingLocationNames: Collection<String>,
        objectTypeNames: Map<Int, String>
    ): Plan {
        val catalogPlan = catalogMerger.plan(
            incoming = incoming.catalog,
            existingCategoryNames = existingCategoryNames,
            existingLocationNames = existingLocationNames
        )

        val categoryKeys =
            existingCategoryNames.map { key(it) }.toMutableSet()
        val locationKeys =
            existingLocationNames.map { key(it) }.toMutableSet()

        for (category in catalogPlan.categoriesToInsert) {
            categoryKeys.add(key(category.name))
        }
        for (location in catalogPlan.locationsToInsert) {
            locationKeys.add(key(location.name))
        }

        val healedCategories = mutableListOf<FamilyCatalogCategory>()
        val healedLocations = mutableListOf<FamilyCatalogLocation>()

        for (box in incoming.inventory.boxes) {
            val categoryKey = key(box.category)
            if (categoryKey.isNotEmpty() && !categoryKeys.contains(categoryKey)) {
                categoryKeys.add(categoryKey)
                healedCategories += FamilyCatalogCategory(
                    name = box.category.trim(),
                    icon = FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
                )
            }
            val locationKey = key(box.position)
            if (locationKey.isNotEmpty() && !locationKeys.contains(locationKey)) {
                locationKeys.add(locationKey)
                healedLocations += FamilyCatalogLocation(
                    name = box.position.trim()
                )
            }
        }

        val projectedCategoryNames = buildMap {
            var syntheticId = 1
            for (name in existingCategoryNames) {
                put(syntheticId++, name)
            }
            for (category in catalogPlan.categoriesToInsert + healedCategories) {
                put(syntheticId++, category.name)
            }
        }
        val projectedLocationNames = buildList {
            addAll(existingLocationNames)
            addAll(catalogPlan.locationsToInsert.map { it.name })
            addAll(healedLocations.map { it.name })
        }

        val inventoryPlan = inventoryMerger.plan(
            incoming = incoming.inventory,
            localBoxes = localBoxes,
            localObjects = localObjects,
            categoryNames = projectedCategoryNames,
            objectTypeNames = objectTypeNames,
            locationNames = projectedLocationNames
        )

        return Plan(
            catalogPlan = catalogPlan,
            healedCategories = healedCategories,
            healedLocations = healedLocations,
            inventoryPlan = inventoryPlan
        )
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }
}
