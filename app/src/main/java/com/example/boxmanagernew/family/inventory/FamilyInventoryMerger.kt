package com.example.boxmanagernew.family.inventory

import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.FamilyDeletionTombstoneEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.family.model.FamilyDeletion
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import java.util.Locale

/**
 * Pianifica l'unione inventario per ID stabili (insert / update / conflitto / delete).
 * createdBy è informativo: non entra nel confronto payload; su update resta quello locale.
 */
class FamilyInventoryMerger {

    data class BoxUpdate(
        val incoming: FamilyInventoryBox,
        val localId: Int,
        val preservedCreatedBy: String
    )

    data class ObjectUpdate(
        val incoming: FamilyInventoryObject,
        val localId: Int,
        val preservedCreatedBy: String
    )

    data class Plan(
        val boxesToInsert: List<FamilyInventoryBox>,
        val boxesToUpdate: List<BoxUpdate>,
        val boxConflicts: List<FamilyInventoryBox>,
        val boxesIgnored: Int,
        val boxesToDelete: List<Int>,
        val objectsToInsert: List<FamilyInventoryObject>,
        val objectsToUpdate: List<ObjectUpdate>,
        val objectConflicts: List<FamilyInventoryObject>,
        val objectsIgnored: Int,
        val objectsToDelete: List<Int>,
        val tombstonesToUpsert: List<FamilyDeletion>,
        val tombstonesToClear: List<FamilyDeletion>,
        val deletionConflicts: List<FamilyDeletion>,
        val blockingErrors: List<String>
    ) {
        val canApply: Boolean
            get() = blockingErrors.isEmpty() &&
                (
                    boxesToInsert.isNotEmpty() ||
                        boxesToUpdate.isNotEmpty() ||
                        boxesToDelete.isNotEmpty() ||
                        objectsToInsert.isNotEmpty() ||
                        objectsToUpdate.isNotEmpty() ||
                        objectsToDelete.isNotEmpty() ||
                        tombstonesToUpsert.isNotEmpty() ||
                        tombstonesToClear.isNotEmpty()
                    )

        val hasConflicts: Boolean
            get() = boxConflicts.isNotEmpty() ||
                objectConflicts.isNotEmpty() ||
                deletionConflicts.isNotEmpty()
    }

