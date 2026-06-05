package com.example.boxmanagernew.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.util.CanonicalNormalizer
import kotlinx.coroutines.launch

class BoxViewModel(
    private val repository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl
) : ViewModel() {

    companion object {

        const val FILTER_EMPTY_BOXES =
            "__EMPTY_BOXES__"
    }

    private val source =
        repository.getAllBoxesLive()

    private val _boxes =
        MediatorLiveData<List<Box>>()

    val boxes: LiveData<List<Box>> =
        _boxes

    private val _isAscending =
        MutableLiveData(true)

    val isAscending =
        _isAscending

    private val _currentQuery =
        MutableLiveData("")

    val currentQuery =
        _currentQuery

    private var lastSource =
        emptyList<Box>()

    private var lastFiltered =
        emptyList<Box>()

    private val _categories =
        MutableLiveData<List<CategoryEntity>>(emptyList())

    private val categories
        get() =
            _categories.value ?: emptyList()

    private val _selectedItems =
        MutableLiveData<Set<Int>>(emptySet())

    val selectedItems =
        _selectedItems

    private val _selectionMode =
        MutableLiveData(false)

    val selectionMode =
        _selectionMode

    private val _hasHiddenSelections =
        MutableLiveData(false)

    val hasHiddenSelections =
        _hasHiddenSelections

    init {

        _boxes.addSource(source) {

            lastSource = it

            applyFilterAndSort()
        }

        _boxes.addSource(_currentQuery) {

            applyFilterAndSort()
        }

        _boxes.addSource(_isAscending) {

            applyFilterAndSort()
        }

        _boxes.addSource(_categories) {

            applyFilterAndSort()
        }

        _boxes.addSource(_selectedItems) {

            updateHiddenSelectionState()
        }
    }

    fun setCategories(
        list: List<CategoryEntity>
    ) {

        _categories.value = list
    }

    fun addBox(
        name: String,
        categoryId: Int,
        position: String
    ) {

        viewModelScope.launch {

            repository.insertBox(
                Box(
                    0,
                    name,
                    null,
                    categoryId,
                    position,
                    System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun addBoxAndReturnId(
        name: String,
        categoryId: Int,
        position: String
    ): Int {

        return repository.insertBox(
            Box(
                0,
                name,
                null,
                categoryId,
                position,
                System.currentTimeMillis()
            )
        ).toInt()
    }

    fun updateBox(
        id: Int,
        newName: String,
        categoryId: Int,
        position: String
    ) {

        viewModelScope.launch {

            repository.updateBox(
                Box(
                    id,
                    newName,
                    null,
                    categoryId,
                    position,
                    System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteBox(
        id: Int
    ) {

        viewModelScope.launch {

            repository.deleteBox(id)

            clearSelection()
        }
    }

    fun deleteBoxes(
        ids: List<Int>
    ) {

        viewModelScope.launch {

            ids.forEach {

                repository.deleteBox(it)
            }

            clearSelection()
        }
    }

    fun moveBoxes(
        newPosition: String
    ) {

        val ids =
            _selectedItems.value?.toList()
                ?: return

        viewModelScope.launch {

            repository.moveBoxes(
                ids,
                newPosition
            )

            clearSelection()
        }
    }

    fun moveObjectsAndDeleteBoxes(
        objectRepository: ObjectRepositoryImpl,
        targetBoxId: Int
    ) { }

    fun toggleSort() {

        _isAscending.value =
            !(_isAscending.value ?: true)
    }

    fun filter(
        query: String
    ) {

        _currentQuery.value =
            query
    }

    fun toggleSelection(
        box: Box
    ) {

        val updated =
            (_selectedItems.value ?: emptySet())
                .toMutableSet()

        if (
            updated.contains(box.id)
        ) {
            updated.remove(box.id)
        } else {
            updated.add(box.id)
        }

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

    private fun applyFilterAndSort() {

        viewModelScope.launch {

            var result =
                lastSource

            val query =
                _currentQuery.value
                    ?.trim()
                    ?: ""

            if (
                query ==
                FILTER_EMPTY_BOXES
            ) {

                val emptyIds =
                    repository.getEmptyBoxIds()

                result =
                    result.filter {

                        emptyIds.contains(
                            it.id
                        )
                    }

            } else if (
                query.isNotBlank()
            ) {

                val canonicalQuery =
                    CanonicalNormalizer.canonical(
                        query
                    )

                val matchingBoxIds =
                    objectRepository
                        .searchObjects(query)
                        .map { it.boxId }
                        .toSet()

                result =
                    result.filter { box ->

                        val category =
                            categories.find {
                                it.id ==
                                        box.categoryId
                            }?.name
                                ?: ""

                        val searchable =
                            CanonicalNormalizer.canonical(
                                buildString {
                                    append(box.name)
                                    append(" ")
                                    append(box.position)
                                    append(" ")
                                    append(category)
                                }
                            )

                        searchable.contains(
                            canonicalQuery
                        ) ||
                                matchingBoxIds.contains(
                                    box.id
                                )
                    }
            }

            result =
                if (
                    _isAscending.value == true
                ) {

                    result.sortedBy {
                        it.name
                    }

                } else {

                    result.sortedByDescending {
                        it.name
                    }
                }

            lastFiltered =
                result

            _boxes.postValue(
                result
            )

            updateHiddenSelectionState()
        }
    }

    private fun updateHiddenSelectionState() {

        val visible =
            lastFiltered
                .map { it.id }
                .toSet()

        _hasHiddenSelections.value =
            (_selectedItems.value ?: emptySet())
                .any { it !in visible }
    }
}