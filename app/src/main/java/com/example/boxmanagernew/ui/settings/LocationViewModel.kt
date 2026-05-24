package com.example.boxmanagernew.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.domain.model.Location

class LocationViewModel(
    private val repository: LocationRepositoryImpl
) : ViewModel() {

    val locations:
            LiveData<List<Location>> =
        repository.getAllLocations()

    suspend fun insert(
        location: Location
    ): Boolean {

        return repository.insert(
            location
        )
    }

    suspend fun update(
        location: Location
    ): Boolean {

        return repository.update(
            location
        )
    }

    suspend fun delete(
        location: Location
    ): Boolean {

        return repository.delete(
            location
        )
    }
}