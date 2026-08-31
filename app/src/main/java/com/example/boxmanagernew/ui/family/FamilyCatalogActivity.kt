package com.example.boxmanagernew.ui.family

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
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
import com.example.boxmanagernew.domain.family.FamilyMergeCopy
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.config.FamilySharedTablesConfiguration
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.google.android.material.card.MaterialCardView

class FamilyCatalogActivity : BaseActivity() {

    private lateinit var mergeViewModel: FamilyMergeViewModel
    private lateinit var persister: FamilyCatalogPersister
    private lateinit var exportPersister: ViewExportPersister
    private lateinit var exportCoordinator: FamilyExportCoordinator
    private lateinit var tvMessages: TextView
    private lateinit var scrollView: ScrollView

    private val folderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
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
            title = FamilyMergeCopy.PAGE_TITLE,
            subtitle = FamilyMergeCopy.PAGE_SUBTITLE
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
                    FamilyMergeCopy.MSG_FOLDER_INACCESSIBLE,
                    blockingError = true
                )
            },
            onExportCompleted = { folderName, fileName ->
                val summary =
                    FamilyMergeCopy.buildExportSummary(folderName, fileName)
                Toast.makeText(this, summary, Toast.LENGTH_LONG).show()
                showUserMessage(
                    summary,
                    blockingError = false,
                    showDialog = false
                )
            },
            launchFolderPicker = {
                folderPicker.launch(exportPersister.rememberedFolderUri())
            }
        )

        val db = DatabaseProvider.getDatabase(applicationContext)
        mergeViewModel = ViewModelProvider(
            this,
            FamilyMergeViewModelFactory(
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
            FamilyMergeCopy.INTRO
        findViewById<TextView>(R.id.textSectionSharedTables).text =
            FamilyMergeCopy.SECTION_SHARED_TABLES
        findViewById<TextView>(R.id.textSectionSharedTablesHint).text =
            FamilyMergeCopy.SECTION_SHARED_TABLES_HINT
        findViewById<TextView>(R.id.textExportSharedTables).text =
            FamilyMergeCopy.BUTTON_SEND_SHARED_TABLES
        findViewById<TextView>(R.id.textImportSharedTables).text =
            FamilyMergeCopy.BUTTON_RECEIVE_SHARED_TABLES
        findViewById<TextView>(R.id.textSectionArchive).text =
            FamilyMergeCopy.SECTION_ARCHIVE
        findViewById<TextView>(R.id.textSectionArchiveHint).text =
            FamilyMergeCopy.SECTION_ARCHIVE_HINT
        findViewById<TextView>(R.id.textExportMerge).text =
            FamilyMergeCopy.BUTTON_SEND
        findViewById<TextView>(R.id.textImportMerge).text =
            FamilyMergeCopy.BUTTON_RECEIVE

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
            .setTitle("Ricevi tabelle condivise")
            .setMessage(preview.summary)
            .setPositiveButton("SI") { _, _ ->
                mergeViewModel.confirmSharedTablesImport()
            }
            .setNegativeButton("NO") { _, _ ->
                mergeViewModel.clearSharedTablesPreview()
            }
            .show()
    }

    private fun showArchivePreview(
        preview: FamilyMergeViewModel.ArchivePreview
    ) {
        AlertDialog.Builder(this)
            .setTitle("Ricevi Archivio")
            .setMessage(preview.summary)
            .setPositiveButton("SI") { _, _ ->
                mergeViewModel.confirmArchiveImport()
            }
            .setNegativeButton("NO") { _, _ ->
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
                FamilyMergeCopy.MSG_READ_FAILED,
                blockingError = true
            )
            return
        }
        mergeViewModel.importSharedTablesText(text)
    }

    private fun onArchiveImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            showUserMessage(
                FamilyMergeCopy.MSG_READ_FAILED,
                blockingError = true
            )
            return
        }
        mergeViewModel.importArchiveText(text)
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
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
