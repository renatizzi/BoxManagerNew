package com.example.boxmanagernew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.boxmanagernew.data.local.entity.ObjectEntity

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

    @Query("""
        SELECT 
            o.id,
            o.typeObjectId,
            o.boxId,
            o.description,
            o.quantity,
            t.name AS typeName
        FROM objects o
        INNER JOIN object_types t ON o.typeObjectId = t.id
        WHERE o.boxId = :boxId
    """)
    fun getObjectsWithTypeByBox(boxId: Int): LiveData<List<ObjectWithTypeName>>

    @Query("""
        UPDATE objects
        SET boxId = :targetBoxId
        WHERE id IN (:ids)
    """)
    suspend fun moveObjects(
        ids: List<Int>,
        targetBoxId: Int
    )

    @Query("""
        SELECT COUNT(*)
        FROM objects
        WHERE boxId = :boxId
    """)
    suspend fun countObjectsByBox(boxId: Int): Int
}