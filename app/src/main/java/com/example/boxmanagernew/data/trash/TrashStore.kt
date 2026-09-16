package com.example.boxmanagernew.data.trash

import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.dao.ObjectDao
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.trash.TrashRetention
import com.example.boxmanagernew.family.deletion.FamilyPropagatingDelete

/**
 * Cestino A2 — soft-delete senza tombstone; hard-delete con tombstone su famiglia.
 */
class TrashStore(
    private val boxDao: BoxDao,
    private val objectDao: ObjectDao,
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val familyHardDelete: FamilyPropagatingDelete?
) {

    suspend fun softDeleteBox(boxId: Int): Long {
        val now = System.currentTimeMillis()
        objectDao.markDeletedByBoxId(boxId, now)
        boxDao.markDeleted(boxId, now)
        return now
    }

    suspend fun softDeleteBoxes(boxIds: List<Int>): Long {
        val now = System.currentTimeMillis()
        for (id in boxIds) {
            objectDao.markDeletedByBoxId(id, now)
            boxDao.markDeleted(id, now)
        }
        return now
    }

    suspend fun softDeleteObjects(objectIds: List<Int>): Long {
        val now = System.currentTimeMillis()
        for (id in objectIds) {
            objectDao.markDeleted(id, now)
        }
        return now
    }

    suspend fun undoSoftDeleteBox(boxId: Int) {
        val now = System.currentTimeMillis()
        boxDao.restoreFromTrash(boxId, now)
        objectDao.restoreByBoxId(boxId, now)
    }

    suspend fun undoSoftDeleteObject(objectId: Int) {
        val now = System.currentTimeMillis()
        objectDao.restoreFromTrash(objectId, now)
    }

    suspend fun undoSoftDeleteBoxes(boxIds: List<Int>) {
        for (id in boxIds) {
            undoSoftDeleteBox(id)
        }
    }

    suspend fun restoreObjectFromTrash(objectId: Int) {
        val obj = objectDao.getByIdAny(objectId) ?: return
        val box = boxDao.getByIdAny(obj.boxId)
        if (box?.deletedAt != null) {
            undoSoftDeleteBox(obj.boxId)
        } else {
            undoSoftDeleteObject(objectId)
        }
    }

    suspend fun emptyTrash(deletedBy: String) {
        val boxIds = boxDao.getAllInTrash().map { it.id }
        for (boxId in boxIds) {
            hardDeleteBox(boxId, deletedBy)
        }
        val orphanObjects =
            objectDao.getAllInTrash().map { it.id }
        hardDeleteObjects(orphanObjects, deletedBy)
    }

    suspend fun purgeExpired(now: Long = System.currentTimeMillis()) {
        val cutoff = now - TrashRetention.RETENTION_MS
        val expiredObjects = objectDao.getTrashExpiredIds(cutoff)
        hardDeleteObjects(expiredObjects, deletedBy = "")
        val expiredBoxes = boxDao.getTrashExpiredIds(cutoff)
        for (boxId in expiredBoxes) {
            hardDeleteBox(boxId, deletedBy = "")
        }
    }

    suspend fun hardDeleteBox(
        boxId: Int,
        deletedBy: String
    ) {
        val objects =
            objectDao.getAllByBoxIdIncludingTrash(boxId)
        if (BuildConfig.FAMILY_BETA && familyHardDelete != null) {
            familyHardDelete.deleteBoxPermanently(
                boxId,
                deletedBy,
                objectIds = objects.map { it.id }
            )
        } else {
            if (objects.isNotEmpty()) {
                objectRepository.deleteByIds(objects.map { it.id })
            }
            boxRepository.deleteBox(boxId)
        }
    }

    suspend fun hardDeleteObjects(
        objectIds: List<Int>,
        deletedBy: String
    ) {
        if (objectIds.isEmpty()) {
            return
        }
        if (BuildConfig.FAMILY_BETA && familyHardDelete != null) {
            familyHardDelete.deleteObjects(objectIds, deletedBy)
        } else {
            objectRepository.deleteByIds(objectIds)
        }
    }

    suspend fun listTrashBoxes(): List<BoxEntity> =
        boxDao.getAllInTrash()

    suspend fun listTrashObjects(): List<ObjectEntity> =
        objectDao.getAllInTrash()
}
