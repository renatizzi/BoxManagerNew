package com.example.boxmanagernew.importdata.inspect

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import java.util.Locale

/**
 * Validazione dati estesa (B–C R5 / V3): quantità, lunghezze campi, duplicati nel file.
 * Blocca l’import (niente apply parziale). ZIP + id già coperti da inspector/merge.
 */
class ImportExtendedValidator {

    sealed class Result {
        object Ok : Result()

        data class Failed(
            val message: String
        ) : Result()
    }

    fun validate(
        boxes: List<ImportFileInspector.BoxRow>,
        objects: List<ImportFileInspector.ObjectRow>
    ): Result {
        val seenBoxes = mutableSetOf<String>()
        for (box in boxes) {
            fieldTooLong(box.name, ImportConfiguration.SECTION_BOXES)?.let { return it }
            fieldTooLong(box.category, ImportConfiguration.SECTION_BOXES)?.let { return it }
            fieldTooLong(box.position, ImportConfiguration.SECTION_BOXES)?.let { return it }
            box.permanentId?.let { id ->
                fieldTooLong(id, ImportConfiguration.SECTION_BOXES)?.let { return it }
            }
            val key = normalize(box.name)
            if (!seenBoxes.add(key)) {
                return Result.Failed(
                    ImportConfiguration.MSG_DUPLICATE_BOX + ": " + box.name
                )
            }
        }

        val seenObjects = mutableSetOf<String>()
        for (obj in objects) {
            fieldTooLong(obj.name, ImportConfiguration.SECTION_OBJECTS)?.let { return it }
            fieldTooLong(obj.box, ImportConfiguration.SECTION_OBJECTS)?.let { return it }
            obj.description?.let { d ->
                fieldTooLong(d, ImportConfiguration.SECTION_OBJECTS)?.let { return it }
            }
            obj.objectPermanentId?.let { id ->
                fieldTooLong(id, ImportConfiguration.SECTION_OBJECTS)?.let { return it }
            }
            quantityInvalid(obj)?.let { return it }
            val key = objectKey(obj)
            if (!seenObjects.add(key)) {
                return Result.Failed(
                    ImportConfiguration.MSG_DUPLICATE_OBJECT + ": " + obj.name
                )
            }
        }

        return Result.Ok
    }

    private fun fieldTooLong(value: String, section: String): Result.Failed? {
        if (value.length <= ImportConfiguration.MAX_FIELD_CHARS) {
            return null
        }
        return Result.Failed(
            ImportConfiguration.MSG_FIELD_TOO_LONG +
                " ($section, max ${ImportConfiguration.MAX_FIELD_CHARS})"
        )
    }

    private fun quantityInvalid(obj: ImportFileInspector.ObjectRow): Result.Failed? {
        val raw = obj.quantity ?: return null
        if (raw.isBlank()) return null
        val parsed = parseQuantity(raw)
            ?: return Result.Failed(
                ImportConfiguration.MSG_QUANTITY_INVALID + ": «$raw»"
            )
        if (parsed < 0 || parsed > ImportConfiguration.MAX_QUANTITY) {
            return Result.Failed(
                ImportConfiguration.MSG_QUANTITY_INVALID + ": «$raw»"
            )
        }
        return null
    }

    /** Accetta interi e forme foglio tipo «12» / «12.0». */
    fun parseQuantity(raw: String): Int? {
        val trimmed = raw.trim().replace(',', '.')
        if (trimmed.isEmpty()) return null
        val asInt = trimmed.toIntOrNull()
        if (asInt != null) return asInt
        val asDouble = trimmed.toDoubleOrNull() ?: return null
        if (asDouble != asDouble.toLong().toDouble()) return null
        val whole = asDouble.toLong()
        if (whole < Int.MIN_VALUE || whole > Int.MAX_VALUE) return null
        return whole.toInt()
    }

    private fun objectKey(obj: ImportFileInspector.ObjectRow): String {
        return listOf(
            normalize(obj.name),
            normalize(obj.box),
            normalize(obj.description.orEmpty()),
            normalize(obj.quantity.orEmpty())
        ).joinToString("|")
    }

    private fun normalize(value: String): String =
        value.trim().lowercase(Locale.ROOT)
}
