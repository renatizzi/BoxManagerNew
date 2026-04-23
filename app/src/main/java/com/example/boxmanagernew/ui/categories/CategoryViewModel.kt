package com.example.boxmanagernew.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepositoryImpl
) : ViewModel() {

    val categories: LiveData<List<Category>> =
        repository.getAllCategories()

    private val _operationResult = MutableLiveData<String?>()
    val operationResult: LiveData<String?> = _operationResult

    // 🔴 NUOVO: selezione singola
    private val _selectedCategory = MutableLiveData<Int?>()
    val selectedCategory: LiveData<Int?> = _selectedCategory

    fun selectCategory(id: Int) {
        _selectedCategory.value = id
    }

    fun clearSelection() {
        _selectedCategory.value = null
    }

    fun clearMessage() {
        _operationResult.value = null
    }

    fun insert(category: Category) {
        viewModelScope.launch {
            val success = repository.insert(category) ?: true
            if (success == false) {
                _operationResult.postValue("Categoria già esistente")
            }
        }
    }

    fun update(category: Category) {
        viewModelScope.launch {
            val success = repository.update(category) ?: true
            if (success == false) {
                _operationResult.postValue("Categoria già esistente")
            }
        }
    }

    fun delete(category: Category) {
        viewModelScope.launch {
            val success = repository.delete(category) ?: true
            if (success == false) {
                _operationResult.postValue("Categoria in uso: eliminazione non consentita")
            }
        }
    }
}