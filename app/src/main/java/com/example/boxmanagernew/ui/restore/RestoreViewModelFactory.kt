package com.example.boxmanagernew.ui.restore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.data.local.dao.ObjectTypeDao
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl

class RestoreViewModelFactory(
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val objectTypeDao: ObjectTypeDao,
    private val backupFacade: BackupFacade,
    private val appContext: android.content.Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(RestoreViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return RestoreViewModel(
                boxRepository = boxRepository,
                objectRepository = objectRepository,
                categoryRepository = categoryRepository,
                locationRepository = locationRepository,
                objectTypeDao = objectTypeDao,
                backupFacade = backupFacade,
                appContext = appContext
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
