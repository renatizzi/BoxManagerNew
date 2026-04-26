package com.example.boxmanagernew.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryViewModel(
    private val repository: CategoryRepositoryImpl
) : ViewModel() {

    private val source: LiveData<List<Category>> =
        repository.getAllCategories()

    private val _categories = MediatorLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _isAscending = MutableLiveData(true)
    val isAscending: LiveData<Boolean> = _isAscending

    private val _currentQuery = MutableLiveData("")
    val currentQuery: LiveData<String> = _currentQuery

    private var lastSource: List<Category> = emptyList()

    private val _operationResult = MutableLiveData<String?>()
    val operationResult: LiveData<String?> = _operationResult

    private val _selectedCategory = MutableLiveData<Int?>()
    val selectedCategory: LiveData<Int?> = _selectedCategory

    init {
        _categories.addSource(source) { list ->
            lastSource = list
            applyFilterAndSort()
        }

        _categories.addSource(_currentQuery) {
            applyFilterAndSort()
        }

        _categories.addSource(_isAscending) {
            applyFilterAndSort()
        }
    }

    fun filter(query: String) {
        _currentQuery.value = query
    }

    fun toggleSort() {
        _isAscending.value = !(_isAscending.value ?: true)
    }

    private fun applyFilterAndSort() {
        var result = lastSource

        val query = _currentQuery.value?.trim()?.lowercase() ?: ""

        if (query.isNotBlank()) {
            result = result.filter {
                it.name.lowercase().contains(query)
            }
        }

        val asc = _isAscending.value ?: true

        result = if (asc) {
            result.sortedBy { it.name }
        } else {
            result.sortedByDescending { it.name }
        }

        _categories.value = result
    }

    fun selectCategory(id: Int) {
        _selectedCategory.value = id
    }

    fun clearSelection() {
        _selectedCategory.value = null
    }

    fun clearMessage() {
        _operationResult.value = null
    }

    suspend fun insert(category: Category): Boolean {
        return withContext(Dispatchers.IO) {
            val success = repository.insert(category) ?: true
            if (success == false) {
                _operationResult.postValue("Categoria già esistente")
            }
            success
        }
    }

    suspend fun update(category: Category): Boolean {
        return withContext(Dispatchers.IO) {
            val success = repository.update(category) ?: true
            if (success == false) {
                _operationResult.postValue("Categoria già esistente")
            }
            success
        }
    }

    suspend fun delete(category: Category): Boolean {
        return withContext(Dispatchers.IO) {
            val success = repository.delete(category) ?: true
            if (success == false) {
                _operationResult.postValue("Categoria in uso: eliminazione non consentita")
            }
            success
        }
    }

    suspend fun isCategoryUsed(categoryId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            repository.isCategoryUsed(categoryId)
        }
    }
}