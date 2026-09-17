package com.example.boxmanagernew.viewoutput.persist

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.ui.common.SafFolderLabel
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import java.io.File

class ViewExportPersister(
    private val context: Context,
    private val folderUriKey: String = StorageFolderConfiguration.KEY_IMPORT_EXPORT
) {

    data class Result(
        val success: Boolean,
        val folderInaccessible: Boolean = false
    )

    fun folderDisplayName(treeUri: Uri): String? {

        val tree = tree(treeUri) ?: return null

        if (!tree.canWrite()) {
            return null
        }

        return SafFolderLabel.of(context, treeUri, tree)
    }

    fun rememberedFolderUri(): Uri? {

        val saved =
            context.getSharedPreferences(
                BackupConfiguration.PREFS_NAME,
                Context.MODE_PRIVATE
            ).getString(
                folderUriKey,
                null
            ) ?: return null

        return try {
            Uri.parse(saved)
        } catch (_: Exception) {
            null
        }
    }

    fun rememberFolder(uri: Uri) {

        context.getSharedPreferences(
            BackupConfiguration.PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(
                folderUriKey,
                uri.toString()
            )
            .apply()
    }

    fun existingFile(
        treeUri: Uri,
        fileName: String
    ): DocumentFile? {

        val tree = tree(treeUri) ?: return null
        val resolved = resolveExportFileName(fileName)

        val byExact =
            tree.findFile(resolved)
                ?: tree.findFile(stemOf(resolved))

        if (
            byExact != null &&
            byExact.isFile &&
            namesMatch(byExact.name.orEmpty(), resolved)
        ) {
            return byExact
        }

        return tree.listFiles().firstOrNull { child ->
            if (!child.isFile) {
                return@firstOrNull false
            }
            val name = child.name ?: return@firstOrNull false
            namesMatch(name, resolved)
        } ?: existingFileFromQuery(treeUri, resolved)
    }

    private fun existingFileFromQuery(
        treeUri: Uri,
        resolvedName: String
    ): DocumentFile? {

        val treeId =
            try {
                DocumentsContract.getTreeDocumentId(treeUri)
            } catch (_: Exception) {
                return null
            }

        val childrenUri =
            DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri,
                treeId
            )

        val cursor =
            try {
                context.contentResolver.query(
                    childrenUri,
                    arrayOf(
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE
                    ),
                    null,
                    null,
                    null
                )
            } catch (_: Exception) {
                null
            } ?: return null

        cursor.use { rows ->
            while (rows.moveToNext()) {
                val mime = rows.getString(2)
                if (
                    mime == DocumentsContract.Document.MIME_TYPE_DIR
                ) {
                    continue
                }
                val name = rows.getString(1) ?: continue
                if (!namesMatch(name, resolvedName)) {
                    continue
                }
                val documentId = rows.getString(0) ?: continue
                val documentUri =
                    DocumentsContract.buildDocumentUriUsingTree(
                        treeUri,
                        documentId
                    )
                return DocumentFile.fromSingleUri(
                    context,
                    documentUri
                )
            }
        }

        return null
    }

    fun persist(
        treeUri: Uri,
        fileName: String,
        bytes: ByteArray,
        overwrite: Boolean
    ): Result {

        val tree = tree(treeUri)

        if (tree == null || !tree.canWrite()) {
            return Result(
                success = false,
                folderInaccessible = true
            )
        }

        val resolved = resolveExportFileName(fileName)
        val zip = isZipFileName(resolved)
        val existing = existingFile(treeUri, resolved)
        var temp: File? = null
        var created: DocumentFile? = null

        try {

            temp = File.createTempFile(
                "view_export_",
                if (zip) ".zip" else ImportConfiguration.FILE_EXTENSION,
                context.cacheDir
            )

            temp.writeBytes(bytes)

            if (overwrite && existing != null) {
                if (!existing.delete()) {
                    return writeFailed()
                }
            }

            // Nome con estensione: su disco di rete il MIME da solo non basta.
            created = tree.createFile(
                if (zip) {
                    BackupConfiguration.ZIP_MIME_TYPE
                } else {
                    ImportConfiguration.CSV_MIME_TYPE
                },
                resolved
            ) ?: return writeFailed()

            copyToDocument(temp, created.uri)
                ?: return writeFailed(created)

            return Result(success = true)

        } catch (_: Exception) {

            return writeFailed(created)

        } finally {

            temp?.delete()
        }
    }

    /** CSV → force `.csv`; ZIP → keep/append `.zip` (T4 Invia Archivio). */
    fun resolveExportFileName(fileName: String): String {
        return if (isZipFileName(fileName)) {
            val trimmed = fileName.trim()
            if (trimmed.endsWith(".zip", ignoreCase = true)) {
                trimmed
            } else {
                trimmed + ".zip"
            }
        } else {
            ViewOutputConfiguration.csvFileName(fileName)
        }
    }

    private fun isZipFileName(fileName: String): Boolean {
        return fileName.trim().endsWith(".zip", ignoreCase = true)
    }

    private fun stemOf(fileName: String): String {
        val trimmed = fileName.trim()
        val dot = trimmed.lastIndexOf('.')
        return if (dot > 0) trimmed.substring(0, dot) else trimmed
    }

    private fun namesMatch(left: String, right: String): Boolean {
        if (isZipFileName(left) || isZipFileName(right)) {
            return stemOf(left).equals(stemOf(right), ignoreCase = true)
        }
        return ViewOutputConfiguration.csvNamesMatch(left, right)
    }

    private fun copyToDocument(
        source: File,
        destination: Uri
    ): Boolean? {

        val resolver: ContentResolver = context.contentResolver

        val output = resolver.openOutputStream(destination)
            ?: return null

        return try {

            source.inputStream().use { input ->
                output.use { stream ->
                    input.copyTo(stream)
                    stream.flush()
                }
            }

            true

        } catch (_: Exception) {

            null
        }
    }

    private fun writeFailed(
        partial: DocumentFile? = null
    ): Result {

        partial?.delete()

        return Result(success = false)
    }

    private fun tree(treeUri: Uri): DocumentFile? {
        return DocumentFile.fromTreeUri(context, treeUri)
    }
}
