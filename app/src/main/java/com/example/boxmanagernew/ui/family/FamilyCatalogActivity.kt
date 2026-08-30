package com.example.boxmanagernew.ui.family

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.material.card.MaterialCardView

class FamilyCatalogActivity : BaseActivity() {

    private lateinit var catalogViewModel: FamilyCatalogViewModel
    private lateinit var inventoryViewModel: FamilyInventoryViewModel
    private lateinit var persister: FamilyCatalogPersister
    private lateinit var tvMessages: TextView

    private var pendingExport: Pair<String, ByteArray>? = null

    private val folderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            if (uri != null) {
                writePendingExport(uri)
            } else {
                pendingExport = null
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
        findViewById<TextView>(R.id.textExportCatalog).text =
            FamilyCatalogCopy.BUTTON_SEND
        findViewById<TextView>(R.id.textImportCatalog).text =
            FamilyCatalogCopy.BUTTON_RECEIVE
        findViewById<TextView>(R.id.textSectionInventory).text =
            FamilyCatalogCopy.SECTION_INVENTORY
        findViewById<TextView>(R.id.textExportInventory).text =
            FamilyCatalogCopy.BUTTON_SEND_INVENTORY
        findViewById<TextView>(R.id.textImportInventory).text =
            FamilyCatalogCopy.BUTTON_RECEIVE_INVENTORY

        catalogViewModel.message.observe(this) { text ->
            tvMessages.text = text
        }

        inventoryViewModel.message.observe(this) { text ->
            tvMessages.text = text
        }

        catalogViewModel.exportBytes.observe(this) { payload ->
            if (payload == null) {
                return@observe
            }
            pendingExport = payload
            catalogViewModel.clearExport()
            folderPicker.launch(null)
        }

        inventoryViewModel.exportBytes.observe(this) { payload ->
            if (payload == null) {
                return@observe
            }
            pendingExport = payload
            inventoryViewModel.clearExport()
            folderPicker.launch(null)
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

    private fun writePendingExport(treeUri: Uri) {
        val payload = pendingExport ?: return
        pendingExport = null
        try {
            contentResolver.takePersistableUriPermission(
                treeUri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Cartella usabile per questa sessione anche senza persist.
        }
        val result = persister.persist(treeUri, payload.first, payload.second)
        if (result.success) {
            Toast.makeText(
                this,
                "Salvato ${result.fileName} in ${result.folderName}",
                Toast.LENGTH_SHORT
            ).show()
            tvMessages.text =
                "Export completato: ${result.fileName}\nCartella: ${result.folderName}"
        } else if (result.folderInaccessible) {
            Toast.makeText(this, "Cartella non accessibile.", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "Salvataggio non riuscito.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onCatalogImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            tvMessages.text = "Impossibile leggere il file."
            return
        }
        catalogViewModel.importCatalogText(text)
    }

    private fun onInventoryImportChosen(uri: Uri) {
        val text = persister.readText(uri) ?: run {
            tvMessages.text = "Impossibile leggere il file."
            return
        }
        inventoryViewModel.importInventoryText(text)
    }
}
