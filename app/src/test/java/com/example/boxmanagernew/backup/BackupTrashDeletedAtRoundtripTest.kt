package com.example.boxmanagernew.backup

import com.example.boxmanagernew.backup.serializer.BackupDeserializer
import com.example.boxmanagernew.backup.serializer.BackupSerializer
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.backup.model.BackupApplicationInfo
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.model.BackupArchiveContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.nio.charset.StandardCharsets

class BackupTrashDeletedAtRoundtripTest {

    @Test
    fun serializeDeserialize_preservesDeletedAt() {
        val boxes =
            listOf(
                BoxEntity(
                    id = 1,
                    name = "Vivo",
                    categoryId = 1,
                    position = "Garage",
                    lastModified = 10L,
                    permanentId = "b1",
                    createdBy = "",
                    deletedAt = null
                ),
                BoxEntity(
                    id = 2,
                    name = "Cestinato",
                    categoryId = 1,
                    position = "Garage",
                    lastModified = 20L,
                    permanentId = "b2",
                    createdBy = "",
                    deletedAt = 99L
                )
            )
        val objects =
            listOf(
                ObjectEntity(
                    id = 10,
                    typeObjectId = 1,
                    boxId = 1,
                    description = null,
                    quantity = 1,
                    objectPermanentId = "o1",
                    lastModified = 11L,
                    createdBy = "",
                    deletedAt = 55L
                )
            )
        val archive =
            BackupArchive(
                formatVersion = 1,
                createdAt = "2026-09-16T12:00:00Z",
                application = BackupApplicationInfo(
                    name = "BoxManager",
                    backupType = "FULL"
                ),
                boxes = boxes,
                objects = objects,
                categories = emptyList(),
                locations = emptyList(),
                objectTypes = emptyList(),
                archive = BackupArchiveContent(
                    boxes = boxes,
                    objects = objects,
                    categories = emptyList(),
                    locations = emptyList(),
                    objectTypes = emptyList()
                )
            )

        val json = BackupSerializer().serialize(archive)
        val restored =
            BackupDeserializer().deserializeArchive(
                json.toByteArray(StandardCharsets.UTF_8)
            )

        assertNull(
            restored.boxes.first { it.id == 1 }.deletedAt
        )
        assertEquals(
            99L,
            restored.boxes.first { it.id == 2 }.deletedAt
        )
        assertEquals(
            55L,
            restored.objects.first().deletedAt
        )
    }
}
