package com.example.boxmanagernew.ui.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl

class FamilyCatalogViewModelFactory(
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamilyCatalogViewModel::class.java)) {
            return FamilyCatalogViewModel(
                categoryRepository = categoryRepository,
                locationRepository = locationRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