    fun plan(
        incoming: FamilyInventorySnapshot,
        localBoxes: List<BoxEntity>,
        localObjects: List<ObjectEntity>,
        categoryNames: Map<Int, String>,
        objectTypeNames: Map<Int, String>,
        locationNames: Collection<String>,
        localTombstones: List<FamilyDeletionTombstoneEntity> = emptyList()
    ): Plan {
        val locationKeys = locationNames.map { key(it) }.toSet()
        val categoryByKey = categoryNames.entries.associate {
            key(it.value) to it.key
        }
        val localBoxByPermanentId =
            localBoxes.associateBy { it.permanentId.trim() }
        val localObjectByPermanentId =
            localObjects.associateBy { it.objectPermanentId.trim() }
        val localTombstoneByKey = localTombstones.associateBy {
            tombstoneKey(it.entityType, it.permanentId)
        }

        val boxesToInsert = mutableListOf<FamilyInventoryBox>()
        val boxesToUpdate = mutableListOf<BoxUpdate>()
        val boxConflicts = mutableListOf<FamilyInventoryBox>()
        var boxesIgnored = 0
        val blockingErrors = mutableListOf<String>()
        val tombstonesToClear = mutableListOf<FamilyDeletion>()

        for (box in incoming.boxes) {
            val permanentId = box.permanentId.trim()
            if (permanentId.isEmpty()) {
                continue
            }
            val tombstone = localTombstoneByKey[
                tombstoneKey(FamilyDeletionTombstoneEntity.TYPE_BOX, permanentId)
            ]
            if (tombstone != null && tombstone.deletedAt >= box.lastModified) {
                boxesIgnored++
                continue
            }
            if (tombstone != null && box.lastModified > tombstone.deletedAt) {
                tombstonesToClear += FamilyDeletion(
                    entityType = FamilyDeletionTombstoneEntity.TYPE_BOX,
                    permanentId = permanentId,
                    deletedAt = tombstone.deletedAt,
                    deletedBy = tombstone.deletedBy
                )
            }

            val local = localBoxByPermanentId[permanentId]
            if (local == null) {
                if (categoryByKey[key(box.category)] == null) {
                    blockingErrors +=
                        "Categoria mancante per contenitore «${box.name}»: ${box.category}"
                }
                if (!locationKeys.contains(key(box.position))) {
                    blockingErrors +=
                        "Posizione mancante per contenitore «${box.name}»: ${box.position}"
                }
                boxesToInsert += box
                continue
            }

            if (sameBoxPayload(box, local, categoryNames)) {
                boxesIgnored++
                continue
            }

            if (box.lastModified > local.lastModified) {
                if (categoryByKey[key(box.category)] == null) {
                    blockingErrors +=
                        "Categoria mancante per aggiornamento «${box.name}»: ${box.category}"
                }
                if (!locationKeys.contains(key(box.position))) {
                    blockingErrors +=
                        "Posizione mancante per aggiornamento «${box.name}»: ${box.position}"
                }
                boxesToUpdate += BoxUpdate(
                    incoming = box,
                    localId = local.id,
                    preservedCreatedBy = local.createdBy
                )
            } else {
                boxConflicts += box
            }
        }

        val incomingBoxIds =
            incoming.boxes.map { it.permanentId.trim() }.toSet() +
                localBoxes.map { it.permanentId.trim() }

        val objectsToInsert = mutableListOf<FamilyInventoryObject>()
        val objectsToUpdate = mutableListOf<ObjectUpdate>()
        val objectConflicts = mutableListOf<FamilyInventoryObject>()
        var objectsIgnored = 0

        for (obj in incoming.objects) {
            val objectId = obj.objectPermanentId.trim()
            val boxId = obj.boxPermanentId.trim()
            if (objectId.isEmpty() || boxId.isEmpty()) {
                continue
            }

            val tombstone = localTombstoneByKey[
                tombstoneKey(FamilyDeletionTombstoneEntity.TYPE_OBJECT, objectId)
            ]
            if (tombstone != null && tombstone.deletedAt >= obj.lastModified) {
                objectsIgnored++
                continue
            }
            if (tombstone != null && obj.lastModified > tombstone.deletedAt) {
                tombstonesToClear += FamilyDeletion(
                    entityType = FamilyDeletionTombstoneEntity.TYPE_OBJECT,
                    permanentId = objectId,
                    deletedAt = tombstone.deletedAt,
                    deletedBy = tombstone.deletedBy
                )
            }

            if (!incomingBoxIds.contains(boxId) && localBoxByPermanentId[boxId] == null) {
                blockingErrors +=
                    "Contenitore mancante per oggetto «${obj.typeName}»: $boxId"
                continue
            }

            val local = localObjectByPermanentId[objectId]
            if (local == null) {
                objectsToInsert += obj
                continue
            }

            if (sameObjectPayload(obj, local, localBoxes, objectTypeNames)) {
                objectsIgnored++
                continue
            }

            if (obj.lastModified > local.lastModified) {
                objectsToUpdate += ObjectUpdate(
                    incoming = obj,
                    localId = local.id,
                    preservedCreatedBy = local.createdBy
                )
            } else {
                objectConflicts += obj
            }
        }

        val boxesToDelete = mutableListOf<Int>()
        val objectsToDelete = mutableListOf<Int>()
        val tombstonesToUpsert = mutableListOf<FamilyDeletion>()
        val deletionConflicts = mutableListOf<FamilyDeletion>()
        val seenDeletionKeys = mutableSetOf<String>()

        for (deletion in incoming.deletions) {
            val permanentId = deletion.permanentId.trim()
            val entityType = deletion.entityType.trim().uppercase(Locale.ROOT)
            if (permanentId.isEmpty()) {
                continue
            }
            val dedupeKey = tombstoneKey(entityType, permanentId)
            if (!seenDeletionKeys.add(dedupeKey)) {
                continue
            }

            when (entityType) {
                FamilyDeletionTombstoneEntity.TYPE_BOX -> {
                    val local = localBoxByPermanentId[permanentId]
                    if (local == null) {
                        tombstonesToUpsert += deletion.copy(
                            entityType = entityType,
                            permanentId = permanentId
                        )
                    } else if (deletion.deletedAt >= local.lastModified) {
                        boxesToDelete += local.id
                        tombstonesToUpsert += deletion.copy(
                            entityType = entityType,
                            permanentId = permanentId
                        )
                    } else {
                        deletionConflicts += deletion
                    }
                }
                FamilyDeletionTombstoneEntity.TYPE_OBJECT -> {
                    val local = localObjectByPermanentId[permanentId]
                    if (local == null) {
                        tombstonesToUpsert += deletion.copy(
                            entityType = entityType,
                            permanentId = permanentId
                        )
                    } else if (deletion.deletedAt >= local.lastModified) {
                        objectsToDelete += local.id
                        tombstonesToUpsert += deletion.copy(
                            entityType = entityType,
                            permanentId = permanentId
                        )
                    } else {
                        deletionConflicts += deletion
                    }
                }
            }
        }

        return Plan(
            boxesToInsert = boxesToInsert,
            boxesToUpdate = boxesToUpdate,
            boxConflicts = boxConflicts,
            boxesIgnored = boxesIgnored,
            boxesToDelete = boxesToDelete.distinct(),
            objectsToInsert = objectsToInsert,
            objectsToUpdate = objectsToUpdate,
            objectConflicts = objectConflicts,
            objectsIgnored = objectsIgnored,
            objectsToDelete = objectsToDelete.distinct(),
            tombstonesToUpsert = tombstonesToUpsert,
            tombstonesToClear = tombstonesToClear.distinctBy {
                tombstoneKey(it.entityType, it.permanentId)
            },
            deletionConflicts = deletionConflicts,
            blockingErrors = blockingErrors.distinct()
        )
    }

    private fun sameBoxPayload(
        incoming: FamilyInventoryBox,
        local: BoxEntity,
        categoryNames: Map<Int, String>
    ): Boolean {
        val localCategory = categoryNames[local.categoryId].orEmpty()
        return key(incoming.name) == key(local.name) &&
            key(incoming.category) == key(localCategory) &&
            key(incoming.position) == key(local.position)
    }

    private fun sameObjectPayload(
        incoming: FamilyInventoryObject,
        local: ObjectEntity,
        localBoxes: List<BoxEntity>,
        objectTypeNames: Map<Int, String>
    ): Boolean {
        val localBoxPermanentId =
            localBoxes.firstOrNull { it.id == local.boxId }?.permanentId.orEmpty()
        val localType = objectTypeNames[local.typeObjectId].orEmpty()
        val sameDescription =
            key(incoming.description.orEmpty()) == key(local.description.orEmpty())
        val sameQuantity = incoming.quantity == local.quantity
        return incoming.boxPermanentId.trim() == localBoxPermanentId.trim() &&
            key(incoming.typeName) == key(localType) &&
            sameDescription &&
            sameQuantity
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }

    private fun tombstoneKey(entityType: String, permanentId: String): String {
        return entityType.trim().uppercase(Locale.ROOT) + "|" + permanentId.trim()
    }
}
