package com.example.boxmanagernew.importdata.config

import android.content.Context
import com.example.boxmanagernew.R
import com.example.boxmanagernew.storage.CsvFileNames
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracciato ufficiale del Modello di Importazione (CSV).
 * V1 = solo testo (Nota 9.1_B5 §3.4.3 / Allegato 4.9).
 * V2 = V1 + id stabili (T5 ZIP Esporta/Importa con foto).
 */
object ImportConfiguration {

    const val FORMAT_NAME = "BoxManager_Import"

    const val FORMAT_VERSION = 1

    /** CSV nel pacchetto ZIP con permanentId / objectPermanentId (T5). */
    const val FORMAT_VERSION_WITH_IDS = 2

    const val SEPARATOR = ";"

    const val FILE_NAME = "Modello_Importazione.csv"

    const val FILE_EXTENSION = CsvFileNames.EXTENSION

    /**
     * Genera Modello riusa la cartella Backup (Nota B7 / salvataggio-file),
     * non [StorageFolderConfiguration.KEY_IMPORT_EXPORT].
     */
    const val TEMPLATE_FOLDER_KEY = StorageFolderConfiguration.KEY_BACKUP

    const val CSV_MIME_TYPE = "text/csv"

    const val ZIP_MIME_TYPE = "application/zip"

    const val SECTION_BOXES = "CONTENITORI"

    const val SECTION_OBJECTS = "OGGETTI"

    const val COL_NAME = "nome"

    const val COL_CATEGORY = "categoria"

    const val COL_POSITION = "posizione"

    const val COL_BOX = "contenitore"

    const val COL_DESCRIPTION = "descrizione"

    const val COL_QUANTITY = "quantita"

    const val COL_PERMANENT_ID = "permanentId"

    const val COL_OBJECT_PERMANENT_ID = "objectPermanentId"

    val UTF8_BOM: ByteArray = byteArrayOf(
        0xEF.toByte(),
        0xBB.toByte(),
        0xBF.toByte()
    )

    val TEMPLATE_LINES: List<String> = listOf(
        "formato$SEPARATOR$FORMAT_NAME$SEPARATOR$FORMAT_VERSION_WITH_IDS",
        "sezione$SEPARATOR$SECTION_BOXES",
        "$COL_NAME$SEPARATOR$COL_CATEGORY$SEPARATOR$COL_POSITION$SEPARATOR$COL_PERMANENT_ID",
        "sezione$SEPARATOR$SECTION_OBJECTS",
        "$COL_NAME$SEPARATOR$COL_BOX$SEPARATOR$COL_DESCRIPTION$SEPARATOR$COL_QUANTITY$SEPARATOR$COL_OBJECT_PERMANENT_ID"
    )

    /** Intestazione Esporta vista (V1, senza id) — canale contestuale distinto. */
    val FORMAT_FIELDS: List<String> = listOf(
        "formato",
        FORMAT_NAME,
        FORMAT_VERSION.toString()
    )

    val FORMAT_FIELDS_V2: List<String> = listOf(
        "formato",
        FORMAT_NAME,
        FORMAT_VERSION_WITH_IDS.toString()
    )

    fun isOfficialFormatLine(fields: List<String>): Boolean {
        return formatVersionOf(fields) != null
    }

    fun formatVersionOf(fields: List<String>): Int? {
        val cols = fields.dropLastWhile { it.isBlank() }
        if (cols.size != 3) {
            return null
        }
        if (!cols[0].equals("formato", ignoreCase = true)) {
            return null
        }
        if (cols[1] != FORMAT_NAME) {
            return null
        }
        val version = cols[2].toIntOrNull() ?: return null
        return if (
            version == FORMAT_VERSION ||
            version == FORMAT_VERSION_WITH_IDS
        ) {
            version
        } else {
            null
        }
    }

    val IMPORT_OPEN_MIME_TYPES: Array<String> = arrayOf(
        CSV_MIME_TYPE,
        "text/comma-separated-values",
        "text/plain",
        ZIP_MIME_TYPE,
        "application/x-zip-compressed"
    )

    val BOX_HEADER_FIELDS: List<String> = listOf(
        COL_NAME,
        COL_CATEGORY,
        COL_POSITION
    )

    val BOX_HEADER_FIELDS_V2: List<String> = listOf(
        COL_NAME,
        COL_CATEGORY,
        COL_POSITION,
        COL_PERMANENT_ID
    )

