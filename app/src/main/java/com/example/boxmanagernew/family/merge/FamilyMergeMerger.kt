package com.example.boxmanagernew.family.merge

import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.inventory.FamilyInventoryMerger
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot
import java.util.Locale

/**
 * Pianifica l'unione archivio: guarigione categorie/posizioni dai contenitori
 * in arrivo, poi inventario per ID stabili. Le tabelle del file non si
 * importano qui (usare Invia/Ricevi tabelle condivise).
 */
class FamilyMergeMerger(
    private val inventoryMerger: FamilyInventoryMerger = FamilyInventoryMerger()
) {

    data class Plan(
        val healedCategories: List<FamilyCatalogCategory>,
        val healedLocations: List<FamilyCatalogLocation>,
        val inventoryPlan: FamilyInventoryMerger.Plan
    ) {
        val categoriesToInsert: List<FamilyCatalogCategory>
            get() = healedCategories

        val locationsToInsert: List<FamilyCatalogLocation>
            get() = healedLocations

        val canApply: Boolean
            get() = inventoryPlan.canApply ||
                categoriesToInsert.isNotEmpty() ||
                locationsToInsert.isNotEmpty()

        val hasConflicts: Boolean
            get() = inventoryPlan.hasConflicts
    }

    fun plan(
        incoming: FamilyMergeSnapshot,
        localBoxes: List<BoxEntity>,
        localObjects: List<ObjectEntity>,
        existingCategoryNames: Collection<String>,
        existingLocationNames: Collection<String>,
        objectTypeNames: Map<Int, String>,
        localTombstones: List<com.example.boxmanagernew.data.local.entity.FamilyDeletionTombstoneEntity> =
            emptyList()
    ): Plan {
        val categoryKeys =
            existingCategoryNames.map { key(it) }.toMutableSet()
        val locationKeys =
            existingLocationNames.map { key(it) }.toMutableSet()

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
            for (category in healedCategories) {
                put(syntheticId++, category.name)
            }
        }
        val projectedLocationNames = buildList {
            addAll(existingLocationNames)
            addAll(healedLocations.map { it.name })
        }

        val inventoryPlan = inventoryMerger.plan(
            incoming = incoming.inventory,
            localBoxes = localBoxes,
            localObjects = localObjects,
            categoryNames = projectedCategoryNames,
            objectTypeNames = objectTypeNames,
            locationNames = projectedLocationNames,
            localTombstones = localTombstones
        )

        return Plan(
            healedCategories = healedCategories,
            healedLocations = healedLocations,
            inventoryPlan = inventoryPlan
        )
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }
}
