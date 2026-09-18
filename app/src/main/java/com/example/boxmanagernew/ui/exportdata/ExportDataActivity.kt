package com.example.boxmanagernew.ui.exportdata

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.storage.OpenStorageTreeContract
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import com.example.boxmanagernew.storage.StorageFolderPicker
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.ui.family.FamilyExportCoordinator
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.google.android.material.card.MaterialCardView

/**
 * Utility → Esporta dati (T5): CSV V1 senza foto, oppure ZIP con id + foto.
 */
class ExportDataActivity : BaseActivity() {

    private lateinit var viewModel: ExportDataViewModel
    private lateinit var exportPersister: ViewExportPersister
    private lateinit var exportCoordinator: FamilyExportCoordinator
    private lateinit var tvMessages: TextView
    private lateinit var btnExportCsv: MaterialCardView
    private lateinit var btnExportZip: MaterialCardView

    private val folderPicker =
        registerForActivityResult(
            OpenStorageTreeContract()
        ) { uri ->
            if (uri != null) {
                exportCoordinator.onFolderChosen(uri)
            } else {
                exportCoordinator.cancelPending()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            !ArchivioCompletoNav.allowActivity(
                this,
                PremiumFeature.EXPORT
            )
        ) {
            return
        }

        setContentView(R.layout.activity_export_data)

        setupAppShell()
        setupPageHeader(
            title = getString(R.string.page_export_title),
            subtitle = getString(R.string.page_export_subtitle)
        )
        setupBottomNav()

        exportPersister = ViewExportPersister(
            this,
            StorageFolderConfiguration.KEY_IMPORT_EXPORT
        )
        exportCoordinator = FamilyExportCoordinator(
            activity = this,
            persister = exportPersister,
            onFolderInaccessible = {
                showMessage(
                    getString(R.string.msg_folder_inaccessible),
                    blocking = true
                )
            },
            onExportCompleted = {
                AlertDialog.Builder(this)
                    .setMessage(R.string.export_msg_completed)
                    .setPositiveButton(R.string.common_ok, null)
                    .show()
            },
            launchFolderPicker = {
                StorageFolderPicker.choose(this, folderPicker)
            }
        )

        val db = DatabaseProvider.getDatabase(applicationContext)
        viewModel = ViewModelProvider(
            this,
            ExportDataViewModelFactory(
                applicationContext,
                db,
                BoxRepositoryImpl(db.boxDao()),
                ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao()),
                db.objectTypeDao()
            )
        )[ExportDataViewModel::class.java]

        tvMessages = findViewById(R.id.tvMessages)
        btnExportCsv = findViewById(R.id.btnExportCsv)
        btnExportZip = findViewById(R.id.btnExportZip)

        btnExportCsv.setOnClickListener { viewModel.requestCsvExport() }
        btnExportZip.setOnClickListener { viewModel.requestZipExport() }

        viewModel.busy.observe(this) { busy ->
            btnExportCsv.isEnabled = !busy
            btnExportZip.isEnabled = !busy
        }

        viewModel.exportReady.observe(this) { ready ->
            if (ready == null) return@observe
            val summary = ready.photoSummary
            if (summary.isNullOrBlank()) {
                beginPersist(ready)
            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.page_export_title)
                    .setMessage(summary)
                    .setPositiveButton(R.string.common_yes) { _, _ ->
                        beginPersist(ready)
                    }
                    .setNegativeButton(R.string.common_no) { _, _ ->
                        viewModel.clearExportReady()
                    }
                    .show()
            }
        }
    }

    private fun beginPersist(ready: ExportDataViewModel.ExportReady) {
        viewModel.clearExportReady()
        exportCoordinator.beginExport(ready.fileName, ready.bytes)
    }

    private fun showMessage(text: String, blocking: Boolean) {
        tvMessages.text = text
        if (blocking) {
            FeedbackUtils.alert(this)
        }
    }
}
