package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.BoxPermanentId
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

                    toDomain(entity)
                }
            }
    }

    private fun toDomain(
        entity: BoxEntity
    ): Box {

        return Box(
            id = entity.id,
            name = entity.name,
            description = null,
            categoryId = entity.categoryId,
            position = entity.position,
            lastModified = entity.lastModified,
            permanentId = entity.permanentId,
            createdBy = entity.createdBy
        )
    }

    suspend fun getAllBoxEntitiesSync():
            List<BoxEntity> {

        return boxDao.getAllSync()
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
                lastModified = box.lastModified,
                permanentId = BoxPermanentId.fromStored(
                    box.permanentId
                ),
                createdBy = box.createdBy.trim()
            )
        )
    }

    override suspend fun updateBox(
        box: Box
    ) {

        val existing =
            boxDao.getById(box.id)

        boxDao.update(
            BoxEntity(
                id = box.id,
                name = box.name,
                categoryId = box.categoryId,
                position = box.position,
                lastModified = box.lastModified,
                permanentId = BoxPermanentId.fromStored(
                    existing?.permanentId
                        ?: box.permanentId
                ),
                createdBy = existing?.createdBy
                    ?: box.createdBy.trim()
            )
        )
    }

    override suspend fun deleteBox(
        id: Int
    ) {

        boxDao.deleteById(id)
    }

    override suspend fun getBoxByPermanentId(
        permanentId: String
    ): Box? {

        val id = permanentId.trim()
        if (id.isEmpty()) {
            return null
        }

        return boxDao.getByPermanentId(id)?.let { entity ->
            toDomain(entity)
        }
    }

    override suspend fun getBoxById(
        id: Int
    ): Box? {

        return boxDao.getById(id)?.let { entity ->
            toDomain(entity)
        }
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