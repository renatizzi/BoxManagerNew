package com.example.boxmanagernew.family

import com.example.boxmanagernew.data.local.dao.FamilyDeletionTombstoneDao
import com.example.boxmanagernew.data.local.entity.FamilyDeletionTombstoneEntity
import com.example.boxmanagernew.family.deletion.FamilyDeletionRecorder
import com.example.boxmanagernew.family.deletion.FamilyPropagatingDelete
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.dao.ObjectDao
import com.example.boxmanagernew.data.local.dao.ObjectTypeDao
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Verifica tombstone box + oggetti figli su delete contenitore (archivio unico B5).
 */
class FamilyPropagatingDeleteTest {

    private class FakeTombstoneDao : FamilyDeletionTombstoneDao {
        val items = mutableListOf<FamilyDeletionTombstoneEntity>()

        override suspend fun upsert(entity: FamilyDeletionTombstoneEntity) {
            items.removeAll {
                it.entityType == entity.entityType &&
                    it.permanentId == entity.permanentId
            }
            items.add(entity)
        }

        override suspend fun upsertAll(entities: List<FamilyDeletionTombstoneEntity>) {
            entities.forEach { upsert(it) }
        }

        override suspend fun getAllSync(): List<FamilyDeletionTombstoneEntity> =
            items.toList()

        override suspend fun get(
            entityType: String,
            permanentId: String
        ): FamilyDeletionTombstoneEntity? =
            items.firstOrNull {
                it.entityType == entityType && it.permanentId == permanentId
            }

        override suspend fun delete(entityType: String, permanentId: String) {
            items.removeAll {
                it.entityType == entityType && it.permanentId == permanentId
            }
        }

        override suspend fun deleteAll() {
            items.clear()
        }
    }

    private class FakeBoxDao : BoxDao {
        private val boxes = mutableMapOf(
            1 to BoxEntity(
                id = 1,
                name = "Scatola",
                categoryId = 1,
                position = "Garage",
                lastModified = 1000L,
                permanentId = "box-1",
                createdBy = "Marco"
            )
        )

        override suspend fun insert(box: BoxEntity) = error("unused")
        override suspend fun update(box: BoxEntity) = error("unused")
        override fun getAllLive() = error("unused")
        override suspend fun getAllSync() = boxes.values.toList()
        override suspend fun getById(id: Int) = boxes[id]
        override suspend fun getByPermanentId(permanentId: String) =
            boxes.values.firstOrNull { it.permanentId == permanentId }
        override suspend fun deleteById(id: Int) { boxes.remove(id) }
        override suspend fun deleteAll() = boxes.clear()
        override suspend fun countBoxesByCategory(categoryId: Int) = 0
        override suspend fun countBoxesByPosition(position: String) = 0
        override suspend fun moveBoxes(
            ids: List<Int>,
            newPosition: String,
            timestamp: Long
        ) = error("unused")
        override suspend fun getEmptyBoxIds() = emptyList<Int>()
        override fun getEmptyBoxesCount() = error("unused")
        override fun getUsedCategoriesCount() = error("unused")
    }

    private class FakeObjectDao : ObjectDao {
        private val objects = mutableMapOf(
            10 to ObjectEntity(
                id = 10,
                typeObjectId = 1,
                boxId = 1,
                description = "Rosso",
                quantity = 1,
                objectPermanentId = "obj-1",
                lastModified = 2000L,
                createdBy = "Anna"
            )
        )

        override suspend fun insert(obj: ObjectEntity) = error("unused")
        override suspend fun update(obj: ObjectEntity) = error("unused")
        override suspend fun delete(obj: ObjectEntity) = error("unused")
        override fun getObjectsWithTypeByBox(boxId: Int) = error("unused")
        override suspend fun searchObjects() = error("unused")
        override suspend fun getAllSync() = objects.values.toList()
        override suspend fun getObjectsByBoxSync(boxId: Int) =
            objects.values.filter { it.boxId == boxId }
        override suspend fun getById(id: Int) = objects[id]
        override suspend fun moveObjects(ids: List<Int>, targetBoxId: Int) =
            error("unused")
        override suspend fun countObjectsByBox(boxId: Int) =
            objects.values.count { it.boxId == boxId }
        override suspend fun deleteById(id: Int) { objects.remove(id) }
        override suspend fun deleteByIds(ids: List<Int>) {
            ids.forEach { objects.remove(it) }
        }
        override suspend fun getByPermanentId(permanentId: String) =
            objects.values.firstOrNull { it.objectPermanentId == permanentId }
        override suspend fun deleteAll() = objects.clear()
    }

    private class FakeObjectTypeDao : ObjectTypeDao {
        override suspend fun insert(type: ObjectTypeEntity) = error("unused")
        override fun getAllTypes() = error("unused")
        override suspend fun getAllTypesSync() =
            listOf(ObjectTypeEntity(id = 1, name = "Tipo"))
        override suspend fun getByName(name: String) =
            ObjectTypeEntity(id = 1, name = "Tipo")
        override suspend fun deleteAll() = Unit
    }

    @Test
    fun deleteBox_recordsTombstonesForBoxAndChildObjects_thenRemovesRows() =
        runBlocking {
            val tombstones = FakeTombstoneDao()
            val boxDao = FakeBoxDao()
            val objectDao = FakeObjectDao()
            val deleter = FamilyPropagatingDelete(
                boxRepository = BoxRepositoryImpl(boxDao),
                objectRepository = ObjectRepositoryImpl(objectDao, FakeObjectTypeDao()),
                recorder = FamilyDeletionRecorder(tombstones)
            )

            deleter.deleteBox(1, "Marco")

            assertEquals(2, tombstones.items.size)
            assertEquals(
                FamilyDeletionTombstoneEntity.TYPE_OBJECT,
                tombstones.items[0].entityType
            )
            assertEquals("obj-1", tombstones.items[0].permanentId)
            assertEquals(
                FamilyDeletionTombstoneEntity.TYPE_BOX,
                tombstones.items[1].entityType
            )
            assertEquals("box-1", tombstones.items[1].permanentId)
            assertNull(boxDao.getById(1))
            assertNull(objectDao.getById(10))
        }
}
