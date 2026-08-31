package com.example.boxmanagernew.family.inventory

import com.example.boxmanagernew.data.local.entity.FamilyDeletionTombstoneEntity
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.model.FamilyDeletion
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import java.util.Locale

/**
 * Legge e valida un file inventario famiglia.
 * createdBy e sezione CANCELLAZIONI sono opzionali (retrocompat B4).
 */
class FamilyInventoryReader {

    sealed class Result {
        data class Ok(
            val snapshot: FamilyInventorySnapshot,
            val skippedRows: Int = 0
        ) : Result()

        data class Error(val message: String) : Result()
    }

    fun parse(text: String): Result {
        val raw = text.removePrefix("\uFEFF")
        val lines = raw.split('\n', '\r')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) {
            return Result.Error(MSG_EMPTY)
        }

        val header = split(lines.first())
        if (
            header.size < 3 ||
            !header[0].equals("formato", ignoreCase = true) ||
            header[1] != FamilyInventoryConfiguration.FORMAT_NAME ||
            header[2].toIntOrNull() != FamilyInventoryConfiguration.FORMAT_VERSION
        ) {
            return Result.Error(MSG_FORMAT)
        }

        var section: String? = null
        val boxes = mutableListOf<FamilyInventoryBox>()
        val objects = mutableListOf<FamilyInventoryObject>()
        val deletions = mutableListOf<FamilyDeletion>()
        var expectBoxHeader = false
        var expectObjectHeader = false
        var expectDeletionHeader = false
        var boxHasCreatedBy = false
        var objectHasCreatedBy = false
        var skippedRows = 0

        for (line in lines.drop(1)) {
            val cols = split(line)
            if (cols.isEmpty()) {
                continue
            }

            if (cols[0].equals("sezione", ignoreCase = true)) {
                if (cols.size < 2) {
                    return Result.Error(MSG_SECTION)
                }
                section = cols[1].uppercase(Locale.ROOT)
                when (section) {
                    FamilyInventoryConfiguration.SECTION_BOXES -> {
                        expectBoxHeader = true
                        expectObjectHeader = false
                        expectDeletionHeader = false
                    }
                    FamilyInventoryConfiguration.SECTION_OBJECTS -> {
                        expectObjectHeader = true
                        expectBoxHeader = false
                        expectDeletionHeader = false
                    }
                    FamilyInventoryConfiguration.SECTION_DELETIONS -> {
                        expectDeletionHeader = true
                        expectBoxHeader = false
                        expectObjectHeader = false
                    }
                    else -> return Result.Error(MSG_SECTION)
                }
                continue
            }

            when (section) {
                FamilyInventoryConfiguration.SECTION_BOXES -> {
                    if (expectBoxHeader) {
                        if (!isBoxHeader(cols)) {
                            return Result.Error(MSG_BOX_HEADER)
                        }
                        boxHasCreatedBy = cols.size >= 6 &&
                            cols[5].equals(
                                FamilyInventoryConfiguration.COL_CREATED_BY,
                                ignoreCase = true
                            )
                        expectBoxHeader = false
                        continue
                    }
                    val parsed = parseBox(cols, boxHasCreatedBy)
                    if (parsed == null) {
                        skippedRows++
                        continue
                    }
                    boxes += parsed
                }
                FamilyInventoryConfiguration.SECTION_OBJECTS -> {
                    if (expectObjectHeader) {
                        if (!isObjectHeader(cols)) {
                            return Result.Error(MSG_OBJECT_HEADER)
                        }
                        objectHasCreatedBy = cols.size >= 7 &&
                            cols[6].equals(
                                FamilyInventoryConfiguration.COL_CREATED_BY,
                                ignoreCase = true
                            )
                        expectObjectHeader = false
                        continue
                    }
                    val parsed = parseObject(cols, objectHasCreatedBy)
                    if (parsed == null) {
                        skippedRows++
                        continue
                    }
                    objects += parsed
                }
                FamilyInventoryConfiguration.SECTION_DELETIONS -> {
                    if (expectDeletionHeader) {
                        if (!isDeletionHeader(cols)) {
                            return Result.Error(MSG_DELETION_HEADER)
                        }
                        expectDeletionHeader = false
                        continue
                    }
                    val parsed = parseDeletion(cols)
                    if (parsed == null) {
                        skippedRows++
                        continue
                    }
                    deletions += parsed
                }
                else -> return Result.Error(MSG_SECTION_ORDER)
            }
        }

