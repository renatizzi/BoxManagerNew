package com.example.boxmanagernew.importdata.config

import com.example.boxmanagernew.storage.StorageFolderConfiguration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracciato ufficiale V1 del Modello di Importazione (CSV).
 * Recepito in Nota 9.1_B5, sezione 3.4.3 / Allegato 4.9.
 */
object ImportConfiguration {

    const val FORMAT_NAME = "BoxManager_Import"

    const val FORMAT_VERSION = 1

    const val SEPARATOR = ";"

    const val FILE_NAME = "Modello_Importazione.csv"

    const val FILE_EXTENSION = ".csv"

    /**
     * Genera Modello riusa la cartella Backup (Nota B7 / salvataggio-file),
     * non [StorageFolderConfiguration.KEY_IMPORT_EXPORT].
     */
    const val TEMPLATE_FOLDER_KEY = StorageFolderConfiguration.KEY_BACKUP

    const val CSV_MIME_TYPE = "text/csv"

    const val SECTION_BOXES = "CONTENITORI"

    const val SECTION_OBJECTS = "OGGETTI"

    const val COL_NAME = "nome"

    const val COL_CATEGORY = "categoria"

    const val COL_POSITION = "posizione"

    const val COL_BOX = "contenitore"

    const val COL_DESCRIPTION = "descrizione"

    const val COL_QUANTITY = "quantita"

    val UTF8_BOM: ByteArray = byteArrayOf(
        0xEF.toByte(),
        0xBB.toByte(),
        0xBF.toByte()
    )

    val TEMPLATE_LINES: List<String> = listOf(
        "formato$SEPARATOR$FORMAT_NAME$SEPARATOR$FORMAT_VERSION",
        "sezione$SEPARATOR$SECTION_BOXES",
        "$COL_NAME$SEPARATOR$COL_CATEGORY$SEPARATOR$COL_POSITION",
        "sezione$SEPARATOR$SECTION_OBJECTS",
        "$COL_NAME$SEPARATOR$COL_BOX$SEPARATOR$COL_DESCRIPTION$SEPARATOR$COL_QUANTITY"
    )

    val FORMAT_FIELDS: List<String> = listOf(
        "formato",
        FORMAT_NAME,
        FORMAT_VERSION.toString()
    )

    fun isOfficialFormatLine(fields: List<String>): Boolean {
        val cols = fields.dropLastWhile { it.isBlank() }
        if (cols.size != FORMAT_FIELDS.size) {
            return false
        }
        return cols[0].equals(FORMAT_FIELDS[0], ignoreCase = true) &&
                cols[1] == FORMAT_NAME &&
                cols[2] == FORMAT_VERSION.toString()
    }

    val IMPORT_OPEN_MIME_TYPES: Array<String> = arrayOf(
        CSV_MIME_TYPE,
        "text/comma-separated-values",
        "text/plain"
    )

    val BOX_HEADER_FIELDS: List<String> = listOf(
        COL_NAME,
        COL_CATEGORY,
        COL_POSITION
    )

    val OBJECT_HEADER_FIELDS: List<String> = listOf(
        COL_NAME,
        COL_BOX,
        COL_DESCRIPTION,
        COL_QUANTITY
    )

    const val CHECK_FILE_EXISTS = "esistenza del file"

    const val CHECK_FORMAT = "formato corretto"

    const val CHECK_STRUCTURE = "struttura conforme al modello ufficiale"

    const val CHECK_REQUIRED = "presenza dei campi obbligatori"

    const val MSG_IMPORT_CANCELLED =
        "Se uno qualsiasi dei controlli fallisce, l'importazione viene annullata senza modificare l'archivio."

    const val REPORT_RECORDS_READ = "record letti"

    const val REPORT_IMPORTED = "record importati"

    const val REPORT_IGNORED = "record ignorati (duplicati)"

    const val REPORT_DISCARDED = "record scartati per errore"

    const val MSG_BOX_DEPENDENCY =
        "un Contenitore non può essere importato se fa riferimento a una Categoria o a una Posizione inesistente"

    const val MSG_OBJECT_DEPENDENCY =
        "un Oggetto non può essere importato se fa riferimento a un Contenitore inesistente"

    const val MSG_RELATION_CANCELLED =
        "qualsiasi violazione delle relazioni previste dal modello dati comporta l'annullamento dell'importazione"

    const val PRE_IMPORT_PREFIX = "PRE_IMPORT_"

    fun preImportFileName(now: Date = Date()): String {
        val formatter = SimpleDateFormat(
            "ddMMyy_HHmm",
            Locale.getDefault()
        )
        return PRE_IMPORT_PREFIX + formatter.format(now)
    }

    fun templateFileName(fileName: String): String {
        val trimmed = fileName.trim().ifBlank { FILE_NAME }
        return if (trimmed.endsWith(FILE_EXTENSION, ignoreCase = true)) {
            trimmed
        } else {
            trimmed + FILE_EXTENSION
        }
    }

    fun templateStem(fileName: String): String {
        val csvName = templateFileName(fileName)
        return csvName.substring(
            0,
            csvName.length - FILE_EXTENSION.length
        )
    }
}
