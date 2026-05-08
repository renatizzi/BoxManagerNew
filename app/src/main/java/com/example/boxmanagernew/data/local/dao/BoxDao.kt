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
    suspend fun insert(box: BoxEntity)

    @Update
    suspend fun update(box: BoxEntity)

    @Query("SELECT * FROM box ORDER BY lastModified DESC")
    fun getAllLive(): LiveData<List<BoxEntity>>

    @Query("DELETE FROM box WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM box WHERE categoryId = :categoryId")
    suspend fun countBoxesByCategory(categoryId: Int): Int

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
}