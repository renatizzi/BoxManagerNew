package com.example.boxmanagernew.ui.backup

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.constants.BackupConstants
import com.example.boxmanagernew.backup.zip.BackupZipWriter
import java.io.File

class BackupZipPersister(
    private val context: Context,
    private val zipWriter: BackupZipWriter = BackupZipWriter()
) {

    data class Result(
        val success: Boolean,
        val folderInaccessible: Boolean = false,
        val fileName: String = "",
        val folderName: String = "",
        val sizeBytes: Long = 0L
    )

    fun folderDisplayName(treeUri: Uri): String? {

        val tree = tree(treeUri) ?: return null

        if (!tree.canWrite()) {
            return null
        }

        return tree.name?.takeIf { it.isNotBlank() }
            ?: "Cartella selezionata"
    }

    data class ZipFileItem(
        val uri: Uri,
        val name: String,
        val lastModified: Long
    )

    fun listZipFiles(
        treeUri: Uri
    ): List<ZipFileItem> {

        val tree = tree(treeUri) ?: return emptyList()

        return tree.listFiles()
            .filter { file ->
                file.isFile &&
                        file.name?.endsWith(
                            BackupConfiguration.BACKUP_FILE_EXTENSION,
                            ignoreCase = true
                        ) == true
            }
            .sortedByDescending { it.lastModified() }
            .map { file ->
                ZipFileItem(
                    uri = file.uri,
                    name = file.name ?: "backup.zip",
                    lastModified = file.lastModified()
                )
            }
    }

    fun existingFile(
        treeUri: Uri,
        fileName: String
    ): DocumentFile? {

        val tree = tree(treeUri) ?: return null
        val zipName = zipFileName(fileName)

        return tree.listFiles().firstOrNull { child ->
            val name = child.name ?: return@firstOrNull false
            name.equals(zipName, ignoreCase = true)
        }
    }

    fun persist(
        treeUri: Uri,
        fileName: String,
        entries: Map<String, ByteArray>,
        overwrite: Boolean
    ): Result {

        val tree = tree(treeUri)

        if (tree == null || !tree.canWrite()) {
            return Result(
                success = false,
                folderInaccessible = true
            )
        }

        val zipName = zipFileName(fileName)
        val existing = existingFile(treeUri, zipName)
        var temp: File? = null
        var created: DocumentFile? = null

        try {

            temp = File.createTempFile(
                BackupConstants.TEMP_FILE_PREFIX,
                BackupConfiguration.BACKUP_FILE_EXTENSION,
                context.cacheDir
            )

            zipWriter.write(temp, entries)

            val size = temp.length()

            if (overwrite && existing != null) {
                if (!existing.delete()) {
                    return writeFailed()
                }
            }

            val baseName =
                zipName.removeSuffix(
                    BackupConfiguration.BACKUP_FILE_EXTENSION
                )

            created = tree.createFile(
                BackupConfiguration.ZIP_MIME_TYPE,
                baseName
            ) ?: return writeFailed()

            copyToDocument(temp, created.uri)
                ?: return writeFailed(created)

            return Result(
                success = true,
                fileName = created.name ?: zipName,
                folderName = tree.name?.takeIf { it.isNotBlank() }
                    ?: "Cartella selezionata",
                sizeBytes = size
            )

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

    companion object {

        fun zipFileName(fileName: String): String {

            val trimmed = fileName.trim()
            val extension = BackupConfiguration.BACKUP_FILE_EXTENSION

            return if (
                trimmed.endsWith(extension, ignoreCase = true)
            ) {
                trimmed
            } else {
                trimmed + extension
            }
        }
    }
}
