package com.example.boxmanagernew.ui.backup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.data.local.dao.ObjectTypeDao
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupViewModel(
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val objectTypeDao: ObjectTypeDao,
    private val backupFacade: BackupFacade
) : ViewModel() {

    data class UserMessage(
        val text: String,
        val blockingError: Boolean = false
    )

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _message = MutableLiveData(UserMessage(""))
    val message: LiveData<UserMessage> = _message

    private val _fileName =
        MutableLiveData(generateDefaultFileName())
    val fileName: LiveData<String> = _fileName

    private val _selectedFolder =
        MutableLiveData("")
    val selectedFolder: LiveData<String> = _selectedFolder

    private val _backupEnabled =
        MutableLiveData(false)
    val backupEnabled: LiveData<Boolean> =
        _backupEnabled

    fun setFileName(value: String) {
        _fileName.value = value.trim()
        updateBackupAvailability()
    }

    fun setSelectedFolder(folder: String) {
        _selectedFolder.value = folder
        updateBackupAvailability()
    }

    fun refreshDefaultFileName() {
        _fileName.value =
            generateDefaultFileName()
        updateBackupAvailability()
    }

    fun exportBackup(
        treeUri: Uri,
        applicationVersion: String,
        overwrite: Boolean,
        persister: BackupZipPersister
    ) {

        val name = _fileName.value.orEmpty()

        if (name.isBlank()) {
            return
        }

        viewModelScope.launch {

            _busy.value = true

            try {

                val result =
                    withContext(Dispatchers.IO) {

                        val payload =
                            backupFacade.exportPayload(
                                boxes = boxRepository
                                    .getAllBoxEntitiesSync(),
                                objects = objectRepository
                                    .getAllObjectEntitiesSync(),
                                categories = categoryRepository
                                    .getAllCategoryEntitiesSync(),
                                locations = locationRepository
                                    .getAllLocationEntitiesSync(),
                                objectTypes = objectTypeDao
                                    .getAllTypesSync(),
                                applicationVersion = applicationVersion
                            )

                        persister.persist(
                            treeUri = treeUri,
                            fileName = name,
                            entries = payload,
                            overwrite = overwrite
                        )
                    }

                if (result.success) {

                    _message.value =
                        UserMessage(
                            buildBackupSummary(result)
                        )

                } else if (result.folderInaccessible) {

                    _message.value =
                        UserMessage(
                            BackupConfiguration.MSG_FOLDER_INACCESSIBLE,
                            blockingError = true
                        )

                } else {

                    _message.value =
                        UserMessage(
                            BackupConfiguration.MSG_WRITE_FAILED,
                            blockingError = true
                        )
                }

            } catch (_: IllegalArgumentException) {

                _message.value =
                    UserMessage(
                        BackupConfiguration.MSG_INVALID_ARCHIVE,
                        blockingError = true
                    )

            } catch (_: Exception) {

                _message.value =
                    UserMessage(
                        BackupConfiguration.MSG_WRITE_FAILED,
                        blockingError = true
                    )

            } finally {

                _busy.value = false
            }
        }
    }

    private fun updateBackupAvailability() {

        val enabled =
            !_selectedFolder.value.isNullOrBlank() &&
                    !_fileName.value.isNullOrBlank()

        _backupEnabled.value = enabled
    }

    private fun generateDefaultFileName(): String {

        val formatter =
            SimpleDateFormat(
                "ddMMyy_HHmm",
                Locale.getDefault()
            )

        return BackupConfiguration.BACKUP_FILE_PREFIX +
                formatter.format(Date())
    }

    private fun buildBackupSummary(
        result: BackupZipPersister.Result
    ): String {

        val whenText =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

        return buildString {

            appendLine(BackupConfiguration.MSG_BACKUP_COMPLETED)
            appendLine()
            appendLine("Nome file: ${result.fileName}")
            appendLine("Cartella: ${result.folderName}")
            appendLine("Dimensione: ${formatSize(result.sizeBytes)}")
            append("Data: $whenText")
        }
    }

    private fun formatSize(bytes: Long): String {

        if (bytes < 1024) {
            return "$bytes byte"
        }

        val kilo = bytes / 1024.0

        return String.format(Locale.getDefault(), "%.1f KB", kilo)
    }
}
