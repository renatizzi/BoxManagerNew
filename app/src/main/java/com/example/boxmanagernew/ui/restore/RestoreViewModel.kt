package com.example.boxmanagernew.ui.restore

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.restore.BackupPackageInspector
import com.example.boxmanagernew.backup.restore.RestoreApplier
import com.example.boxmanagernew.backup.zip.BackupZipReader
import com.example.boxmanagernew.data.local.dao.ObjectTypeDao
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.ui.backup.BackupZipPersister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RestoreViewModel(
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val objectTypeDao: ObjectTypeDao,
    private val backupFacade: BackupFacade,
    private val inspector: BackupPackageInspector = BackupPackageInspector(),
    private val zipReader: BackupZipReader = BackupZipReader()
) : ViewModel() {

    data class UserMessage(
        val text: String,
        val blockingError: Boolean = false
    )

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _message = MutableLiveData(UserMessage(""))
    val message: LiveData<UserMessage> = _message

    private val _fileName = MutableLiveData("")
    val fileName: LiveData<String> = _fileName

    private val _preview = MutableLiveData("")
    val preview: LiveData<String> = _preview

    private val _restoreEnabled = MutableLiveData(false)
    val restoreEnabled: LiveData<Boolean> = _restoreEnabled

    private var inspectedArchive: BackupArchive? = null

    fun inspect(
        fileLabel: String,
        zipBytes: ByteArray
    ) {

        viewModelScope.launch {

            _busy.value = true
            _fileName.value = fileLabel
            inspectedArchive = null
            _restoreEnabled.value = false

            try {

                val result = withContext(Dispatchers.IO) {

                    val entries = zipReader.read(
                        ByteArrayInputStream(zipBytes)
                    )
                    inspector.inspect(entries)
                }

                when (result) {

                    is BackupPackageInspector.Result.Ready -> {

                        inspectedArchive = result.archive

                        _preview.value = buildPreview(result)
                        _message.value = UserMessage("")
                        _restoreEnabled.value = true
                    }

                    BackupPackageInspector.Result.Incompatible -> {

                        _preview.value = ""
                        _message.value = UserMessage(
                            BackupConfiguration.MSG_RESTORE_INCOMPATIBLE,
                            blockingError = true
                        )
                    }

                    BackupPackageInspector.Result.Invalid -> {

                        _preview.value = ""
                        _message.value = UserMessage(
                            BackupConfiguration.MSG_RESTORE_INVALID_FILE,
                            blockingError = true
                        )
                    }
                }

            } catch (_: Exception) {

                _preview.value = ""
                _message.value = UserMessage(
                    BackupConfiguration.MSG_RESTORE_INVALID_FILE,
                    blockingError = true
                )

            } finally {

                _busy.value = false
            }
        }
    }

    fun restore(
        treeUri: Uri,
        applicationVersion: String,
        preRestoreFileName: String,
        overwritePreRestore: Boolean,
        persister: BackupZipPersister,
        applier: RestoreApplier
    ) {

        val archive = inspectedArchive ?: return

        if (preRestoreFileName.isBlank()) {
            return
        }

        viewModelScope.launch {

            _busy.value = true

            try {

                val preResult = withContext(Dispatchers.IO) {

                    val payload = backupFacade.exportPayload(
                        boxes = boxRepository.getAllBoxEntitiesSync(),
                        objects = objectRepository.getAllObjectEntitiesSync(),
                        categories = categoryRepository.getAllCategoryEntitiesSync(),
                        locations = locationRepository.getAllLocationEntitiesSync(),
                        objectTypes = objectTypeDao.getAllTypesSync(),
                        applicationVersion = applicationVersion
                    )

                    persister.persist(
                        treeUri = treeUri,
                        fileName = preRestoreFileName,
                        entries = payload,
                        overwrite = overwritePreRestore
                    )
                }

                if (!preResult.success) {

                    _message.value = UserMessage(
                        if (preResult.folderInaccessible) {
                            BackupConfiguration.MSG_FOLDER_INACCESSIBLE
                        } else {
                            BackupConfiguration.MSG_RESTORE_FAILED
                        },
                        blockingError = true
                    )

                    return@launch
                }

                withContext(Dispatchers.IO) {
                    applier.replace(archive)
                }

                _message.value = UserMessage(
                    buildRestoreSummary(fileName.value.orEmpty())
                )

            } catch (_: Exception) {

                _message.value = UserMessage(
                    BackupConfiguration.MSG_RESTORE_FAILED,
                    blockingError = true
                )

            } finally {

                _busy.value = false
            }
        }
    }

    fun preRestoreFileName(now: Date = Date()): String {
        return BackupConfiguration.proposedPreRestoreFileName(now)
    }

    private fun buildPreview(
        result: BackupPackageInspector.Result.Ready
    ): String {

        val metadata = result.metadata

        return buildString {
            appendLine("Contenitori: ${metadata.boxCount}")
            appendLine("Oggetti: ${metadata.objectCount}")
            appendLine("Categorie: ${metadata.categoryCount}")
            appendLine("Luoghi: ${metadata.locationCount}")
            appendLine()
            append(BackupConfiguration.MSG_RESTORE_REPLACE_WARNING)
        }
    }

    private fun buildRestoreSummary(
        sourceFile: String
    ): String {

        val whenText = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        return buildString {
            appendLine(BackupConfiguration.MSG_RESTORE_COMPLETED)
            appendLine()
            appendLine("Nome file: $sourceFile")
            append("Data: $whenText")
        }
    }
}
