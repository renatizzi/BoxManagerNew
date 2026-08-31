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
        val blockingErrors: List<String>
    ) {
        val canApply: Boolean
            get() = blockingErrors.isEmpty() &&
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
        val blockingErrors = mutableListOf<String>()

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
                if (count > 0) {
                    blockingErrors +=
                        "Categoria «${local.name}» usata da $count contenitori: " +
                            "non può essere rimossa."
                } else {
                    categoriesToRemove += CategoryRemoval(local, count)
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
                if (count > 0) {
                    blockingErrors +=
                        "Posizione «${local.name}» usata da $count contenitori: " +
                            "non può essere rimossa."
                } else {
                    locationsToRemove += LocationRemoval(local, count)
                }
            }
        }

        return Plan(
            categoriesToInsert = categoriesToInsert,
            categoriesToUpdate = categoriesToUpdate,
            categoriesToRemove = categoriesToRemove,
            locationsToInsert = locationsToInsert,
            locationsToRemove = locationsToRemove,
            blockingErrors = blockingErrors.distinct()
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
