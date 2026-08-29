package com.example.boxmanagernew.ui.family

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.ui.common.BaseActivity
import com.google.android.material.card.MaterialCardView

class FamilyCatalogActivity : BaseActivity() {

    private lateinit var viewModel: FamilyCatalogViewModel
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

    private val filePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                onImportFileChosen(uri)
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
            title = "Catalogo famiglia",
            subtitle = "Categorie e luoghi di custodia condivisi"
        )
        setupBottomNav()

        persister = FamilyCatalogPersister(this)
        tvMessages = findViewById(R.id.tvMessages)

        val db = DatabaseProvider.getDatabase(applicationContext)
        viewModel = ViewModelProvider(
            this,
            FamilyCatalogViewModelFactory(
                CategoryRepositoryImpl(db.categoryDao(), db.boxDao()),
                LocationRepositoryImpl(db.locationDao(), db.boxDao())
            )
        )[FamilyCatalogViewModel::class.java]

        findViewById<MaterialCardView>(R.id.btnExportCatalog)
            .setOnClickListener { viewModel.requestExport() }

        findViewById<MaterialCardView>(R.id.btnImportCatalog)
            .setOnClickListener {
                filePicker.launch(
                    arrayOf(
                        FamilyCatalogConfiguration.CSV_MIME_TYPE,
                        "text/*",
                        "*/*"
                    )
                )
            }

        viewModel.message.observe(this) { text ->
            tvMessages.text = text
        }

        viewModel.exportBytes.observe(this) { payload ->
            if (payload == null) {
                return@observe
            }
            pendingExport = payload
            viewModel.clearExport()
            folderPicker.launch(null)
        }
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

    private fun onImportFileChosen(uri: Uri) {
        val text = persister.readText(uri)
        if (text == null) {
            tvMessages.text = "Impossibile leggere il file."
            return
        }
        viewModel.importCatalogText(text)
    }
}
