package com.example.boxmanagernew.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.boxmanagernew.data.local.entity.ObjectPhotoEntity

@Dao
interface ObjectPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(photo: ObjectPhotoEntity)

    @Query("SELECT * FROM object_photos WHERE objectPermanentId = :permanentId LIMIT 1")
    suspend fun getByPermanentId(permanentId: String): ObjectPhotoEntity?

    @Query("SELECT * FROM object_photos")
    suspend fun getAll(): List<ObjectPhotoEntity>

    @Query("DELETE FROM object_photos WHERE objectPermanentId = :permanentId")
    suspend fun deleteByPermanentId(permanentId: String)

    @Query("DELETE FROM object_photos WHERE objectPermanentId IN (:permanentIds)")
    suspend fun deleteByPermanentIds(permanentIds: List<String>)

    @Query("DELETE FROM object_photos")
    suspend fun deleteAll()
}
