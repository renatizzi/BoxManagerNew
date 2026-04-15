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

    private var isAscending = true
    private var currentQuery: String = ""

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

            applyFilterAndSort(list)
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
        isAscending = !isAscending
        applyFilterAndSort(lastSource)
    }

    fun filter(query: String) {
        currentQuery = query
        applyFilterAndSort(lastSource)
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

    private fun applyFilterAndSort(list: List<Box>) {
        _boxes.value = applyFilterAndSortInternal(list)
    }

    private fun applyFilterAndSortInternal(list: List<Box>): List<Box> {
        var result = list

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

        return result
    }
}