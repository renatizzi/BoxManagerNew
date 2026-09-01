package com.example.boxmanagernew.family.merge

import androidx.room.withTransaction
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.inventory.FamilyInventoryApplier

/**
 * Applica un piano di condivisione archivio: categorie/posizioni guarite
 * dai contenitori in arrivo, poi inventario.
 */
class FamilyMergeApplier(
    private val database: AppDatabase,
    private val inventoryApplier: FamilyInventoryApplier = FamilyInventoryApplier(database)
) {

    suspend fun apply(plan: FamilyMergeMerger.Plan) {
        if (!plan.canApply) {
            return
        }

        database.withTransaction {
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
            for (location in plan.locationsToInsert) {
                database.locationDao().insert(
                    LocationEntity(
                        id = 0,
                        name = location.name
                    )
                )
            }
            inventoryApplier.apply(plan.inventoryPlan)
        }
    }
}
