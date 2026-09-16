package com.example.boxmanagernew.family.deletion

import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl

/**
 * Delete locale + tombstone per propagazione famiglia (archivio unico B5).
 * Ogni eliminazione in flavor famiglia registra CANCELLAZIONI per il merge.
 */
class FamilyPropagatingDelete(
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val recorder: FamilyDeletionRecorder
) {

    suspend fun deleteBox(
        boxId: Int,
        deletedBy: String
    ) {
        deleteBoxPermanently(
            boxId,
            deletedBy,
            objectIds = null
        )
    }

    /** Hard delete (anche da Cestino): tombstone + rimozione righe. */
    suspend fun deleteBoxPermanently(
        boxId: Int,
        deletedBy: String,
        objectIds: List<Int>?
    ) {
        val box = boxRepository.getBoxEntityByIdAny(boxId) ?: return
        val objects =
            if (objectIds != null) {
                objectIds.mapNotNull { objectRepository.getObjectEntityByIdAny(it) }
            } else {
                objectRepository.getObjectsByBoxSync(boxId)
                    .mapNotNull { objectRepository.getObjectEntityByIdAny(it.id) }
            }
        val deletedAt = System.currentTimeMillis()

        for (obj in objects) {
            recorder.recordObjectDeletion(
                permanentId = obj.objectPermanentId,
                deletedBy = deletedBy,
                deletedAt = deletedAt
            )
        }
        recorder.recordBoxDeletion(
            permanentId = box.permanentId,
            deletedBy = deletedBy,
            deletedAt = deletedAt
        )

        if (objects.isNotEmpty()) {
            objectRepository.deleteByIds(objects.map { it.id })
        }
        boxRepository.deleteBox(boxId)
    }

    suspend fun deleteBoxes(
        boxIds: List<Int>,
        deletedBy: String
    ) {
        for (boxId in boxIds) {
            deleteBox(boxId, deletedBy)
        }
    }

    suspend fun deleteObjects(
        objectIds: List<Int>,
        deletedBy: String
    ) {
        if (objectIds.isEmpty()) {
            return
        }
        val deletedAt = System.currentTimeMillis()
        for (objectId in objectIds) {
            val obj = objectRepository.getObjectById(objectId) ?: continue
            recorder.recordObjectDeletion(
                permanentId = obj.objectPermanentId,
                deletedBy = deletedBy,
                deletedAt = deletedAt
            )
        }
        objectRepository.deleteByIds(objectIds)
    }
}
