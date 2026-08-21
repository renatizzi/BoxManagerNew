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
        "SELECT * FROM box ORDER BY lastModified DESC"
    )
    fun getAllLive():
            LiveData<List<BoxEntity>>

    @Query(
        "SELECT * FROM box ORDER BY lastModified DESC"
    )
    suspend fun getAllSync():
            List<BoxEntity>

    @Query(
        "SELECT * FROM box WHERE id = :id"
    )
    suspend fun getById(
        id: Int
    ): BoxEntity?

    @Query(
        "SELECT * FROM box WHERE permanentId = :permanentId LIMIT 1"
    )
    suspend fun getByPermanentId(
        permanentId: String
    ): BoxEntity?

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
            ON o.boxId = b.id
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
                ON o.boxId = b.id
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
        """
    )
    fun getUsedCategoriesCount():
            LiveData<Int>
}