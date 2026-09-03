package com.example.boxmanagernew.ui.restore

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.R
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
    private val zipReader: BackupZipReader = BackupZipReader(),
    private val appContext: android.content.Context
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
                            BackupConfiguration.restoreIncompatible(appContext),
                            blockingError = true
                        )
                    }

                    BackupPackageInspector.Result.Invalid -> {

                        _preview.value = ""
                        _message.value = UserMessage(
                            BackupConfiguration.restoreInvalidFile(appContext),
                            blockingError = true
                        )
                    }
                }

            } catch (_: Exception) {

                _preview.value = ""
                _message.value = UserMessage(
                    BackupConfiguration.restoreInvalidFile(appContext),
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
                            BackupConfiguration.folderInaccessible(appContext)
                        } else {
                            BackupConfiguration.restoreFailed(appContext)
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
                    BackupConfiguration.restoreFailed(appContext),
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
            appendLine(appContext.getString(R.string.label_containers_count, metadata.boxCount))
            appendLine(appContext.getString(R.string.label_objects_count, metadata.objectCount))
            appendLine(appContext.getString(R.string.label_categories_count, metadata.categoryCount))
            appendLine(appContext.getString(R.string.label_locations_count, metadata.locationCount))
            appendLine()
            append(BackupConfiguration.restoreReplaceWarning(appContext))
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
            appendLine(BackupConfiguration.restoreCompleted(appContext))
            appendLine()
            appendLine(appContext.getString(R.string.label_file_name, sourceFile))
            append(appContext.getString(R.string.label_date, whenText))
        }
    }
}
