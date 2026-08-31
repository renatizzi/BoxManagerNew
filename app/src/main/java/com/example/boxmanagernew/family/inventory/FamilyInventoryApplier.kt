package com.example.boxmanagernew.family.inventory

import androidx.room.withTransaction
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.FamilyDeletionTombstoneEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import com.example.boxmanagernew.domain.model.BoxPermanentId
import com.example.boxmanagernew.domain.model.ObjectPermanentId
import com.example.boxmanagernew.ui.common.CreatedByResolver
import java.util.Locale

/**
 * Applica un piano di unione inventario famiglia.
 */
class FamilyInventoryApplier(
    private val database: AppDatabase
) {

    suspend fun apply(plan: FamilyInventoryMerger.Plan) {
        if (!plan.canApply) {
            return
        }

        database.withTransaction {
            val categoryByKey = database.categoryDao().getAllSync()
                .associate { key(it.name) to it }
            val locationByKey = database.locationDao().getAllLocationsSync()
                .associate { key(it.name) to it }

            val boxIdByPermanentId = database.boxDao().getAllSync()
                .associate { it.permanentId.trim() to it.id }
                .toMutableMap()

            for (clear in plan.tombstonesToClear) {
                database.familyDeletionTombstoneDao().delete(
                    clear.entityType,
                    clear.permanentId
                )
            }

            for (box in plan.boxesToInsert) {
                val category = categoryByKey[key(box.category)]
                    ?: error("category")
                val location = locationByKey[key(box.position)]
                    ?: error("location")
                val id = database.boxDao().insert(
                    BoxEntity(
                        id = 0,
                        name = box.name,
                        categoryId = category.id,
                        position = location.name,
                        lastModified = box.lastModified,
                        permanentId = BoxPermanentId.fromStored(box.permanentId),
                        createdBy = CreatedByResolver.normalize(box.createdBy)
                    )
                ).toInt()
                boxIdByPermanentId[box.permanentId.trim()] = id
            }

            for (update in plan.boxesToUpdate) {
                val box = update.incoming
                val category = categoryByKey[key(box.category)]
                    ?: error("category")
                val location = locationByKey[key(box.position)]
                    ?: error("location")
                database.boxDao().update(
                    BoxEntity(
                        id = update.localId,
                        name = box.name,
                        categoryId = category.id,
                        position = location.name,
                        lastModified = box.lastModified,
                        permanentId = BoxPermanentId.fromStored(box.permanentId),
                        createdBy = update.preservedCreatedBy
                    )
                )
                boxIdByPermanentId[box.permanentId.trim()] = update.localId
            }

            for (obj in plan.objectsToInsert) {
                insertObject(obj, boxIdByPermanentId)
            }

            for (update in plan.objectsToUpdate) {
                val obj = update.incoming
                val boxId = resolveBoxId(obj.boxPermanentId, boxIdByPermanentId)
                    ?: continue
                val typeId = resolveTypeId(obj.typeName)
                database.objectDao().update(
                    ObjectEntity(
                        id = update.localId,
                        typeObjectId = typeId,
                        boxId = boxId,
                        description = obj.description,
                        quantity = obj.quantity,
                        objectPermanentId = ObjectPermanentId.fromStored(
                            obj.objectPermanentId
                        ),
                        lastModified = obj.lastModified,
                        createdBy = update.preservedCreatedBy
                    )
                )
            }

            for (objectId in plan.objectsToDelete) {
                database.objectDao().deleteById(objectId)
            }
            for (boxId in plan.boxesToDelete) {
                database.boxDao().deleteById(boxId)
            }

            if (plan.tombstonesToUpsert.isNotEmpty()) {
                database.familyDeletionTombstoneDao().upsertAll(
                    plan.tombstonesToUpsert.map { deletion ->
                        FamilyDeletionTombstoneEntity(
                            entityType = deletion.entityType,
                            permanentId = deletion.permanentId,
                            deletedAt = deletion.deletedAt,
                            deletedBy = CreatedByResolver.normalize(deletion.deletedBy)
                        )
                    }
                )
            }
        }
    }

    private suspend fun insertObject(
        obj: com.example.boxmanagernew.family.model.FamilyInventoryObject,
        boxIdByPermanentId: Map<String, Int>
    ) {
        val boxId = resolveBoxId(obj.boxPermanentId, boxIdByPermanentId) ?: return
        val typeId = resolveTypeId(obj.typeName)
        database.objectDao().insert(
            ObjectEntity(
                id = 0,
                typeObjectId = typeId,
                boxId = boxId,
                description = obj.description,
                quantity = obj.quantity,
                objectPermanentId = ObjectPermanentId.fromStored(
                    obj.objectPermanentId
                ),
                lastModified = obj.lastModified,
                createdBy = CreatedByResolver.normalize(obj.createdBy)
            )
        )
    }

    private suspend fun resolveTypeId(name: String): Int {
        val existing = database.objectTypeDao().getByName(name)
        if (existing != null) {
            return existing.id
        }
        database.objectTypeDao().insert(ObjectTypeEntity(name = name))
        return database.objectTypeDao().getByName(name)?.id
            ?: error("type")
    }

    private fun resolveBoxId(
        permanentId: String,
        boxIdByPermanentId: Map<String, Int>
    ): Int? {
        return boxIdByPermanentId[permanentId.trim()]
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }
}
