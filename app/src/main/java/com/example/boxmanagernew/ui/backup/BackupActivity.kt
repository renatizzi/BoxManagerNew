package com.example.boxmanagernew.ui.backup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.boxmanagernew.R
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.*
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class BackupActivity : BaseActivity() {

    private lateinit var viewModel: BackupViewModel

    private lateinit var etBackupFileName: EditText
    private lateinit var etBackupFolder: EditText
    private lateinit var tvMessages: TextView
    private lateinit var btnBrowse: Button
    private lateinit var btnCreateBackup: Button

    private var selectedUri: Uri? = null

    private val folderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (uri != null) {

                selectedUri = uri

                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                } catch (_: Exception) {
                }

                viewModel.setSelectedFolder(uri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_backup)

        setupEdgeToEdge()
        setupTopBar()

        setupPageHeader(
            title = "Backup Archivio",
            subtitle = "Creazione copia completa dell'archivio"
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_UTILITY
        )

        val db = DatabaseProvider.getDatabase(applicationContext)

        val factory = BackupViewModelFactory(
            BoxRepositoryImpl(db.boxDao()),
            ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao()),
            CategoryRepositoryImpl(db.categoryDao(), db.boxDao()),
            LocationRepositoryImpl(db.locationDao(), db.boxDao()),
            db.objectTypeDao(),
            BackupFacade()
        )

        viewModel = ViewModelProvider(this, factory)[BackupViewModel::class.java]

        etBackupFileName = findViewById(R.id.etBackupFileName)
        etBackupFolder = findViewById(R.id.etBackupFolder)
        tvMessages = findViewById(R.id.tvMessages)
        btnBrowse = findViewById(R.id.btnBrowse)
        btnCreateBackup = findViewById(R.id.btnCreateBackup)

        viewModel.fileName.observe(this) {
            if (etBackupFileName.text.toString() != it)
                etBackupFileName.setText(it)
        }

        viewModel.selectedFolder.observe(this) {
            if (etBackupFolder.text.toString() != it)
                etBackupFolder.setText(it)
        }

        viewModel.message.observe(this) {
            tvMessages.text = it
        }

        viewModel.backupEnabled.observe(this) {
            btnCreateBackup.isEnabled = it
        }

        viewModel.busy.observe(this) {
            btnBrowse.isEnabled = !it
            btnCreateBackup.isEnabled =
                !it && (viewModel.backupEnabled.value ?: false)
        }

        etBackupFileName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setFileName(s?.toString() ?: "")
            }
        })

        btnBrowse.setOnClickListener {
            folderPicker.launch(null)
        }

        btnCreateBackup.setOnClickListener {
            viewModel.exportBackup { summary ->
                tvMessages.text = summary
            }
        }
    }
}