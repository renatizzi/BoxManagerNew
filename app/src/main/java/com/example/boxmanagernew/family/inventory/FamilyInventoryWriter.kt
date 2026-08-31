package com.example.boxmanagernew.family.inventory

import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.model.FamilyDeletion
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot

/**
 * Serializza l'inventario famiglia nel tracciato ufficiale B2/B5.
 * createdBy e CANCELLAZIONI sono colonne/sezioni opzionali retrocompatibili.
 */
object FamilyInventoryWriter {

    fun toCsvLines(snapshot: FamilyInventorySnapshot): List<String> {
        val sep = FamilyInventoryConfiguration.SEPARATOR
        val lines = mutableListOf<String>()

        lines += "formato$sep${FamilyInventoryConfiguration.FORMAT_NAME}" +
            "$sep${FamilyInventoryConfiguration.FORMAT_VERSION}"

        lines += "sezione$sep${FamilyInventoryConfiguration.SECTION_BOXES}"
        lines += listOf(
            FamilyInventoryConfiguration.COL_PERMANENT_ID,
            FamilyInventoryConfiguration.COL_NAME,
            FamilyInventoryConfiguration.COL_CATEGORY,
            FamilyInventoryConfiguration.COL_POSITION,
            FamilyInventoryConfiguration.COL_LAST_MODIFIED,
            FamilyInventoryConfiguration.COL_CREATED_BY
        ).joinToString(sep)
        for (box in snapshot.boxes) {
            if (box.permanentId.trim().isEmpty()) {
                continue
            }
            lines += listOf(
                escape(box.permanentId),
                escape(box.name),
                escape(box.category),
                escape(box.position),
                box.lastModified.toString(),
                escape(box.createdBy)
            ).joinToString(sep)
        }

        lines += "sezione$sep${FamilyInventoryConfiguration.SECTION_OBJECTS}"
        lines += listOf(
            FamilyInventoryConfiguration.COL_OBJECT_PERMANENT_ID,
            FamilyInventoryConfiguration.COL_BOX_PERMANENT_ID,
            FamilyInventoryConfiguration.COL_TYPE,
            FamilyInventoryConfiguration.COL_DESCRIPTION,
            FamilyInventoryConfiguration.COL_QUANTITY,
            FamilyInventoryConfiguration.COL_LAST_MODIFIED,
            FamilyInventoryConfiguration.COL_CREATED_BY
        ).joinToString(sep)
        for (obj in snapshot.objects) {
            if (
                obj.objectPermanentId.trim().isEmpty() ||
                obj.boxPermanentId.trim().isEmpty()
            ) {
                continue
            }
            lines += listOf(
                escape(obj.objectPermanentId),
                escape(obj.boxPermanentId),
                escape(obj.typeName.ifBlank { FamilyInventoryReader.DEFAULT_OBJECT_TYPE }),
                escape(obj.description.orEmpty()),
                obj.quantity?.toString().orEmpty(),
                obj.lastModified.toString(),
                escape(obj.createdBy)
            ).joinToString(sep)
        }

        lines += "sezione$sep${FamilyInventoryConfiguration.SECTION_DELETIONS}"
        lines += listOf(
            FamilyInventoryConfiguration.COL_ENTITY_TYPE,
            FamilyInventoryConfiguration.COL_PERMANENT_ID,
            FamilyInventoryConfiguration.COL_DELETED_AT,
            FamilyInventoryConfiguration.COL_DELETED_BY
        ).joinToString(sep)
        for (deletion in snapshot.deletions) {
            if (deletion.permanentId.trim().isEmpty()) {
                continue
            }
            lines += listOf(
                escape(deletion.entityType),
                escape(deletion.permanentId),
                deletion.deletedAt.toString(),
                escape(deletion.deletedBy)
            ).joinToString(sep)
        }

        return lines
    }

    fun toCsvBytes(snapshot: FamilyInventorySnapshot): ByteArray {
        val body = toCsvLines(snapshot).joinToString("\r\n") + "\r\n"
        return FamilyInventoryConfiguration.UTF8_BOM +
            body.toByteArray(Charsets.UTF_8)
    }

    private fun escape(value: String): String {
        return value.replace("\r", " ").replace("\n", " ").trim()
    }
}
