package com.example.boxmanagernew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.boxmanagernew.data.local.entity.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(
        location: LocationEntity
    )

    @Update
    suspend fun update(
        location: LocationEntity
    )

    @Delete
    suspend fun delete(
        location: LocationEntity
    )

    @Query(
        """
        SELECT *
        FROM locations
        ORDER BY name ASC
        """
    )
    fun getAllLocations():
            LiveData<List<LocationEntity>>

    @Query(
        """
        SELECT *
        FROM locations
        WHERE LOWER(name)=LOWER(:name)
        LIMIT 1
        """
    )
    suspend fun getByName(
        name: String
    ): LocationEntity?
}