package com.example.boxmanagernew.ui.family

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister

/**
 * Export condivisione archivio: cartella dedicata + box nome file standard.
 */
class FamilyExportCoordinator(
    private val activity: AppCompatActivity,
    private val persister: ViewExportPersister = ViewExportPersister(
        activity,
        StorageFolderConfiguration.KEY_FAMILY_SHARE
    ),
    private val onFolderInaccessible: () -> Unit,
    private val onExportCompleted: () -> Unit,
    private val launchFolderPicker: () -> Unit
) {

    private var pendingBytes: ByteArray? = null
    private var pendingDefaultName: String = ""

    fun beginExport(
        defaultFileName: String,
        bytes: ByteArray
    ) {
        pendingDefaultName = defaultFileName
        pendingBytes = bytes

        val saved = persister.rememberedFolderUri()
        if (
            saved != null &&
            persister.folderDisplayName(saved) != null
        ) {
            askExportFileName(saved)
        } else {
            launchFolderPicker()
        }
    }

    fun onFolderChosen(uri: Uri) {
        val bytes = pendingBytes ?: return

        try {
            activity.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: Exception) {
            // Cartella usabile per la sessione anche senza persist.
        }

        if (persister.folderDisplayName(uri) == null) {
            clearPending()
            FeedbackUtils.alert(activity)
            onFolderInaccessible()
            return
        }

        persister.rememberFolder(uri)
        askExportFileName(uri)
    }

    fun cancelPending() {
        clearPending()
    }

    private fun askExportFileName(uri: Uri) {
        val bytes = pendingBytes ?: return
        DialogUtils.showExportFileName(
            activity,
            ViewOutputConfiguration.csvFileName(pendingDefaultName),
            exists = { fileName ->
                persister.existingFile(uri, fileName) != null
            },
            onSave = { fileName, overwrite ->
                writeExport(uri, bytes, fileName, overwrite)
            }
        )
    }

    private fun writeExport(
        uri: Uri,
        bytes: ByteArray,
        fileName: String,
        overwrite: Boolean
    ) {
        val result = persister.persist(
            uri,
            fileName,
            bytes,
            overwrite
        )
        clearPending()

        when {
            result.folderInaccessible -> {
                FeedbackUtils.alert(activity)
                onFolderInaccessible()
            }
            result.success -> onExportCompleted()
            else -> {
                FeedbackUtils.alert(activity)
                onFolderInaccessible()
            }
        }
    }

    private fun clearPending() {
        pendingBytes = null
        pendingDefaultName = ""
    }

    companion object {
        const val MSG_EXPORT_COMPLETED = "Salvataggio completato."
        const val MSG_FOLDER_INACCESSIBLE =
            BackupConfiguration.MSG_FOLDER_INACCESSIBLE
        const val MSG_WRITE_FAILED = "Salvataggio non riuscito. Riprovare."
    }
}
