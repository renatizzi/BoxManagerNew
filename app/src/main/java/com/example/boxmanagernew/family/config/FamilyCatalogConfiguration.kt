package com.example.boxmanagernew.family.config

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracciato catalogo famiglia (Nota B0 / B1).
 * Solo categorie e posizioni — non inventario.
 */
object FamilyCatalogConfiguration {

    const val FORMAT_NAME = "BoxManager_FamilyCatalog"

    const val FORMAT_VERSION = 1

    const val SEPARATOR = ";"

    const val FILE_PREFIX = "Catalogo_Famiglia_"

    const val FILE_EXTENSION = ".csv"

    const val CSV_MIME_TYPE = "text/csv"

    const val SECTION_CATEGORIES = "CATEGORIE"

    const val SECTION_LOCATIONS = "POSIZIONI"

    const val COL_NAME = "nome"

    const val COL_ICON = "icona"

    const val DEFAULT_CATEGORY_ICON = "outline_browse_24"

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
