package com.example.boxmanagernew.ui.boxdetail

import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.data.trash.TrashStore
import com.example.boxmanagernew.data.photo.ObjectPhotoStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ObjectViewModelFactory(
    private val repository: ObjectRepositoryImpl,
    private val trashStore: TrashStore,
    private val photoStore: ObjectPhotoStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ObjectViewModel(repository, trashStore, photoStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
