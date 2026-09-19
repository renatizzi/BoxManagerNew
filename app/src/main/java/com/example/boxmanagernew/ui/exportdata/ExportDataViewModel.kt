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
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.importdata.export.DataExportCsvBuilder
import com.example.boxmanagernew.importdata.export.ExportSelectionFilter
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

    enum class Scope {
        ALL,
        SELECTION
    }

    enum class Format {
        CSV,
        ZIP
    }

    data class ExportReady(
        val fileName: String,
        val bytes: ByteArray,
        val photoSummary: String?
    )

    data class BoxChoice(
        val id: Int,
        val name: String
    )

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _exportReady = MutableLiveData<ExportReady?>()
    val exportReady: LiveData<ExportReady?> = _exportReady

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _pickBoxes = MutableLiveData<Pair<Format, List<BoxChoice>>?>()
    val pickBoxes: LiveData<Pair<Format, List<BoxChoice>>?> = _pickBoxes

    var scope: Scope = Scope.ALL

    fun clearExportReady() {
        _exportReady.value = null
    }

    fun clearPickBoxes() {
        _pickBoxes.value = null
    }

    fun requestCsvExport() {
        requestExport(Format.CSV)
    }

    fun requestZipExport() {
        requestExport(Format.ZIP)
    }

    fun confirmSelection(format: Format, selectedBoxIds: Set<Int>) {
        if (selectedBoxIds.isEmpty()) {
            _message.value =
                appContext.getString(R.string.export_msg_select_at_least_one)
            return
        }
        runExport(format, selectedBoxIds)
    }

    private fun requestExport(format: Format) {
        if (scope == Scope.ALL) {
            runExport(format, selectedBoxIds = null)
            return
        }
        viewModelScope.launch {
            _busy.value = true
            try {
                val choices = withContext(Dispatchers.IO) {
                    boxRepository.getAllBoxEntitiesSync()
                        .sortedBy { it.name.lowercase(Locale.getDefault()) }
                        .map { BoxChoice(it.id, it.name) }
                }
                if (choices.isEmpty()) {
                    _message.value =
                        appContext.getString(R.string.export_msg_no_boxes)
                    return@launch
                }
                _pickBoxes.value = format to choices
            } finally {
                _busy.value = false
            }
        }
    }

    private fun runExport(format: Format, selectedBoxIds: Set<Int>?) {
        viewModelScope.launch {
            _busy.value = true
            try {
                val ready = withContext(Dispatchers.IO) {
                    val (boxes, objects) = loadRows(selectedBoxIds)
                    when (format) {
                        Format.CSV -> {
                            val csv = csvBuilder.build(boxes, objects, withIds = true)
                            ExportReady(
                                fileName = ViewOutputConfiguration.proposedFileName(),
                                bytes = csv,
                                photoSummary = null
                            )
                        }
                        Format.ZIP -> {
                            val csv = csvBuilder.build(boxes, objects, withIds = true)
                            val photoIds = objects.map { it.objectPermanentId }
                                .filter { it.isNotBlank() }
                                .toSet()
                            val photos = ObjectPhotoStoreProvider.get(appContext)
                                .zipEntriesForBackup(photoIds)
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
                    }
                }
                _exportReady.value = ready
            } finally {
                _busy.value = false
            }
        }
    }

    private suspend fun loadRows(
        selectedBoxIds: Set<Int>?
    ): Pair<
        List<DataExportCsvBuilder.BoxRow>,
        List<DataExportCsvBuilder.ObjectRow>
        > {
        val allBoxes = boxRepository.getAllBoxEntitiesSync()
        val boxes = ExportSelectionFilter.filterBoxes(
            allBoxes,
            BoxEntity::id,
            selectedBoxIds
        )
        val categories = database.categoryDao().getAllSync()
            .associate { it.id to it.name }
        val allObjects = objectRepository.getAllObjectEntitiesSync()
        val objects = ExportSelectionFilter.filterObjects(
            allObjects,
            { it.boxId },
            selectedBoxIds
        )
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
