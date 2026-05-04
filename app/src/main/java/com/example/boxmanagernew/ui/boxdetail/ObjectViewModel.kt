package com.example.boxmanagernew.ui.boxdetail

import androidx.lifecycle.*
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

    private val _hasHiddenSelections = MutableLiveData(false)
    val hasHiddenSelections: LiveData<Boolean> = _hasHiddenSelections

    private var currentSource: LiveData<List<ObjectWithType>>? = null
    private var lastSource: List<ObjectWithType> = emptyList()
    private var lastFiltered: List<ObjectWithType> = emptyList()

    private var currentQuery: String = ""

    fun load(boxId: Int) {
        currentSource?.let { _objects.removeSource(it) }

        val source = repository.getObjectsWithType(boxId)
        currentSource = source

        _objects.addSource(source) {
            lastSource = it
            applyFilterAndSort()
        }

        _objects.addSource(_selectedItems) {
            updateHiddenSelectionState()
        }
    }

    fun filter(query: String) {
        currentQuery = query
        applyFilterAndSort()
    }

    fun toggleSort() {
        _isAscending.value = !(_isAscending.value ?: true)
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        var result = lastSource

        if (currentQuery.isNotBlank()) {
            result = result.filter {
                it.typeName.contains(currentQuery, true) ||
                        (it.obj.description?.contains(currentQuery, true) == true)
            }
        }

        val asc = _isAscending.value ?: true

        result = if (asc) result.sortedBy { it.typeName }
        else result.sortedByDescending { it.typeName }

        lastFiltered = result
        _objects.value = result

        updateHiddenSelectionState()
    }

    private fun updateHiddenSelectionState() {
        val selected = _selectedItems.value ?: emptySet()
        if (selected.isEmpty()) {
            _hasHiddenSelections.value = false
            return
        }

        val visibleIds = lastFiltered.map { it.obj.id }.toSet()
        val hidden = selected.any { it !in visibleIds }

        _hasHiddenSelections.value = hidden
    }

    fun toggleSelection(id: Int) {
        val current = _selectedItems.value ?: emptySet()
        val updated = current.toMutableSet()

        if (updated.contains(id)) updated.remove(id)
        else updated.add(id)

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
                if (ids.contains(it.obj.id)) repository.delete(it.obj)
            }
            clearSelection()
        }
    }

    // 🔴 NUOVO: MOVE OBJECTS
    fun moveObjects(targetBoxId: Int) {
        val ids = _selectedItems.value?.toList() ?: return
        if (ids.isEmpty()) return

        viewModelScope.launch {
            repository.moveObjects(ids, targetBoxId)
            clearSelection()
        }
    }

    fun addObject(name: String, boxId: Int, description: String?, quantity: Int?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertDynamic(name, boxId, description, quantity)
        }
    }

    fun updateObjectWithName(id: Int, name: String, boxId: Int, description: String?, quantity: Int?) {
        viewModelScope.launch {
            repository.updateWithName(id, name, boxId, description, quantity)
        }
    }

    fun deleteObject(obj: Object) {
        viewModelScope.launch {
            repository.delete(obj)
            clearSelection()
        }
    }
}