package com.example.boxmanagernew.family.config

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracciato unione famiglia unificato (struttura + inventario).
 */
object FamilyMergeConfiguration {

    const val FORMAT_NAME = "BoxManager_FamilyMerge"

    const val FORMAT_VERSION = 1

    const val SEPARATOR = ";"

    const val FILE_PREFIX = "Unione_Famiglia_"

    const val FILE_EXTENSION = ".csv"

    const val CSV_MIME_TYPE = "text/csv"

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
