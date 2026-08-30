package com.example.boxmanagernew.ui.family

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.family.FamilyCatalogCopy
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.inventory.FamilyInventoryApplier
import com.example.boxmanagernew.family.inventory.FamilyInventoryMerger
import com.example.boxmanagernew.family.inventory.FamilyInventoryReader
import com.example.boxmanagernew.family.inventory.FamilyInventoryWriter
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyInventoryViewModel(
    private val database: AppDatabase,
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val reader: FamilyInventoryReader = FamilyInventoryReader(),
    private val merger: FamilyInventoryMerger = FamilyInventoryMerger(),
    private val applier: FamilyInventoryApplier = FamilyInventoryApplier(database)
) : ViewModel() {

    data class Preview(
        val summary: String,
        val plan: FamilyInventoryMerger.Plan
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
            val name = FamilyInventoryConfiguration.proposedFileName()
            val bytes = FamilyInventoryWriter.toCsvBytes(snapshot)
            _exportBytes.value = name to bytes
        }
    }

    fun importInventoryText(text: String) {
        viewModelScope.launch {
            when (val parsed = reader.parse(text)) {
                is FamilyInventoryReader.Result.Error -> {
                    _message.value = parsed.message
                }
                is FamilyInventoryReader.Result.Ok -> {
                    val preview = withContext(Dispatchers.IO) {
                        buildPreview(parsed.snapshot)
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
            _message.value =
                "Nessuna novità da unire."
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                applier.apply(current.plan)
            }
            _preview.value = null
            _message.value = buildString {
                appendLine(FamilyCatalogCopy.MSG_RECEIVE_COMPLETED)
                append(current.summary.removePrefix("Anteprima unione inventario:\n"))
            }
        }
    }

    private suspend fun buildPreview(
        incoming: FamilyInventorySnapshot
    ): Preview? {
        val localBoxes = boxRepository.getAllBoxEntitiesSync()
        val localObjects = objectRepository.getAllObjectEntitiesSync()
        val categories = database.categoryDao().getAllSync()
        val categoryNames = categories.associate { it.id to it.name }
        val objectTypes = database.objectTypeDao().getAllTypesSync()
        val objectTypeNames = objectTypes.associate { it.id to it.name }
        val locations = database.locationDao().getAllLocationsSync()

        val plan = merger.plan(
            incoming = incoming,
            localBoxes = localBoxes,
            localObjects = localObjects,
            categoryNames = categoryNames,
            objectTypeNames = objectTypeNames,
            locationNames = locations.map { it.name }
        )

        if (plan.blockingErrors.isNotEmpty()) {
            _message.postValue(
                plan.blockingErrors.joinToString("\n")
            )
            return null
        }

        if (!plan.canApply && !plan.hasConflicts) {
            _message.postValue("Nessuna novità da unire.")
            return null
        }

        val summary = buildString {
            appendLine("Anteprima unione inventario:")
            appendLine(
                "Contenitori: ${plan.boxesToInsert.size} nuovi, " +
                    "${plan.boxesToUpdate.size} aggiornamenti, " +
                    "${plan.boxConflicts.size} conflitti, " +
                    "${plan.boxesIgnored} invariati."
            )
            append(
                "Oggetti: ${plan.objectsToInsert.size} nuovi, " +
                    "${plan.objectsToUpdate.size} aggiornamenti, " +
                    "${plan.objectConflicts.size} conflitti, " +
                    "${plan.objectsIgnored} invariati."
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

    private suspend fun loadSnapshot(): FamilyInventorySnapshot {
        val boxes = boxRepository.getAllBoxEntitiesSync()
        val categories = database.categoryDao().getAllSync()
            .associate { it.id to it.name }
        val objects = objectRepository.getAllObjectEntitiesSync()
        val objectTypes = database.objectTypeDao().getAllTypesSync()
            .associate { it.id to it.name }
        val boxPermanentIds = boxes.associate { it.id to it.permanentId }

        val inventoryBoxes = boxes.map { box ->
            FamilyInventoryBox(
                permanentId = box.permanentId,
                name = box.name,
                category = categories[box.categoryId].orEmpty(),
                position = box.position,
                lastModified = box.lastModified
            )
        }

        val inventoryObjects = objects.map { obj ->
            FamilyInventoryObject(
                objectPermanentId = obj.objectPermanentId,
                boxPermanentId = boxPermanentIds[obj.boxId].orEmpty(),
                typeName = objectTypes[obj.typeObjectId].orEmpty(),
                description = obj.description,
                quantity = obj.quantity,
                lastModified = obj.lastModified
            )
        }

        return FamilyInventorySnapshot(
            boxes = inventoryBoxes,
            objects = inventoryObjects
        )
    }
}
