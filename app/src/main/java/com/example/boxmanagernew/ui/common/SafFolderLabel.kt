package com.example.boxmanagernew.ui.common

import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile

object SafFolderLabel {

    fun of(
        treeUri: Uri,
        tree: DocumentFile
    ): String {

        val fallback = tree.name?.takeIf { it.isNotBlank() }
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

        return relative.takeIf { it.isNotBlank() } ?: fallback
    }
}
