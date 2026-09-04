package com.example.boxmanagernew.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.domain.model.Location
import kotlinx.coroutines.runBlocking

class LocationViewModel(
    private val repository: LocationRepositoryImpl
) : ViewModel() {

    companion object {

        const val FILTER_USED =
            "__USED_LOCATIONS__"
    }

    private val source =
        repository.getAllLocations()

    private val _filter =
        MutableLiveData("")

    private val _locations =
        MediatorLiveData<List<Location>>()

    val locations:
            LiveData<List<Location>> =
        _locations

    private var lastSource =
        emptyList<Location>()

    init {

        _locations.addSource(source) {

            lastSource = it
            applyFilter()
        }

        _locations.addSource(_filter) {

            applyFilter()
        }
    }

    fun filter(
        query: String
    ) {

        _filter.value =
            query
    }

    private fun applyFilter() {

        var result =
            lastSource

        val query =
            _filter.value
                ?.trim()
                .orEmpty()

        if (
            query ==
            FILTER_USED
        ) {

            result =
                result.filter { location ->

                    runCatching {

                        runBlocking {

                            repository.isLocationUsed(
                                location.name
                            )
                        }

                    }.getOrDefault(false)
                }
        }

        _locations.value =
            result
    }

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
