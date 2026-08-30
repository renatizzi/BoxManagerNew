package com.example.boxmanagernew.ui.family

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
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
import com.example.boxmanagernew.domain.family.FamilyMergeCopy
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.FeedbackUtils
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

    private val mergeFilePicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.data?.let { onMergeImportChosen(it) }
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
        exportPersister = ViewExportPersister(this)
        tvMessages = findViewById(R.id.tvMessages)
        scrollView = findViewById(R.id.familyCatalogScroll)
        exportCoordinator = FamilyExportCoordinator(
            activity = this,
            onFolderInaccessible = {
                showUserMessage(
                    FamilyMergeCopy.MSG_FOLDER_INACCESSIBLE,
                    blockingError = true
                )
            },
            onExportCompleted = {
                showUserMessage(
                    FamilyMergeCopy.MSG_EXPORT_COMPLETED,
                    blockingError = false,
                    showDialog = true
                )
            },
            launchFolderPicker = {
                folderPicker.launch(null)
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

        findViewById<MaterialCardView>(R.id.btnExportMerge)
            .setOnClickListener { mergeViewModel.requestExport() }

        findViewById<MaterialCardView>(R.id.btnImportMerge)
            .setOnClickListener { launchMergeFilePicker() }

        findViewById<TextView>(R.id.textFamilyIntro).text =
            FamilyMergeCopy.INTRO
        findViewById<TextView>(R.id.textFamilyFolderHint).text =
            FamilyMergeCopy.HINT_FOLDER
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

        mergeViewModel.preview.observe(this) { preview ->
            if (preview == null) {
                return@observe
            }
            showMergePreview(preview)
        }
    }

    private fun showMergePreview(
        preview: FamilyMergeViewModel.Preview
    ) {
        AlertDialog.Builder(this)
            .setTitle("Ricevi Archivio")
            .setMessage(preview.summary)
            .setPositiveButton("SI") { _, _ ->
                mergeViewModel.confirmImport()
            }
            .setNegativeButton("NO") { _, _ ->
                mergeViewModel.clearPreview()
            }
            .show()
    }

    private fun launchMergeFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, csvMimeTypes())
            exportPersister.rememberedFolderUri()?.let { folderUri ->
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, folderUri)
            }
        }
        mergeFilePicker.launch(intent)
    }

    private fun csvMimeTypes(): Array<String> {
        return arrayOf(
            FamilyMergeConfiguration.CSV_MIME_TYPE,
            FamilyCatalogConfiguration.CSV_MIME_TYPE,
            FamilyInventoryConfiguration.CSV_MIME_TYPE,
            "text/*",
            "*/*"
        )
    }

    private fun onMergeImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            showUserMessage(
                FamilyMergeCopy.MSG_READ_FAILED,
                blockingError = true
            )
            return
        }
        mergeViewModel.importMergeText(text)
    }

    private fun showUserMessage(
        text: String,
        blockingError: Boolean,
        showDialog: Boolean = false
    ) {
        if (text.isBlank()) {
            return
        }
        tvMessages.text = text
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
