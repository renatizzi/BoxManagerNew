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

    // ✅ STATO SELEZIONE
    private val _selectedItems = MutableLiveData<Set<Box>>(emptySet())
    val selectedItems: LiveData<Set<Box>> = _selectedItems

    private val _selectionMode = MutableLiveData(false)
    val selectionMode: LiveData<Boolean> = _selectionMode

    fun loadBoxes() {
        viewModelScope.launch {
            val data = repository.getAllBoxes()
            currentList = data

            // ✅ FIX: riallineamento selezione dopo reload
            val currentSelected = _selectedItems.value ?: emptySet()

            if (currentSelected.isNotEmpty()) {
                val updatedSelection = data.filter { newBox ->
                    currentSelected.any { it.id == newBox.id }
                }.toSet()

                _selectedItems.value = updatedSelection
                _selectionMode.value = updatedSelection.isNotEmpty()
            }

            applyFilterAndSort()
        }
    }

    fun addBox(name: String) {
        viewModelScope.launch {
            val box = Box(
                id = 0,
                name = name
            )
            repository.insertBox(box)
            loadBoxes()
        }
    }

    fun updateBox(id: Int, newName: String) {
        viewModelScope.launch {
            val box = Box(
                id = id,
                name = newName
            )
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

    fun deleteBoxes(boxes: List<Box>) {
        viewModelScope.launch {
            boxes.forEach {
                repository.deleteBox(it.id)
            }
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

        if (updated.contains(box)) {
            updated.remove(box)
        } else {
            updated.add(box)
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