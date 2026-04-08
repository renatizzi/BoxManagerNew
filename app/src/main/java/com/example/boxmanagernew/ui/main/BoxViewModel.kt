package com.example.boxmanagernew.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import kotlinx.coroutines.launch

class BoxViewModel(
    private val repository: BoxRepositoryImpl
) : ViewModel() {

    private val _boxes = MutableLiveData<List<Box>>()
    val boxes: LiveData<List<Box>> = _boxes

    private var currentList: List<Box> = emptyList()

    private var isAscending = true
    private var currentQuery: String = ""

    private val _selectedItems = MutableLiveData<Set<Int>>(emptySet())
    val selectedItems: LiveData<Set<Int>> = _selectedItems

    private val _selectionMode = MutableLiveData(false)
    val selectionMode: LiveData<Boolean> = _selectionMode

    fun loadBoxes() {
        viewModelScope.launch {
            val data = repository.getAllBoxes()
            currentList = data

            val currentSelected = _selectedItems.value ?: emptySet()

            if (currentSelected.isNotEmpty()) {
                val validIds = data.map { it.id }.toSet()
                val updatedSelection = currentSelected.intersect(validIds)

                _selectedItems.value = updatedSelection
                _selectionMode.value = updatedSelection.isNotEmpty()
            }

            applyFilterAndSort()
        }
    }

    fun addBox(name: String) {
        viewModelScope.launch {
            val box = Box(id = 0, name = name)
            repository.insertBox(box)
            loadBoxes()
        }
    }

    fun updateBox(id: Int, newName: String) {
        viewModelScope.launch {
            val box = Box(id = id, name = newName)
            repository.updateBox(box)
            loadBoxes()
        }
    }

    fun deleteBox(id: Int) {
        viewModelScope.launch {
            repository.deleteBox(id)
            loadBoxes()
        }
    }

    fun deleteBoxes(ids: List<Int>) {
        viewModelScope.launch {
            ids.forEach { repository.deleteBox(it) }
            clearSelection()
            loadBoxes()
        }
    }

    fun toggleSort() {
        isAscending = !isAscending
        applyFilterAndSort()
    }

    fun filter(query: String) {
        currentQuery = query
        applyFilterAndSort()
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

    private fun applyFilterAndSort() {
        var result = currentList

        if (currentQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(currentQuery, ignoreCase = true)
            }
        }

        result = if (isAscending) {
            result.sortedBy { it.name }
        } else {
            result.sortedByDescending { it.name }
        }

        _boxes.value = result
    }
}