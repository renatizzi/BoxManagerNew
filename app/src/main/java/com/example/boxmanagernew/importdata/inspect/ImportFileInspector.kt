package com.example.boxmanagernew.importdata.inspect

import com.example.boxmanagernew.importdata.config.ImportConfiguration

class ImportFileInspector {

    data class BoxRow(
        val name: String,
        val category: String,
        val position: String
    )

    data class ObjectRow(
        val name: String,
        val box: String,
        val description: String?,
        val quantity: String?
    )

    sealed class Result {
        data class Ready(
            val boxes: List<BoxRow>,
            val objects: List<ObjectRow>
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

        val text = decode(bytes)
        val lines = text.split("\r\n", "\n", "\r")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) {
            return Result.Failed(ImportConfiguration.CHECK_FORMAT)
        }

        var index = 0
        val format = splitCsv(lines[index])
        if (!ImportConfiguration.isOfficialFormatLine(format)) {
            return Result.Failed(ImportConfiguration.CHECK_FORMAT)
        }
        index++

        if (index >= lines.size ||
            splitCsv(lines[index]) != listOf("sezione", ImportConfiguration.SECTION_BOXES)
        ) {
            return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
        }
        index++

        if (index >= lines.size ||
            splitCsv(lines[index]) != ImportConfiguration.BOX_HEADER_FIELDS
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
            if (fields.size != ImportConfiguration.BOX_HEADER_FIELDS.size) {
                return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
            }
            if (fields.any { it.isBlank() }) {
                return Result.Failed(ImportConfiguration.CHECK_REQUIRED)
            }
            boxes.add(
                BoxRow(
                    name = fields[0],
                    category = fields[1],
                    position = fields[2]
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
            splitCsv(lines[index]) != ImportConfiguration.OBJECT_HEADER_FIELDS
        ) {
            return Result.Failed(ImportConfiguration.CHECK_STRUCTURE)
        }
        index++

        val objects = mutableListOf<ObjectRow>()
        while (index < lines.size) {
            val fields = splitCsv(lines[index])
            if (fields.size != ImportConfiguration.OBJECT_HEADER_FIELDS.size) {
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
                    quantity = fields[3].takeIf { it.isNotBlank() }
                )
            )
            index++
        }

        return Result.Ready(
            boxes = boxes,
            objects = objects
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
