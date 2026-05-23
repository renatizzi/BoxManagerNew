package com.example.boxmanagernew.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.boxmanagernew.data.local.dao.ObjectDao
import com.example.boxmanagernew.data.local.dao.ObjectTypeDao
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.domain.model.SearchResult
import com.example.boxmanagernew.domain.repository.ObjectRepository

class ObjectRepositoryImpl(
    private val dao: ObjectDao,
    private val typeDao: ObjectTypeDao
) : ObjectRepository {

    override fun getObjectsByBox(
        boxId: Int
    ): LiveData<List<Object>> {

        return dao.getObjectsWithTypeByBox(boxId)
            .map { list ->

                list.map { row ->

                    Object(
                        id = row.id,
                        typeObjectId = row.typeObjectId,
                        boxId = row.boxId,
                        description = row.description,
                        quantity = row.quantity
                    )
                }
            }
    }

    override fun getObjectsWithType(
        boxId: Int
    ): LiveData<List<ObjectWithType>> {

        return dao.getObjectsWithTypeByBox(boxId)
            .map { list ->

                list.map { row ->

                    ObjectWithType(
                        obj = Object(
                            id = row.id,
                            typeObjectId = row.typeObjectId,
                            boxId = row.boxId,
                            description = row.description,
                            quantity = row.quantity
                        ),
                        typeName = row.typeName
                    )
                }
            }
    }

    suspend fun searchObjects(
        query: String
    ): List<SearchResult> {

        return dao.searchObjects(query)
    }

    suspend fun getObjectsByBoxSync(
        boxId: Int
    ): List<Object> {

        return dao.getObjectsByBoxSync(boxId)
            .map {

                Object(
                    id = it.id,
                    typeObjectId = it.typeObjectId,
                    boxId = it.boxId,
                    description = it.description,
                    quantity = it.quantity
                )
            }
    }

    suspend fun insertDynamic(
        name: String,
        boxId: Int,
        description: String?,
        quantity: Int?
    ) {

        val normalized =
            normalize(name)

        var type =
            typeDao.getByName(normalized)

        if (type == null) {

            typeDao.insert(
                ObjectTypeEntity(
                    name = normalized
                )
            )

            type =
                typeDao.getByName(normalized)
        }

        val typeId =
            type?.id ?: return

        dao.insert(
            ObjectEntity(
                id = 0,
                typeObjectId = typeId,
                boxId = boxId,
                description = description,
                quantity = quantity
            )
        )
    }

    suspend fun updateWithName(
        id: Int,
        name: String,
        boxId: Int,
        description: String?,
        quantity: Int?
    ) {

        val normalized =
            normalize(name)

        var type =
            typeDao.getByName(normalized)

        if (type == null) {

            typeDao.insert(
                ObjectTypeEntity(
                    name = normalized
                )
            )

            type =
                typeDao.getByName(normalized)
        }

        val typeId =
            type?.id ?: return

        dao.update(
            ObjectEntity(
                id = id,
                typeObjectId = typeId,
                boxId = boxId,
                description = description,
                quantity = quantity
            )
        )
    }

    override suspend fun insert(
        obj: Object
    ) {

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

    override suspend fun update(
        obj: Object
    ) {

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

    override suspend fun delete(
        obj: Object
    ) {

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

    suspend fun moveObjects(
        ids: List<Int>,
        targetBoxId: Int
    ) {

        dao.moveObjects(
            ids,
            targetBoxId
        )
    }

    suspend fun countObjectsByBox(
        boxId: Int
    ): Int {

        return dao.countObjectsByBox(
            boxId
        )
    }

    private fun normalize(
        input: String
    ): String {

        return input
            .trim()
            .lowercase()
    }
}