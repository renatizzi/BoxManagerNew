package com.example.boxmanagernew.family.shared

import androidx.room.withTransaction
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration

/**
 * Applica l'allineamento alle tabelle condivise (categorie e posizioni).
 */
class SharedTablesApplier(
    private val database: AppDatabase
) {

    suspend fun apply(plan: SharedTablesMerger.Plan) {
        if (!plan.canApply) {
            return
        }

        database.withTransaction {
            for (removal in plan.categoriesToRemove) {
                database.categoryDao().delete(removal.entity)
            }
            for (update in plan.categoriesToUpdate) {
                database.categoryDao().update(
                    CategoryEntity(
                        id = update.entity.id,
                        name = update.entity.name,
                        icon = update.incoming.icon.ifBlank {
                            FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
                        }
                    )
                )
            }
            for (category in plan.categoriesToInsert) {
                database.categoryDao().insert(
                    CategoryEntity(
                        id = 0,
                        name = category.name,
                        icon = category.icon.ifBlank {
                            FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
                        }
                    )
                )
            }

            for (removal in plan.locationsToRemove) {
                database.locationDao().delete(removal.entity)
            }
            for (location in plan.locationsToInsert) {
                database.locationDao().insert(
                    LocationEntity(
                        id = 0,
                        name = location.name
                    )
                )
            }
        }
    }
}