        if (section == null) {
            return Result.Error(MSG_SECTION_ORDER)
        }

        return Result.Ok(
            FamilyInventorySnapshot(
                boxes = boxes,
                objects = objects,
                deletions = deletions
            ),
            skippedRows = skippedRows
        )
    }

    private fun parseBox(
        cols: List<String>,
        hasCreatedBy: Boolean
    ): FamilyInventoryBox? {
        val minSize = if (hasCreatedBy) 6 else 5
        if (cols.size < minSize) {
            return null
        }
        val permanentId = cols[0].trim()
        val name = cols[1].trim()
        val category = cols[2].trim()
        val createdBy: String
        val lastModified: Long
        val position: String
        if (hasCreatedBy) {
            lastModified = parseLong(cols[cols.size - 2]) ?: return null
            createdBy = cols.last().trim()
            position = if (cols.size == 6) {
                cols[3].trim()
            } else {
                cols.subList(3, cols.size - 2)
                    .joinToString(FamilyInventoryConfiguration.SEPARATOR)
                    .trim()
            }
        } else {
            lastModified = parseLong(cols.last()) ?: return null
            createdBy = ""
            position = if (cols.size == 5) {
                cols[3].trim()
            } else {
                cols.subList(3, cols.size - 1)
                    .joinToString(FamilyInventoryConfiguration.SEPARATOR)
                    .trim()
            }
        }
        if (permanentId.isEmpty() || name.isEmpty()) {
            return null
        }
        return FamilyInventoryBox(
            permanentId = permanentId,
            name = name,
            category = category,
            position = position,
            lastModified = lastModified,
            createdBy = createdBy
        )
    }

    private fun parseObject(
        cols: List<String>,
        hasCreatedBy: Boolean
    ): FamilyInventoryObject? {
        val minSize = if (hasCreatedBy) 7 else 6
        if (cols.size < minSize) {
            return null
        }
        val objectPermanentId = cols[0].trim()
        val boxPermanentId = cols[1].trim()
        val typeName = cols[2].trim().ifEmpty { DEFAULT_OBJECT_TYPE }
        if (objectPermanentId.isEmpty() || boxPermanentId.isEmpty()) {
            return null
        }

        val createdBy: String
        val lastModified: Long
        val quantityColumn: String
        val descriptionEndExclusive: Int
        if (hasCreatedBy) {
            lastModified = parseLong(cols[cols.size - 2]) ?: return null
            createdBy = cols.last().trim()
            quantityColumn = cols[cols.size - 3].trim()
            descriptionEndExclusive = cols.size - 3
        } else {
            lastModified = parseLong(cols.last()) ?: return null
            createdBy = ""
            quantityColumn = cols[cols.size - 2].trim()
            descriptionEndExclusive = cols.size - 2
        }

        val quantity = when {
            quantityColumn.isEmpty() -> null
            else -> parseInt(quantityColumn) ?: return null
        }

        val description = if (descriptionEndExclusive == 3) {
            null
        } else if (descriptionEndExclusive == 4) {
            cols[3].trim().ifEmpty { null }
        } else {
            cols.subList(3, descriptionEndExclusive)
                .joinToString(FamilyInventoryConfiguration.SEPARATOR)
                .trim()
                .ifEmpty { null }
        }

        return FamilyInventoryObject(
            objectPermanentId = objectPermanentId,
            boxPermanentId = boxPermanentId,
            typeName = typeName,
            description = description,
            quantity = quantity,
            lastModified = lastModified,
            createdBy = createdBy
        )
    }

    private fun parseDeletion(cols: List<String>): FamilyDeletion? {
        if (cols.size < 3) {
            return null
        }
        val entityType = cols[0].trim().uppercase(Locale.ROOT)
        val permanentId = cols[1].trim()
        val deletedAt = parseLong(cols[2]) ?: return null
        val deletedBy = if (cols.size >= 4) cols[3].trim() else ""
        if (
            permanentId.isEmpty() ||
            (
                entityType != FamilyDeletionTombstoneEntity.TYPE_BOX &&
                    entityType != FamilyDeletionTombstoneEntity.TYPE_OBJECT
                )
        ) {
            return null
        }
        return FamilyDeletion(
            entityType = entityType,
            permanentId = permanentId,
            deletedAt = deletedAt,
            deletedBy = deletedBy
        )
    }

    private fun parseLong(value: String): Long? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) {
            return null
        }
        trimmed.toLongOrNull()?.let { return it }
        trimmed.toDoubleOrNull()?.let { return it.toLong() }
        return null
    }

    private fun parseInt(value: String): Int? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) {
            return null
        }
        trimmed.toIntOrNull()?.let { return it }
        trimmed.toDoubleOrNull()?.let { return it.toInt() }
        return null
    }

    private fun isBoxHeader(cols: List<String>): Boolean {
        return cols.size >= 5 &&
            cols[0].equals(FamilyInventoryConfiguration.COL_PERMANENT_ID, ignoreCase = true) &&
            cols[1].equals(FamilyInventoryConfiguration.COL_NAME, ignoreCase = true) &&
            cols[2].equals(FamilyInventoryConfiguration.COL_CATEGORY, ignoreCase = true) &&
            cols[3].equals(FamilyInventoryConfiguration.COL_POSITION, ignoreCase = true) &&
            cols[4].equals(FamilyInventoryConfiguration.COL_LAST_MODIFIED, ignoreCase = true) &&
            (
                cols.size == 5 ||
                    cols[5].equals(
                        FamilyInventoryConfiguration.COL_CREATED_BY,
                        ignoreCase = true
                    )
                )
    }

    private fun isObjectHeader(cols: List<String>): Boolean {
        return cols.size >= 6 &&
            cols[0].equals(FamilyInventoryConfiguration.COL_OBJECT_PERMANENT_ID, ignoreCase = true) &&
            cols[1].equals(FamilyInventoryConfiguration.COL_BOX_PERMANENT_ID, ignoreCase = true) &&
            cols[2].equals(FamilyInventoryConfiguration.COL_TYPE, ignoreCase = true) &&
            cols[3].equals(FamilyInventoryConfiguration.COL_DESCRIPTION, ignoreCase = true) &&
            cols[4].equals(FamilyInventoryConfiguration.COL_QUANTITY, ignoreCase = true) &&
            cols[5].equals(FamilyInventoryConfiguration.COL_LAST_MODIFIED, ignoreCase = true) &&
            (
                cols.size == 6 ||
                    cols[6].equals(
                        FamilyInventoryConfiguration.COL_CREATED_BY,
                        ignoreCase = true
                    )
                )
    }

    private fun isDeletionHeader(cols: List<String>): Boolean {
        return cols.size >= 3 &&
            cols[0].equals(FamilyInventoryConfiguration.COL_ENTITY_TYPE, ignoreCase = true) &&
            cols[1].equals(FamilyInventoryConfiguration.COL_PERMANENT_ID, ignoreCase = true) &&
            cols[2].equals(FamilyInventoryConfiguration.COL_DELETED_AT, ignoreCase = true)
    }

    private fun split(line: String): List<String> {
        return line.split(FamilyInventoryConfiguration.SEPARATOR)
    }

    companion object {
        const val MSG_EMPTY = "File inventario vuoto."
        const val MSG_FORMAT =
            "Formato non riconosciuto. Serve BoxManager_FamilyInventory v1."
        const val MSG_SECTION = "Sezione inventario non valida."
        const val MSG_SECTION_ORDER =
            "File incompleto: servono sezioni CONTENITORI e/o OGGETTI."
        const val MSG_BOX_HEADER =
            "Intestazione CONTENITORI non valida."
        const val MSG_OBJECT_HEADER =
            "Intestazione OGGETTI non valida."
        const val MSG_DELETION_HEADER =
            "Intestazione CANCELLAZIONI non valida."
        const val MSG_BOX_ROW = "Riga contenitore non valida."
        const val MSG_OBJECT_ROW = "Riga oggetto non valida."
        const val DEFAULT_OBJECT_TYPE = "Oggetto"
    }
}
