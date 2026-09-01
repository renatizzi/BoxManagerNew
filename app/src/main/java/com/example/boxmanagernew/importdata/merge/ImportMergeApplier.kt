package com.example.boxmanagernew.importdata.merge

import androidx.room.withTransaction
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import com.example.boxmanagernew.domain.model.BoxPermanentId
import com.example.boxmanagernew.domain.model.ObjectPermanentId
import java.util.Locale

class ImportMergeApplier(
    private val database: AppDatabase
) {

    suspend fun apply(
        plan: ImportMergePlanner.Plan
    ) {

        if (!plan.canApply) {
            return
        }

        database.withTransaction {
            val boxIds = mutableMapOf<String, Int>()

            for (box in database.boxDao().getAllSync()) {
                val key = key(box.name)
                if (!boxIds.contains(key)) {
                    boxIds[key] = box.id
                }
            }

            val now = System.currentTimeMillis()

            for (box in plan.boxesToInsert) {
                val category = database.categoryDao().getCategoryByName(box.category)
                    ?: error("category")
                val location = database.locationDao().getByName(box.position)
                    ?: error("location")

                val id = database.boxDao().insert(
                    BoxEntity(
                        id = 0,
                        name = box.name,
                        categoryId = category.id,
                        position = location.name,
                        lastModified = now,
                        permanentId = BoxPermanentId.fromStored(null)
                    )
                ).toInt()

                boxIds[key(box.name)] = id
            }

            for (obj in plan.objectsToInsert) {
                val boxId = boxIds[key(obj.box)] ?: error("box")
                val typeId = typeId(obj.name)

                database.objectDao().insert(
                    ObjectEntity(
                        id = 0,
                        typeObjectId = typeId,
                        boxId = boxId,
                        description = obj.description,
                        quantity = obj.quantity,
                        objectPermanentId = ObjectPermanentId.generate(),
                        lastModified = now
                    )
                )
            }
        }
    }

    private suspend fun typeId(name: String): Int {
        val existing = database.objectTypeDao().getAllTypesSync().firstOrNull { type ->
            type.name.equals(name, ignoreCase = true)
        }
        if (existing != null) {
            return existing.id
        }

        database.objectTypeDao().insert(
            ObjectTypeEntity(name = name)
        )

        return database.objectTypeDao().getByName(name)?.id
            ?: error("type")
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }
}
