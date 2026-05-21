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

    override fun getAllBoxesLive():
            LiveData<List<Box>> {

        return boxDao
            .getAllLive()
            .map { list ->

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

    suspend fun getEmptyBoxIds():
            List<Int> {

        return boxDao.getEmptyBoxIds()
    }

    override suspend fun insertBox(
        box: Box
    ): Long {

        return boxDao.insert(
            BoxEntity(
                id = 0,
                name = box.name,
                categoryId = box.categoryId,
                position = box.position,
                lastModified = box.lastModified
            )
        )
    }

    override suspend fun updateBox(
        box: Box
    ) {

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

    override suspend fun deleteBox(
        id: Int
    ) {

        boxDao.deleteById(id)
    }

    suspend fun moveBoxes(
        ids: List<Int>,
        newPosition: String
    ) {

        boxDao.moveBoxes(
            ids,
            newPosition,
            System.currentTimeMillis()
        )
    }
}