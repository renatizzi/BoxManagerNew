package com.example.boxmanagernew.family.catalog

import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import java.util.Locale

/**
 * Unione additiva del catalogo: aggiunge solo voci mancanti.
 */
class FamilyCatalogMerger {

    data class Plan(
        val categoriesToInsert: List<FamilyCatalogCategory>,
        val locationsToInsert: List<FamilyCatalogLocation>,
        val ignoredCategories: Int,
        val ignoredLocations: Int
    ) {
        val inserted: Int
            get() = categoriesToInsert.size + locationsToInsert.size
    }

    fun plan(
        incoming: FamilyCatalogSnapshot,
        existingCategoryNames: Collection<String>,
        existingLocationNames: Collection<String>
    ): Plan {
        val categoryKeys =
            existingCategoryNames.map { key(it) }.toMutableSet()
        val locationKeys =
            existingLocationNames.map { key(it) }.toMutableSet()

        val categoriesToInsert = mutableListOf<FamilyCatalogCategory>()
        var ignoredCategories = 0
        for (category in incoming.categories) {
            val k = key(category.name)
            if (k.isEmpty()) {
                continue
            }
            if (categoryKeys.contains(k)) {
                ignoredCategories++
            } else {
                categoryKeys.add(k)
                categoriesToInsert += category
            }
        }

        val locationsToInsert = mutableListOf<FamilyCatalogLocation>()
        var ignoredLocations = 0
        for (location in incoming.locations) {
            val k = key(location.name)
            if (k.isEmpty()) {
                continue
            }
            if (locationKeys.contains(k)) {
                ignoredLocations++
            } else {
                locationKeys.add(k)
                locationsToInsert += location
            }
        }

        return Plan(
            categoriesToInsert = categoriesToInsert,
            locationsToInsert = locationsToInsert,
            ignoredCategories = ignoredCategories,
            ignoredLocations = ignoredLocations
        )
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }
}
