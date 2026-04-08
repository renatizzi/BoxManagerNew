package com.example.boxmanagernew.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepositoryImpl
) : ViewModel() {

    val categories: LiveData<List<CategoryEntity>> =
        repository.getAllCategories()

    fun insert(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insert(category)
        }
    }

    fun update(category: CategoryEntity) {
        viewModelScope.launch {
            repository.update(category)
        }
    }

    fun delete(category: CategoryEntity) {
        viewModelScope.launch {
            repository.delete(category)
        }
    }
}