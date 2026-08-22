package com.example.boxmanagernew.viewoutput.persist

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.ui.common.SafFolderLabel
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import java.io.File

class ViewExportPersister(
    private val context: Context
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

        return SafFolderLabel.of(treeUri, tree)
    }

    fun existingFile(
        treeUri: Uri,
        fileName: String
    ): DocumentFile? {

        val tree = tree(treeUri) ?: return null
        val csvName = ViewOutputConfiguration.csvFileName(fileName)

        return tree.listFiles().firstOrNull { child ->
            val name = child.name ?: return@firstOrNull false
            name.equals(csvName, ignoreCase = true)
        }
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

        val csvName = ViewOutputConfiguration.csvFileName(fileName)
        val existing = existingFile(treeUri, csvName)
        var temp: File? = null
        var created: DocumentFile? = null

        try {

            temp = File.createTempFile(
                "view_export_",
                ImportConfiguration.FILE_EXTENSION,
                context.cacheDir
            )

            temp.writeBytes(bytes)

            if (overwrite && existing != null) {
                if (!existing.delete()) {
                    return writeFailed()
                }
            }

            val baseName =
                csvName.substringBeforeLast(
                    '.',
                    csvName
                )

            created = tree.createFile(
                ImportConfiguration.CSV_MIME_TYPE,
                baseName
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
