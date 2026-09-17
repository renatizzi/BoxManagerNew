package com.example.boxmanagernew.ui.boxdetail

import androidx.lifecycle.*
import android.net.Uri
import com.example.boxmanagernew.data.photo.ObjectPhotoStore
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.data.trash.TrashStore
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.domain.search.ObjectSearchMatcher
import com.example.boxmanagernew.util.SimpleSearch
import kotlinx.coroutines.launch

class ObjectViewModel(
    private val repository: ObjectRepositoryImpl,
    private val trashStore: TrashStore,
    private val photoStore: ObjectPhotoStore
) : ViewModel() {

    private val _selectedItems = MutableLiveData<Set<Int>>(emptySet())
    val selectedItems: LiveData<Set<Int>> = _selectedItems

    private val _selectionMode = MutableLiveData(false)
    val selectionMode: LiveData<Boolean> = _selectionMode

    data class TrashUndoEvent(
        val objectIds: List<Int> = emptyList()
    )

    private val _trashUndoEvent =
        MutableLiveData<TrashUndoEvent?>()

    val trashUndoEvent: LiveData<TrashUndoEvent?> =
        _trashUndoEvent

    fun consumeTrashUndoEvent() {
        _trashUndoEvent.value = null
    }

    private val _objects = MediatorLiveData<List<ObjectWithType>>()
    val objects: LiveData<List<ObjectWithType>> = _objects

    private val _isAscending = MutableLiveData(true)
    val isAscending: LiveData<Boolean> = _isAscending

    private val _hasHiddenSelections = MutableLiveData(false)
    val hasHiddenSelections: LiveData<Boolean> = _hasHiddenSelections

    private var currentSource: LiveData<List<ObjectWithType>>? = null
    private var lastSource: List<ObjectWithType> = emptyList()
    private var lastFiltered: List<ObjectWithType> = emptyList()

    private var currentQuery: String = ""

    private var matchWholeWords: Boolean = false

    private var loadedBoxId: Int? = null

    init {

        _objects.addSource(_selectedItems) {

            updateHiddenSelectionState()
        }
    }

    fun load(boxId: Int) {

        if (
            loadedBoxId == boxId &&
            currentSource != null
        ) {

            applyFilterAndSort()
            return
        }

        currentSource?.let {
            _objects.removeSource(it)
        }

        val source =
            repository.getObjectsWithType(boxId)

        currentSource =
            source

        loadedBoxId =
            boxId

        _objects.addSource(source) {

            lastSource = it
            applyFilterAndSort()
        }
    }

    fun filter(query: String) {
        matchWholeWords = false
        currentQuery = query.trim()
        applyFilterAndSort()
    }

    fun filterByWholeWords(query: String) {
        matchWholeWords = true
        currentQuery = query.trim()
        applyFilterAndSort()
    }

    fun toggleSort() {
        _isAscending.value = !(_isAscending.value ?: true)
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {

        var result = lastSource

        if (matchWholeWords) {

            if (currentQuery.isNotBlank()) {

                result =
                    result.filter { item ->

                        ObjectSearchMatcher.matchesAnyPackedTerm(
                            item.typeName,
                            item.obj.description,
                            currentQuery
                        )
                    }
            }

        } else if (
            SimpleSearch.needle(currentQuery).isNotEmpty()
        ) {

            result =
                result.filter { item ->

                    SimpleSearch.matchesAny(
                        currentQuery,
                        item.typeName,
                        item.obj.description
                    )
                }
        }

        val asc =
            _isAscending.value ?: true

        result =
            if (asc)
                result.sortedBy { it.typeName }
            else
                result.sortedByDescending { it.typeName }

        lastFiltered = result
        _objects.value = result

        updateHiddenSelectionState()
    }

    private fun updateHiddenSelectionState() {

        val selected =
            _selectedItems.value
                ?: emptySet()

        if (selected.isEmpty()) {

            _hasHiddenSelections.value =
                false

            return
        }

        val visibleIds =
            lastFiltered
                .map { it.obj.id }
                .toSet()

        _hasHiddenSelections.value =
            selected.any {
                it !in visibleIds
            }
    }

    fun toggleSelection(id: Int) {

        val current =
            _selectedItems.value
                ?: emptySet()

        val updated =
            current.toMutableSet()

        if (updated.contains(id))
            updated.remove(id)
        else
            updated.add(id)

        _selectedItems.value =
            updated

        _selectionMode.value =
            updated.isNotEmpty()
    }

    fun clearSelection() {

        _selectedItems.value =
            emptySet()

        _selectionMode.value =
            false
    }

    fun deleteObjects(
        ids: List<Int>,
        deletedBy: String = ""
    ) {

        if (ids.isEmpty()) {
            return
        }

        viewModelScope.launch {
            trashStore.softDeleteObjects(ids)
            _trashUndoEvent.value =
                TrashUndoEvent(objectIds = ids)
            clearSelection()
        }
    }

    fun moveObjects(
        targetBoxId: Int
    ) {

        val ids =
            _selectedItems.value
                ?.toList()
                ?: return

        if (ids.isEmpty())
            return

        viewModelScope.launch {

            repository.moveObjects(
                ids,
                targetBoxId
            )

            clearSelection()
        }
    }

    fun addObject(
        name: String,
        boxId: Int,
        description: String?,
        quantity: Int?,
        createdBy: String = "",
        photoUri: Uri? = null,
        photoFile: java.io.File? = null,
        removePhoto: Boolean = false
    ) {

        if (name.isBlank())
            return

        viewModelScope.launch {

            val permanentId =
                repository.insertDynamic(
                    name,
                    boxId,
                    description,
                    quantity,
                    createdBy
                )
            if (permanentId.isBlank()) return@launch
            when {
                removePhoto -> photoStore.deletePhoto(permanentId)
                photoFile != null -> photoStore.saveFromFile(permanentId, photoFile)
                photoUri != null -> photoStore.saveFromUri(permanentId, photoUri)
            }
        }
    }

    fun updateObjectWithName(
        id: Int,
        name: String,
        boxId: Int,
        description: String?,
        quantity: Int?,
        photoUri: Uri? = null,
        photoFile: java.io.File? = null,
        removePhoto: Boolean = false
    ) {

        viewModelScope.launch {

            repository.updateWithName(
                id,
                name,
                boxId,
                description,
                quantity
            )
            val entity = repository.getObjectEntityByIdAny(id) ?: return@launch
            val permanentId = entity.objectPermanentId
            when {
                removePhoto -> photoStore.deletePhoto(permanentId)
                photoFile != null -> photoStore.saveFromFile(permanentId, photoFile)
                photoUri != null -> photoStore.saveFromUri(permanentId, photoUri)
            }
        }
    }

    fun deleteObject(
        obj: Object
    ) {

        viewModelScope.launch {
            trashStore.softDeleteObjects(listOf(obj.id))
            _trashUndoEvent.value =
                TrashUndoEvent(objectIds = listOf(obj.id))
            clearSelection()
        }
    }

    fun undoTrashObjects(objectIds: List<Int>) {
        viewModelScope.launch {
            for (id in objectIds) {
                trashStore.restoreObjectFromTrash(id)
            }
            consumeTrashUndoEvent()
        }
    }
}