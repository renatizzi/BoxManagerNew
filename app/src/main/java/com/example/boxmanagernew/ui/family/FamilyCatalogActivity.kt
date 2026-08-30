package com.example.boxmanagernew.ui.family

import android.net.Uri
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.family.FamilyCatalogCopy
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.google.android.material.card.MaterialCardView

class FamilyCatalogActivity : BaseActivity() {

    private lateinit var catalogViewModel: FamilyCatalogViewModel
    private lateinit var inventoryViewModel: FamilyInventoryViewModel
    private lateinit var persister: FamilyCatalogPersister
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

    private val catalogFilePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                onCatalogImportChosen(uri)
            }
        }

    private val inventoryFilePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                onInventoryImportChosen(uri)
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
            title = FamilyCatalogCopy.PAGE_TITLE,
            subtitle = FamilyCatalogCopy.PAGE_SUBTITLE
        )
        setupBottomNav()

        persister = FamilyCatalogPersister(this)
        tvMessages = findViewById(R.id.tvMessages)
        scrollView = findViewById(R.id.familyCatalogScroll)
        exportCoordinator = FamilyExportCoordinator(
            activity = this,
            onFolderInaccessible = {
                showUserMessage(
                    FamilyCatalogCopy.MSG_FOLDER_INACCESSIBLE,
                    blockingError = true
                )
            },
            onExportCompleted = {
                showUserMessage(
                    FamilyCatalogCopy.MSG_EXPORT_COMPLETED,
                    blockingError = false,
                    showDialog = true
                )
            },
            launchFolderPicker = {
                folderPicker.launch(null)
            }
        )

        val db = DatabaseProvider.getDatabase(applicationContext)
        catalogViewModel = ViewModelProvider(
            this,
            FamilyCatalogViewModelFactory(
                CategoryRepositoryImpl(db.categoryDao(), db.boxDao()),
                LocationRepositoryImpl(db.locationDao(), db.boxDao())
            )
        )[FamilyCatalogViewModel::class.java]
        inventoryViewModel = ViewModelProvider(
            this,
            FamilyInventoryViewModelFactory(
                db,
                BoxRepositoryImpl(db.boxDao()),
                ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao())
            )
        )[FamilyInventoryViewModel::class.java]

        findViewById<MaterialCardView>(R.id.btnExportCatalog)
            .setOnClickListener { catalogViewModel.requestExport() }

        findViewById<MaterialCardView>(R.id.btnImportCatalog)
            .setOnClickListener {
                catalogFilePicker.launch(csvMimeTypes())
            }

        findViewById<MaterialCardView>(R.id.btnExportInventory)
            .setOnClickListener { inventoryViewModel.requestExport() }

        findViewById<MaterialCardView>(R.id.btnImportInventory)
            .setOnClickListener {
                inventoryFilePicker.launch(csvMimeTypes())
            }

        findViewById<TextView>(R.id.textFamilyIntro).text =
            FamilyCatalogCopy.INTRO
        findViewById<TextView>(R.id.textSectionCatalog).text =
            FamilyCatalogCopy.SECTION_CATALOG
        findViewById<TextView>(R.id.textSectionCatalogHint).text =
            FamilyCatalogCopy.SECTION_CATALOG_HINT
        findViewById<TextView>(R.id.textExportCatalog).text =
            FamilyCatalogCopy.BUTTON_SEND
        findViewById<TextView>(R.id.textImportCatalog).text =
            FamilyCatalogCopy.BUTTON_RECEIVE
        findViewById<TextView>(R.id.textSectionInventory).text =
            FamilyCatalogCopy.SECTION_INVENTORY
        findViewById<TextView>(R.id.textSectionInventoryHint).text =
            FamilyCatalogCopy.SECTION_INVENTORY_HINT
        findViewById<TextView>(R.id.textExportInventory).text =
            FamilyCatalogCopy.BUTTON_SEND_INVENTORY
        findViewById<TextView>(R.id.textImportInventory).text =
            FamilyCatalogCopy.BUTTON_RECEIVE_INVENTORY

        catalogViewModel.message.observe(this) { text ->
            showUserMessage(text, blockingError = false, showDialog = true)
        }

        inventoryViewModel.message.observe(this) { text ->
            showUserMessage(text, blockingError = false, showDialog = true)
        }

        catalogViewModel.exportBytes.observe(this) { payload ->
            if (payload == null) {
                return@observe
            }
            catalogViewModel.clearExport()
            exportCoordinator.beginExport(
                defaultFileName = payload.first,
                bytes = payload.second
            )
        }

        inventoryViewModel.exportBytes.observe(this) { payload ->
            if (payload == null) {
                return@observe
            }
            inventoryViewModel.clearExport()
            exportCoordinator.beginExport(
                defaultFileName = payload.first,
                bytes = payload.second
            )
        }

        inventoryViewModel.preview.observe(this) { preview ->
            if (preview == null) {
                return@observe
            }
            showInventoryPreview(preview)
        }
    }

    private fun showInventoryPreview(
        preview: FamilyInventoryViewModel.Preview
    ) {
        AlertDialog.Builder(this)
            .setTitle("Ricevi Inventario")
            .setMessage(preview.summary)
            .setPositiveButton("SI") { _, _ ->
                inventoryViewModel.confirmImport()
            }
            .setNegativeButton("NO") { _, _ ->
                inventoryViewModel.clearPreview()
            }
            .show()
    }

    private fun csvMimeTypes(): Array<String> {
        return arrayOf(
            FamilyCatalogConfiguration.CSV_MIME_TYPE,
            FamilyInventoryConfiguration.CSV_MIME_TYPE,
            "text/*",
            "*/*"
        )
    }

    private fun onCatalogImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            showUserMessage(
                FamilyCatalogCopy.MSG_READ_FAILED,
                blockingError = true
            )
            return
        }
        catalogViewModel.importCatalogText(text)
    }

    private fun onInventoryImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            showUserMessage(
                FamilyCatalogCopy.MSG_READ_FAILED,
                blockingError = true
            )
            return
        }
        inventoryViewModel.importInventoryText(text)
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
