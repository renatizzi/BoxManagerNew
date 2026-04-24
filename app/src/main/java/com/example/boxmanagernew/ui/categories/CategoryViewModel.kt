package com.example.boxmanagernew.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryViewModel(
    private val repository: CategoryRepositoryImpl
) : ViewModel() {

    val categories: LiveData<List<Category>> =
        repository.getAllCategories()

    private val _operationResult = MutableLiveData<String?>()
    val operationResult: LiveData<String?> = _operationResult

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

    // 🔴 NUOVO METODO
    suspend fun isCategoryUsed(categoryId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            repository.isCategoryUsed(categoryId)
        }
    }
}