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
    val typeName: String
)

@Dao
interface ObjectDao {

    @Insert
    suspend fun insert(obj: ObjectEntity)

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
            t.name AS typeName
        FROM objects o
        INNER JOIN object_types t
            ON o.typeObjectId = t.id
        WHERE o.boxId = :boxId
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
        ORDER BY id ASC
        """
    )
    suspend fun getAllSync():
            List<ObjectEntity>

    @Query(
        """
        SELECT *
        FROM objects
        WHERE boxId = :boxId
        """
    )
    suspend fun getObjectsByBoxSync(
        boxId: Int
    ): List<ObjectEntity>

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
        """
    )
    suspend fun countObjectsByBox(
        boxId: Int
    ): Int

    @Query("DELETE FROM objects WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM objects WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("DELETE FROM objects")
    suspend fun deleteAll()
}