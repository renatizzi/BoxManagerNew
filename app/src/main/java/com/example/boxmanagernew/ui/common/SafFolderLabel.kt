package com.example.boxmanagernew.ui.common

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object SafFolderLabel {

    const val DEFAULT_LABEL = "Cartella selezionata"

    fun of(
        context: Context,
        treeUri: Uri,
        tree: DocumentFile
    ): String {
        return of(context.contentResolver, treeUri, tree)
    }

    fun of(
        resolver: ContentResolver,
        treeUri: Uri,
        tree: DocumentFile
    ): String {
        queryDisplayName(resolver, treeUri)?.let { return it }
        queryDisplayName(resolver, tree.uri)?.let { return it }

        val documentId = try {
            DocumentsContract.getTreeDocumentId(treeUri)
        } catch (_: Exception) {
            null
        }

        if (!documentId.isNullOrBlank()) {
            val docUri =
                DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
            queryDisplayName(resolver, docUri)?.let { return it }
        }

        val treeName = tree.name?.trim().orEmpty()
        if (isReadableLabel(treeName)) {
            return treeName
        }

        return fromDocumentId(
            documentId.orEmpty(),
            DEFAULT_LABEL
        )
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

        return decoded.takeIf { isReadableLabel(it) }
            ?: fallback.takeIf { isReadableLabel(it) }
            ?: DEFAULT_LABEL
    }

    fun isReadableLabel(value: String?): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }
        val trimmed = value.trim()
        if (looksLikeEncodedUri(trimmed)) {
            return false
        }
        if (looksLikeOpaqueId(trimmed)) {
            return false
        }
        return true
    }

    private fun queryDisplayName(
        resolver: ContentResolver,
        uri: Uri?
    ): String? {
        if (uri == null) {
            return null
        }
        return try {
            resolver.query(
                uri,
                arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (!cursor.moveToFirst()) {
                    return null
                }
                val index =
                    cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (index < 0) {
                    return null
                }
                cursor.getString(index)
                    ?.trim()
                    ?.takeIf { isReadableLabel(it) }
            }
        } catch (_: Exception) {
            null
        }
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
        if (!isReadableLabel(decoded)) {
            return null
        }
        val relative = if (decoded.contains(':')) {
            decoded.substringAfter(':').trim('/')
        } else {
            decoded
        }
        return relative.takeIf { isReadableLabel(it) }
    }

    private fun decodePath(value: String): String {
        if (value.isBlank()) {
            return value
        }
        return try {
            var current = value
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

    /**
     * ID documento opaco (es. base64 Google Drive) non adatto all'UI.
     */
    internal fun looksLikeOpaqueId(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.length < 28) {
            return false
        }
        if (trimmed.contains('/') || trimmed.contains(' ')) {
            return false
        }
        if (!trimmed.matches(OPAQUE_ID_PATTERN)) {
            return false
        }
        return true
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

    private val OPAQUE_ID_PATTERN =
        Regex("^[A-Za-z0-9+/=_-]+$")
}
