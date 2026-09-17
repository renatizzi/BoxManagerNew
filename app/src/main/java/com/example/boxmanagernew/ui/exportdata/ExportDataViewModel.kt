package com.example.boxmanagernew.ui.exportdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.photo.ObjectPhotoStoreProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.data.local.dao.ObjectTypeDao
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.importdata.export.DataExportCsvBuilder
import com.example.boxmanagernew.importdata.zip.ImportDataZip
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ExportDataViewModel(
    private val appContext: Context,
    private val database: AppDatabase,
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val objectTypeDao: ObjectTypeDao,
    private val csvBuilder: DataExportCsvBuilder = DataExportCsvBuilder()
) : ViewModel() {

    data class ExportReady(
        val fileName: String,
        val bytes: ByteArray,
        val photoSummary: String?
    )

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _exportReady = MutableLiveData<ExportReady?>()
    val exportReady: LiveData<ExportReady?> = _exportReady

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun clearExportReady() {
        _exportReady.value = null
    }

    fun requestCsvExport() {
        viewModelScope.launch {
            _busy.value = true
            try {
                val ready = withContext(Dispatchers.IO) {
                    val (boxes, objects) = loadRows()
                    val csv = csvBuilder.build(boxes, objects, withIds = false)
                    ExportReady(
                        fileName = ViewOutputConfiguration.proposedFileName(),
                        bytes = csv,
                        photoSummary = null
                    )
                }
                _exportReady.value = ready
            } finally {
                _busy.value = false
            }
        }
    }

    fun requestZipExport() {
        viewModelScope.launch {
            _busy.value = true
            try {
                val ready = withContext(Dispatchers.IO) {
                    val (boxes, objects) = loadRows()
                    val csv = csvBuilder.build(boxes, objects, withIds = true)
                    val photos = ObjectPhotoStoreProvider.get(appContext)
                        .zipEntriesForBackup()
                    val count = photos.keys.count {
                        it.endsWith(".jpg") && !it.contains("_thumb")
                    }
                    val bytesApprox = photos.values.sumOf { it.size.toLong() }
                    val zip = ImportDataZip.pack(csv, photos)
                    val name = ViewOutputConfiguration.proposedFileName()
                        .removeSuffix(".csv") + ".zip"
                    val summary = appContext.getString(
                        R.string.export_msg_photo_summary,
                        count,
                        formatMb(bytesApprox)
                    )
                    ExportReady(name, zip, summary)
                }
                _exportReady.value = ready
            } finally {
                _busy.value = false
            }
        }
    }

    private suspend fun loadRows(): Pair<
        List<DataExportCsvBuilder.BoxRow>,
        List<DataExportCsvBuilder.ObjectRow>
        > {
        val boxes = boxRepository.getAllBoxEntitiesSync()
        val categories = database.categoryDao().getAllSync()
            .associate { it.id to it.name }
        val objects = objectRepository.getAllObjectEntitiesSync()
        val typeNames = objectTypeDao.getAllTypesSync()
            .associate { it.id to it.name }
        val boxNames = boxes.associate { it.id to it.name }

        val boxRows = boxes.map { box ->
            DataExportCsvBuilder.BoxRow(
                name = box.name,
                category = categories[box.categoryId].orEmpty(),
                position = box.position,
                permanentId = box.permanentId
            )
        }
        val objectRows = objects.map { obj ->
            DataExportCsvBuilder.ObjectRow(
                name = typeNames[obj.typeObjectId].orEmpty(),
                boxName = boxNames[obj.boxId].orEmpty(),
                description = obj.description.orEmpty(),
                quantity = obj.quantity?.toString().orEmpty(),
                objectPermanentId = obj.objectPermanentId
            )
        }
        return boxRows to objectRows
    }

    private fun formatMb(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return String.format(Locale.ITALY, "%.1f", mb)
    }
}
