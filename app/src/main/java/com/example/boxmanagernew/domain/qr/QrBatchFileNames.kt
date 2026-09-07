package com.example.boxmanagernew.domain.qr

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Nome file PDF batch: `QR_ddMMyy_HHmm.pdf` (REQUISITI_QR_AVANZATO R7).
 */
object QrBatchFileNames {

    const val PREFIX = "QR_"
    const val EXTENSION = ".pdf"

    fun proposed(now: Date = Date()): String {
        val stamp =
            SimpleDateFormat("ddMMyy_HHmm", Locale.ITALY).format(now)
        return PREFIX + stamp + EXTENSION
    }

    fun force(
        fileName: String,
        blankDefault: String = proposed()
    ): String {
        var stem = fileName.trim().ifBlank { blankDefault.trim() }
        while (stem.endsWith(EXTENSION, ignoreCase = true)) {
            stem = stem.substring(0, stem.length - EXTENSION.length).trimEnd()
        }
        val dot = stem.lastIndexOf('.')
        if (dot > 0) {
            val ext = stem.substring(dot + 1)
            if (ext.isNotEmpty() && ext.length <= 5 && ext.all { it.isLetterOrDigit() }) {
                stem = stem.substring(0, dot).trimEnd()
            }
        }
        if (stem.isBlank()) {
            stem = blankDefault.removeSuffix(EXTENSION).ifBlank { "QR" }
        }
        return stem + EXTENSION
    }
}
