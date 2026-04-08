package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import com.example.boxmanagernew.data.local.dao.CategoryDao
import com.example.boxmanagernew.data.local.entity.CategoryEntity

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) {

    fun getAllCategories(): LiveData<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun insert(category: CategoryEntity) {
        categoryDao.insert(category)
    }

    suspend fun update(category: CategoryEntity) {
        categoryDao.update(category)
    }

    suspend fun delete(category: CategoryEntity) {
        categoryDao.delete(category)
    }
}