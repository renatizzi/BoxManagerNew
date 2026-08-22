package com.example.boxmanagernew.viewoutput.config

import com.example.boxmanagernew.backup.config.BackupConfiguration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ViewOutputConfiguration {

    const val EXPORT_FILE_PREFIX = "ESPORTA_"

    const val MSG_CONFIRM = "Confermi?"

    fun exportFilePrompt(fileExists: Boolean): String {
        return if (fileExists) {
            BackupConfiguration.MSG_FILE_EXISTS
        } else {
            MSG_CONFIRM
        }
    }

    const val PAGE_TITLE =
        "Contenitori - Gestione Contenitori e loro contenuti"

    const val PAGE_TITLE_FOUND_OBJECTS =
        "Lista Oggetti Trovati - Risultati ricerca archivio"

    fun objectsInBoxTitle(boxName: String): String {
        return "Lista Oggetti - Contenuto del box $boxName"
    }

    fun filterLine(
        query: String,
        now: Date = Date()
    ): String {

        val stamp = SimpleDateFormat(
            "dd/MM/yyyy - HH:mm",
            Locale.getDefault()
        ).format(now)

        return "Lista filtrata per \"$query\" il $stamp"
    }

    fun countBoxes(count: Int): String {
        return "N. Contenitori: $count"
    }

    fun countObjects(count: Int): String {
        return "N. Oggetti: $count"
    }

    fun proposedFileName(
        now: Date = Date()
    ): String {

        return csvFileName(
            EXPORT_FILE_PREFIX + stamp(now),
            now
        )
    }

    fun csvFileName(
        fileName: String,
        now: Date = Date()
    ): String {

        val trimmed = fileName.trim().ifBlank {
            EXPORT_FILE_PREFIX + stamp(now)
        }
        val extension = ".csv"

        return if (
            trimmed.endsWith(extension, ignoreCase = true)
        ) {
            trimmed
        } else {
            trimmed + extension
        }
    }

    fun csvNamesMatch(
        left: String,
        right: String
    ): Boolean {

        val leftStem = csvStem(left)
        val rightStem = csvStem(right)

        return leftStem.isNotEmpty() &&
                leftStem.equals(rightStem, ignoreCase = true)
    }

    fun csvStem(fileName: String): String {

        var stem = fileName.trim()
        val extension = ".csv"

        while (
            stem.endsWith(extension, ignoreCase = true)
        ) {
            stem = stem.substring(
                0,
                stem.length - extension.length
            ).trimEnd()
        }

        return stem
    }

    private fun stamp(now: Date): String {

        return SimpleDateFormat(
            "ddMMyy_HHmm",
            Locale.getDefault()
        ).format(now)
    }
}
