package com.example.boxmanagernew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.domain.model.SearchResult

data class ObjectWithTypeName(
    val id: Int,
    val typeObjectId: Int,
    val boxId: Int,
    val description: String?,
    val quantity: Int?,
    val objectPermanentId: String = "",
    val lastModified: Long = 0L,
    val createdBy: String = "",
    val typeName: String
)

@Dao
interface ObjectDao {

    @Insert
    suspend fun insert(obj: ObjectEntity): Long

    @Update
    suspend fun update(obj: ObjectEntity)

    @Delete
    suspend fun delete(obj: ObjectEntity)

    @Query(
        """
        SELECT
            o.id,
            o.typeObjectId,
            o.boxId,
            o.description,
            o.quantity,
            o.objectPermanentId,
            o.lastModified,
            o.createdBy,
            t.name AS typeName
        FROM objects o
        INNER JOIN object_types t
            ON o.typeObjectId = t.id
        WHERE o.boxId = :boxId
        AND o.deletedAt IS NULL
        """
    )
    fun getObjectsWithTypeByBox(
        boxId: Int
    ): LiveData<List<ObjectWithTypeName>>

    @Query(
        """
        SELECT
            t.name AS objectName,
            o.description AS description,
            o.quantity AS quantity,
            b.id AS boxId,
            b.name AS boxName,
            b.position AS boxPosition,
            c.name AS categoryName,
            NULL AS boxDescription

        FROM objects o

        INNER JOIN object_types t
            ON t.id = o.typeObjectId

        INNER JOIN box b
            ON b.id = o.boxId

        LEFT JOIN categories c
            ON c.id = b.categoryId

        WHERE o.deletedAt IS NULL
        AND b.deletedAt IS NULL

        ORDER BY
            b.name ASC,
            t.name ASC
        """
    )
    suspend fun searchObjects(
    ): List<SearchResult>

    @Query(
        """
        SELECT *
        FROM objects
        WHERE deletedAt IS NULL
        ORDER BY id ASC
        """
    )
    suspend fun getAllSync():
            List<ObjectEntity>

    @Query(
        """
        SELECT *
        FROM objects
        ORDER BY id ASC
        """
    )
    suspend fun getAllSyncIncludingTrash():
            List<ObjectEntity>

    @Query(
        """
        SELECT *
        FROM objects
        WHERE boxId = :boxId
        AND deletedAt IS NULL
        """
    )
    suspend fun getObjectsByBoxSync(
        boxId: Int
    ): List<ObjectEntity>

    @Query(
        """
        SELECT *
        FROM objects
        WHERE boxId = :boxId
        """
    )
    suspend fun getAllByBoxIdIncludingTrash(
        boxId: Int
    ): List<ObjectEntity>

    @Query(
        """
        SELECT *
        FROM objects
        WHERE id = :id
        AND deletedAt IS NULL
        LIMIT 1
        """
    )
    suspend fun getById(
        id: Int
    ): ObjectEntity?

    @Query(
        """
        SELECT *
        FROM objects
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getByIdAny(
        id: Int
    ): ObjectEntity?

    @Query(
        """
        SELECT *
        FROM objects
        WHERE deletedAt IS NOT NULL
        ORDER BY deletedAt DESC
        """
    )
    suspend fun getAllInTrash():
            List<ObjectEntity>

    @Query(
        """
        UPDATE objects
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
        UPDATE objects
        SET deletedAt = :deletedAt,
            lastModified = :deletedAt
        WHERE boxId = :boxId
        AND deletedAt IS NULL
        """
    )
    suspend fun markDeletedByBoxId(
        boxId: Int,
        deletedAt: Long
    )

    @Query(
        """
        UPDATE objects
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
        UPDATE objects
        SET deletedAt = NULL,
            lastModified = :restoredAt
        WHERE boxId = :boxId
        AND deletedAt IS NOT NULL
        """
    )
    suspend fun restoreByBoxId(
        boxId: Int,
        restoredAt: Long
    )

    @Query(
        """
        SELECT id FROM objects
        WHERE deletedAt IS NOT NULL
        AND deletedAt < :cutoff
        """
    )
    suspend fun getTrashExpiredIds(
        cutoff: Long
    ): List<Int>

    @Query(
        """
        UPDATE objects
        SET boxId = :targetBoxId
        WHERE id IN (:ids)
        """
    )
    suspend fun moveObjects(
        ids: List<Int>,
        targetBoxId: Int
    )

    @Query(
        """
        SELECT COUNT(*)
        FROM objects
        WHERE boxId = :boxId
        AND deletedAt IS NULL
        """
    )
    suspend fun countObjectsByBox(
        boxId: Int
    ): Int

    @Query("DELETE FROM objects WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM objects WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query(
        """
        SELECT *
        FROM objects
        WHERE objectPermanentId = :permanentId
        AND deletedAt IS NULL
        LIMIT 1
        """
    )
    suspend fun getByPermanentId(
        permanentId: String
    ): ObjectEntity?

    @Query("DELETE FROM objects")
    suspend fun deleteAll()
}
