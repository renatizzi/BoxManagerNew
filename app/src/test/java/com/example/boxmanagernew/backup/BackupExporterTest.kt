package com.example.boxmanagernew.backup

import com.example.boxmanagernew.backup.checksum.BackupChecksum
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.export.BackupExporter
import com.example.boxmanagernew.backup.mapper.BackupMapper
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class BackupExporterTest {

    @Test
    fun exportPayload_preservesArchiveDataAndProducesConsistentManifest() {

        val archive =
            BackupMapper().buildArchive(
                boxes = listOf(
                    BoxEntity(
                        id = 1,
                        name = "Box \"A\"",
                        categoryId = 2,
                        position = "Garage",
                        lastModified = 123L,
                        permanentId = "box-a-permanent"
                    )
                ),
                objects = listOf(
                    ObjectEntity(
                        id = 3,
                        typeObjectId = 4,
                        boxId = 1,
                        description = null,
                        quantity = null,
                        objectPermanentId = "obj-3",
                        lastModified = 100L
                    ),
                    ObjectEntity(
                        id = 5,
                        typeObjectId = 4,
                        boxId = 1,
                        description = "Riga uno\nRiga due",
                        quantity = 0,
                        objectPermanentId = "obj-5",
                        lastModified = 200L
                    )
                ),
                categories = listOf(
                    CategoryEntity(2, "Categoria", "icon")
                ),
                locations = listOf(
                    LocationEntity(6, "Garage")
                ),
                objectTypes = listOf(
                    ObjectTypeEntity(4, "Attrezzo")
                )
            )

        val payload =
            BackupExporter().exportPayload(
                archive = archive,
                applicationVersion = "1.0"
            )

        assertEquals(
            listOf(
                BackupConfiguration.ARCHIVE_FILE_NAME,
                BackupConfiguration.METADATA_FILE_NAME,
                BackupConfiguration.MANIFEST_FILE_NAME
            ),
            payload.keys.toList()
        )

        val archiveJson =
            payload.getValue(BackupConfiguration.ARCHIVE_FILE_NAME)
                .toString(StandardCharsets.UTF_8)

        assertTrue(archiveJson.contains("\"boxes\": [{\"id\":1"))
        assertTrue(archiveJson.contains("\"permanentId\":\"box-a-permanent\""))
        assertTrue(archiveJson.contains("\"description\":null"))
        assertTrue(archiveJson.contains("\"quantity\":null"))
        assertTrue(archiveJson.contains("\"quantity\":0"))
        assertTrue(archiveJson.contains("Riga uno\\nRiga due"))

        val checksum =
            ByteArrayInputStream(
                payload.getValue(BackupConfiguration.ARCHIVE_FILE_NAME)
            ).use {
                BackupChecksum.calculate(it)
            }

        val manifestJson =
            payload.getValue(BackupConfiguration.MANIFEST_FILE_NAME)
                .toString(StandardCharsets.UTF_8)

        assertTrue(manifestJson.contains("\"checksum\": \"$checksum\""))

        val metadataJson =
            payload.getValue(BackupConfiguration.METADATA_FILE_NAME)
                .toString(StandardCharsets.UTF_8)

        assertTrue(metadataJson.contains("\"applicationVersion\": \"1.0\""))
        assertTrue(metadataJson.contains("\"objectCount\": 2"))
    }
}
