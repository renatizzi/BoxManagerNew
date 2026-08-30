package com.example.boxmanagernew.ui.family

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.family.FamilyMergeCopy
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.merge.FamilyMergeApplier
import com.example.boxmanagernew.family.merge.FamilyMergeMerger
import com.example.boxmanagernew.family.merge.FamilyMergeReader
import com.example.boxmanagernew.family.merge.FamilyMergeWriter
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyMergeViewModel(
    private val database: AppDatabase,
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val reader: FamilyMergeReader = FamilyMergeReader(),
    private val merger: FamilyMergeMerger = FamilyMergeMerger(),
    private val applier: FamilyMergeApplier = FamilyMergeApplier(database)
) : ViewModel() {

    data class Preview(
        val summary: String,
        val plan: FamilyMergeMerger.Plan
    )

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _exportBytes = MutableLiveData<Pair<String, ByteArray>?>()
    val exportBytes: LiveData<Pair<String, ByteArray>?> = _exportBytes

    private val _preview = MutableLiveData<Preview?>()
    val preview: LiveData<Preview?> = _preview

    fun clearExport() {
        _exportBytes.value = null
    }

    fun clearPreview() {
        _preview.value = null
    }

    fun requestExport() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.IO) { loadSnapshot() }
            val name = FamilyMergeConfiguration.proposedFileName()
            val bytes = FamilyMergeWriter.toCsvBytes(snapshot)
            _exportBytes.value = name to bytes
        }
    }

    fun importMergeText(text: String) {
        viewModelScope.launch {
            when (val parsed = reader.parse(text)) {
                is FamilyMergeReader.Result.Error -> {
                    _message.value = parsed.message
                }
                is FamilyMergeReader.Result.Ok -> {
                    val preview = withContext(Dispatchers.IO) {
                        buildPreview(parsed.snapshot, parsed.skippedRows)
                    }
                    if (preview == null) {
                        return@launch
                    }
                    _preview.value = preview
                }
            }
        }
    }

    fun confirmImport() {
        val current = _preview.value ?: return
        if (!current.plan.canApply) {
            _preview.value = null
            _message.value = "Nessuna novità da unire."
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                applier.apply(current.plan)
            }
            _preview.value = null
            _message.value = buildString {
                appendLine(FamilyMergeCopy.MSG_RECEIVE_COMPLETED)
                append(current.summary.removePrefix("Anteprima condivisione archivio:\n"))
            }
        }
    }

    private suspend fun buildPreview(
        incoming: FamilyMergeSnapshot,
        skippedRows: Int = 0
    ): Preview? {
        val localBoxes = boxRepository.getAllBoxEntitiesSync()
        val localObjects = objectRepository.getAllObjectEntitiesSync()
        val categories = categoryRepository.getAllCategoryEntitiesSync()
        val locations = locationRepository.getAllLocationEntitiesSync()
        val objectTypes = database.objectTypeDao().getAllTypesSync()
        val objectTypeNames = objectTypes.associate { it.id to it.name }

        val plan = merger.plan(
            incoming = incoming,
            localBoxes = localBoxes,
            localObjects = localObjects,
            existingCategoryNames = categories.map { it.name },
            existingLocationNames = locations.map { it.name },
            objectTypeNames = objectTypeNames
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
            appendLine(
                "Struttura: ${plan.categoriesToInsert.size} categorie, " +
                    "${plan.locationsToInsert.size} posizioni da aggiungere."
            )
            if (plan.healedCategories.isNotEmpty() || plan.healedLocations.isNotEmpty()) {
                appendLine(
                    "Ripristinate dai contenitori: " +
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
            if (plan.hasConflicts) {
                appendLine()
                append(
                    "I conflitti non verranno sovrascritti " +
                        "(versione locale più recente o uguale)."
                )
            }
        }

        return Preview(summary = summary, plan = plan)
    }

    private suspend fun loadSnapshot(): FamilyMergeSnapshot {
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
                lastModified = box.lastModified
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
                lastModified = obj.lastModified
            )
        }

        return FamilyMergeSnapshot(
            catalog = FamilyCatalogSnapshot(
                categories = categories,
                locations = locations
            ),
            inventory = FamilyInventorySnapshot(
                boxes = inventoryBoxes,
                objects = inventoryObjects
            )
        )
    }
}
