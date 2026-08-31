package com.example.boxmanagernew.backup.serializer

import com.example.boxmanagernew.backup.model.BackupApplicationInfo
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.model.BackupArchiveContent
import com.example.boxmanagernew.backup.model.BackupManifest
import com.example.boxmanagernew.backup.model.BackupMetadata
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.domain.model.ObjectPermanentId
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import com.example.boxmanagernew.domain.model.BoxPermanentId
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class BackupDeserializer {

    fun deserializeArchive(
        bytes: ByteArray
    ): BackupArchive {

        val root = JSONObject(bytes.toString(StandardCharsets.UTF_8))
        val application = root.getJSONObject("application")
        val content = root.getJSONObject("archive")

        val boxes = parseBoxes(content.getJSONArray("boxes"))
        val objects = parseObjects(content.getJSONArray("objects"))
        val categories = parseCategories(content.getJSONArray("categories"))
        val locations = parseLocations(content.getJSONArray("locations"))
        val objectTypes = parseObjectTypes(content.getJSONArray("objectTypes"))

        return BackupArchive(
            formatVersion = root.getInt("formatVersion"),
            createdAt = root.getString("createdAt"),
            application = BackupApplicationInfo(
                name = application.getString("name"),
                backupType = application.getString("backupType")
            ),
            boxes = boxes,
            objects = objects,
            categories = categories,
            locations = locations,
            objectTypes = objectTypes,
            archive = BackupArchiveContent(
                boxes = boxes,
                objects = objects,
                categories = categories,
                locations = locations,
                objectTypes = objectTypes
            )
        )
    }

    fun deserializeMetadata(
        bytes: ByteArray
    ): BackupMetadata {

        val root = JSONObject(bytes.toString(StandardCharsets.UTF_8))

        return BackupMetadata(
            backupFormatVersion = root.getInt("backupFormatVersion"),
            applicationVersion = root.getString("applicationVersion"),
            creationTimestamp = root.getString("creationTimestamp"),
            boxCount = root.getInt("boxCount"),
            objectCount = root.getInt("objectCount"),
            categoryCount = root.getInt("categoryCount"),
            locationCount = root.getInt("locationCount"),
            objectTypeCount = root.getInt("objectTypeCount")
        )
    }

    fun deserializeManifest(
        bytes: ByteArray
    ): BackupManifest {

        val root = JSONObject(bytes.toString(StandardCharsets.UTF_8))

        return BackupManifest(
            checksumAlgorithm = root.getString("checksumAlgorithm"),
            checksum = root.getString("checksum"),
            manifestVersion = root.getInt("manifestVersion")
        )
    }

    private fun parseBoxes(array: JSONArray): List<BoxEntity> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            BoxEntity(
                id = item.getInt("id"),
                name = item.getString("name"),
                categoryId = item.getInt("categoryId"),
                position = item.getString("position"),
                lastModified = item.getLong("lastModified"),
                permanentId = BoxPermanentId.fromStored(
                    if (item.has("permanentId") && !item.isNull("permanentId")) {
                        item.getString("permanentId")
                    } else {
                        null
                    }
                ),
                createdBy = if (item.has("createdBy") && !item.isNull("createdBy")) {
                    item.getString("createdBy")
                } else {
                    ""
                }
            )
        }
    }

    private fun parseObjects(array: JSONArray): List<ObjectEntity> {
        val now = System.currentTimeMillis()
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            ObjectEntity(
                id = item.getInt("id"),
                typeObjectId = item.getInt("typeObjectId"),
                boxId = item.getInt("boxId"),
                description = if (item.isNull("description")) {
                    null
                } else {
                    item.getString("description")
                },
                quantity = if (item.isNull("quantity")) {
                    null
                } else {
                    item.getInt("quantity")
                },
                objectPermanentId = ObjectPermanentId.fromStored(
                    if (item.has("objectPermanentId") && !item.isNull("objectPermanentId")) {
                        item.getString("objectPermanentId")
                    } else {
                        null
                    }
                ),
                lastModified = if (item.has("lastModified") && !item.isNull("lastModified")) {
                    item.getLong("lastModified")
                } else {
                    now
                },
                createdBy = if (item.has("createdBy") && !item.isNull("createdBy")) {
                    item.getString("createdBy")
                } else {
                    ""
                }
            )
        }
    }

    private fun parseCategories(array: JSONArray): List<CategoryEntity> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            CategoryEntity(
                id = item.getInt("id"),
                name = item.getString("name"),
                icon = item.getString("icon")
            )
        }
    }

    private fun parseLocations(array: JSONArray): List<LocationEntity> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            LocationEntity(
                id = item.getInt("id"),
                name = item.getString("name")
            )
        }
    }

    private fun parseObjectTypes(array: JSONArray): List<ObjectTypeEntity> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            ObjectTypeEntity(
                id = item.getInt("id"),
                name = item.getString("name")
            )
        }
    }
}
