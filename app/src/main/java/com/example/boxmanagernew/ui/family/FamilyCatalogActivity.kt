package com.example.boxmanagernew.ui.family

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.config.FamilySharedTablesConfiguration
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.google.android.material.card.MaterialCardView
import com.example.boxmanagernew.storage.OpenStorageTreeContract
import com.example.boxmanagernew.storage.StorageFolderPicker

class FamilyCatalogActivity : BaseActivity() {

    private lateinit var mergeViewModel: FamilyMergeViewModel
    private lateinit var persister: FamilyCatalogPersister
    private lateinit var exportPersister: ViewExportPersister
    private lateinit var exportCoordinator: FamilyExportCoordinator
    private lateinit var tvMessages: TextView
    private lateinit var scrollView: ScrollView

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

    private val sharedTablesFilePicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.data?.let { onSharedTablesImportChosen(it) }
            }
        }

    private val archiveFilePicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.data?.let { onArchiveImportChosen(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BuildConfig.FAMILY_BETA) {
            finish()
            return
        }

        setContentView(R.layout.activity_family_catalog)

        setupAppShell()
        setupPageHeader(
            title = getString(R.string.family_page_title),
            subtitle = getString(R.string.family_page_subtitle)
        )
        setupBottomNav()

        persister = FamilyCatalogPersister(this)
        exportPersister = ViewExportPersister(
            this,
            StorageFolderConfiguration.KEY_FAMILY_SHARE
        )
        tvMessages = findViewById(R.id.tvMessages)
        scrollView = findViewById(R.id.familyCatalogScroll)
        exportCoordinator = FamilyExportCoordinator(
            activity = this,
            persister = exportPersister,
            onFolderInaccessible = {
                showUserMessage(
                    getString(R.string.msg_folder_inaccessible),
                    blockingError = true
                )
            },
            onExportCompleted = {
                showExportCompletedDialog()
            },
            launchFolderPicker = {
                StorageFolderPicker.choose(this, folderPicker)
            }
        )

        val db = DatabaseProvider.getDatabase(applicationContext)
        mergeViewModel = ViewModelProvider(
            this,
            FamilyMergeViewModelFactory(
                applicationContext,
                db,
                CategoryRepositoryImpl(db.categoryDao(), db.boxDao()),
                LocationRepositoryImpl(db.locationDao(), db.boxDao()),
                BoxRepositoryImpl(db.boxDao()),
                ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao())
            )
        )[FamilyMergeViewModel::class.java]

        findViewById<MaterialCardView>(R.id.btnExportSharedTables)
            .setOnClickListener { mergeViewModel.requestSharedTablesExport() }

        findViewById<MaterialCardView>(R.id.btnImportSharedTables)
            .setOnClickListener { launchSharedTablesFilePicker() }

        findViewById<MaterialCardView>(R.id.btnExportMerge)
            .setOnClickListener { mergeViewModel.requestArchiveExport() }

        findViewById<MaterialCardView>(R.id.btnImportMerge)
            .setOnClickListener { launchArchiveFilePicker() }

        findViewById<TextView>(R.id.textFamilyIntro).text =
            getString(R.string.family_intro)
        findViewById<TextView>(R.id.textSectionSharedTables).text =
            getString(R.string.family_section_shared_tables)
        findViewById<TextView>(R.id.textSectionSharedTablesHint).text =
            getString(R.string.family_section_shared_tables_hint)
        findViewById<TextView>(R.id.textExportSharedTables).text =
            getString(R.string.family_button_send_shared_tables)
        findViewById<TextView>(R.id.textImportSharedTables).text =
            getString(R.string.family_button_receive_shared_tables)
        findViewById<TextView>(R.id.textSectionArchive).text =
            getString(R.string.family_section_archive)
        findViewById<TextView>(R.id.textSectionArchiveHint).text =
            getString(R.string.family_section_archive_hint)
        findViewById<TextView>(R.id.textExportMerge).text =
            getString(R.string.family_button_send)
        findViewById<TextView>(R.id.textImportMerge).text =
            getString(R.string.family_button_receive)

        mergeViewModel.message.observe(this) { text ->
            showUserMessage(text, blockingError = false, showDialog = true)
        }

        mergeViewModel.exportBytes.observe(this) { payload ->
            if (payload == null) {
                return@observe
            }
            mergeViewModel.clearExport()
            exportCoordinator.beginExport(
                defaultFileName = payload.first,
                bytes = payload.second
            )
        }

        mergeViewModel.sharedTablesPreview.observe(this) { preview ->
            if (preview == null) {
                return@observe
            }
            showSharedTablesPreview(preview)
        }

        mergeViewModel.archivePreview.observe(this) { preview ->
            if (preview == null) {
                return@observe
            }
            showArchivePreview(preview)
        }
    }

    private fun showSharedTablesPreview(
        preview: FamilyMergeViewModel.SharedTablesPreview
    ) {
        AlertDialog.Builder(this)
            .setTitle(R.string.family_dialog_receive_shared_tables)
            .setMessage(preview.summary)
            .setPositiveButton(R.string.common_yes) { _, _ ->
                mergeViewModel.confirmSharedTablesImport()
            }
            .setNegativeButton(R.string.common_no) { _, _ ->
                mergeViewModel.clearSharedTablesPreview()
            }
            .show()
    }

    private fun showArchivePreview(
        preview: FamilyMergeViewModel.ArchivePreview
    ) {
        AlertDialog.Builder(this)
            .setTitle(R.string.family_dialog_receive_archive)
            .setMessage(preview.summary)
            .setPositiveButton(R.string.common_yes) { _, _ ->
                mergeViewModel.confirmArchiveImport()
            }
            .setNegativeButton(R.string.common_no) { _, _ ->
                mergeViewModel.clearArchivePreview()
            }
            .show()
    }

    private fun launchSharedTablesFilePicker() {
        sharedTablesFilePicker.launch(
            buildFamilyFilePickerIntent(
                FamilySharedTablesConfiguration.CSV_MIME_TYPE,
                FamilyCatalogConfiguration.CSV_MIME_TYPE
            )
        )
    }

    private fun launchArchiveFilePicker() {
        archiveFilePicker.launch(
            buildFamilyFilePickerIntent(
                FamilyMergeConfiguration.CSV_MIME_TYPE,
                FamilyCatalogConfiguration.CSV_MIME_TYPE,
                FamilyInventoryConfiguration.CSV_MIME_TYPE
            )
        )
    }

    private fun buildFamilyFilePickerIntent(vararg mimeTypes: String): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(*mimeTypes, "text/*", "*/*")
            )
            exportPersister.rememberedFolderUri()?.let { folderUri ->
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, folderUri)
            }
        }
    }

    private fun onSharedTablesImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            showUserMessage(
                getString(R.string.family_msg_read_failed),
                blockingError = true
            )
            return
        }
        mergeViewModel.importSharedTablesText(text)
    }

    private fun onArchiveImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            showUserMessage(
                getString(R.string.family_msg_read_failed),
                blockingError = true
            )
            return
        }
        mergeViewModel.importArchiveText(text)
    }

    private fun showExportCompletedDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.family_msg_export_completed)
            .setPositiveButton(R.string.common_ok, null)
            .show()
    }

    private fun showUserMessage(
        text: String,
        blockingError: Boolean,
        showDialog: Boolean = false
    ) {
        if (text.isBlank()) {
            tvMessages.visibility = View.GONE
            return
        }
        tvMessages.text = text
        tvMessages.visibility = View.VISIBLE
        scrollView.scrollTo(0, 0)
        if (blockingError) {
            FeedbackUtils.alert(this)
        }
        if (showDialog) {
            AlertDialog.Builder(this)
                .setMessage(text)
                .setPositiveButton(R.string.common_ok, null)
                .show()
        }
    }
}
