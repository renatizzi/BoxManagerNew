package com.example.boxmanagernew.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.boxmanagernew.data.local.entity.FamilyDeletionTombstoneEntity

@Dao
interface FamilyDeletionTombstoneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FamilyDeletionTombstoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<FamilyDeletionTombstoneEntity>)

    @Query("SELECT * FROM family_deletion_tombstone")
    suspend fun getAllSync(): List<FamilyDeletionTombstoneEntity>

    @Query(
        """
        SELECT * FROM family_deletion_tombstone
        WHERE entityType = :entityType AND permanentId = :permanentId
        LIMIT 1
        """
    )
    suspend fun get(
        entityType: String,
        permanentId: String
    ): FamilyDeletionTombstoneEntity?

    @Query(
        """
        DELETE FROM family_deletion_tombstone
        WHERE entityType = :entityType AND permanentId = :permanentId
        """
    )
    suspend fun delete(
        entityType: String,
        permanentId: String
    )

    @Query("DELETE FROM family_deletion_tombstone")
    suspend fun deleteAll()
}
