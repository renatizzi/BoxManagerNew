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
        val box = boxRepository.getBoxById(boxId) ?: return
        val objects = objectRepository.getObjectsByBoxSync(boxId)
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
