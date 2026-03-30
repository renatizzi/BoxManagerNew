package com.example.boxmanagernew.data.repository

import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.repository.BoxRepository

class BoxRepositoryImpl(
    private val boxDao: BoxDao
) : BoxRepository {

    override suspend fun getAllBoxes(): List<Box> {
        return boxDao.getAll().map { Box(it.id, it.name) }
    }

    override suspend fun getAllBoxesSortedAsc(): List<Box> {
        return boxDao.getAll()
            .sortedBy { it.name.lowercase() }
            .map { Box(it.id, it.name) }
    }

    override suspend fun getAllBoxesSortedDesc(): List<Box> {
        return boxDao.getAll()
            .sortedByDescending { it.name.lowercase() }
            .map { Box(it.id, it.name) }
    }

    override suspend fun insertBox(box: Box) {
        boxDao.insert(BoxEntity(0, box.name))
    }

    override suspend fun updateBox(box: Box) {
        boxDao.update(BoxEntity(box.id, box.name))
    }

    override suspend fun deleteBox(id: Int) {
        boxDao.deleteById(id)
    }
}