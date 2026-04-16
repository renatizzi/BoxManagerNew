package com.example.boxmanagernew.ui.boxdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.domain.repository.ObjectRepository
import kotlinx.coroutines.launch

class ObjectViewModel(
    private val repository: ObjectRepository
) : ViewModel() {

    fun getObjects(boxId: Int): LiveData<List<Object>> {
        return repository.getObjectsByBox(boxId)
    }

    fun getObjectsWithType(boxId: Int): LiveData<List<ObjectWithType>> {
        return repository.getObjectsWithType(boxId)
    }

    fun addObject(
        typeObjectId: Int,
        boxId: Int,
        description: String?,
        quantity: Int?
    ) {
        viewModelScope.launch {
            repository.insert(
                Object(
                    id = 0,
                    typeObjectId = typeObjectId,
                    boxId = boxId,
                    description = description,
                    quantity = quantity
                )
            )
        }
    }

    fun updateObject(
        id: Int,
        typeObjectId: Int,
        boxId: Int,
        description: String?,
        quantity: Int?
    ) {
        viewModelScope.launch {
            repository.update(
                Object(
                    id = id,
                    typeObjectId = typeObjectId,
                    boxId = boxId,
                    description = description,
                    quantity = quantity
                )
            )
        }
    }

    fun deleteObject(obj: Object) {
        viewModelScope.launch {
            repository.delete(obj)
        }
    }
}