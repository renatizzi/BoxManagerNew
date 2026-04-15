package com.example.boxmanagernew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.boxmanagernew.data.local.entity.ObjectEntity

@Dao
interface ObjectDao {

    @Insert
    suspend fun insert(obj: ObjectEntity)

    @Update
    suspend fun update(obj: ObjectEntity)

    @Delete
    suspend fun delete(obj: ObjectEntity)

    @Query("SELECT * FROM objects WHERE boxId = :boxId")
    fun getObjectsByBox(boxId: Int): LiveData<List<ObjectEntity>>
}