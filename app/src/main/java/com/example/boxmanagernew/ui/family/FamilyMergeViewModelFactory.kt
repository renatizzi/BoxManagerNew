package com.example.boxmanagernew.ui.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl

class FamilyMergeViewModelFactory(
    private val database: AppDatabase,
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamilyMergeViewModel::class.java)) {
            return FamilyMergeViewModel(
                database,
                categoryRepository,
                locationRepository,
                boxRepository,
                objectRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
