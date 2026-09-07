package com.example.boxmanagernew.family.shared

import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import java.util.Locale

/**
 * Allinea le tabelle locali alle categorie e posizioni condivise in famiglia.
 */
class SharedTablesMerger {

    data class CategoryRemoval(
        val entity: CategoryEntity,
        val boxCount: Int
    )

    data class LocationRemoval(
        val entity: LocationEntity,
        val boxCount: Int
    )

    data class CategoryUpdate(
        val entity: CategoryEntity,
        val incoming: FamilyCatalogCategory
    )

    data class Plan(
        val categoriesToInsert: List<FamilyCatalogCategory>,
        val categoriesToUpdate: List<CategoryUpdate>,
        val categoriesToRemove: List<CategoryRemoval>,
        val locationsToInsert: List<FamilyCatalogLocation>,
        val locationsToRemove: List<LocationRemoval>,
        val blockedCategoryRemovals: List<CategoryRemoval>,
        val blockedLocationRemovals: List<LocationRemoval>
    ) {
        val blockingErrors: List<String>
            get() = blockedCategoryRemovals.map {
                "category:${it.entity.name}:${it.boxCount}"
            } + blockedLocationRemovals.map {
                "location:${it.entity.name}:${it.boxCount}"
            }

        val hasBlockingErrors: Boolean
            get() = blockedCategoryRemovals.isNotEmpty() ||
                blockedLocationRemovals.isNotEmpty()

        val canApply: Boolean
            get() = !hasBlockingErrors &&
                (
                    categoriesToInsert.isNotEmpty() ||
                        categoriesToUpdate.isNotEmpty() ||
                        categoriesToRemove.isNotEmpty() ||
                        locationsToInsert.isNotEmpty() ||
                        locationsToRemove.isNotEmpty()
                    )
    }

    fun plan(
        incoming: FamilyCatalogSnapshot,
        localCategories: List<CategoryEntity>,
        localLocations: List<LocationEntity>,
        categoryBoxCounts: Map<Int, Int>,
        locationBoxCounts: Map<Int, Int>
    ): Plan {
        val incomingCategoryKeys =
            incoming.categories.associateBy { key(it.name) }
        val incomingLocationKeys =
            incoming.locations.associateBy { key(it.name) }

        val categoriesToInsert = mutableListOf<FamilyCatalogCategory>()
        val categoriesToUpdate = mutableListOf<CategoryUpdate>()
        val categoriesToRemove = mutableListOf<CategoryRemoval>()
        val locationsToInsert = mutableListOf<FamilyCatalogLocation>()
        val locationsToRemove = mutableListOf<LocationRemoval>()
        val blockedCategoryRemovals = mutableListOf<CategoryRemoval>()
        val blockedLocationRemovals = mutableListOf<LocationRemoval>()

        for (category in incoming.categories) {
            val local = localCategories.firstOrNull {
                key(it.name) == key(category.name)
            }
            if (local == null) {
                categoriesToInsert += category
            } else if (!sameIcon(local.icon, category.icon)) {
                categoriesToUpdate += CategoryUpdate(local, category)
            }
        }

        for (local in localCategories) {
            if (!incomingCategoryKeys.containsKey(key(local.name))) {
                val count = categoryBoxCounts[local.id] ?: 0
                val removal = CategoryRemoval(local, count)
                if (count > 0) {
                    blockedCategoryRemovals += removal
                } else {
                    categoriesToRemove += removal
                }
            }
        }

        for (location in incoming.locations) {
            if (
                localLocations.none { key(it.name) == key(location.name) }
            ) {
                locationsToInsert += location
            }
        }

        for (local in localLocations) {
            if (!incomingLocationKeys.containsKey(key(local.name))) {
                val count = locationBoxCounts[local.id] ?: 0
                val removal = LocationRemoval(local, count)
                if (count > 0) {
                    blockedLocationRemovals += removal
                } else {
                    locationsToRemove += removal
                }
            }
        }

        return Plan(
            categoriesToInsert = categoriesToInsert,
            categoriesToUpdate = categoriesToUpdate,
            categoriesToRemove = categoriesToRemove,
            locationsToInsert = locationsToInsert,
            locationsToRemove = locationsToRemove,
            blockedCategoryRemovals = blockedCategoryRemovals,
            blockedLocationRemovals = blockedLocationRemovals
        )
    }

    private fun sameIcon(localIcon: String, incomingIcon: String): Boolean {
        val local = localIcon.ifBlank {
            FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
        }
        val incoming = incomingIcon.ifBlank {
            FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
        }
        return key(local) == key(incoming)
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }
}
