package com.example.boxmanagernew.ui.backup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.backup.model.BackupArchive
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

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

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
    }

    fun setSelectedFolder(folder: String) {
        _selectedFolder.value = folder
        updateBackupAvailability()
    }

    fun refreshDefaultFileName() {
        _fileName.value =
            generateDefaultFileName()
    }

    fun exportBackup(
        onCompleted: (String) -> Unit
    ) {

        viewModelScope.launch {

            _busy.value = true

            try {

                val archive =
                    withContext(Dispatchers.IO) {

                        backupFacade.export(

                            boxes =
                                boxRepository
                                    .getAllBoxEntitiesSync(),

                            objects =
                                objectRepository
                                    .getAllObjectEntitiesSync(),

                            categories =
                                categoryRepository
                                    .getAllCategoryEntitiesSync(),

                            locations =
                                locationRepository
                                    .getAllLocationEntitiesSync(),

                            objectTypes =
                                objectTypeDao
                                    .getAllTypesSync()
                        )
                    }

                _message.value =
                    buildBackupSummary(archive)

                onCompleted(archive)

            } catch (e: Exception) {

                _message.value =
                    e.message
                        ?: "Errore durante il backup."

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

        return "BCK_${formatter.format(Date())}"
    }

    private fun buildBackupSummary(
        archive: String
    ): String {

        return buildString {

            appendLine("Backup completato")
            appendLine()

            appendLine(
                "Formato: ${BackupArchive.CURRENT_FORMAT_VERSION}"
            )

            appendLine(
                "Dimensione: ${archive.length} caratteri"
            )

            appendLine(
                "Nome file: ${_fileName.value.orEmpty()}"
            )

            appendLine(
                "Destinazione: ${_selectedFolder.value.orEmpty()}"
            )
        }
    }
}