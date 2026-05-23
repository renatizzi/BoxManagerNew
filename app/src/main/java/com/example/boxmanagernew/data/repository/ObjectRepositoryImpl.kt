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
import java.text.Normalizer

class ObjectRepositoryImpl(
    private val dao: ObjectDao,
    private val typeDao: ObjectTypeDao
) : ObjectRepository {

    private val excluded =
        setOf(
            "usb","hdmi","wifi","tv","pc","ssd","hdd",
            "bluetooth","ethernet","gps","dvd","ram",
            "cpu","gpu","lcd","oled"
        )

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

    suspend fun searchObjects(
        query: String
    ): List<SearchResult> {

        val tokens =
            normalize(query)
                .split(" ")
                .filter {
                    it.isNotBlank() &&
                            it !in ignoredWords
                }

        if (tokens.isEmpty())
            return emptyList()

        return dao.searchObjects()
            .filter { row ->

                val searchable =
                    normalize(
                        buildString {

                            append(row.objectName)
                            append(" ")
                            append(
                                row.description ?: ""
                            )
                        }
                    )

                tokens.all {

                    val singular =
                        singularPluralVariant(it)

                    val irregular =
                        irregularVariant(it)

                    searchable.contains(it) ||
                            searchable.contains(singular) ||
                            searchable.contains(irregular)
                }
            }
    }

    private fun normalize(
        value: String
    ): String {

        return Normalizer
            .normalize(
                value.lowercase().trim(),
                Normalizer.Form.NFD
            )
            .replace(
                "\\p{InCombiningDiacriticalMarks}+"
                    .toRegex(),
                ""
            )
            .replace(
                "[^a-z0-9 ]"
                    .toRegex(),
                " "
            )
            .replace(
                "\\s+"
                    .toRegex(),
                " "
            )
            .trim()
    }

    private fun singularPluralVariant(
        value: String
    ): String {

        if (
            value.length < 5 ||
            excluded.contains(value)
        ) return value

        return when {

            value.endsWith("a") ->
                value.dropLast(1)+"e"

            value.endsWith("e") ->
                value.dropLast(1)+"i"

            value.endsWith("o") ->
                value.dropLast(1)+"i"

            value.endsWith("i") ->
                value.dropLast(1)+"e"

            else -> value
        }
    }

    private fun irregularVariant(
        value:String
    ):String{

        return when(value){

            "mano" -> "mani"
            "mani" -> "mano"

            "uomo" -> "uomini"
            "uomini" -> "uomo"

            "uovo" -> "uova"
            "uova" -> "uovo"

            else -> value
        }
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

        val normalized =
            normalize(name)

        var type =
            typeDao.getByName(normalized)

        if(type==null){

            typeDao.insert(
                ObjectTypeEntity(
                    name=normalized
                )
            )

            type =
                typeDao.getByName(normalized)
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

        val normalized =
            normalize(name)

        var type =
            typeDao.getByName(normalized)

        if(type==null){

            typeDao.insert(
                ObjectTypeEntity(
                    name=normalized
                )
            )

            type =
                typeDao.getByName(normalized)
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