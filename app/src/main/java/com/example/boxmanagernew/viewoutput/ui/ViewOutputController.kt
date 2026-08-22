package com.example.boxmanagernew.viewoutput.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.csv.ViewExportCsvBuilder
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot
import com.example.boxmanagernew.viewoutput.model.ViewPrintHeader
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.example.boxmanagernew.viewoutput.print.ViewPrintAdapter
import com.example.boxmanagernew.viewoutput.print.ViewPrintPdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewOutputController(
    private val activity: AppCompatActivity,
    private val persister: ViewExportPersister,
    private val showFolderInaccessible: () -> Unit,
    private val launchFolderPicker: () -> Unit
) {

    private var pendingCsvBytes: ByteArray? = null

    fun inflateActions(
        container: FrameLayout,
        onPrint: () -> Unit,
        onExport: () -> Unit
    ) {

        val actions =
            LayoutInflater.from(activity)
                .inflate(
                    R.layout.layout_header_print_export,
                    container,
                    false
                )

        container.addView(actions)

        actions.findViewById<View>(
            R.id.btnPrintView
        ).setOnClickListener {
            onPrint()
        }

        actions.findViewById<View>(
            R.id.btnExportView
        ).setOnClickListener {
            onExport()
        }
    }

    fun inflatePrintOnly(
        container: FrameLayout,
        onPrint: () -> Unit
    ) {

        inflateActions(
            container,
            onPrint,
            onExport = {}
        )
        container.findViewById<View>(
            R.id.btnExportView
        )?.visibility = View.GONE
    }

    fun print(
        snapshot: ContainerViewSnapshot,
        header: ViewPrintHeader
    ) {

        activity.lifecycleScope.launch {

            val result =
                withContext(
                    Dispatchers.Default
                ) {

                    ViewPrintPdf.toBytes(
                        activity,
                        snapshot,
                        header
                    )
                }

            val printManager =
                activity.getSystemService(
                    Context.PRINT_SERVICE
                ) as? PrintManager
                    ?: return@launch

            try {
                printManager.print(
                    "Stampa",
                    ViewPrintAdapter(
                        result.bytes,
                        result.pageCount
                    ),
                    PrintAttributes.Builder()
                        .setMediaSize(
                            PrintAttributes.MediaSize.ISO_A4
                        )
                        .setColorMode(
                            PrintAttributes.COLOR_MODE_MONOCHROME
                        )
                        .build()
                )
            } catch (_: Exception) {
                return@launch
            }
        }
    }

    fun export(
        snapshot: ContainerViewSnapshot
    ) {

        activity.lifecycleScope.launch {

            val bytes =
                withContext(
                    Dispatchers.Default
                ) {

                    ViewExportCsvBuilder().build(
                        snapshot
                    )
                }

            pendingCsvBytes = bytes

            val saved =
                persister.rememberedFolderUri()

            if (
                saved != null &&
                persister.folderDisplayName(saved) != null
            ) {
                askExportFileName(saved, bytes)
            } else {
                launchFolderPicker()
            }
        }
    }

    fun onFolderChosen(
        uri: Uri
    ) {

        val bytes = pendingCsvBytes
        if (bytes == null) {
            return
        }

        try {
            activity.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: Exception) {
        }

        if (persister.folderDisplayName(uri) == null) {
            pendingCsvBytes = null
            FeedbackUtils.alert(activity)
            showFolderInaccessible()
            return
        }

        persister.rememberFolder(uri)
        askExportFileName(uri, bytes)
    }

    private fun askExportFileName(
        uri: Uri,
        bytes: ByteArray
    ) {

        DialogUtils.showExportFileName(
            activity,
            ViewOutputConfiguration.proposedFileName(),
            exists = { fileName ->
                persister.existingFile(uri, fileName) != null
            },
            onSave = { fileName, overwrite ->
                writeExport(
                    uri,
                    bytes,
                    fileName,
                    overwrite
                )
            }
        )
    }

    private fun writeExport(
        uri: Uri,
        bytes: ByteArray,
        fileName: String,
        overwrite: Boolean
    ) {

        val result =
            persister.persist(
                uri,
                fileName,
                bytes,
                overwrite
            )

        pendingCsvBytes = null

        if (result.folderInaccessible) {
            FeedbackUtils.alert(activity)
            showFolderInaccessible()
        }
    }
}
