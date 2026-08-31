package com.example.boxmanagernew.ui.common

import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object SafFolderLabel {

    fun of(
        treeUri: Uri,
        tree: DocumentFile
    ): String {

        val fallback = tree.name
            ?.takeIf { it.isNotBlank() && !looksLikeEncodedUri(it) }
            ?: "Cartella selezionata"

        val docId = try {
            DocumentsContract.getTreeDocumentId(treeUri)
        } catch (_: Exception) {
            return fallback
        }

        return fromDocumentId(docId, fallback)
    }

    fun fromDocumentId(
        documentId: String,
        fallback: String
    ): String {

        val fromEncoded = extractEncodedDocumentPath(documentId)
        if (fromEncoded != null) {
            return fromEncoded
        }

        val relative = documentId
            .substringAfter(':', documentId)
            .replace('\\', '/')
            .trim('/')

        val decoded = decodePath(relative)

        return decoded.takeIf { it.isNotBlank() && !looksLikeEncodedUri(it) }
            ?: fallback.takeUnless { looksLikeEncodedUri(it) }
            ?: "Cartella selezionata"
    }

    /**
     * Alcuni provider espongono tree id tipo
     * `acc=1;doc=encoded=...` invece di `primary:Download/...`.
     */
    private fun extractEncodedDocumentPath(documentId: String): String? {
        if (!documentId.contains("encoded=", ignoreCase = true)) {
            return null
        }
        val encodedPart = documentId
            .substringAfter("encoded=", "")
            .substringBefore(';')
            .trim()
        if (encodedPart.isEmpty()) {
            return null
        }
        val decoded = decodePath(encodedPart)
            .replace('\\', '/')
            .trim('/')
        if (decoded.isBlank() || looksLikeEncodedUri(decoded)) {
            return null
        }
        // Spesso l'encoded include volume + path (primary:Download/X) o solo path.
        val relative = if (decoded.contains(':')) {
            decoded.substringAfter(':').trim('/')
        } else {
            decoded
        }
        return relative.takeIf { it.isNotBlank() && !looksLikeEncodedUri(it) }
    }

    private fun decodePath(value: String): String {
        if (value.isBlank()) {
            return value
        }
        return try {
            var current = value
            // Doppio encoding frequente su alcuni SAF provider.
            repeat(2) {
                val next = URLDecoder.decode(current, StandardCharsets.UTF_8.name())
                if (next == current) {
                    return@repeat
                }
                current = next
            }
            current
        } catch (_: Exception) {
            value
        }
    }

    private fun looksLikeEncodedUri(value: String): Boolean {
        val lower = value.lowercase()
        return lower.startsWith("content://") ||
            lower.contains("documents/tree") ||
            (
                lower.contains("acc=") &&
                    lower.contains("doc=")
                ) ||
            (
                value.contains("%3A", ignoreCase = true) &&
                    lower.contains("documents/tree")
                )
    }
}
