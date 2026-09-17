package com.example.boxmanagernew.ui.family

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.family.catalog.FamilyCatalogReader
import com.example.boxmanagernew.family.catalog.FamilyCatalogWriter
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilySharedTablesConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.merge.FamilyMergeApplier
import com.example.boxmanagernew.family.merge.FamilyMergeMerger
import com.example.boxmanagernew.family.merge.FamilyMergeReader
import com.example.boxmanagernew.family.merge.FamilyMergeWriter
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import com.example.boxmanagernew.family.model.FamilyDeletion
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot
import com.example.boxmanagernew.family.shared.SharedTablesApplier
import com.example.boxmanagernew.family.shared.SharedTablesMerger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyMergeViewModel(
    private val appContext: Context,
    private val database: AppDatabase,
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val catalogReader: FamilyCatalogReader = FamilyCatalogReader(),
    private val mergeReader: FamilyMergeReader = FamilyMergeReader(),
    private val mergeMerger: FamilyMergeMerger = FamilyMergeMerger(),
    private val mergeApplier: FamilyMergeApplier = FamilyMergeApplier(database),
    private val sharedTablesMerger: SharedTablesMerger = SharedTablesMerger(),
    private val sharedTablesApplier: SharedTablesApplier = SharedTablesApplier(database)
) : ViewModel() {

    data class ArchivePreview(
        val summary: String,
        val plan: FamilyMergeMerger.Plan,
        val photoEntries: Map<String, ByteArray> = emptyMap()
    )

    data class SharedTablesPreview(
        val summary: String,
        val plan: SharedTablesMerger.Plan
    )

    data class ArchiveExportReady(
        val fileName: String,
        val bytes: ByteArray,
        val photoSummary: String
    )

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _importFailure = MutableLiveData<String?>()
    val importFailure: LiveData<String?> = _importFailure

    private val _exportBytes = MutableLiveData<Pair<String, ByteArray>?>()
    val exportBytes: LiveData<Pair<String, ByteArray>?> = _exportBytes

    private val _archiveExportReady = MutableLiveData<ArchiveExportReady?>()
    val archiveExportReady: LiveData<ArchiveExportReady?> = _archiveExportReady

    private val _archivePreview = MutableLiveData<ArchivePreview?>()
    val archivePreview: LiveData<ArchivePreview?> = _archivePreview

    private val _sharedTablesPreview = MutableLiveData<SharedTablesPreview?>()
    val sharedTablesPreview: LiveData<SharedTablesPreview?> = _sharedTablesPreview

    fun clearExport() {
        _exportBytes.value = null
    }

    fun clearArchiveExportReady() {
        _archiveExportReady.value = null
    }

    fun confirmArchiveExport() {
        val ready = _archiveExportReady.value ?: return
        _archiveExportReady.value = null
        _exportBytes.value = ready.fileName to ready.bytes
    }

    fun clearArchivePreview() {
        _archivePreview.value = null
    }

    fun clearSharedTablesPreview() {
        _sharedTablesPreview.value = null
    }

    fun clearImportFailure() {
        _importFailure.value = null
    }

    fun requestSharedTablesExport() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.IO) { loadSharedTablesSnapshot() }
            val name = FamilySharedTablesConfiguration.proposedFileName()
            val bytes = FamilyCatalogWriter.toCsvBytes(snapshot)
            _exportBytes.value = name to bytes
        }
    }

    fun requestArchiveExport() {
        viewModelScope.launch {
            val packed = withContext(Dispatchers.IO) {
                val snapshot = loadArchiveSnapshot()
                val csv = FamilyMergeWriter.toCsvBytes(snapshot)
                val photos =
                    com.example.boxmanagernew.data.photo.ObjectPhotoStoreProvider
                        .get(appContext)
                        .zipEntriesForBackup()
                val count = photos.keys.count {
                    it.endsWith(".jpg") && !it.contains("_thumb")
                }
                val bytesApprox = photos.values.sumOf { it.size.toLong() }
                val zip = com.example.boxmanagernew.family.zip.FamilyArchiveZip.pack(
                    csv,
                    photos
                )
                val name = FamilyMergeConfiguration.proposedZipFileName()
                val summary = appContext.getString(
                    R.string.family_msg_photo_summary,
                    count,
                    formatMb(bytesApprox)
                )
                ArchiveExportReady(name, zip, summary)
            }
            _archiveExportReady.value = packed
        }
    }

    private fun formatMb(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return String.format(java.util.Locale.ITALY, "%.1f", mb)
    }

    fun importSharedTablesText(text: String) {
        viewModelScope.launch {
            when (val parsed = catalogReader.parse(text)) {
                is FamilyCatalogReader.Result.Error -> {
                    _message.value = parsed.message
                }
                is FamilyCatalogReader.Result.Ok -> {
                    val preview = withContext(Dispatchers.IO) {
                        buildSharedTablesPreview(parsed.snapshot)
                    }
                    if (preview == null) {
                        return@launch
                    }
                    _sharedTablesPreview.value = preview
                }
            }
        }
    }

    fun importArchiveText(text: String) {
        importArchiveBytes(text.toByteArray(Charsets.UTF_8))
    }

    fun importArchiveBytes(bytes: ByteArray) {
        viewModelScope.launch {
            val csvText: String
            val photos: Map<String, ByteArray>
            if (com.example.boxmanagernew.family.zip.FamilyArchiveZip.looksLikeZip(bytes)) {
                val unpacked =
                    com.example.boxmanagernew.family.zip.FamilyArchiveZip.unpack(bytes)
                if (unpacked == null) {
                    _message.value = appContext.getString(R.string.family_msg_read_failed)
                    return@launch
                }
                csvText = unpacked.csvBytes.toString(Charsets.UTF_8)
                photos = unpacked.photoEntries
            } else {
                csvText = bytes.toString(Charsets.UTF_8)
                photos = emptyMap()
            }
            when (val parsed = mergeReader.parse(csvText)) {
                is FamilyMergeReader.Result.Error -> {
                    _message.value = parsed.message
                }
                is FamilyMergeReader.Result.Ok -> {
                    val preview = withContext(Dispatchers.IO) {
                        buildArchivePreview(
                            parsed.snapshot,
                            parsed.skippedRows,
                            photos
                        )
                    }
                    if (preview == null) {
                        return@launch
                    }
                    _archivePreview.value = preview
                }
            }
        }
    }

    fun confirmSharedTablesImport() {
        val current = _sharedTablesPreview.value ?: return
        if (!current.plan.canApply) {
            _sharedTablesPreview.value = null
            _message.value = appContext.getString(R.string.family_msg_no_changes)
            return
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    sharedTablesApplier.apply(current.plan)
                }
                _sharedTablesPreview.value = null
                _message.value = buildString {
                    appendLine(appContext.getString(R.string.family_msg_receive_completed))
                    append(
                        current.summary.removePrefix(
                            appContext.getString(R.string.family_preview_shared_prefix)
                        )
                    )
                }
            } catch (error: Exception) {
                _sharedTablesPreview.value = null
                _importFailure.value = appContext.getString(
                    R.string.family_msg_import_blocked,
                    error.message?.takeIf { it.isNotBlank() }
                        ?: appContext.getString(R.string.family_msg_write_failed)
                )
            }
        }
    }

    fun confirmArchiveImport() {
        val current = _archivePreview.value ?: return
        if (!current.plan.canApply) {
            _archivePreview.value = null
            _message.value = appContext.getString(R.string.family_msg_nothing_to_merge)
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val deleteObjectIds =
                    current.plan.inventoryPlan.objectsToDelete.toSet()
                val deletePermanentIds =
                    if (deleteObjectIds.isEmpty()) {
                        emptyList()
                    } else {
                        objectRepository.getAllObjectEntitiesSync()
                            .filter { it.id in deleteObjectIds }
                            .map { it.objectPermanentId }
                    }
                mergeApplier.apply(current.plan)
                val photoStore =
                    com.example.boxmanagernew.data.photo.ObjectPhotoStoreProvider
                        .get(appContext)
                if (deletePermanentIds.isNotEmpty()) {
                    photoStore.deletePhotos(deletePermanentIds)
                }
                if (current.photoEntries.isNotEmpty()) {
                    val keepIds = objectRepository.getAllObjectEntitiesSync()
                        .map { it.objectPermanentId }
                        .filter { it.isNotBlank() }
                        .toSet()
                    photoStore.mergeFromZipEntries(current.photoEntries, keepIds)
                }
            }
            _archivePreview.value = null
            _message.value = buildString {
                appendLine(appContext.getString(R.string.family_msg_receive_completed))
                append(
                    current.summary.removePrefix(
                        appContext.getString(R.string.family_preview_archive_prefix)
                    )
                )
            }
        }
    }

    private suspend fun buildSharedTablesPreview(
        incoming: FamilyCatalogSnapshot
    ): SharedTablesPreview? {
        val categories = categoryRepository.getAllCategoryEntitiesSync()
        val locations = locationRepository.getAllLocationEntitiesSync()
        val boxes = boxRepository.getAllBoxEntitiesSync()

        val categoryBoxCounts = categories.associate { category ->
            category.id to boxes.count { it.categoryId == category.id }
        }
        val locationBoxCounts = locations.associate { location ->
            location.id to boxes.count {
                it.position.equals(location.name, ignoreCase = true)
            }
        }

        val plan = sharedTablesMerger.plan(
            incoming = incoming,
            localCategories = categories,
            localLocations = locations,
            categoryBoxCounts = categoryBoxCounts,
            locationBoxCounts = locationBoxCounts
        )

        if (plan.hasBlockingErrors) {
            _importFailure.postValue(
                formatSharedTablesBlocking(plan)
            )
            return null
        }

        if (!plan.canApply) {
            _message.postValue(
                appContext.getString(R.string.family_msg_tables_aligned)
            )
            return null
        }

        val summary = buildString {
            appendLine(appContext.getString(R.string.family_preview_shared_title))
            appendLine(
                appContext.getString(
                    R.string.family_preview_shared_categories,
                    plan.categoriesToInsert.size,
                    plan.categoriesToUpdate.size,
                    plan.categoriesToRemove.size
                )
            )
            append(
                appContext.getString(
                    R.string.family_preview_shared_locations,
                    plan.locationsToInsert.size,
                    plan.locationsToRemove.size
                )
            )
        }

        return SharedTablesPreview(summary = summary, plan = plan)
    }

    private suspend fun buildArchivePreview(
        incoming: FamilyMergeSnapshot,
        skippedRows: Int = 0,
        photoEntries: Map<String, ByteArray> = emptyMap()
    ): ArchivePreview? {
        val localBoxes = boxRepository.getAllBoxEntitiesSync()
        val localObjects = objectRepository.getAllObjectEntitiesSync()
        val categories = categoryRepository.getAllCategoryEntitiesSync()
        val locations = locationRepository.getAllLocationEntitiesSync()
        val objectTypes = database.objectTypeDao().getAllTypesSync()
        val objectTypeNames = objectTypes.associate { it.id to it.name }

        val plan = mergeMerger.plan(
            incoming = incoming,
            localBoxes = localBoxes,
            localObjects = localObjects,
            existingCategoryNames = categories.map { it.name },
            existingLocationNames = locations.map { it.name },
            objectTypeNames = objectTypeNames,
            localTombstones = database.familyDeletionTombstoneDao().getAllSync()
        )

        if (plan.inventoryPlan.blockingErrors.isNotEmpty()) {
            _importFailure.postValue(
                appContext.getString(
                    R.string.family_msg_import_blocked,
                    plan.inventoryPlan.blockingErrors.joinToString("\n")
                )
            )
            return null
        }

        if (!plan.canApply && !plan.hasConflicts) {
            _message.postValue(
                appContext.getString(R.string.family_msg_nothing_to_merge)
            )
            return null
        }

        val photoCount = photoEntries.keys.count {
            it.endsWith(".jpg") && !it.contains("_thumb")
        }

        val summary = buildString {
            appendLine(appContext.getString(R.string.family_preview_archive_title))
            if (skippedRows > 0) {
                appendLine(
                    appContext.getString(
                        R.string.family_preview_skipped_rows,
                        skippedRows
                    )
                )
            }
            if (plan.healedCategories.isNotEmpty() || plan.healedLocations.isNotEmpty()) {
                appendLine(
                    appContext.getString(
                        R.string.family_preview_healed,
                        plan.healedCategories.size,
                        plan.healedLocations.size
                    )
                )
            }
            appendLine(
                appContext.getString(
                    R.string.family_preview_boxes,
                    plan.inventoryPlan.boxesToInsert.size,
                    plan.inventoryPlan.boxesToUpdate.size,
                    plan.inventoryPlan.boxConflicts.size,
                    plan.inventoryPlan.boxesIgnored
                )
            )
            append(
                appContext.getString(
                    R.string.family_preview_objects,
                    plan.inventoryPlan.objectsToInsert.size,
                    plan.inventoryPlan.objectsToUpdate.size,
                    plan.inventoryPlan.objectConflicts.size,
                    plan.inventoryPlan.objectsIgnored
                )
            )
            val deletes =
                plan.inventoryPlan.boxesToDelete.size +
                    plan.inventoryPlan.objectsToDelete.size
            if (deletes > 0 || plan.inventoryPlan.deletionConflicts.isNotEmpty()) {
                appendLine()
                append(
                    appContext.getString(
                        R.string.family_preview_deletes,
                        plan.inventoryPlan.boxesToDelete.size,
                        plan.inventoryPlan.objectsToDelete.size,
                        plan.inventoryPlan.deletionConflicts.size
                    )
                )
            }
            if (photoCount > 0) {
                appendLine()
                append(
                    appContext.getString(
                        R.string.family_preview_photos,
                        photoCount
                    )
                )
            }
            if (plan.hasConflicts) {
                appendLine()
                append(
                    appContext.getString(R.string.family_preview_conflicts_note)
                )
            }
        }

        return ArchivePreview(
            summary = summary,
            plan = plan,
            photoEntries = photoEntries
        )
    }

    private suspend fun loadSharedTablesSnapshot(): FamilyCatalogSnapshot {
        val categories =
            categoryRepository.getAllCategoryEntitiesSync().map {
                FamilyCatalogCategory(
                    name = it.name,
                    icon = it.icon.ifBlank {
                        FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
                    }
                )
            }
        val locations =
            locationRepository.getAllLocationEntitiesSync().map {
                FamilyCatalogLocation(name = it.name)
            }
        return FamilyCatalogSnapshot(
            categories = categories,
            locations = locations
        )
    }

    private suspend fun loadArchiveSnapshot(): FamilyMergeSnapshot {
        val shared = loadSharedTablesSnapshot()
        val boxes = boxRepository.getAllBoxEntitiesSync()
        val categoryNames = database.categoryDao().getAllSync()
            .associate { it.id to it.name }
        val objects = objectRepository.getAllObjectEntitiesSync()
        val objectTypes = database.objectTypeDao().getAllTypesSync()
            .associate { it.id to it.name }
        val boxPermanentIds = boxes.associate { it.id to it.permanentId }

        val inventoryBoxes = boxes.map { box ->
            FamilyInventoryBox(
                permanentId = box.permanentId,
                name = box.name,
                category = categoryNames[box.categoryId].orEmpty(),
                position = box.position,
                lastModified = box.lastModified,
                createdBy = box.createdBy
            )
        }

        val inventoryObjects = objects.mapNotNull { obj ->
            val objectId = obj.objectPermanentId.trim()
            val boxPermanentId = boxPermanentIds[obj.boxId].orEmpty().trim()
            if (objectId.isEmpty() || boxPermanentId.isEmpty()) {
                return@mapNotNull null
            }
            FamilyInventoryObject(
                objectPermanentId = objectId,
                boxPermanentId = boxPermanentId,
                typeName = objectTypes[obj.typeObjectId].orEmpty(),
                description = obj.description,
                quantity = obj.quantity,
                lastModified = obj.lastModified,
                createdBy = obj.createdBy
            )
        }

        val deletions = database.familyDeletionTombstoneDao().getAllSync().map { tombstone ->
            FamilyDeletion(
                entityType = tombstone.entityType,
                permanentId = tombstone.permanentId,
                deletedAt = tombstone.deletedAt,
                deletedBy = tombstone.deletedBy
            )
        }

        return FamilyMergeSnapshot(
            catalog = shared,
            inventory = FamilyInventorySnapshot(
                boxes = inventoryBoxes,
                objects = inventoryObjects,
                deletions = deletions
            )
        )
    }

    private fun formatSharedTablesBlocking(
        plan: SharedTablesMerger.Plan
    ): String {
        val lines = buildList {
            for (removal in plan.blockedCategoryRemovals) {
                add(
                    appContext.getString(
                        R.string.family_msg_category_in_use_block,
                        removal.entity.name,
                        removal.boxCount
                    )
                )
            }
            for (removal in plan.blockedLocationRemovals) {
                add(
                    appContext.getString(
                        R.string.family_msg_location_in_use_block,
                        removal.entity.name,
                        removal.boxCount
                    )
                )
            }
        }
        return appContext.getString(
            R.string.family_msg_import_blocked,
            lines.joinToString("\n")
        )
    }
}
