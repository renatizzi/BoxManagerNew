package com.example.boxmanagernew.importdata.inspect

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.zip.ImportDataZip

class ImportFileInspector {

    data class BoxRow(
        val name: String,
        val category: String,
        val position: String,
        val permanentId: String? = null,
        val sourceLine: Int = 0
    )

    data class ObjectRow(
        val name: String,
        val box: String,
        val description: String?,
        val quantity: String?,
        val objectPermanentId: String? = null,
        val sourceLine: Int = 0
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
            val check: String,
            val errors: List<ImportRowError> = emptyList()
        ) : Result()
    }

    private data class NumberedLine(
        val line: Int,
        val text: String
    )

    fun inspect(bytes: ByteArray?): Result {
        if (bytes == null) {
            return Result.Failed(
                check = ImportConfiguration.CHECK_FILE_EXISTS,
                errors = listOf(
                    ImportRowError(
                        section = "",
                        line = 0,
                        reason = ImportConfiguration.CHECK_FILE_EXISTS
                    )
                )
            )
        }

        val csvBytes: ByteArray
        val photos: Map<String, ByteArray>
        if (ImportDataZip.looksLikeZip(bytes)) {
            val unpacked = ImportDataZip.unpack(bytes)
                ?: return Result.Failed(
                    check = ImportConfiguration.CHECK_FORMAT,
                    errors = listOf(
                        ImportRowError(
                            section = "",
                            line = 0,
                            reason = ImportConfiguration.CHECK_FORMAT
                        )
                    )
                )
            csvBytes = unpacked.csvBytes
            photos = unpacked.photoEntries
        } else {
            csvBytes = bytes
            photos = emptyMap()
        }

        val text = decode(csvBytes)
        val lines = text.split("\r\n", "\n", "\r")
            .mapIndexed { index, raw ->
                NumberedLine(line = index + 1, text = raw.trim())
            }
            .filter { it.text.isNotEmpty() }

        if (lines.isEmpty()) {
            return Result.Failed(
                check = ImportConfiguration.CHECK_FORMAT,
                errors = listOf(
                    ImportRowError(
                        section = "",
                        line = 0,
                        reason = ImportConfiguration.CHECK_FORMAT
                    )
                )
            )
        }

        var index = 0
        val format = splitCsv(lines[index].text)
        val version = ImportConfiguration.formatVersionOf(format)
            ?: return Result.Failed(
                check = ImportConfiguration.CHECK_FORMAT,
                errors = listOf(
                    ImportRowError(
                        section = "",
                        line = lines[index].line,
                        reason = ImportConfiguration.CHECK_FORMAT
                    )
                )
            )
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
            splitCsv(lines[index].text) != listOf("sezione", ImportConfiguration.SECTION_BOXES)
        ) {
            val line = lines.getOrNull(index)?.line ?: 0
            return Result.Failed(
                check = ImportConfiguration.CHECK_STRUCTURE,
                errors = listOf(
                    ImportRowError(
                        section = ImportConfiguration.SECTION_BOXES,
                        line = line,
                        reason = ImportConfiguration.CHECK_STRUCTURE
                    )
                )
            )
        }
        index++

        if (index >= lines.size ||
            splitCsv(lines[index].text) != boxHeader
        ) {
            val line = lines.getOrNull(index)?.line ?: 0
            return Result.Failed(
                check = ImportConfiguration.CHECK_STRUCTURE,
                errors = listOf(
                    ImportRowError(
                        section = ImportConfiguration.SECTION_BOXES,
                        line = line,
                        reason = ImportConfiguration.CHECK_STRUCTURE
                    )
                )
            )
        }
        index++

        val boxes = mutableListOf<BoxRow>()
        while (index < lines.size) {
            val numbered = lines[index]
            val fields = splitCsv(numbered.text)
            if (fields == listOf("sezione", ImportConfiguration.SECTION_OBJECTS)) {
                break
            }
            if (fields.size != boxHeader.size) {
                return Result.Failed(
                    check = ImportConfiguration.CHECK_STRUCTURE,
                    errors = listOf(
                        ImportRowError(
                            section = ImportConfiguration.SECTION_BOXES,
                            line = numbered.line,
                            reason = ImportConfiguration.CHECK_STRUCTURE
                        )
                    )
                )
            }
            if (fields[0].isBlank() || fields[1].isBlank() || fields[2].isBlank()) {
                return Result.Failed(
                    check = ImportConfiguration.CHECK_REQUIRED,
                    errors = listOf(
                        ImportRowError(
                            section = ImportConfiguration.SECTION_BOXES,
                            line = numbered.line,
                            reason = ImportConfiguration.CHECK_REQUIRED
                        )
                    )
                )
            }
            boxes.add(
                BoxRow(
                    name = fields[0],
                    category = fields[1],
                    position = fields[2],
                    permanentId = fields.getOrNull(3)?.takeIf { it.isNotBlank() },
                    sourceLine = numbered.line
                )
            )
            index++
        }

        if (index >= lines.size ||
            splitCsv(lines[index].text) != listOf("sezione", ImportConfiguration.SECTION_OBJECTS)
        ) {
            val line = lines.getOrNull(index)?.line ?: 0
            return Result.Failed(
                check = ImportConfiguration.CHECK_STRUCTURE,
                errors = listOf(
                    ImportRowError(
                        section = ImportConfiguration.SECTION_OBJECTS,
                        line = line,
                        reason = ImportConfiguration.CHECK_STRUCTURE
                    )
                )
            )
        }
        index++

        if (index >= lines.size ||
            splitCsv(lines[index].text) != objectHeader
        ) {
            val line = lines.getOrNull(index)?.line ?: 0
            return Result.Failed(
                check = ImportConfiguration.CHECK_STRUCTURE,
                errors = listOf(
                    ImportRowError(
                        section = ImportConfiguration.SECTION_OBJECTS,
                        line = line,
                        reason = ImportConfiguration.CHECK_STRUCTURE
                    )
                )
            )
        }
        index++

        val objects = mutableListOf<ObjectRow>()
        while (index < lines.size) {
            val numbered = lines[index]
            val fields = splitCsv(numbered.text)
            if (fields.size != objectHeader.size) {
                return Result.Failed(
                    check = ImportConfiguration.CHECK_STRUCTURE,
                    errors = listOf(
                        ImportRowError(
                            section = ImportConfiguration.SECTION_OBJECTS,
                            line = numbered.line,
                            reason = ImportConfiguration.CHECK_STRUCTURE
                        )
                    )
                )
            }
            if (fields[0].isBlank() || fields[1].isBlank()) {
                return Result.Failed(
                    check = ImportConfiguration.CHECK_REQUIRED,
                    errors = listOf(
                        ImportRowError(
                            section = ImportConfiguration.SECTION_OBJECTS,
                            line = numbered.line,
                            reason = ImportConfiguration.CHECK_REQUIRED
                        )
                    )
                )
            }
            objects.add(
                ObjectRow(
                    name = fields[0],
                    box = fields[1],
                    description = fields[2].takeIf { it.isNotBlank() },
                    quantity = fields[3].takeIf { it.isNotBlank() },
                    objectPermanentId = fields.getOrNull(4)?.takeIf { it.isNotBlank() },
                    sourceLine = numbered.line
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
