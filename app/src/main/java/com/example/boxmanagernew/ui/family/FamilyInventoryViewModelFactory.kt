package com.example.boxmanagernew.ui.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl

class FamilyInventoryViewModelFactory(
    private val database: AppDatabase,
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamilyInventoryViewModel::class.java)) {
            return FamilyInventoryViewModel(
                database,
                boxRepository,
                objectRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
