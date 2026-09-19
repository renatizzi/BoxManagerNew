package com.example.boxmanagernew.importdata.inspect

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import java.util.Locale

/**
 * Validazione dati estesa (B–C R5 / V3): quantità, lunghezze campi, oggetti duplicati nel file.
 * Contenitori omonimi ammessi (come in archivio / Room: nessun unique sul nome).
 * Duplicati esatti BOX (nome+categoria+posizione) = soft: merge li ignora, non blocco qui.
 * Raccoglie tutti gli errori riga-per-riga (R6). Blocca l’import (niente apply parziale).
 */
class ImportExtendedValidator {

    sealed class Result {
        object Ok : Result()

        data class Failed(
            val errors: List<ImportRowError>
        ) : Result() {
            val message: String
                get() = errors.firstOrNull()?.reason.orEmpty()
        }
    }

    fun validate(
        boxes: List<ImportFileInspector.BoxRow>,
        objects: List<ImportFileInspector.ObjectRow>
    ): Result {
        val errors = mutableListOf<ImportRowError>()

        for (box in boxes) {
            fieldTooLong(box.name, ImportConfiguration.SECTION_BOXES, box.sourceLine)
                ?.let { errors.add(it) }
            fieldTooLong(box.category, ImportConfiguration.SECTION_BOXES, box.sourceLine)
                ?.let { errors.add(it) }
            fieldTooLong(box.position, ImportConfiguration.SECTION_BOXES, box.sourceLine)
                ?.let { errors.add(it) }
            box.permanentId?.let { id ->
                fieldTooLong(id, ImportConfiguration.SECTION_BOXES, box.sourceLine)
                    ?.let { errors.add(it) }
            }
        }

        val seenObjects = mutableSetOf<String>()
        for (obj in objects) {
            fieldTooLong(obj.name, ImportConfiguration.SECTION_OBJECTS, obj.sourceLine)
                ?.let { errors.add(it) }
            fieldTooLong(obj.box, ImportConfiguration.SECTION_OBJECTS, obj.sourceLine)
                ?.let { errors.add(it) }
            obj.description?.let { d ->
                fieldTooLong(d, ImportConfiguration.SECTION_OBJECTS, obj.sourceLine)
                    ?.let { errors.add(it) }
            }
            obj.objectPermanentId?.let { id ->
                fieldTooLong(id, ImportConfiguration.SECTION_OBJECTS, obj.sourceLine)
                    ?.let { errors.add(it) }
            }
            quantityInvalid(obj)?.let { errors.add(it) }
            val key = objectKey(obj)
            if (!seenObjects.add(key)) {
                errors.add(
                    ImportRowError(
                        section = ImportConfiguration.SECTION_OBJECTS,
                        line = obj.sourceLine,
                        reason = ImportConfiguration.MSG_DUPLICATE_OBJECT + ": " + obj.name
                    )
                )
            }
        }

        return if (errors.isEmpty()) {
            Result.Ok
        } else {
            Result.Failed(errors)
        }
    }

    private fun fieldTooLong(
        value: String,
        section: String,
        line: Int
    ): ImportRowError? {
        if (value.length <= ImportConfiguration.MAX_FIELD_CHARS) {
            return null
        }
        return ImportRowError(
            section = section,
            line = line,
            reason = ImportConfiguration.MSG_FIELD_TOO_LONG +
                " ($section, max ${ImportConfiguration.MAX_FIELD_CHARS})"
        )
    }

    private fun quantityInvalid(obj: ImportFileInspector.ObjectRow): ImportRowError? {
        val raw = obj.quantity ?: return null
        if (raw.isBlank()) return null
        val parsed = parseQuantity(raw)
            ?: return ImportRowError(
                section = ImportConfiguration.SECTION_OBJECTS,
                line = obj.sourceLine,
                reason = ImportConfiguration.MSG_QUANTITY_INVALID + ": «$raw»"
            )
        if (parsed < 0 || parsed > ImportConfiguration.MAX_QUANTITY) {
            return ImportRowError(
                section = ImportConfiguration.SECTION_OBJECTS,
                line = obj.sourceLine,
                reason = ImportConfiguration.MSG_QUANTITY_INVALID + ": «$raw»"
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
