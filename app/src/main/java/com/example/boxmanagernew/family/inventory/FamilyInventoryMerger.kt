package com.example.boxmanagernew.family.inventory

import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import java.util.Locale

/**
 * Pianifica l'unione inventario per ID stabili (insert / update / conflitto).
 */
class FamilyInventoryMerger {

    data class BoxUpdate(
        val incoming: FamilyInventoryBox,
        val localId: Int
    )

    data class ObjectUpdate(
        val incoming: FamilyInventoryObject,
        val localId: Int
    )

    data class Plan(
        val boxesToInsert: List<FamilyInventoryBox>,
        val boxesToUpdate: List<BoxUpdate>,
        val boxConflicts: List<FamilyInventoryBox>,
        val boxesIgnored: Int,
        val objectsToInsert: List<FamilyInventoryObject>,
        val objectsToUpdate: List<ObjectUpdate>,
        val objectConflicts: List<FamilyInventoryObject>,
        val objectsIgnored: Int,
        val blockingErrors: List<String>
    ) {
        val canApply: Boolean
            get() = blockingErrors.isEmpty() &&
                (boxesToInsert.isNotEmpty() ||
                    boxesToUpdate.isNotEmpty() ||
                    objectsToInsert.isNotEmpty() ||
                    objectsToUpdate.isNotEmpty())

        val hasConflicts: Boolean
            get() = boxConflicts.isNotEmpty() || objectConflicts.isNotEmpty()
    }

    fun plan(
        incoming: FamilyInventorySnapshot,
        localBoxes: List<BoxEntity>,
        localObjects: List<ObjectEntity>,
        categoryNames: Map<Int, String>,
        objectTypeNames: Map<Int, String>,
        locationNames: Collection<String>
    ): Plan {
        val locationKeys = locationNames.map { key(it) }.toSet()
        val categoryByKey = categoryNames.entries.associate {
            key(it.value) to it.key
        }
        val localBoxByPermanentId =
            localBoxes.associateBy { it.permanentId.trim() }
        val localObjectByPermanentId =
            localObjects.associateBy { it.objectPermanentId.trim() }

        val boxesToInsert = mutableListOf<FamilyInventoryBox>()
        val boxesToUpdate = mutableListOf<BoxUpdate>()
        val boxConflicts = mutableListOf<FamilyInventoryBox>()
        var boxesIgnored = 0
        val blockingErrors = mutableListOf<String>()

        for (box in incoming.boxes) {
            val permanentId = box.permanentId.trim()
            if (permanentId.isEmpty()) {
                continue
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
                boxesToUpdate += BoxUpdate(box, local.id)
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
                objectsToUpdate += ObjectUpdate(obj, local.id)
            } else {
                objectConflicts += obj
            }
        }

        return Plan(
            boxesToInsert = boxesToInsert,
            boxesToUpdate = boxesToUpdate,
            boxConflicts = boxConflicts,
            boxesIgnored = boxesIgnored,
            objectsToInsert = objectsToInsert,
            objectsToUpdate = objectsToUpdate,
            objectConflicts = objectConflicts,
            objectsIgnored = objectsIgnored,
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
}
