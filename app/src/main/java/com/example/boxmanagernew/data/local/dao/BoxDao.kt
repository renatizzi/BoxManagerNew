package com.example.boxmanagernew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.boxmanagernew.data.local.entity.BoxEntity

@Dao
interface BoxDao {

    @Insert
    suspend fun insert(
        box: BoxEntity
    ): Long

    @Update
    suspend fun update(
        box: BoxEntity
    )

    @Query(
        """
        SELECT * FROM box
        WHERE deletedAt IS NULL
        ORDER BY lastModified DESC
        """
    )
    fun getAllLive():
            LiveData<List<BoxEntity>>

    @Query(
        """
        SELECT * FROM box
        WHERE deletedAt IS NULL
        ORDER BY lastModified DESC
        """
    )
    suspend fun getAllSync():
            List<BoxEntity>

    @Query(
        """
        SELECT * FROM box
        ORDER BY lastModified DESC
        """
    )
    suspend fun getAllSyncIncludingTrash():
            List<BoxEntity>

    @Query(
        """
        SELECT * FROM box
        WHERE id = :id AND deletedAt IS NULL
        """
    )
    suspend fun getById(
        id: Int
    ): BoxEntity?

    @Query(
        """
        SELECT * FROM box
        WHERE id = :id
        """
    )
    suspend fun getByIdAny(
        id: Int
    ): BoxEntity?

    @Query(
        """
        SELECT * FROM box
        WHERE permanentId = :permanentId
        AND deletedAt IS NULL
        LIMIT 1
        """
    )
    suspend fun getByPermanentId(
        permanentId: String
    ): BoxEntity?

    @Query(
        """
        SELECT * FROM box
        WHERE deletedAt IS NOT NULL
        ORDER BY deletedAt DESC
        """
    )
    suspend fun getAllInTrash():
            List<BoxEntity>

    @Query(
        """
        UPDATE box
        SET deletedAt = :deletedAt,
            lastModified = :deletedAt
        WHERE id = :id
        """
    )
    suspend fun markDeleted(
        id: Int,
        deletedAt: Long
    )

    @Query(
        """
        UPDATE box
        SET deletedAt = NULL,
            lastModified = :restoredAt
        WHERE id = :id
        """
    )
    suspend fun restoreFromTrash(
        id: Int,
        restoredAt: Long
    )

    @Query(
        """
        SELECT id FROM box
        WHERE deletedAt IS NOT NULL
        AND deletedAt < :cutoff
        """
    )
    suspend fun getTrashExpiredIds(
        cutoff: Long
    ): List<Int>

    @Query(
        "DELETE FROM box WHERE id = :id"
    )
    suspend fun deleteById(
        id: Int
    )

    @Query("DELETE FROM box")
    suspend fun deleteAll()

    @Query(
        """
        SELECT COUNT(*)
        FROM box
        WHERE categoryId = :categoryId
        AND deletedAt IS NULL
        """
    )
    suspend fun countBoxesByCategory(
        categoryId: Int
    ): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM box
        WHERE LOWER(position)=LOWER(:position)
        AND deletedAt IS NULL
        """
    )
    suspend fun countBoxesByPosition(
        position: String
    ): Int

    @Query(
        """
        UPDATE box
        SET position = :newPosition,
            lastModified = :timestamp
        WHERE id IN (:ids)
        """
    )
    suspend fun moveBoxes(
        ids: List<Int>,
        newPosition: String,
        timestamp: Long
    )

    @Query(
        """
        SELECT b.id
        FROM box b
        LEFT JOIN objects o
            ON o.boxId = b.id AND o.deletedAt IS NULL
        WHERE b.deletedAt IS NULL
        GROUP BY b.id
        HAVING COUNT(o.id)=0
        """
    )
    suspend fun getEmptyBoxIds():
            List<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM (
            SELECT b.id
            FROM box b
            LEFT JOIN objects o
                ON o.boxId = b.id AND o.deletedAt IS NULL
            WHERE b.deletedAt IS NULL
            GROUP BY b.id
            HAVING COUNT(o.id)=0
        )
        """
    )
    fun getEmptyBoxesCount():
            LiveData<Int>

    @Query(
        """
        SELECT COUNT(DISTINCT categoryId)
        FROM box
        WHERE deletedAt IS NULL
        """
    )
    fun getUsedCategoriesCount():
            LiveData<Int>
}
