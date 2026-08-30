package com.example.boxmanagernew.family.inventory

import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import java.util.Locale

/**
 * Legge e valida un file inventario famiglia.
 */
class FamilyInventoryReader {

    sealed class Result {
        data class Ok(val snapshot: FamilyInventorySnapshot) : Result()
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
        var expectBoxHeader = false
        var expectObjectHeader = false

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
                    }
                    FamilyInventoryConfiguration.SECTION_OBJECTS -> {
                        expectObjectHeader = true
                        expectBoxHeader = false
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
                        expectBoxHeader = false
                        continue
                    }
                    val parsed = parseBox(cols) ?: return Result.Error(MSG_BOX_ROW)
                    boxes += parsed
                }
                FamilyInventoryConfiguration.SECTION_OBJECTS -> {
                    if (expectObjectHeader) {
                        if (!isObjectHeader(cols)) {
                            return Result.Error(MSG_OBJECT_HEADER)
                        }
                        expectObjectHeader = false
                        continue
                    }
                    val parsed = parseObject(cols) ?: return Result.Error(MSG_OBJECT_ROW)
                    objects += parsed
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
                objects = objects
            )
        )
    }

    private fun parseBox(cols: List<String>): FamilyInventoryBox? {
        if (cols.size < 5) {
            return null
        }
        val permanentId = cols[0].trim()
        val name = cols[1].trim()
        val category = cols[2].trim()
        val position = cols[3].trim()
        val lastModified = cols[4].trim().toLongOrNull() ?: return null
        if (permanentId.isEmpty() || name.isEmpty()) {
            return null
        }
        return FamilyInventoryBox(
            permanentId = permanentId,
            name = name,
            category = category,
            position = position,
            lastModified = lastModified
        )
    }

    private fun parseObject(cols: List<String>): FamilyInventoryObject? {
        if (cols.size < 6) {
            return null
        }
        val objectPermanentId = cols[0].trim()
        val boxPermanentId = cols[1].trim()
        val typeName = cols[2].trim()
        val description = cols[3].trim().ifEmpty { null }
        val quantity = cols[4].trim().toIntOrNull()
        val lastModified = cols[5].trim().toLongOrNull() ?: return null
        if (objectPermanentId.isEmpty() || boxPermanentId.isEmpty() || typeName.isEmpty()) {
            return null
        }
        return FamilyInventoryObject(
            objectPermanentId = objectPermanentId,
            boxPermanentId = boxPermanentId,
            typeName = typeName,
            description = description,
            quantity = quantity,
            lastModified = lastModified
        )
    }

    private fun isBoxHeader(cols: List<String>): Boolean {
        return cols.size >= 5 &&
            cols[0].equals(FamilyInventoryConfiguration.COL_PERMANENT_ID, ignoreCase = true) &&
            cols[1].equals(FamilyInventoryConfiguration.COL_NAME, ignoreCase = true) &&
            cols[2].equals(FamilyInventoryConfiguration.COL_CATEGORY, ignoreCase = true) &&
            cols[3].equals(FamilyInventoryConfiguration.COL_POSITION, ignoreCase = true) &&
            cols[4].equals(FamilyInventoryConfiguration.COL_LAST_MODIFIED, ignoreCase = true)
    }

    private fun isObjectHeader(cols: List<String>): Boolean {
        return cols.size >= 6 &&
            cols[0].equals(FamilyInventoryConfiguration.COL_OBJECT_PERMANENT_ID, ignoreCase = true) &&
            cols[1].equals(FamilyInventoryConfiguration.COL_BOX_PERMANENT_ID, ignoreCase = true) &&
            cols[2].equals(FamilyInventoryConfiguration.COL_TYPE, ignoreCase = true) &&
            cols[3].equals(FamilyInventoryConfiguration.COL_DESCRIPTION, ignoreCase = true) &&
            cols[4].equals(FamilyInventoryConfiguration.COL_QUANTITY, ignoreCase = true) &&
            cols[5].equals(FamilyInventoryConfiguration.COL_LAST_MODIFIED, ignoreCase = true)
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
            "Struttura incompleta: servono sezioni CONTENITORI e/o OGGETTI."
        const val MSG_BOX_HEADER =
            "Intestazione CONTENITORI non valida."
        const val MSG_OBJECT_HEADER =
            "Intestazione OGGETTI non valida."
        const val MSG_BOX_ROW = "Riga contenitore non valida."
        const val MSG_OBJECT_ROW = "Riga oggetto non valida."
    }
}
