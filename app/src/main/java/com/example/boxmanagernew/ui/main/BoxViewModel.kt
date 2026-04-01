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
    private var currentQuery: String = "" // 🔴 STATO FILTRO

    fun loadBoxes() {
        viewModelScope.launch {
            val data = repository.getAllBoxes()
            currentList = data
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

    // 🔴 CORE LOGIC CENTRALIZZATA
    private fun applyFilterAndSort() {
        var result = currentList

        // filtro
        if (currentQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(currentQuery, ignoreCase = true)
            }
        }

        // ordinamento
        result = if (isAscending) {
            result.sortedBy { it.name }
        } else {
            result.sortedByDescending { it.name }
        }

        _boxes.value = result
    }
}