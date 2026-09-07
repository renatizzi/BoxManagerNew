package com.example.boxmanagernew.storage

/**
 * Nomi file CSV: estensione sempre `.csv`, come [BackupZipPersister.zipFileName] per `.zip`.
 * Omissione o sostituzione da parte dell'utente non devono produrre file non riconoscibili.
 */
object CsvFileNames {

    const val EXTENSION = ".csv"

    /**
     * Restituisce sempre `stem.csv` (estensione minuscola).
     * - senza estensione → aggiunge `.csv`
     * - già `.csv` / `.CSV` / `.csv.csv` → un solo `.csv`
     * - altra estensione breve (es. `.txt`) → sostituita con `.csv`
     */
    fun force(
        fileName: String,
        blankDefault: String
    ): String {
        var stem = fileName.trim().ifBlank { blankDefault.trim() }
        stem = stripTrailingCsv(stem)
        stem = stripAlternateExtension(stem)
        if (stem.isBlank()) {
            stem = stripTrailingCsv(blankDefault.trim()).ifBlank { "export" }
        }
        return stem + EXTENSION
    }

    fun stem(
        fileName: String,
        blankDefault: String = "export"
    ): String {
        val forced = force(fileName, blankDefault)
        return forced.substring(0, forced.length - EXTENSION.length)
    }

    fun endsWithCsv(fileName: String): Boolean {
        return fileName.trim().endsWith(EXTENSION, ignoreCase = true)
    }

    /**
     * Riconoscimento tollerante in lettura (allineato allo spirito di Ripristina ZIP):
     * nome con `.csv` oppure MIME CSV/testo senza altra estensione nota.
     */
    fun isLikelyCsv(
        fileName: String?,
        mimeType: String? = null
    ): Boolean {
        val name = fileName.orEmpty().trim()
        if (endsWithCsv(name)) {
            return true
        }
        val mime = mimeType.orEmpty()
        val csvMime =
            mime.equals("text/csv", ignoreCase = true) ||
                mime.equals("text/comma-separated-values", ignoreCase = true) ||
                mime.equals("text/plain", ignoreCase = true)
        if (!csvMime) {
            return false
        }
        // MIME CSV/testo ma nome con altra estensione (es. .zip) → no
        val dot = name.lastIndexOf('.')
        if (dot > 0) {
            val ext = name.substring(dot)
            if (ext.isNotEmpty() && !ext.equals(EXTENSION, ignoreCase = true)) {
                return false
            }
        }
        return name.isNotEmpty()
    }

    private fun stripTrailingCsv(name: String): String {
        var stem = name
        while (stem.endsWith(EXTENSION, ignoreCase = true)) {
            stem = stem.substring(
                0,
                stem.length - EXTENSION.length
            ).trimEnd()
        }
        return stem
    }

    private fun stripAlternateExtension(name: String): String {
        val dot = name.lastIndexOf('.')
        if (dot <= 0) {
            return name
        }
        val ext = name.substring(dot + 1)
        if (ext.isEmpty() || ext.length > 5) {
            return name
        }
        if (!ext.all { it.isLetterOrDigit() }) {
            return name
        }
        return name.substring(0, dot).trimEnd()
    }
}
