package com.example.boxmanagernew.ui.boxdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.repository.ObjectRepository
import kotlinx.coroutines.launch

class ObjectViewModel(
    private val repository: ObjectRepository
) : ViewModel() {

    fun getObjects(boxId: Int): LiveData<List<Object>> {
        return repository.getObjectsByBox(boxId)
    }

    fun addObject(obj: Object) {
        viewModelScope.launch {
            repository.insert(obj)
        }
    }

    fun updateObject(obj: Object) {
        viewModelScope.launch {
            repository.update(obj)
        }
    }

    fun deleteObject(obj: Object) {
        viewModelScope.launch {
            repository.delete(obj)
        }
    }
}