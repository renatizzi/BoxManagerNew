package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.dao.CategoryDao
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.domain.model.Category

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val boxDao: BoxDao
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

    /**
     * API sincrona dedicata al modulo Backup.
     */
    suspend fun getAllCategoryEntitiesSync():
            List<CategoryEntity> {

        return categoryDao.getAllSync()
    }

    suspend fun insert(category: Category): Boolean {

        val existing = categoryDao.getCategoryByName(category.name)

        return if (existing != null) {
            false
        } else {
            categoryDao.insert(
                CategoryEntity(
                    id = category.id,
                    name = category.name,
                    icon = category.icon
                )
            )
            true
        }
    }

    suspend fun update(category: Category): Boolean {

        val existing = categoryDao.getCategoryByName(category.name)

        return if (existing != null && existing.id != category.id) {
            false
        } else {
            categoryDao.update(
                CategoryEntity(
                    id = category.id,
                    name = category.name,
                    icon = category.icon
                )
            )
            true
        }
    }

    suspend fun delete(category: Category): Boolean {

        val count = boxDao.countBoxesByCategory(category.id)

        return if (count > 0) {
            false
        } else {
            categoryDao.delete(
                CategoryEntity(
                    id = category.id,
                    name = category.name,
                    icon = category.icon
                )
            )
            true
        }
    }

    suspend fun isCategoryUsed(categoryId: Int): Boolean {
        return boxDao.countBoxesByCategory(categoryId) > 0
    }
}