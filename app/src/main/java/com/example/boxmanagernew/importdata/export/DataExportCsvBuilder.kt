package com.example.boxmanagernew.importdata.export

import com.example.boxmanagernew.importdata.config.ImportConfiguration

/**
 * Builder CSV per Esporta dati (Utility).
 * Utility Esporta CSV/ZIP usano V2 con id stabili (B–C R7).
 * Esporta vista contestuale resta V1 (ViewExportCsvBuilder).
 */
class DataExportCsvBuilder {

    data class BoxRow(
        val name: String,
        val category: String,
        val position: String,
        val permanentId: String = ""
    )

    data class ObjectRow(
        val name: String,
        val boxName: String,
        val description: String,
        val quantity: String,
        val objectPermanentId: String = ""
    )

    fun build(
        boxes: List<BoxRow>,
        objects: List<ObjectRow>,
        withIds: Boolean
    ): ByteArray {
        val version =
            if (withIds) {
                ImportConfiguration.FORMAT_VERSION_WITH_IDS
            } else {
                ImportConfiguration.FORMAT_VERSION
            }
        val boxHeader =
            if (withIds) {
                ImportConfiguration.BOX_HEADER_FIELDS_V2
            } else {
                ImportConfiguration.BOX_HEADER_FIELDS
            }
        val objectHeader =
            if (withIds) {
                ImportConfiguration.OBJECT_HEADER_FIELDS_V2
            } else {
                ImportConfiguration.OBJECT_HEADER_FIELDS
            }

        val lines = mutableListOf<String>()
        lines.add(
            join(
                listOf(
                    "formato",
                    ImportConfiguration.FORMAT_NAME,
                    version.toString()
                )
            )
        )
        lines.add(join(listOf("sezione", ImportConfiguration.SECTION_BOXES)))
        lines.add(join(boxHeader))
        for (box in boxes) {
            val cols = mutableListOf(box.name, box.category, box.position)
            if (withIds) {
                cols.add(box.permanentId)
            }
            lines.add(join(cols))
        }
        lines.add(join(listOf("sezione", ImportConfiguration.SECTION_OBJECTS)))
        lines.add(join(objectHeader))
        for (obj in objects) {
            val cols = mutableListOf(
                obj.name,
                obj.boxName,
                obj.description,
                obj.quantity
            )
            if (withIds) {
                cols.add(obj.objectPermanentId)
            }
            lines.add(join(cols))
        }
        val body = lines.joinToString(separator = "\r\n", postfix = "\r\n")
        return ImportConfiguration.UTF8_BOM + body.toByteArray(Charsets.UTF_8)
    }

    private fun join(fields: List<String>): String {
        return fields.joinToString(ImportConfiguration.SEPARATOR) { escape(it) }
    }

    private fun escape(value: String): String {
        val needsQuotes =
            value.contains(ImportConfiguration.SEPARATOR) ||
                value.contains('"') ||
                value.contains('\n') ||
                value.contains('\r')
        if (!needsQuotes) {
            return value
        }
        return "\"" + value.replace("\"", "\"\"") + "\""
    }
}
