package com.example.boxmanagernew.viewoutput.config

import android.content.Context
import com.example.boxmanagernew.R
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

    fun exportFilePrompt(context: Context, fileExists: Boolean): String {
        return if (fileExists) {
            context.getString(R.string.dialog_file_exists)
        } else {
            context.getString(R.string.dialog_confirm_question)
        }
    }

    fun pageTitle(context: Context) =
        context.getString(R.string.view_page_title_boxes)

    fun pageTitleFoundObjects(context: Context) =
        context.getString(R.string.view_page_title_found_objects)

    fun pageTitleCategories(context: Context) =
        context.getString(R.string.view_page_title_categories)

    fun pageTitleLocations(context: Context) =
        context.getString(R.string.view_page_title_locations)

    fun objectsInBoxTitle(context: Context, boxName: String): String {
        return context.getString(R.string.view_objects_in_box, boxName)
    }

    fun filterLine(
        context: Context,
        query: String,
        now: Date = Date()
    ): String {
        val stamp = SimpleDateFormat(
            "dd/MM/yyyy - HH:mm",
            Locale.getDefault()
        ).format(now)
        return context.getString(R.string.view_filter_line, query, stamp)
    }

    fun countBoxes(context: Context, count: Int): String {
        return context.getString(R.string.view_count_boxes, count)
    }

    fun countObjects(context: Context, count: Int): String {
        return context.getString(R.string.view_count_objects, count)
    }

    fun countCategories(context: Context, count: Int): String {
        return context.getString(R.string.view_count_categories, count)
    }

    fun countLocations(context: Context, count: Int): String {
        return context.getString(R.string.view_count_locations, count)
    }

    const val PAGE_TITLE =
        "Contenitori - Gestione Contenitori e loro contenuti"

    const val PAGE_TITLE_FOUND_OBJECTS =
        "Lista Oggetti Trovati - Risultati ricerca archivio"

    const val PAGE_TITLE_CATEGORIES =
        "Categorie - Classificazione Contenitori"

    const val PAGE_TITLE_LOCATIONS =
        "Posizione - Luoghi abituali di custodia"

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

    fun countCategories(count: Int): String {
        return "N. Categorie: $count"
    }

    fun countLocations(count: Int): String {
        return "N. Posizioni: $count"
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
