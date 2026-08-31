package com.example.boxmanagernew.family.deletion

import com.example.boxmanagernew.data.local.dao.FamilyDeletionTombstoneDao
import com.example.boxmanagernew.data.local.entity.FamilyDeletionTombstoneEntity
import com.example.boxmanagernew.ui.common.CreatedByResolver

/**
 * Registra tombstone per delete esplicito propagabile (B5).
 * La UI decide quando chiamarlo («Rimuovi anche in famiglia»).
 */
class FamilyDeletionRecorder(
    private val tombstoneDao: FamilyDeletionTombstoneDao
) {

    suspend fun recordBoxDeletion(
        permanentId: String,
        deletedBy: String,
        deletedAt: Long = System.currentTimeMillis()
    ) {
        record(
            entityType = FamilyDeletionTombstoneEntity.TYPE_BOX,
            permanentId = permanentId,
            deletedBy = deletedBy,
            deletedAt = deletedAt
        )
    }

    suspend fun recordObjectDeletion(
        permanentId: String,
        deletedBy: String,
        deletedAt: Long = System.currentTimeMillis()
    ) {
        record(
            entityType = FamilyDeletionTombstoneEntity.TYPE_OBJECT,
            permanentId = permanentId,
            deletedBy = deletedBy,
            deletedAt = deletedAt
        )
    }

    private suspend fun record(
        entityType: String,
        permanentId: String,
        deletedBy: String,
        deletedAt: Long
    ) {
        val id = permanentId.trim()
        if (id.isEmpty()) {
            return
        }
        tombstoneDao.upsert(
            FamilyDeletionTombstoneEntity(
                entityType = entityType,
                permanentId = id,
                deletedAt = deletedAt,
                deletedBy = CreatedByResolver.normalize(deletedBy)
                    .ifEmpty { CreatedByResolver.FALLBACK }
            )
        )
    }
}
