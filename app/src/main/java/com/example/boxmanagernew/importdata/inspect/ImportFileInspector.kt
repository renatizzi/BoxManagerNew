package com.example.boxmanagernew.importdata.inspect

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.zip.ImportDataZip

class ImportFileInspector {

    data class BoxRow(
        val name: String,
        val category: String,
        val position: String,
        val permanentId: String? = null
    )

    data class ObjectRow(
        val name: String,
        val box: String,
        val description: String?,
        val quantity: String?,
        val objectPermanentId: String? = null
    )

    sealed class Result {
        data class Ready(
            val boxes: List<BoxRow>,
            val objects: List<ObjectRow>,
            val photoEntries: Map<String, ByteArray> = emptyMap(),
            val formatVersion: Int = ImportConfiguration.FORMAT_VERSION
        ) : Result() {
            val recordsRead: Int
                get() = boxes.size + objects.size
        }

        data class Failed(
            val check: String
        ) : Result()
    }

    fun inspect(bytes: ByteArray?): Result {
        if (bytes == null) {
            return Result.Failed(ImportConfiguration.CHECK_FILE_EXISTS)
        }

        val csvBytes: ByteArray
        val photos: Map<String, ByteArray>
        if (ImportDataZip.looksLikeZip(bytes)) {
            val unpacked = ImportDataZip.unpack(bytes)
                ?: return Result.Failed(ImportConfiguration.CHECK_FORMAT)
            csvBytes = unpacked.csvBytes
            photos = unpacked.photoEntries
        } else {
            csvBytes = bytes
            photos = emptyMap()
        }

        val text = decode(csvBytes)
        val lines = text.split("\r\n", "\n", "\r")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) {
            return Result.Failed(ImportConfiguration.CHECK_FORMAT)
        }

        var index = 0
        val format = splitCsv(lines[index])
        val version = ImportConfiguration.formatVersionOf(format)
            ?: return Result.Failed(ImportConfiguration.CHECK_FORMAT)
        index++

        val boxHeader =
            if (version == ImportConfiguration.FORMAT_VERSION_WITH_IDS) {
                ImportConfiguration.BOX_HEADER_FIELDS_V2
            } else {
                ImportConfiguration.BOX_HEADER_FIELDS
            }
        val objectHeader =
            if (version == ImportConfiguration.FORMAT_VERSION_WITH_IDS) {
                ImportConfiguration.OBJECT_HEADER_FIELDS_V2
            } else {
                ImportConfiguration.OBJECT_HEADER_FIELDS
            }

        if (index >= lines.size ||
            splitCsv(lines[index]) != listOf("sezione", ImportConfiguration.SECTION_BOXES)
        ) {
            return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
        }
        index++

        if (index >= lines.size ||
            splitCsv(lines[index]) != boxHeader
        ) {
            return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
        }
        index++

        val boxes = mutableListOf<BoxRow>()
        while (index < lines.size) {
            val fields = splitCsv(lines[index])
            if (fields == listOf("sezione", ImportConfiguration.SECTION_OBJECTS)) {
                break
            }
            if (fields.size != boxHeader.size) {
                return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
            }
            if (fields[0].isBlank() || fields[1].isBlank() || fields[2].isBlank()) {
                return Result.Failed(ImportConfiguration.CHECK_REQUIRED)
            }
            boxes.add(
                BoxRow(
                    name = fields[0],
                    category = fields[1],
                    position = fields[2],
                    permanentId = fields.getOrNull(3)?.takeIf { it.isNotBlank() }
                )
            )
            index++
        }

        if (index >= lines.size ||
            splitCsv(lines[index]) != listOf("sezione", ImportConfiguration.SECTION_OBJECTS)
        ) {
            return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
        }
        index++

        if (index >= lines.size ||
            splitCsv(lines[index]) != objectHeader
        ) {
            return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
        }
        index++

        val objects = mutableListOf<ObjectRow>()
        while (index < lines.size) {
            val fields = splitCsv(lines[index])
            if (fields.size != objectHeader.size) {
                return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
            }
            if (fields[0].isBlank() || fields[1].isBlank()) {
                return Result.Failed(ImportConfiguration.CHECK_REQUIRED)
            }
            objects.add(
                ObjectRow(
                    name = fields[0],
                    box = fields[1],
                    description = fields[2].takeIf { it.isNotBlank() },
                    quantity = fields[3].takeIf { it.isNotBlank() },
                    objectPermanentId = fields.getOrNull(4)?.takeIf { it.isNotBlank() }
                )
            )
            index++
        }

        return Result.Ready(
            boxes = boxes,
            objects = objects,
            photoEntries = photos,
            formatVersion = version
        )
    }

    private fun decode(bytes: ByteArray): String {
        val start =
            if (
                bytes.size >= ImportConfiguration.UTF8_BOM.size &&
                bytes[0] == ImportConfiguration.UTF8_BOM[0] &&
                bytes[1] == ImportConfiguration.UTF8_BOM[1] &&
                bytes[2] == ImportConfiguration.UTF8_BOM[2]
            ) {
                ImportConfiguration.UTF8_BOM.size
            } else {
                0
            }

        return String(
            bytes,
            start,
            bytes.size - start,
            Charsets.UTF_8
        )
    }

    private fun splitCsv(line: String): List<String> {
        val out = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ImportConfiguration.SEPARATOR[0] && !inQuotes -> {
                    out.add(current.toString().trim())
                    current.clear()
                }
                else -> current.append(c)
            }
            i++
        }

        out.add(current.toString().trim())
        return out
    }
}
