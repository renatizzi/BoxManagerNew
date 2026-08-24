package com.example.boxmanagernew.ui.restore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.backup.restore.RestoreApplier
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.ui.backup.BackupZipPersister
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.FeedbackUtils

class RestoreActivity : BaseActivity() {

    private lateinit var viewModel: RestoreViewModel
    private lateinit var persister: BackupZipPersister
    private lateinit var applier: RestoreApplier
    private lateinit var fileAdapter: RestoreFileAdapter

    private lateinit var etRestoreFolder: EditText
    private lateinit var tvEmptyFiles: TextView
    private lateinit var rvRestoreFiles: RecyclerView
    private lateinit var tvSelectedFile: TextView
    private lateinit var tvPreview: TextView
    private lateinit var tvPreRestoreFolder: TextView
    private lateinit var tvMessages: TextView
    private lateinit var btnBrowseFile: Button
    private lateinit var btnRestore: Button

    private var folderUri: Uri? = null
    private var selectedFileUri: Uri? = null
    private var pendingRestoreAfterFolder = false
    private var showSafetyCopies = false

    private val folderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (uri != null) {
                val saved = persistFolder(uri)

                if (saved && pendingRestoreAfterFolder) {
                    pendingRestoreAfterFolder = false
                    confirmAndRestore()
                } else {
                    pendingRestoreAfterFolder = false
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_restore)

        setupAppShell()

        setupPageHeader(
            title = "Ripristino Archivio",
            subtitle = "Sostituzione completa dell'archivio"
        )

        setupBottomNav()

        persister = BackupZipPersister(this)

        val db = DatabaseProvider.getDatabase(applicationContext)
        applier = RestoreApplier(db)

        val factory = RestoreViewModelFactory(
            BoxRepositoryImpl(db.boxDao()),
            ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao()),
            CategoryRepositoryImpl(db.categoryDao(), db.boxDao()),
            LocationRepositoryImpl(db.locationDao(), db.boxDao()),
            db.objectTypeDao(),
            BackupFacade()
        )

        viewModel = ViewModelProvider(this, factory)[RestoreViewModel::class.java]

        etRestoreFolder = findViewById(R.id.etRestoreFolder)
        tvEmptyFiles = findViewById(R.id.tvEmptyFiles)
        rvRestoreFiles = findViewById(R.id.rvRestoreFiles)
        tvSelectedFile = findViewById(R.id.tvSelectedFile)
        tvPreview = findViewById(R.id.tvPreview)
        tvPreRestoreFolder = findViewById(R.id.tvPreRestoreFolder)
        tvMessages = findViewById(R.id.tvMessages)
        btnBrowseFile = findViewById(R.id.btnBrowseFile)
        btnRestore = findViewById(R.id.btnRestore)

        fileAdapter = RestoreFileAdapter { item ->
            if (viewModel.busy.value == true) {
                return@RestoreFileAdapter
            }
            inspectFile(item.uri, item.name)
        }

        rvRestoreFiles.layoutManager = LinearLayoutManager(this)
        rvRestoreFiles.adapter = fileAdapter

        viewModel.preview.observe(this) {
            tvPreview.text = it
        }

        viewModel.fileName.observe(this) { name ->
            tvSelectedFile.text =
                if (name.isBlank()) {
                    ""
                } else {
                    "File selezionato: $name"
                }
        }

        viewModel.message.observe(this) { userMessage ->
            tvMessages.text = userMessage.text

            if (userMessage.blockingError) {
                FeedbackUtils.alert(this)
            }

            if (
                userMessage.text.startsWith(
                    BackupConfiguration.MSG_RESTORE_COMPLETED
                )
            ) {
                showSafetyCopies = false
                refreshFileList()
            } else if (
                userMessage.text == BackupConfiguration.MSG_RESTORE_FAILED
            ) {
                showSafetyCopies = true
                refreshFileList()
            }
        }

        viewModel.restoreEnabled.observe(this) {
            btnRestore.isEnabled =
                it && (viewModel.busy.value != true)
        }

        viewModel.busy.observe(this) { busy ->
            btnBrowseFile.isEnabled = !busy
            btnRestore.isEnabled =
                !busy && (viewModel.restoreEnabled.value ?: false)
        }

        btnBrowseFile.setOnClickListener {
            folderPicker.launch(null)
        }

        btnRestore.setOnClickListener {
            startRestore()
        }

