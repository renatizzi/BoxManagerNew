package com.example.boxmanagernew.ui.exportdata

import android.os.Bundle
import android.widget.RadioGroup
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
 * Utility → Esporta dati (T5 + B–C B1): CSV / ZIP; profilo Intero o Selezione.
 */
class ExportDataActivity : BaseActivity() {

    private lateinit var viewModel: ExportDataViewModel
    private lateinit var exportPersister: ViewExportPersister
    private lateinit var exportCoordinator: FamilyExportCoordinator
    private lateinit var tvMessages: TextView
    private lateinit var btnExportCsv: MaterialCardView
    private lateinit var btnExportZip: MaterialCardView
    private lateinit var radioScope: RadioGroup

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
        radioScope = findViewById(R.id.radioExportScope)

        radioScope.setOnCheckedChangeListener { _, checkedId ->
            viewModel.scope =
                if (checkedId == R.id.radioScopeSelection) {
                    ExportDataViewModel.Scope.SELECTION
                } else {
                    ExportDataViewModel.Scope.ALL
                }
        }

        btnExportCsv.setOnClickListener { viewModel.requestCsvExport() }
        btnExportZip.setOnClickListener { viewModel.requestZipExport() }

        viewModel.busy.observe(this) { busy ->
            btnExportCsv.isEnabled = !busy
            btnExportZip.isEnabled = !busy
            radioScope.isEnabled = !busy
        }

        viewModel.message.observe(this) { text ->
            if (!text.isNullOrBlank()) {
                showMessage(text, blocking = true)
            }
        }

        viewModel.pickBoxes.observe(this) { pick ->
            if (pick == null) return@observe
            showBoxPicker(pick.first, pick.second)
            viewModel.clearPickBoxes()
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

    private fun showBoxPicker(
        format: ExportDataViewModel.Format,
        choices: List<ExportDataViewModel.BoxChoice>
    ) {
        val labels = choices.map { it.name }.toTypedArray()
        val checked = BooleanArray(choices.size)
        AlertDialog.Builder(this)
            .setTitle(R.string.export_pick_boxes_title)
            .setMultiChoiceItems(labels, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton(R.string.common_ok) { _, _ ->
                val selected = choices
                    .filterIndexed { index, _ -> checked[index] }
                    .map { it.id }
                    .toSet()
                viewModel.confirmSelection(format, selected)
            }
            .setNeutralButton(R.string.export_pick_all) { _, _ ->
                viewModel.confirmSelection(
                    format,
                    choices.map { it.id }.toSet()
                )
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
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
