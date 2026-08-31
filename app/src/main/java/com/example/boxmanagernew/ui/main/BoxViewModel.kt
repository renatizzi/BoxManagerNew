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
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.util.CanonicalNormalizer
import com.example.boxmanagernew.util.SimpleSearch
import kotlinx.coroutines.Job
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

    private var matchContainedObjectsOnly =
        false

    private var matchLocationOnly =
        false

    private var locationFilterTerms =
        ""

    private var matchCategoryOnly =
        false

    private var categoryFilterTerms =
        ""

    private var matchBoxNameOnly =
        false

    private var boxFilterTerms =
        ""

    private var filterJob: Job? =
        null

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
        position: String,
        createdBy: String = ""
    ) {

        viewModelScope.launch {

            repository.insertBox(
                Box(
                    0,
                    name,
                    null,
                    categoryId,
                    position,
                    System.currentTimeMillis(),
                    createdBy = createdBy
                )
            )
        }
    }

    suspend fun addBoxAndReturnId(
        name: String,
        categoryId: Int,
        position: String,
        createdBy: String = ""
    ): Int {

        return repository.insertBox(
            Box(
                0,
                name,
                null,
                categoryId,
                position,
                System.currentTimeMillis(),
                createdBy = createdBy
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

        matchContainedObjectsOnly =
            false

        matchLocationOnly =
            false

        locationFilterTerms =
            ""

        matchCategoryOnly =
            false

        categoryFilterTerms =
            ""

        matchBoxNameOnly =
            false

        boxFilterTerms =
            ""

        _currentQuery.value =
            query
    }

    fun isContainedObjectsFilter(): Boolean =
        matchContainedObjectsOnly

    fun filterByLocation(
        terms: String
    ) {

        matchContainedObjectsOnly =
            false

        matchLocationOnly =
            true

        locationFilterTerms =
            terms

        matchCategoryOnly =
            false

        categoryFilterTerms =
            ""

        matchBoxNameOnly =
            false

        boxFilterTerms =
            ""

        if (
            _currentQuery.value ==
            terms
        ) {

            applyFilterAndSort()

        } else {

            _currentQuery.value =
                terms
        }
    }

    fun filterByContainedObjects(
        terms: String
    ) {

        matchContainedObjectsOnly =
            true

        matchLocationOnly =
            false

        locationFilterTerms =
            ""

        matchCategoryOnly =
            false

        categoryFilterTerms =
            ""

        matchBoxNameOnly =
            false

        boxFilterTerms =
            ""

        if (
            _currentQuery.value ==
            terms
        ) {

            applyFilterAndSort()

        } else {

            _currentQuery.value =
                terms
        }
    }

    fun filterByCategory(
        terms: String,
        locationTerms: String = ""
    ) {

        matchContainedObjectsOnly =
            false

        matchLocationOnly =
            false

        locationFilterTerms =
            locationTerms

        matchCategoryOnly =
            true

        categoryFilterTerms =
            terms

        matchBoxNameOnly =
            false

        boxFilterTerms =
            ""

        val queryKey =
            if (
                locationTerms.isBlank()
            ) {
                terms
            } else {
                "$terms $locationTerms"
            }

        if (
            _currentQuery.value ==
            queryKey
        ) {

            applyFilterAndSort()

        } else {

            _currentQuery.value =
                queryKey
        }
    }

    fun filterByBoxNames(
        terms: String
    ) {

        matchContainedObjectsOnly =
            false

        matchLocationOnly =
            false

        locationFilterTerms =
            ""

        matchCategoryOnly =
            false

        categoryFilterTerms =
            ""

        matchBoxNameOnly =
            true

        boxFilterTerms =
            terms

        if (
            _currentQuery.value ==
            terms
        ) {

            applyFilterAndSort()

        } else {

            _currentQuery.value =
                terms
        }
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

        val query =
            _currentQuery.value
                ?.trim()
                ?: ""

        val containedOnly =
            matchContainedObjectsOnly

        val locationOnly =
            matchLocationOnly

        val categoryOnly =
            matchCategoryOnly

        val categoryTerms =
            categoryFilterTerms

        val locationTerms =
            locationFilterTerms

        val boxNameOnly =
            matchBoxNameOnly

        val boxTerms =
            boxFilterTerms

        filterJob?.cancel()

        filterJob =
            viewModelScope.launch {

            var result =
                lastSource

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
                boxNameOnly &&
                boxTerms.isNotBlank()
            ) {

                result =
                    result.filter { box ->

                        SearchConfiguration.splitLocationTerms(
                            boxTerms
                        ).any { name ->

                            CanonicalNormalizer.allTokensMatchWords(
                                name,
                                box.name
                            ) &&
                                    CanonicalNormalizer.allTokensMatchWords(
                                        box.name,
                                        name
                                    )
                        }
                    }

            } else if (
                categoryOnly &&
                categoryTerms.isNotBlank()
            ) {

                result =
                    result.filter { box ->

                        val categoryName =
                            categories.find {
                                it.id ==
                                        box.categoryId
                            }?.name
                                ?: ""

                        SearchConfiguration.splitLocationTerms(
                            categoryTerms
                        ).any { name ->

                            CanonicalNormalizer.allTokensMatchWords(
                                name,
                                categoryName
                            )
                        }
                    }

                if (
                    locationTerms.isNotBlank()
                ) {

                    result =
                        result.filter { box ->

                            SearchConfiguration.splitLocationTerms(
                                locationTerms
                            ).any { name ->

                                CanonicalNormalizer.allTokensMatchWords(
                                    name,
                                    box.position
                                )
                            }
                        }
                }

            } else if (
                locationOnly &&
                locationFilterTerms.isNotBlank()
            ) {

                result =
                    result.filter { box ->

                        SearchConfiguration.splitLocationTerms(
                            locationFilterTerms
                        ).any { name ->

                            CanonicalNormalizer.allTokensMatchWords(
                                name,
                                box.position
                            )
                        }
                    }

            } else if (
                query.isNotBlank() &&
                containedOnly
            ) {

                val matchingBoxIds =
                    objectRepository
                        .findBoxIdsByObjectTerms(
                            query
                        )

                result =
                    result.filter { box ->

                        matchingBoxIds.contains(
                            box.id
                        )
                    }

            } else if (
                SimpleSearch.needle(query).isNotEmpty()
            ) {

                val matchingBoxIds =
                    objectRepository
                        .searchObjectsInline(query)
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

                        SimpleSearch.matchesAny(
                            query,
                            box.name,
                            box.position,
                            category
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