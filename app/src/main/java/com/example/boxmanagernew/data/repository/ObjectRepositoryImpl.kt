package com.example.boxmanagernew.data.repository

import android.util.Log
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
import com.example.boxmanagernew.domain.model.ObjectPermanentId
import com.example.boxmanagernew.domain.search.ObjectSearchMatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.util.SimpleSearch

class ObjectRepositoryImpl(
    private val dao: ObjectDao,
    private val typeDao: ObjectTypeDao
) : ObjectRepository {

    override fun getObjectsByBox(
        boxId: Int
    ): LiveData<List<Object>> {

        return dao.getObjectsWithTypeByBox(boxId)
            .map { rows ->

                rows.map {

                    Object(
                        it.id,
                        it.typeObjectId,
                        it.boxId,
                        it.description,
                        it.quantity,
                        it.objectPermanentId,
                        it.lastModified,
                        it.createdBy
                    )
                }
            }
    }

    override fun getObjectsWithType(
        boxId: Int
    ): LiveData<List<ObjectWithType>> {

        return dao.getObjectsWithTypeByBox(boxId)
            .map { rows ->

                rows.map {

                    ObjectWithType(
                        Object(
                            it.id,
                            it.typeObjectId,
                            it.boxId,
                            it.description,
                            it.quantity,
                            it.objectPermanentId,
                            it.lastModified,
                            it.createdBy
                        ),
                        it.typeName
                    )
                }
            }
    }

    /**
     * API sincrona dedicata al modulo Backup.
     */
    suspend fun getAllObjectEntitiesSync():
            List<ObjectEntity> {

        return dao.getAllSync()
    }

    suspend fun searchObjects(
        query: String
    ): List<SearchResult> {

        if (
            query.isBlank()
        ) {
            return emptyList()
        }

        val results =
            dao.searchObjects()
                .filter { row ->

                    ObjectSearchMatcher.matches(
                        row.objectName,
                        row.description,
                        query
                    )
                }

        Log.d(
            "BOX_M8",
            "[M8] MATCHES=${results.size}"
        )

        return results
    }

    suspend fun searchObjectsInline(
        query: String
    ): List<SearchResult> {

        if (
            SimpleSearch.needle(query).isEmpty()
        ) {
            return emptyList()
        }

        return dao.searchObjects()
            .filter { row ->

                SimpleSearch.matchesAny(
                    query,
                    row.objectName,
                    row.description
                )
            }
    }

    suspend fun findBoxIdsByObjectTerms(
        packed: String
    ): Set<Int> {

        val terms =
            SearchConfiguration.splitLocationTerms(
                packed
            ).ifEmpty {
                listOf(packed).filter {
                    it.isNotBlank()
                }
            }

        if (terms.isEmpty()) {
            return emptySet()
        }

        val rows =
            dao.searchObjects()

        return terms.flatMap { term ->

            rows.filter { row ->

                ObjectSearchMatcher.matches(
                    row.objectName,
                    row.description,
                    term
                )
            }.map { row ->
                row.boxId
            }
        }.toSet()
    }

    suspend fun getObjectsByBoxSync(
        boxId:Int
    ):List<Object>{

        return dao.getObjectsByBoxSync(
            boxId
        ).map {

            Object(
                it.id,
                it.typeObjectId,
                it.boxId,
                it.description,
                it.quantity,
                it.objectPermanentId,
                it.lastModified,
                it.createdBy
            )
        }
    }

    suspend fun getObjectById(id: Int): Object? {
        return dao.getById(id)?.let { entity ->
            Object(
                entity.id,
                entity.typeObjectId,
                entity.boxId,
                entity.description,
                entity.quantity,
                entity.objectPermanentId,
                entity.lastModified,
                entity.createdBy
            )
        }
    }

    suspend fun insertDynamic(
        name:String,
        boxId:Int,
        description:String?,
        quantity:Int?,
        createdBy: String = ""
    ){

        var type =
            typeDao.getByName(name)

        if(type==null){

            typeDao.insert(
                ObjectTypeEntity(
                    name=name
                )
            )

            type =
                typeDao.getByName(name)
        }

        val now = System.currentTimeMillis()

        dao.insert(
            ObjectEntity(
                0,
                type?.id ?: return,
                boxId,
                description,
                quantity,
                objectPermanentId = ObjectPermanentId.generate(),
                lastModified = now,
                createdBy = createdBy.trim()
            )
        )
    }

    suspend fun updateWithName(
        id:Int,
        name:String,
        boxId:Int,
        description:String?,
        quantity:Int?
    ){

        var type =
            typeDao.getByName(name)

        if(type==null){

            typeDao.insert(
                ObjectTypeEntity(
                    name=name
                )
            )

            type =
                typeDao.getByName(name)
        }

        val existing = dao.getById(id)
        val now = System.currentTimeMillis()

        dao.update(
            ObjectEntity(
                id,
                type?.id ?: return,
                boxId,
                description,
                quantity,
                objectPermanentId = ObjectPermanentId.fromStored(
                    existing?.objectPermanentId
                ),
                lastModified = now,
                createdBy = existing?.createdBy.orEmpty()
            )
        )
    }

    suspend fun objectsInBoxes(
        boxIds: Set<Int>
    ): List<SearchResult> {

        if (boxIds.isEmpty()) {
            return emptyList()
        }

        return dao.searchObjects()
            .filter { row ->
                row.boxId in boxIds
            }
    }

    override suspend fun insert(obj: Object) {}

    override suspend fun update(obj: Object) {}

    override suspend fun delete(obj: Object) {

        dao.deleteById(obj.id)
    }

    suspend fun deleteByIds(ids: List<Int>) {

        if (ids.isEmpty()) {
            return
        }

        dao.deleteByIds(ids)
    }

    suspend fun moveObjects(
        ids: List<Int>,
        targetBoxId: Int
    ){

        dao.moveObjects(
            ids,
            targetBoxId
        )
    }

    suspend fun countObjectsByBox(
        boxId:Int
    ):Int{

        return dao.countObjectsByBox(
            boxId
        )
    }
}