        restoreSavedFolder()
    }

    override fun onResume() {
        super.onResume()
        refreshFileList()
    }

    private fun inspectFile(
        uri: Uri,
        label: String
    ) {

        selectedFileUri = uri
        fileAdapter.select(uri)

        val bytes = try {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: Exception) {
            null
        }

        if (bytes == null) {
            showBlocking(BackupConfiguration.MSG_RESTORE_INVALID_FILE)
            return
        }

        viewModel.inspect(label, bytes)
    }

    private fun startRestore() {

        if (folderUri == null) {
            pendingRestoreAfterFolder = true
            folderPicker.launch(null)
            return
        }

        if (persister.folderDisplayName(folderUri!!) == null) {
            showBlocking(BackupConfiguration.MSG_FOLDER_INACCESSIBLE)
            pendingRestoreAfterFolder = true
            folderPicker.launch(null)
            return
        }

        confirmAndRestore()
    }

    private fun confirmAndRestore() {

        val uri = folderUri ?: return

        if (persister.folderDisplayName(uri) == null) {
            showBlocking(BackupConfiguration.MSG_FOLDER_INACCESSIBLE)
            return
        }

        val preName = viewModel.preRestoreFileName()
        val existing = persister.existingFile(uri, preName)

        if (existing != null) {

            DialogUtils.showReplaceBackupConfirmation(this) {
                showRestoreConfirmation(
                    preRestoreFileName = preName,
                    overwritePreRestore = true
                )
            }

            return
        }

        showRestoreConfirmation(
            preRestoreFileName = preName,
            overwritePreRestore = false
        )
    }

    private fun showRestoreConfirmation(
        preRestoreFileName: String,
        overwritePreRestore: Boolean
    ) {

        DialogUtils.showRestoreConfirmation(this) onConfirm@{
            val uri = folderUri ?: return@onConfirm

            viewModel.restore(
                treeUri = uri,
                applicationVersion = BuildConfig.VERSION_NAME,
                preRestoreFileName = preRestoreFileName,
                overwritePreRestore = overwritePreRestore,
                persister = persister,
                applier = applier
            )
        }
    }

    private fun persistFolder(uri: Uri): Boolean {

        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: Exception) {
        }

        val displayName = persister.folderDisplayName(uri)

        if (displayName == null) {
            showBlocking(BackupConfiguration.MSG_FOLDER_INACCESSIBLE)
            return false
        }

        folderUri = uri
        selectedFileUri = null

        getSharedPreferences(
            BackupConfiguration.PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(BackupConfiguration.PREFS_KEY_FOLDER_URI, uri.toString())
            .apply()

        showFolderName(displayName)
        refreshFileList()

        return true
    }

    private fun restoreSavedFolder() {

        val saved = getSharedPreferences(
            BackupConfiguration.PREFS_NAME,
            Context.MODE_PRIVATE
        ).getString(BackupConfiguration.PREFS_KEY_FOLDER_URI, null)
            ?: return

        val uri = Uri.parse(saved)
        val displayName = persister.folderDisplayName(uri) ?: return

        folderUri = uri
        showFolderName(displayName)
        refreshFileList()
    }

    private fun refreshFileList() {

        val uri = folderUri

        if (uri == null || persister.folderDisplayName(uri) == null) {
            fileAdapter.submit(emptyList(), selectedFileUri)
            tvEmptyFiles.visibility = View.GONE
            return
        }

        val files = visibleBackupFiles(
            persister.listZipFiles(uri)
        )

        tvEmptyFiles.visibility =
            if (files.isEmpty()) View.VISIBLE else View.GONE

        fileAdapter.submit(files, selectedFileUri)
    }

    private fun visibleBackupFiles(
        files: List<BackupZipPersister.ZipFileItem>
    ): List<BackupZipPersister.ZipFileItem> {

        if (showSafetyCopies) {
            return files
        }

        return files.filterNot { item ->
            item.name.startsWith(
                BackupConfiguration.PRE_RESTORE_PREFIX,
                ignoreCase = true
            )
        }
    }

    private fun showFolderName(displayName: String) {

        etRestoreFolder.setText(displayName)
        tvPreRestoreFolder.text =
            "Copia di sicurezza in: $displayName"
    }

    private fun showBlocking(text: String) {

        tvMessages.text = text
        FeedbackUtils.alert(this)
    }
}
