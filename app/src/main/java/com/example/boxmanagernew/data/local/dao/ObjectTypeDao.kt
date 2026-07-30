package com.example.boxmanagernew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

@Dao
interface ObjectTypeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(type: ObjectTypeEntity)

    @Query("SELECT * FROM object_types ORDER BY name ASC")
    fun getAllTypes(): LiveData<List<ObjectTypeEntity>>

    @Query("SELECT * FROM object_types ORDER BY name ASC")
    suspend fun getAllTypesSync(): List<ObjectTypeEntity>

    @Query("SELECT * FROM object_types WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): ObjectTypeEntity?
}