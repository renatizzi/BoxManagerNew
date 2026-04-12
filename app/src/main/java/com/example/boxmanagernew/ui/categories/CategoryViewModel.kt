package com.example.boxmanagernew.ui.categories

import androidx.lifecycle.LiveData
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

    fun insert(category: Category) {
        viewModelScope.launch {
            repository.insert(category)
        }
    }

    fun update(category: Category) {
        viewModelScope.launch {
            repository.update(category)
        }
    }

    fun delete(category: Category) {
        viewModelScope.launch {
            repository.delete(category)
        }
    }
}