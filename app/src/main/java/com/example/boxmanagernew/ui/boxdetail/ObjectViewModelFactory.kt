package com.example.boxmanagernew.ui.boxdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.data.trash.TrashStore

class ObjectViewModelFactory(
    private val repository: ObjectRepositoryImpl,
    private val trashStore: TrashStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ObjectViewModel(repository, trashStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
