package com.example.boxmanagernew.viewoutput.config

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ViewOutputConfiguration {

    const val EXPORT_FILE_NAME = "Esporta.csv"

    const val EXPORT_BASE_NAME = "Esporta"

    const val PAGE_TITLE =
        "Contenitori - Gestione Contenitori e loro contenuti"

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

    fun csvFileName(fileName: String): String {

        val trimmed = fileName.trim().ifBlank {
            EXPORT_BASE_NAME
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
}
