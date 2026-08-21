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
import com.example.boxmanagernew.domain.search.ObjectSearchMatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.util.CanonicalNormalizer

class ObjectRepositoryImpl(
    private val dao: ObjectDao,
    private val typeDao: ObjectTypeDao
) : ObjectRepository {

    private val ignoredWords =
        setOf(
            "a","ad","da","di","del","della",
            "dello","dei","degli","con",
            "per","in","su","al","alla"
        )

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
                        it.quantity
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
                            it.quantity
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

        val tokens =
            CanonicalNormalizer.normalize(query)
                .split(" ")
                .filter {
                    it.isNotBlank() &&
                            it !in ignoredWords
                }

        if (tokens.isEmpty()) {
            return emptyList()
        }

        val results =
            dao.searchObjects()
                .filter { row ->

                    val searchable =
                        CanonicalNormalizer.canonical(
                            buildString {
                                append(row.objectName)
                                append(" ")
                                append(row.description ?: "")
                            }
                        )

                    tokens.all { token ->

                        val singular =
                            CanonicalNormalizer
                                .singularPluralVariant(token)

                        val irregular =
                            CanonicalNormalizer
                                .irregularVariant(token)

                        val variants =
                            setOf(
                                CanonicalNormalizer.canonical(token),
                                CanonicalNormalizer.canonical(singular),
                                CanonicalNormalizer.canonical(irregular)
                            )

                        variants.any {
                            searchable.contains(it)
                        }
                    }
                }

        Log.d(
            "BOX_M8",
            "[M8] TOKENS=${tokens.size} MATCHES=${results.size}"
        )

        return results
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
                it.quantity
            )
        }
    }

    suspend fun insertDynamic(
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

        dao.insert(
            ObjectEntity(
                0,
                type?.id ?: return,
                boxId,
                description,
                quantity
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

        dao.update(
            ObjectEntity(
                id,
                type?.id ?: return,
                boxId,
                description,
                quantity
            )
        )
    }

    override suspend fun insert(obj:Object){}
    override suspend fun update(obj:Object){}
    override suspend fun delete(obj:Object){}

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