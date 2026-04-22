package com.example.boxmanagernew.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import kotlinx.coroutines.launch

class BoxViewModel(
    private val repository: BoxRepositoryImpl
) : ViewModel() {

    private val source: LiveData<List<Box>> = repository.getAllBoxesLive()

    private val _boxes = MediatorLiveData<List<Box>>()
    val boxes: LiveData<List<Box>> = _boxes

    private val _isAscending = MutableLiveData(true)
    val isAscending: LiveData<Boolean> = _isAscending

    private val _currentQuery = MutableLiveData("")
    val currentQuery: LiveData<String> = _currentQuery

    private var lastSource: List<Box> = emptyList()

    private val _selectedItems = MutableLiveData<Set<Int>>(emptySet())
    val selectedItems: LiveData<Set<Int>> = _selectedItems

    private val _selectionMode = MutableLiveData(false)
    val selectionMode: LiveData<Boolean> = _selectionMode

    init {
        _boxes.addSource(source) { list ->

            lastSource = list

            val currentSelected = _selectedItems.value ?: emptySet()

            if (currentSelected.isNotEmpty()) {
                val validIds = list.map { it.id }.toSet()
                val updatedSelection = currentSelected.intersect(validIds)

                _selectedItems.value = updatedSelection
                _selectionMode.value = updatedSelection.isNotEmpty()
            }

            applyFilterAndSort()
        }

        _boxes.addSource(_currentQuery) {
            applyFilterAndSort()
        }

        _boxes.addSource(_isAscending) {
            applyFilterAndSort()
        }
    }

    fun addBox(name: String, categoryId: Int, position: String) {
        viewModelScope.launch {
            repository.insertBox(
                Box(
                    id = 0,
                    name = name,
                    description = null,
                    categoryId = categoryId,
                    position = position,
                    lastModified = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateBox(id: Int, newName: String, categoryId: Int, position: String) {
        viewModelScope.launch {
            repository.updateBox(
                Box(
                    id = id,
                    name = newName,
                    description = null,
                    categoryId = categoryId,
                    position = position,
                    lastModified = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteBox(id: Int) {
        viewModelScope.launch {
            repository.deleteBox(id)
            clearSelection()
        }
    }

    fun deleteBoxes(ids: List<Int>) {
        viewModelScope.launch {
            ids.forEach { repository.deleteBox(it) }
            clearSelection()
        }
    }

    fun toggleSort() {
        val current = _isAscending.value ?: true
        _isAscending.value = !current
    }

    fun filter(query: String) {
        _currentQuery.value = query
    }

    fun toggleSelection(box: Box) {
        val current = _selectedItems.value ?: emptySet()
        val updated = current.toMutableSet()

        if (updated.contains(box.id)) {
            updated.remove(box.id)
        } else {
            updated.add(box.id)
        }

        _selectedItems.value = updated
        _selectionMode.value = updated.isNotEmpty()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
        _selectionMode.value = false
    }

    fun hasHiddenSelections(): Boolean {
        val selected = _selectedItems.value ?: emptySet()
        val visible = _boxes.value?.map { it.id }?.toSet() ?: emptySet()

        if (selected.isEmpty()) return false

        return selected.any { it !in visible }
    }

    private fun applyFilterAndSort() {
        var result = lastSource

        val query = _currentQuery.value?.trim()?.lowercase() ?: ""

        if (query.isNotBlank()) {
            result = result.filter { box ->
                box.name.lowercase().contains(query) ||
                        box.position.lowercase().contains(query)
            }
        }

        val asc = _isAscending.value ?: true

        result = if (asc) {
            result.sortedBy { it.name }
        } else {
            result.sortedByDescending { it.name }
        }

        _boxes.value = result
    }
}