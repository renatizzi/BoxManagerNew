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
            ?.takeIf { it.isNotBlank() && !looksLikeUri(it) }
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

        val relative = documentId
            .substringAfter(':', documentId)
            .replace('\\', '/')
            .trim('/')

        val decoded = decodePath(relative)

        return decoded.takeIf { it.isNotBlank() && !looksLikeUri(it) }
            ?: fallback.takeUnless { looksLikeUri(it) }
            ?: "Cartella selezionata"
    }

    private fun decodePath(value: String): String {
        if (value.isBlank()) {
            return value
        }
        return try {
            URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        } catch (_: Exception) {
            value
        }
    }

    private fun looksLikeUri(value: String): Boolean {
        return value.startsWith("content://", ignoreCase = true) ||
            value.contains("%3A", ignoreCase = true) &&
            value.contains("documents/tree", ignoreCase = true)
    }
}
