package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.boxmanagernew.data.local.dao.ObjectDao
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.repository.ObjectRepository

class ObjectRepositoryImpl(
    private val dao: ObjectDao
) : ObjectRepository {

    override fun getObjectsByBox(boxId: Int): LiveData<List<Object>> {
        return dao.getObjectsByBox(boxId).map { list ->
            list.map {
                Object(
                    id = it.id,
                    typeObjectId = it.typeObjectId,
                    boxId = it.boxId,
                    description = it.description,
                    quantity = it.quantity
                )
            }
        }
    }

    override suspend fun insert(obj: Object) {
        dao.insert(
            ObjectEntity(
                id = 0,
                typeObjectId = obj.typeObjectId,
                boxId = obj.boxId,
                description = obj.description,
                quantity = obj.quantity
            )
        )
    }

    override suspend fun update(obj: Object) {
        dao.update(
            ObjectEntity(
                id = obj.id,
                typeObjectId = obj.typeObjectId,
                boxId = obj.boxId,
                description = obj.description,
                quantity = obj.quantity
            )
        )
    }

    override suspend fun delete(obj: Object) {
        dao.delete(
            ObjectEntity(
                id = obj.id,
                typeObjectId = obj.typeObjectId,
                boxId = obj.boxId,
                description = obj.description,
                quantity = obj.quantity
            )
        )
    }
}