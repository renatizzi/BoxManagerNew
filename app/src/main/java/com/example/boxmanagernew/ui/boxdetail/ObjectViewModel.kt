package com.example.boxmanagernew.ui.boxdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import kotlinx.coroutines.launch

class ObjectViewModel(
    private val repository: ObjectRepositoryImpl
) : ViewModel() {

    private val _selectedItems = MutableLiveData<Set<Int>>(emptySet())
    val selectedItems: LiveData<Set<Int>> = _selectedItems

    private val _selectionMode = MutableLiveData(false)
    val selectionMode: LiveData<Boolean> = _selectionMode

    private val _objects = MediatorLiveData<List<ObjectWithType>>()
    val objects: LiveData<List<ObjectWithType>> = _objects

    private var lastSource: List<ObjectWithType> = emptyList()

    fun load(boxId: Int) {
        val source = repository.getObjectsWithType(boxId)

        _objects.addSource(source) { list ->

            lastSource = list

            val currentSelected = _selectedItems.value ?: emptySet()

            if (currentSelected.isNotEmpty()) {
                val validIds = list.map { it.obj.id }.toSet()
                val updatedSelection = currentSelected.intersect(validIds)

                _selectedItems.value = updatedSelection
                _selectionMode.value = updatedSelection.isNotEmpty()
            }

            _objects.value = list
        }
    }

    fun toggleSelection(item: ObjectWithType) {
        val current = _selectedItems.value ?: emptySet()
        val updated = current.toMutableSet()

        if (updated.contains(item.obj.id)) {
            updated.remove(item.obj.id)
        } else {
            updated.add(item.obj.id)
        }

        _selectedItems.value = updated
        _selectionMode.value = updated.isNotEmpty()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
        _selectionMode.value = false
    }

    fun deleteObjects(ids: List<Int>) {
        viewModelScope.launch {
            lastSource.forEach {
                if (ids.contains(it.obj.id)) {
                    repository.delete(it.obj)
                }
            }
            clearSelection()
        }
    }

    fun addObject(
        name: String,
        boxId: Int,
        description: String?,
        quantity: Int?
    ) {
        if (name.isBlank()) return

        viewModelScope.launch {
            repository.insertDynamic(
                name = name,
                boxId = boxId,
                description = description,
                quantity = quantity
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
            clearSelection()
        }
    }
}