package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.repository.BoxRepository

class BoxRepositoryImpl(
    private val boxDao: BoxDao
) : BoxRepository {

    override suspend fun getAllBoxes(): List<Box> {
        return boxDao.getAll().map {
            Box(
                id = it.id,
                name = it.name,
                description = null,
                categoryId = it.categoryId,
                position = it.position,
                lastModified = it.lastModified
            )
        }
    }

    override suspend fun getAllBoxesSortedAsc(): List<Box> {
        return boxDao.getAll()
            .sortedBy { it.name.lowercase() }
            .map {
                Box(
                    id = it.id,
                    name = it.name,
                    description = null,
                    categoryId = it.categoryId,
                    position = it.position,
                    lastModified = it.lastModified
                )
            }
    }

    override suspend fun getAllBoxesSortedDesc(): List<Box> {
        return boxDao.getAll()
            .sortedByDescending { it.name.lowercase() }
            .map {
                Box(
                    id = it.id,
                    name = it.name,
                    description = null,
                    categoryId = it.categoryId,
                    position = it.position,
                    lastModified = it.lastModified
                )
            }
    }

    fun getAllBoxesLive(): LiveData<List<Box>> {
        return boxDao.getAllLive().map { list ->
            list.map { entity ->
                Box(
                    id = entity.id,
                    name = entity.name,
                    description = null,
                    categoryId = entity.categoryId,
                    position = entity.position,
                    lastModified = entity.lastModified
                )
            }
        }
    }

    override suspend fun insertBox(box: Box) {
        boxDao.insert(
            BoxEntity(
                id = 0,
                name = box.name,
                categoryId = box.categoryId,
                position = box.position,
                lastModified = box.lastModified
            )
        )
    }

    override suspend fun updateBox(box: Box) {
        boxDao.update(
            BoxEntity(
                id = box.id,
                name = box.name,
                categoryId = box.categoryId,
                position = box.position,
                lastModified = box.lastModified
            )
        )
    }

    override suspend fun deleteBox(id: Int) {
        boxDao.deleteById(id)
    }
}