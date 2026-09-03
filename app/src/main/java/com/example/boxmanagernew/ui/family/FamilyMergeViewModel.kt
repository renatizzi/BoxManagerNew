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
        val plan: FamilyMergeMerger.Plan
    )

    data class SharedTablesPreview(
        val summary: String,
        val plan: SharedTablesMerger.Plan
    )

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _exportBytes = MutableLiveData<Pair<String, ByteArray>?>()
    val exportBytes: LiveData<Pair<String, ByteArray>?> = _exportBytes

    private val _archivePreview = MutableLiveData<ArchivePreview?>()
    val archivePreview: LiveData<ArchivePreview?> = _archivePreview

    private val _sharedTablesPreview = MutableLiveData<SharedTablesPreview?>()
    val sharedTablesPreview: LiveData<SharedTablesPreview?> = _sharedTablesPreview

    fun clearExport() {
        _exportBytes.value = null
    }

    fun clearArchivePreview() {
        _archivePreview.value = null
    }

    fun clearSharedTablesPreview() {
        _sharedTablesPreview.value = null
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
            val snapshot = withContext(Dispatchers.IO) { loadArchiveSnapshot() }
            val name = FamilyMergeConfiguration.proposedFileName()
            val bytes = FamilyMergeWriter.toCsvBytes(snapshot)
            _exportBytes.value = name to bytes
        }
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
        viewModelScope.launch {
            when (val parsed = mergeReader.parse(text)) {
                is FamilyMergeReader.Result.Error -> {
                    _message.value = parsed.message
                }
                is FamilyMergeReader.Result.Ok -> {
                    val preview = withContext(Dispatchers.IO) {
                        buildArchivePreview(parsed.snapshot, parsed.skippedRows)
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
            _message.value = "Nessuna modifica da applicare."
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedTablesApplier.apply(current.plan)
            }
            _sharedTablesPreview.value = null
            _message.value = buildString {
                appendLine(appContext.getString(R.string.family_msg_receive_completed))
                append(
                    current.summary.removePrefix(
                        "Anteprima tabelle condivise:\n"
                    )
                )
            }
        }
    }

    fun confirmArchiveImport() {
        val current = _archivePreview.value ?: return
        if (!current.plan.canApply) {
            _archivePreview.value = null
            _message.value = "Nessuna novità da unire."
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mergeApplier.apply(current.plan)
            }
            _archivePreview.value = null
            _message.value = buildString {
                appendLine(appContext.getString(R.string.family_msg_receive_completed))
                append(
                    current.summary.removePrefix(
                        "Anteprima condivisione archivio:\n"
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

        if (plan.blockingErrors.isNotEmpty()) {
            _message.postValue(plan.blockingErrors.joinToString("\n"))
            return null
        }

        if (!plan.canApply) {
            _message.postValue(
                "Le tabelle locali sono già allineate alle tabelle condivise."
            )
            return null
        }

        val summary = buildString {
            appendLine("Anteprima tabelle condivise:")
            appendLine(
                "Categorie: ${plan.categoriesToInsert.size} da aggiungere, " +
                    "${plan.categoriesToUpdate.size} da aggiornare, " +
                    "${plan.categoriesToRemove.size} da rimuovere."
            )
            append(
                "Posizioni: ${plan.locationsToInsert.size} da aggiungere, " +
                    "${plan.locationsToRemove.size} da rimuovere."
            )
        }

        return SharedTablesPreview(summary = summary, plan = plan)
    }

    private suspend fun buildArchivePreview(
        incoming: FamilyMergeSnapshot,
        skippedRows: Int = 0
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
            _message.postValue(
                plan.inventoryPlan.blockingErrors.joinToString("\n")
            )
            return null
        }

        if (!plan.canApply && !plan.hasConflicts) {
            _message.postValue("Nessuna novità da unire.")
            return null
        }

        val summary = buildString {
            appendLine("Anteprima condivisione archivio:")
            if (skippedRows > 0) {
                appendLine(
                    "Righe non valide ignorate nel file: $skippedRows."
                )
            }
            if (plan.healedCategories.isNotEmpty() || plan.healedLocations.isNotEmpty()) {
                appendLine(
                    "Categorie/posizioni aggiunte dai contenitori in arrivo: " +
                        "${plan.healedCategories.size} categorie, " +
                        "${plan.healedLocations.size} posizioni."
                )
            }
            appendLine(
                "Contenitori: ${plan.inventoryPlan.boxesToInsert.size} nuovi, " +
                    "${plan.inventoryPlan.boxesToUpdate.size} aggiornamenti, " +
                    "${plan.inventoryPlan.boxConflicts.size} conflitti, " +
                    "${plan.inventoryPlan.boxesIgnored} invariati."
            )
            append(
                "Oggetti: ${plan.inventoryPlan.objectsToInsert.size} nuovi, " +
                    "${plan.inventoryPlan.objectsToUpdate.size} aggiornamenti, " +
                    "${plan.inventoryPlan.objectConflicts.size} conflitti, " +
                    "${plan.inventoryPlan.objectsIgnored} invariati."
            )
            val deletes =
                plan.inventoryPlan.boxesToDelete.size +
                    plan.inventoryPlan.objectsToDelete.size
            if (deletes > 0 || plan.inventoryPlan.deletionConflicts.isNotEmpty()) {
                appendLine()
                append(
                    "Cancellazioni: ${plan.inventoryPlan.boxesToDelete.size} contenitori, " +
                        "${plan.inventoryPlan.objectsToDelete.size} oggetti, " +
                        "${plan.inventoryPlan.deletionConflicts.size} conflitti."
                )
            }
            if (plan.hasConflicts) {
                appendLine()
                append(
                    "I conflitti non verranno sovrascritti " +
                        "(versione locale più recente o uguale)."
                )
            }
        }

        return ArchivePreview(summary = summary, plan = plan)
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
}