    val OBJECT_HEADER_FIELDS: List<String> = listOf(
        COL_NAME,
        COL_BOX,
        COL_DESCRIPTION,
        COL_QUANTITY
    )

    val OBJECT_HEADER_FIELDS_V2: List<String> = listOf(
        COL_NAME,
        COL_BOX,
        COL_DESCRIPTION,
        COL_QUANTITY,
        COL_OBJECT_PERMANENT_ID
    )

    const val CHECK_FILE_EXISTS = "esistenza del file"

    const val CHECK_FORMAT = "formato corretto"

    const val CHECK_STRUCTURE = "struttura conforme al modello ufficiale"

    const val CHECK_REQUIRED = "presenza dei campi obbligatori"

    const val CHECK_DATA = "coerenza dei dati (quantità, lunghezze, oggetti duplicati)"

    /** Limite stringhe import (allineato Descrizione UI / OCR). */
    const val MAX_FIELD_CHARS = 100

    const val MAX_QUANTITY = 999_999

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

    const val MSG_QUANTITY_INVALID =
        "quantità non valida (intero ≥ 0)"

    const val MSG_FIELD_TOO_LONG =
        "campo troppo lungo"

    /** Soft su BOX (omonimi ammessi); usato solo se un giorno si ripristina un hard-check. */
    const val MSG_DUPLICATE_BOX =
        "contenitore duplicato nel file"

    const val MSG_DUPLICATE_OBJECT =
        "oggetto duplicato nel file"

    const val MSG_RELATION_CANCELLED =
        "qualsiasi violazione delle relazioni previste dal modello dati comporta l'annullamento dell'importazione"

    fun localizeCheck(context: Context, check: String): String {
        return when (check) {
            CHECK_FILE_EXISTS ->
                context.getString(R.string.import_check_file_exists)
            CHECK_FORMAT ->
                context.getString(R.string.import_check_format)
            CHECK_STRUCTURE ->
                context.getString(R.string.import_check_structure)
            CHECK_REQUIRED ->
                context.getString(R.string.import_check_required)
            CHECK_DATA ->
                context.getString(R.string.import_check_data)
            else -> check
        }
    }

    fun localizeDependency(context: Context, message: String): String {
        return when {
            message == MSG_BOX_DEPENDENCY ->
                context.getString(R.string.import_msg_box_dependency)
            message == MSG_OBJECT_DEPENDENCY ->
                context.getString(R.string.import_msg_object_dependency)
            message.startsWith(MSG_QUANTITY_INVALID) ->
                context.getString(R.string.import_msg_quantity_invalid) +
                    message.removePrefix(MSG_QUANTITY_INVALID)
            message.startsWith(MSG_FIELD_TOO_LONG) ->
                context.getString(R.string.import_msg_field_too_long) +
                    message.removePrefix(MSG_FIELD_TOO_LONG)
            message.startsWith(MSG_DUPLICATE_BOX) ->
                context.getString(R.string.import_msg_duplicate_box) +
                    message.removePrefix(MSG_DUPLICATE_BOX)
            message.startsWith(MSG_DUPLICATE_OBJECT) ->
                context.getString(R.string.import_msg_duplicate_object) +
                    message.removePrefix(MSG_DUPLICATE_OBJECT)
            else -> message
        }
    }

    fun importCancelled(context: Context) =
        context.getString(R.string.import_msg_cancelled)

    fun relationCancelled(context: Context) =
        context.getString(R.string.import_msg_relation_cancelled)

    fun reportRecordsRead(context: Context) =
        context.getString(R.string.import_report_records_read)

    fun reportImported(context: Context) =
        context.getString(R.string.import_report_imported)

    fun reportIgnored(context: Context) =
        context.getString(R.string.import_report_ignored)

    fun reportDiscarded(context: Context) =
        context.getString(R.string.import_report_discarded)

    const val PRE_IMPORT_PREFIX = "PRE_IMPORT_"

    fun preImportFileName(now: Date = Date()): String {
        val formatter = SimpleDateFormat(
            "ddMMyy_HHmm",
            Locale.getDefault()
        )
        return PRE_IMPORT_PREFIX + formatter.format(now)
    }

    fun templateFileName(fileName: String): String {
        return CsvFileNames.force(fileName, FILE_NAME)
    }

    fun templateStem(fileName: String): String {
        return CsvFileNames.stem(fileName, FILE_NAME)
    }
}
