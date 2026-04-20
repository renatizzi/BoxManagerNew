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

    private val _isAscending = MutableLiveData(true)
    val isAscending: LiveData<Boolean> = _isAscending

    private var currentSource: LiveData<List<ObjectWithType>>? = null
    private var lastSource: List<ObjectWithType> = emptyList()

    private var currentQuery: String = ""

    fun load(boxId: Int) {

        currentSource?.let {
            _objects.removeSource(it)
        }

        val source = repository.getObjectsWithType(boxId)
        currentSource = source

        _objects.addSource(source) { list ->

            lastSource = list

            val currentSelected = _selectedItems.value ?: emptySet()

            if (currentSelected.isNotEmpty()) {
                val validIds = list.map { it.obj.id }.toSet()
                val updatedSelection = currentSelected.intersect(validIds)

                _selectedItems.value = updatedSelection
                _selectionMode.value = updatedSelection.isNotEmpty()
            }

            applyFilterAndSort()
        }
    }

    fun filter(query: String) {
        currentQuery = query
        applyFilterAndSort()
    }

    fun toggleSort() {
        val current = _isAscending.value ?: true
        _isAscending.value = !current
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        var result = lastSource

        if (currentQuery.isNotBlank()) {
            result = result.filter {
                it.typeName.contains(currentQuery, ignoreCase = true) ||
                        (it.obj.description?.contains(currentQuery, ignoreCase = true) == true)
            }
        }

        val asc = _isAscending.value ?: true

        result = if (asc) {
            result.sortedBy { it.typeName }
        } else {
            result.sortedByDescending { it.typeName }
        }

        _objects.value = result
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

    // 🔥 NUOVO METODO
    fun updateObjectWithName(
        id: Int,
        name: String,
        boxId: Int,
        description: String?,
        quantity: Int?
    ) {
        viewModelScope.launch {
            repository.updateWithName(
                id = id,
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