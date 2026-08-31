package com.example.boxmanagernew.ui.family

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.ui.common.SafFolderLabel
import java.io.File

class FamilyCatalogPersister(
    private val context: Context
) {

    data class Result(
        val success: Boolean,
        val folderInaccessible: Boolean = false,
        val fileName: String = "",
        val folderName: String = ""
    )

    fun persist(
        treeUri: Uri,
        fileName: String,
        bytes: ByteArray
    ): Result {
        val tree = DocumentFile.fromTreeUri(context, treeUri)
        if (tree == null || !tree.canWrite()) {
            return Result(success = false, folderInaccessible = true)
        }

        var temp: File? = null
        var created: DocumentFile? = null

        try {
            temp = File.createTempFile(
                "family_catalog_",
                FamilyCatalogConfiguration.FILE_EXTENSION,
                context.cacheDir
            )
            temp.writeBytes(bytes)

            val existing = tree.listFiles().firstOrNull { child ->
                child.name.equals(fileName, ignoreCase = true)
            }
            if (existing != null && !existing.delete()) {
                return Result(success = false)
            }

            created = tree.createFile(
                FamilyCatalogConfiguration.CSV_MIME_TYPE,
                fileName
            ) ?: return Result(success = false)

            context.contentResolver.openOutputStream(created.uri)?.use { output ->
                temp.inputStream().use { input -> input.copyTo(output) }
            } ?: return Result(success = false)

            return Result(
                success = true,
                fileName = fileName,
                folderName = SafFolderLabel.of(context, treeUri, tree)
            )
        } catch (_: Exception) {
            created?.delete()
            return Result(success = false)
        } finally {
            temp?.delete()
        }
    }

    fun readText(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            }
        } catch (_: Exception) {
            null
        }
    }
}
