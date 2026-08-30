package com.example.boxmanagernew.ui.family

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.domain.model.Location
import com.example.boxmanagernew.family.catalog.FamilyCatalogMerger
import com.example.boxmanagernew.family.catalog.FamilyCatalogReader
import com.example.boxmanagernew.family.catalog.FamilyCatalogWriter
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyCatalogViewModel(
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val reader: FamilyCatalogReader = FamilyCatalogReader(),
    private val merger: FamilyCatalogMerger = FamilyCatalogMerger()
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _exportBytes = MutableLiveData<Pair<String, ByteArray>?>()
    val exportBytes: LiveData<Pair<String, ByteArray>?> = _exportBytes

    fun clearExport() {
        _exportBytes.value = null
    }

    fun requestExport() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.IO) { loadSnapshot() }
            val name = FamilyCatalogConfiguration.proposedFileName()
            val bytes = FamilyCatalogWriter.toCsvBytes(snapshot)
            _exportBytes.value = name to bytes
            _message.value =
                "Catalogo pronto: ${snapshot.categories.size} categorie, " +
                    "${snapshot.locations.size} posizioni."
        }
    }

    fun importCatalogText(text: String) {
        viewModelScope.launch {
            when (val parsed = reader.parse(text)) {
                is FamilyCatalogReader.Result.Error -> {
                    _message.value = parsed.message
                }
                is FamilyCatalogReader.Result.Ok -> {
                    applyIncoming(parsed.snapshot)
                }
            }
        }
    }

    private suspend fun applyIncoming(incoming: FamilyCatalogSnapshot) {
        val result = withContext(Dispatchers.IO) {
            val categories =
                categoryRepository.getAllCategoryEntitiesSync()
            val locations =
                locationRepository.getAllLocationEntitiesSync()
            val plan = merger.plan(
                incoming = incoming,
                existingCategoryNames = categories.map { it.name },
                existingLocationNames = locations.map { it.name }
            )

            for (category in plan.categoriesToInsert) {
                categoryRepository.insert(
                    Category(
                        name = category.name,
                        icon = category.icon.ifBlank {
                            FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
                        }
                    )
                )
            }
            for (location in plan.locationsToInsert) {
                locationRepository.insert(
                    Location(name = location.name)
                )
            }
            plan
        }

        _message.value = buildString {
            appendLine("Catalogo Famiglia applicato.")
            appendLine(
                "Aggiunte: ${result.categoriesToInsert.size} categorie, " +
                    "${result.locationsToInsert.size} posizioni."
            )
            append(
                "Già presenti (ignorate): ${result.ignoredCategories} categorie, " +
                    "${result.ignoredLocations} posizioni."
            )
        }
    }

    private suspend fun loadSnapshot(): FamilyCatalogSnapshot {
        val categories =
            categoryRepository.getAllCategoryEntitiesSync().map {
                com.example.boxmanagernew.family.model.FamilyCatalogCategory(
                    name = it.name,
                    icon = it.icon.ifBlank {
                        FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
                    }
                )
            }
        val locations =
            locationRepository.getAllLocationEntitiesSync().map {
                com.example.boxmanagernew.family.model.FamilyCatalogLocation(
                    name = it.name
                )
            }
        return FamilyCatalogSnapshot(
            categories = categories,
            locations = locations
        )
    }
}
