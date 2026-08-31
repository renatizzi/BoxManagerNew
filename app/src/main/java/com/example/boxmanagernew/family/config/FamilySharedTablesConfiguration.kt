package com.example.boxmanagernew.family.config

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracciato tabelle condivise famiglia (categorie + posizioni).
 * Stesso formato CSV del catalogo B1, prefisso file dedicato.
 */
object FamilySharedTablesConfiguration {

    const val FILE_PREFIX = "Tabelle_Condivise_"

    const val FILE_EXTENSION = FamilyCatalogConfiguration.FILE_EXTENSION

    const val CSV_MIME_TYPE = FamilyCatalogConfiguration.CSV_MIME_TYPE

    fun proposedFileName(now: Date = Date()): String {
        val formatter =
            SimpleDateFormat("ddMMyy_HHmm", Locale.ITALY)
        return FILE_PREFIX + formatter.format(now) + FILE_EXTENSION
    }
}
