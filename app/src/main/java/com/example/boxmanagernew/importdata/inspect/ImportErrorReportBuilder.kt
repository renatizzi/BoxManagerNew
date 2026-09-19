package com.example.boxmanagernew.importdata.inspect

import android.content.Context
import com.example.boxmanagernew.R
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.storage.CsvFileNames
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Report errori import riga-per-riga (R6 / G1): schermata + CSV scaricabile.
 */
object ImportErrorReportBuilder {

    const val COL_SECTION = "sezione"
    const val COL_LINE = "riga"
    const val COL_REASON = "motivo"

    const val FILE_PREFIX = "REPORT_ERRORI_"

    fun fileName(now: Date = Date()): String {
        val stamp = SimpleDateFormat("ddMMyy_HHmm", Locale.getDefault()).format(now)
        return CsvFileNames.force(FILE_PREFIX + stamp, FILE_PREFIX + "report")
    }

    fun toCsvBytes(
        context: Context,
        errors: List<ImportRowError>
    ): ByteArray {
        val body = buildString {
            append(COL_SECTION)
            append(ImportConfiguration.SEPARATOR)
            append(COL_LINE)
            append(ImportConfiguration.SEPARATOR)
            append(COL_REASON)
            append('\n')
            for (error in errors) {
                val section =
                    if (error.section.isBlank()) {
                        context.getString(R.string.import_error_section_file)
                    } else {
                        error.section
                    }
                append(escape(section))
                append(ImportConfiguration.SEPARATOR)
                append(error.line)
                append(ImportConfiguration.SEPARATOR)
                append(escape(localizeReason(context, error.reason)))
                append('\n')
            }
        }
        return ImportConfiguration.UTF8_BOM + body.toByteArray(Charsets.UTF_8)
    }

    fun toScreenText(
        context: Context,
        errors: List<ImportRowError>
    ): String {
        if (errors.isEmpty()) {
            return ""
        }
        return buildString {
            appendLine(context.getString(R.string.import_error_report_heading))
            for (error in errors) {
                val sectionLabel =
                    if (error.section.isBlank()) {
                        context.getString(R.string.import_error_section_file)
                    } else {
                        error.section
                    }
                val reason = localizeReason(context, error.reason)
                if (error.line > 0) {
                    appendLine(
                        context.getString(
                            R.string.import_error_line_format,
                            sectionLabel,
                            error.line,
                            reason
                        )
                    )
                } else {
                    appendLine(
                        context.getString(
                            R.string.import_error_line_no_row,
                            sectionLabel,
                            reason
                        )
                    )
                }
            }
        }.trimEnd()
    }

    fun localizeReason(context: Context, reason: String): String {
        val asDependency = ImportConfiguration.localizeDependency(context, reason)
        if (asDependency != reason) {
            return asDependency
        }
        return ImportConfiguration.localizeCheck(context, reason)
    }

    private fun escape(value: String): String {
        if (
            value.contains(ImportConfiguration.SEPARATOR) ||
            value.contains('"') ||
            value.contains('\n') ||
            value.contains('\r')
        ) {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }
        return value
    }
}
