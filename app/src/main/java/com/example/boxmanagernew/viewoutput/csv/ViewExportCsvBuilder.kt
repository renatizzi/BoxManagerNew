package com.example.boxmanagernew.viewoutput.csv

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot

class ViewExportCsvBuilder {

    fun build(snapshot: ContainerViewSnapshot): ByteArray {
        val lines = mutableListOf<String>()
        lines.add(
            join(
                ImportConfiguration.FORMAT_FIELDS
            )
        )
        lines.add(
            join(
                listOf(
                    "sezione",
                    ImportConfiguration.SECTION_BOXES
                )
            )
        )
        lines.add(
            join(
                ImportConfiguration.BOX_HEADER_FIELDS
            )
        )
        snapshot.boxes.forEach { box ->
            lines.add(
                join(
                    listOf(
                        box.name,
                        box.category,
                        box.position
                    )
                )
            )
        }
        lines.add(
            join(
                listOf(
                    "sezione",
                    ImportConfiguration.SECTION_OBJECTS
                )
            )
        )
        lines.add(
            join(
                ImportConfiguration.OBJECT_HEADER_FIELDS
            )
        )
        snapshot.boxes.forEach { box ->
            box.objects.forEach { obj ->
                lines.add(
                    join(
                        listOf(
                            obj.name,
                            box.name,
                            obj.description,
                            obj.quantity
                        )
                    )
                )
            }
        }
        val body = lines.joinToString(
            separator = "\r\n",
            postfix = "\r\n"
        )
        return ImportConfiguration.UTF8_BOM +
                body.toByteArray(Charsets.UTF_8)
    }

    private fun join(fields: List<String>): String {
        return fields.joinToString(
            ImportConfiguration.SEPARATOR
        ) { field ->
            escape(field)
        }
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
