package com.example.boxmanagernew.family.config

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracciato unione inventario famiglia (Nota B0 / B2).
 * Contenitori e oggetti per ID stabili.
 */
object FamilyInventoryConfiguration {

    const val FORMAT_NAME = "BoxManager_FamilyInventory"

    const val FORMAT_VERSION = 1

    const val SEPARATOR = ";"

    const val FILE_PREFIX = "Inventario_Famiglia_"

    const val FILE_EXTENSION = ".csv"

    const val CSV_MIME_TYPE = "text/csv"

    const val SECTION_BOXES = "CONTENITORI"

    const val SECTION_OBJECTS = "OGGETTI"

    const val COL_PERMANENT_ID = "permanentId"

    const val COL_OBJECT_PERMANENT_ID = "objectPermanentId"

    const val COL_BOX_PERMANENT_ID = "boxPermanentId"

    const val COL_NAME = "nome"

    const val COL_CATEGORY = "categoria"

    const val COL_POSITION = "posizione"

    const val COL_TYPE = "tipo"

    const val COL_DESCRIPTION = "descrizione"

    const val COL_QUANTITY = "quantita"

    const val COL_LAST_MODIFIED = "lastModified"

    const val COL_CREATED_BY = "createdBy"

    const val SECTION_DELETIONS = "CANCELLAZIONI"

    const val COL_ENTITY_TYPE = "entityType"

    const val COL_DELETED_AT = "deletedAt"

    const val COL_DELETED_BY = "deletedBy"

    val UTF8_BOM: ByteArray = byteArrayOf(
        0xEF.toByte(),
        0xBB.toByte(),
        0xBF.toByte()
    )

    fun proposedFileName(now: Date = Date()): String {
        val formatter =
            SimpleDateFormat("ddMMyy_HHmm", Locale.ITALY)
        return FILE_PREFIX + formatter.format(now) + FILE_EXTENSION
    }
}
