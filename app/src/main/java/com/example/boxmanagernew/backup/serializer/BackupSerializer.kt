package com.example.boxmanagernew.backup.serializer

import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.model.BackupManifest
import com.example.boxmanagernew.backup.model.BackupMetadata
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

class BackupSerializer {

    fun serialize(
        archive: BackupArchive
    ): String {

        return buildString {

            appendLine("{")
            appendLine("  \"formatVersion\": ${archive.formatVersion},")
            appendLine("  \"createdAt\": \"${escape(archive.createdAt)}\",")
            appendLine("  \"application\": {")
            appendLine("    \"name\": \"${escape(archive.application.name)}\",")
            appendLine("    \"backupType\": \"${escape(archive.application.backupType)}\"")
            appendLine("  },")
            appendLine("  \"archive\": {")
            appendLine("    \"boxes\": ${serializeBoxes(archive.archive.boxes)},")
            appendLine("    \"objects\": ${serializeObjects(archive.archive.objects)},")
            appendLine("    \"categories\": ${serializeCategories(archive.archive.categories)},")
            appendLine("    \"locations\": ${serializeLocations(archive.archive.locations)},")
            appendLine("    \"objectTypes\": ${serializeObjectTypes(archive.archive.objectTypes)}")
            appendLine("  }")
            append("}")
        }
    }

    fun serialize(
        metadata: BackupMetadata
    ): String {

        return buildString {

            appendLine("{")
            appendLine("  \"backupFormatVersion\": ${metadata.backupFormatVersion},")
            appendLine("  \"applicationVersion\": \"${escape(metadata.applicationVersion)}\",")
            appendLine("  \"creationTimestamp\": \"${escape(metadata.creationTimestamp)}\",")
            appendLine("  \"boxCount\": ${metadata.boxCount},")
            appendLine("  \"objectCount\": ${metadata.objectCount},")
            appendLine("  \"categoryCount\": ${metadata.categoryCount},")
            appendLine("  \"locationCount\": ${metadata.locationCount},")
            append("  \"objectTypeCount\": ${metadata.objectTypeCount}")
            appendLine()
            append("}")
        }
    }

    fun serialize(
        manifest: BackupManifest
    ): String {

        return buildString {

            appendLine("{")
            appendLine("  \"checksumAlgorithm\": \"${escape(manifest.checksumAlgorithm)}\",")
            appendLine("  \"checksum\": \"${escape(manifest.checksum)}\",")
            append("  \"manifestVersion\": ${manifest.manifestVersion}")
            appendLine()
            append("}")
        }
    }

    private fun serializeBoxes(
        boxes: List<BoxEntity>
    ): String = boxes.joinToString(",", "[", "]") { box ->

        "{\"id\":${box.id},\"name\":\"${escape(box.name)}\"," +
                "\"categoryId\":${box.categoryId},\"position\":\"${escape(box.position)}\"," +
                "\"lastModified\":${box.lastModified}," +
                "\"permanentId\":\"${escape(box.permanentId)}\"}"
    }

    private fun serializeObjects(
        objects: List<ObjectEntity>
    ): String = objects.joinToString(",", "[", "]") { obj ->

        "{\"id\":${obj.id},\"typeObjectId\":${obj.typeObjectId}," +
                "\"boxId\":${obj.boxId},\"description\":${jsonStringOrNull(obj.description)}," +
                "\"quantity\":${obj.quantity ?: "null"}," +
                "\"objectPermanentId\":\"${escape(obj.objectPermanentId)}\"," +
                "\"lastModified\":${obj.lastModified}}"
    }

    private fun serializeCategories(
        categories: List<CategoryEntity>
    ): String = categories.joinToString(",", "[", "]") { category ->

        "{\"id\":${category.id},\"name\":\"${escape(category.name)}\"," +
                "\"icon\":\"${escape(category.icon)}\"}"
    }

    private fun serializeLocations(
        locations: List<LocationEntity>
    ): String = locations.joinToString(",", "[", "]") { location ->

        "{\"id\":${location.id},\"name\":\"${escape(location.name)}\"}"
    }

    private fun serializeObjectTypes(
        objectTypes: List<ObjectTypeEntity>
    ): String = objectTypes.joinToString(",", "[", "]") { type ->

        "{\"id\":${type.id},\"name\":\"${escape(type.name)}\"}"
    }

    private fun jsonStringOrNull(
        value: String?
    ): String = value?.let { "\"${escape(it)}\"" } ?: "null"

    private fun escape(value: String): String =
        buildString {

            value.forEach { character ->

                when (character) {
                    '\\' -> append("\\\\")
                    '\"' -> append("\\\"")
                    '\b' -> append("\\b")
                    '\u000C' -> append("\\f")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> {
                        if (character.code < 0x20) {
                            append("\\u%04x".format(character.code))
                        } else {
                            append(character)
                        }
                    }
                }
            }
        }
}
