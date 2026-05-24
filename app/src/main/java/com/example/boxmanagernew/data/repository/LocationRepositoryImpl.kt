package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.dao.LocationDao
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.domain.model.Location

class LocationRepositoryImpl(
    private val locationDao: LocationDao,
    private val boxDao: BoxDao
) {

    fun getAllLocations():
            LiveData<List<Location>> {

        return locationDao
            .getAllLocations()
            .map { list ->

                list.map {

                    Location(
                        id = it.id,
                        name = it.name
                    )
                }
            }
    }

    suspend fun insert(
        location: Location
    ): Boolean {

        val existing =
            locationDao.getByName(
                location.name
            )

        return if (
            existing != null
        ) {

            false

        } else {

            locationDao.insert(
                LocationEntity(
                    id = location.id,
                    name = location.name
                )
            )

            true
        }
    }

    suspend fun update(
        location: Location
    ): Boolean {

        val existing =
            locationDao.getByName(
                location.name
            )

        return if (
            existing != null &&
            existing.id != location.id
        ) {

            false

        } else {

            locationDao.update(
                LocationEntity(
                    id = location.id,
                    name = location.name
                )
            )

            true
        }
    }

    suspend fun delete(
        location: Location
    ): Boolean {

        return true
    }
}