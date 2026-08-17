package com.example.boxmanagernew.ui.categories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.util.CanonicalNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryViewModel(
    private val repository: CategoryRepositoryImpl
) : ViewModel() {

    companion object {

        const val FILTER_USED =
            "__USED_CATEGORIES__"
    }

    private val source =
        repository.getAllCategories()

    private val _categories =
        MediatorLiveData<List<Category>>()

    val categories =
        _categories

    private val _allCategoriesCount =
        MutableLiveData(0)

    val allCategoriesCount =
        _allCategoriesCount

    private val _isAscending =
        MutableLiveData(true)

    val isAscending =
        _isAscending

    private val _currentQuery =
        MutableLiveData("")

    private var boxLocationTerms =
        ""

    private var lastSource =
        emptyList<Category>()

    private val _operationResult =
        MutableLiveData<String?>()

    val operationResult =
        _operationResult

    private val _selectedCategory =
        MutableLiveData<Int?>()

    val selectedCategory =
        _selectedCategory

    init {

        _categories.addSource(source) {

            lastSource = it

            _allCategoriesCount.value =
                it.size

            applyFilterAndSort()
        }

        _categories.addSource(_currentQuery) {

            applyFilterAndSort()
        }

        _categories.addSource(_isAscending) {

            applyFilterAndSort()
        }
    }

    fun filter(
        query: String
    ) {

        boxLocationTerms =
            ""

        _currentQuery.value =
            query
    }

    fun filterByBoxLocation(
        terms: String
    ) {

        boxLocationTerms =
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

    fun toggleSort() {

        _isAscending.value =
            !(_isAscending.value ?: true)
    }

    private fun applyFilterAndSort() {

        var result =
            lastSource

        val query =
            _currentQuery.value
                ?.trim()
                ?: ""

        if (
            query ==
            FILTER_USED
        ) {

            result =
                result.filter {

                    runCatching {

                        kotlinx.coroutines.runBlocking {

                            repository.isCategoryUsed(
                                it.id
                            )
                        }

                    }.getOrDefault(false)
                }

        } else if (
            boxLocationTerms.isNotBlank()
        ) {

            val ids =
                runCatching {

                    kotlinx.coroutines.runBlocking {

                        repository.categoryIdsForLocation(
                            boxLocationTerms
                        )
                    }

                }.getOrDefault(emptySet())

            result =
                result.filter {
                    it.id in ids
                }

        } else if (
            query.isNotBlank()
        ) {

            val canonicalQuery =
                CanonicalNormalizer.canonical(
                    query
                )

            result =
                result.filter {

                    CanonicalNormalizer
                        .canonical(
                            it.name
                        )
                        .contains(
                            canonicalQuery
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

        _categories.value =
            result
    }

    fun selectCategory(
        id: Int
    ) {

        _selectedCategory.value =
            id
    }

    fun clearSelection() {

        _selectedCategory.value =
            null
    }

    fun clearMessage() {

        _operationResult.value =
            null
    }

    suspend fun insert(
        category: Category
    ): Boolean =
        withContext(Dispatchers.IO) {

            repository.insert(category)
                ?: true
        }

    suspend fun update(
        category: Category
    ): Boolean =
        withContext(Dispatchers.IO) {

            repository.update(category)
                ?: true
        }

    suspend fun delete(
        category: Category
    ): Boolean =
        withContext(Dispatchers.IO) {

            repository.delete(category)
                ?: true
        }

    suspend fun isCategoryUsed(
        categoryId: Int
    ): Boolean =
        withContext(Dispatchers.IO) {

            repository.isCategoryUsed(
                categoryId
            )
        }
}