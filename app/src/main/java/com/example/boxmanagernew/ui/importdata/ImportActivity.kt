package com.example.boxmanagernew.ui.importdata

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.merge.ImportMergeApplier
import com.example.boxmanagernew.ui.backup.BackupZipPersister
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.google.android.material.card.MaterialCardView

class ImportActivity : BaseActivity() {

    private lateinit var viewModel: ImportViewModel
    private lateinit var templatePersister: ImportTemplatePersister
    private lateinit var backupPersister: BackupZipPersister
    private lateinit var tvMessages: TextView
    private lateinit var btnGenerateTemplate: MaterialCardView
    private lateinit var btnImportData: MaterialCardView

    private var importExportFolderUri: Uri? = null
    private var backupFolderUri: Uri? = null
    private var pendingTemplateAfterFolder = false

    private val backupFolderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (uri != null && persistBackupFolder(uri)) {
                if (pendingTemplateAfterFolder) {
                    pendingTemplateAfterFolder = false
                    askTemplateFileName(uri)
                } else {
                    startAutoBackup()
                }
            } else {
                pendingTemplateAfterFolder = false
            }
        }

    private val filePicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { onImportFileChosen(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            !ArchivioCompletoNav.allowActivity(
                this,
                PremiumFeature.IMPORT
            )
        ) {
            return
        }

        setContentView(R.layout.activity_import)

        setupAppShell()

        setupPageHeader(
            title = "Importa Dati",
            subtitle = ""
        )

        setupBottomNav()

        templatePersister = ImportTemplatePersister(this)
        backupPersister = BackupZipPersister(this)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val factory = ImportViewModelFactory(
            BoxRepositoryImpl(db.boxDao()),
            ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao()),
            CategoryRepositoryImpl(db.categoryDao(), db.boxDao()),
            LocationRepositoryImpl(db.locationDao(), db.boxDao()),
            db.objectTypeDao(),
            BackupFacade(),
            ImportMergeApplier(db)
        )
        viewModel = ViewModelProvider(this, factory)[ImportViewModel::class.java]

        tvMessages = findViewById(R.id.tvMessages)
        btnGenerateTemplate = findViewById(R.id.btnGenerateTemplate)
        btnImportData = findViewById(R.id.btnImportData)

        restoreSavedImportExportFolder()
        restoreSavedBackupFolder()

        viewModel.message.observe(this) { userMessage ->
            tvMessages.text = userMessage.text

            if (userMessage.blockingError && userMessage.text.isNotBlank()) {
                FeedbackUtils.alert(this)
            }
        }

        viewModel.awaitingAutoBackup.observe(this) { preview ->
            if (preview != null) {
                startAutoBackup()
            }
        }

        viewModel.busy.observe(this) { busy ->
            btnGenerateTemplate.isEnabled = !busy
            btnGenerateTemplate.isClickable = !busy
            btnImportData.isEnabled = !busy
            btnImportData.isClickable = !busy
        }

        btnGenerateTemplate.setOnClickListener {
            startGenerateTemplate()
        }

        btnImportData.setOnClickListener {
            launchImportFilePicker()
        }
    }

    private fun onImportFileChosen(uri: Uri) {

        val fileName = DocumentFile.fromSingleUri(this, uri)?.name
            ?: uri.lastPathSegment.orEmpty()

        val bytes = try {
            contentResolver.openInputStream(uri)?.use { stream ->
                stream.readBytes()
            }
        } catch (_: Exception) {
            null
        }

        viewModel.inspectImportFile(
            fileName = fileName,
            bytes = bytes
        )
    }

    private fun startAutoBackup() {

        val uri = backupFolderUri

        if (uri == null || backupPersister.folderDisplayName(uri) == null) {
            showBlocking(BackupConfiguration.MSG_FOLDER_INACCESSIBLE)
            backupFolderPicker.launch(null)
            return
        }

        val fileName = viewModel.autoBackupFileName()
        val existing = backupPersister.existingFile(uri, fileName)

        if (existing != null) {

            DialogUtils.showReplaceBackupConfirmation(this) {
                viewModel.persistAutoBackup(
                    treeUri = uri,
                    applicationVersion = BuildConfig.VERSION_NAME,
                    fileName = fileName,
                    overwrite = true,
                    persister = backupPersister
                )
            }

            return
        }

        viewModel.persistAutoBackup(
            treeUri = uri,
            applicationVersion = BuildConfig.VERSION_NAME,
            fileName = fileName,
            overwrite = false,
            persister = backupPersister
        )
    }

    private fun persistBackupFolder(uri: Uri): Boolean {

        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: Exception) {
        }

        if (backupPersister.folderDisplayName(uri) == null) {
            showBlocking(BackupConfiguration.MSG_FOLDER_INACCESSIBLE)
            return false
        }

        backupFolderUri = uri

        getSharedPreferences(
            StorageFolderConfiguration.PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(StorageFolderConfiguration.KEY_BACKUP, uri.toString())
            .apply()

        return true
    }

    private fun restoreSavedBackupFolder() {

        val saved = getSharedPreferences(
            StorageFolderConfiguration.PREFS_NAME,
            Context.MODE_PRIVATE
        ).getString(StorageFolderConfiguration.KEY_BACKUP, null)
            ?: return

        val uri = Uri.parse(saved)
        if (backupPersister.folderDisplayName(uri) == null) {
            return
        }

        backupFolderUri = uri
    }

    private fun startGenerateTemplate() {

        val uri = backupFolderUri

        if (
            uri != null &&
            templatePersister.folderDisplayName(uri) != null
        ) {
            askTemplateFileName(uri)
            return
        }

        pendingTemplateAfterFolder = true
        backupFolderPicker.launch(null)
    }

    private fun launchImportFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    ImportConfiguration.CSV_MIME_TYPE,
                    "text/comma-separated-values",
                    "text/plain",
                    "*/*"
                )
            )
            importExportFolderUri?.let { folderUri ->
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, folderUri)
            }
        }
        filePicker.launch(intent)
    }

    private fun restoreSavedImportExportFolder() {

        val saved = getSharedPreferences(
            StorageFolderConfiguration.PREFS_NAME,
            Context.MODE_PRIVATE
        ).getString(StorageFolderConfiguration.KEY_IMPORT_EXPORT, null)
            ?: return

        val uri = Uri.parse(saved)
        if (templatePersister.folderDisplayName(uri) == null) {
            return
        }

        importExportFolderUri = uri
    }

    private fun askTemplateFileName(uri: Uri) {

        DialogUtils.showExportFileName(
            this,
            ImportConfiguration.FILE_NAME,
            exists = { fileName ->
                templatePersister.existingFile(uri, fileName) != null
            },
            onSave = { fileName, overwrite ->
                viewModel.persistTemplate(
                    treeUri = uri,
                    fileName = fileName,
                    overwrite = overwrite,
                    persister = templatePersister
                )
            },
            normalizeName = { raw ->
                ImportConfiguration.templateFileName(raw)
            }
        )
    }

    private fun showBlocking(text: String) {

        tvMessages.text = text
        FeedbackUtils.alert(this)
    }
}
