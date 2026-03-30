package com.example.boxmanagernew.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.repository.BoxRepository
import kotlinx.coroutines.launch

class BoxViewModel(
    private val repository: BoxRepository
) : ViewModel() {

    private val _boxes = MutableLiveData<List<Box>>()
    val boxes: LiveData<List<Box>> = _boxes

    private var allBoxes: List<Box> = emptyList()
    private var isAsc = true

    fun loadBoxes() {
        viewModelScope.launch {
            allBoxes = if (isAsc) {
                repository.getAllBoxesSortedAsc()
            } else {
                repository.getAllBoxesSortedDesc()
            }
            _boxes.postValue(allBoxes)
        }
    }

    fun toggleSort() {
        isAsc = !isAsc
        loadBoxes()
    }

    fun filter(query: String) {
        val filtered = if (query.isBlank()) {
            allBoxes
        } else {
            allBoxes.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        _boxes.postValue(filtered)
    }

    fun addBox(name: String) {
        viewModelScope.launch {
            repository.insertBox(Box(0, name))
            loadBoxes()
        }
    }

    fun updateBox(id: Int, name: String) {
        viewModelScope.launch {
            repository.updateBox(Box(id, name))
            loadBoxes()
        }
    }

    fun deleteBox(id: Int) {
        viewModelScope.launch {
            repository.deleteBox(id)
            loadBoxes()
        }
    }
}