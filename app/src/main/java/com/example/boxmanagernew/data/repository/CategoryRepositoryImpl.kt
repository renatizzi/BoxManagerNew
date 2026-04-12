package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.boxmanagernew.data.local.dao.CategoryDao
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.domain.model.Category

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) {

    fun getAllCategories(): LiveData<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map {
                Category(
                    id = it.id,
                    name = it.name,
                    icon = it.icon
                )
            }
        }
    }

    suspend fun insert(category: Category) {
        categoryDao.insert(
            CategoryEntity(
                id = category.id,
                name = category.name,
                icon = category.icon
            )
        )
    }

    suspend fun update(category: Category) {
        categoryDao.update(
            CategoryEntity(
                id = category.id,
                name = category.name,
                icon = category.icon
            )
        )
    }

    suspend fun delete(category: Category) {
        categoryDao.delete(
            CategoryEntity(
                id = category.id,
                name = category.name,
                icon = category.icon
            )
        )
    }
}