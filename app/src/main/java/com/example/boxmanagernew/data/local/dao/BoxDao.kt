package com.example.boxmanagernew.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.boxmanagernew.data.local.entity.BoxEntity

@Dao
interface BoxDao {

    @Insert
    suspend fun insert(box: BoxEntity)

    @Update
    suspend fun update(box: BoxEntity)

    @Query("SELECT * FROM box")
    suspend fun getAll(): List<BoxEntity>

    @Query("DELETE FROM box WHERE id = :id")
    suspend fun deleteById(id: Int)
}