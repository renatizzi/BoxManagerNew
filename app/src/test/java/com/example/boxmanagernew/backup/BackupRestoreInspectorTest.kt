package com.example.boxmanagernew.backup

import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.export.BackupExporter
import com.example.boxmanagernew.backup.mapper.BackupMapper
import com.example.boxmanagernew.backup.restore.BackupPackageInspector
import com.example.boxmanagernew.backup.serializer.BackupDeserializer
import com.example.boxmanagernew.backup.zip.BackupZipReader
import com.example.boxmanagernew.backup.zip.BackupZipWriter
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class BackupRestoreInspectorTest {

    private val mapper = BackupMapper()
    private val exporter = BackupExporter()
    private val inspector = BackupPackageInspector()
    private val deserializer = BackupDeserializer()

    @Test
    fun inspect_validZip_returnsReadyWithCounts() {

        val payload = samplePayload()
        val zipBytes = zip(payload)

        val entries = BackupZipReader().read(
            ByteArrayInputStream(zipBytes)
        )

        val result = inspector.inspect(entries)

        assertTrue(result is BackupPackageInspector.Result.Ready)

        val ready = result as BackupPackageInspector.Result.Ready

        assertEquals(1, ready.metadata.boxCount)
        assertEquals(2, ready.metadata.objectCount)
        assertEquals(1, ready.metadata.categoryCount)
        assertEquals(1, ready.metadata.locationCount)
        assertEquals("Box \"A\"", ready.archive.boxes.single().name)
        assertEquals("box-a-permanent", ready.archive.boxes.single().permanentId)
        assertEquals(null, ready.archive.objects.first().description)
        assertEquals("Riga uno\nRiga due", ready.archive.objects.last().description)
    }

    @Test
    fun inspect_badChecksum_returnsInvalid() {

        val payload = samplePayload().toMutableMap()
        val archiveJson = payload.getValue(BackupConfiguration.ARCHIVE_FILE_NAME)
            .toString(StandardCharsets.UTF_8)
            .replace("\"lastModified\":123", "\"lastModified\":124")

        payload[BackupConfiguration.ARCHIVE_FILE_NAME] =
            archiveJson.toByteArray(StandardCharsets.UTF_8)

        val result = inspector.inspect(payload)

        assertEquals(BackupPackageInspector.Result.Invalid, result)
    }

    @Test
    fun inspect_futureFormatVersion_returnsIncompatible() {

        val payload = samplePayload().toMutableMap()
        val metadata = payload.getValue(BackupConfiguration.METADATA_FILE_NAME)
            .toString(StandardCharsets.UTF_8)
            .replace(
                "\"backupFormatVersion\": 1",
                "\"backupFormatVersion\": 99"
            )

        payload[BackupConfiguration.METADATA_FILE_NAME] =
            metadata.toByteArray(StandardCharsets.UTF_8)

        val result = inspector.inspect(payload)

        assertEquals(BackupPackageInspector.Result.Incompatible, result)
    }

    @Test
    fun zipRoundtrip_preservesJsonFiles() {

        val payload = samplePayload()
        val zipBytes = zip(payload)
        val read = BackupZipReader().read(
            ByteArrayInputStream(zipBytes)
        )

        assertEquals(payload.keys.toList(), read.keys.toList())

        payload.forEach { (name, bytes) ->
            assertEquals(
                bytes.toString(StandardCharsets.UTF_8),
                read.getValue(name).toString(StandardCharsets.UTF_8)
            )
        }
    }

    @Test
    fun deserializer_roundtrip_preservesEntities() {

        val archive = sampleArchive()
        val payload = exporter.exportPayload(archive, "1.0")
        val restored = deserializer.deserializeArchive(
            payload.getValue(BackupConfiguration.ARCHIVE_FILE_NAME)
        )

        assertEquals(archive.formatVersion, restored.formatVersion)
        assertEquals(archive.boxes, restored.boxes)
        assertEquals(archive.objects, restored.objects)
        assertEquals(archive.categories, restored.categories)
        assertEquals(archive.locations, restored.locations)
        assertEquals(archive.objectTypes, restored.objectTypes)
    }

    @Test
    fun deserializer_missingPermanentId_assignsTechnicalId() {

        val payload = exporter.exportPayload(sampleArchive(), "1.0")
        val archiveJson =
            payload.getValue(BackupConfiguration.ARCHIVE_FILE_NAME)
                .toString(StandardCharsets.UTF_8)
                .replace(
                    ",\"permanentId\":\"box-a-permanent\"",
                    ""
                )

        val restored =
            deserializer.deserializeArchive(
                archiveJson.toByteArray(StandardCharsets.UTF_8)
            )

        val assigned =
            restored.boxes.single().permanentId

        assertTrue(assigned.isNotBlank())
        assertTrue(assigned != "box-a-permanent")
    }

    private fun sampleArchive() = mapper.buildArchive(
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
                quantity = null
            ),
            ObjectEntity(
                id = 5,
                typeObjectId = 4,
                boxId = 1,
                description = "Riga uno\nRiga due",
                quantity = 0
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

    private fun samplePayload() =
        exporter.exportPayload(sampleArchive(), "1.0")

    private fun zip(entries: Map<String, ByteArray>): ByteArray {
        val output = ByteArrayOutputStream()
        BackupZipWriter().write(output, entries)
        return output.toByteArray()
    }
}
