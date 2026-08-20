package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.util.CanonicalNormalizer

data class SearchNavigationPlan(

    val resolved: Boolean,

    val fulcrum: SearchFulcrum? = null,

    val locationTerms: String = "",

    val categoryTerms: String = "",

    val objectTerms: String = ""
)

class SearchNavigationPlanner {

    fun plan(
        question: String,
        index: SearchArchiveIndex
    ): SearchNavigationPlan {

        val locations =
            matchingNames(
                question,
                index.locations
            )

        val objects =
            matchingNames(
                question,
                index.objects
            )

        val categories =
            matchingNames(
                question,
                index.categories
            )

        val locationTerms =
            SearchConfiguration.packLocationTerms(
                locations.filterNot { name ->
                    isGenericLocationWord(name)
                }
            )

        val categoryTerms =
            SearchConfiguration.packLocationTerms(
                categories.filterNot { name ->
                    SearchCoreAliases.isCategoryAlias(
                        name
                    )
                }
            )

        if (
            categoryTerms.isNotBlank()
        ) {

            return SearchNavigationPlan(
                resolved = true,
                fulcrum = SearchFulcrum.BOX,
                locationTerms = locationTerms,
                categoryTerms = categoryTerms
            )
        }

        if (
            locationTerms.isNotBlank() &&
            objects.isEmpty()
        ) {

            return SearchNavigationPlan(
                resolved = true,
                fulcrum = SearchFulcrum.BOX,
                locationTerms = locationTerms
            )
        }

        return SearchNavigationPlan(
            resolved = false
        )
    }

    private fun isGenericLocationWord(
        name: String
    ): Boolean {

        return SearchCoreAliases.isLocationAlias(
            name
        )
    }

    private fun matchingNames(
        question: String,
        names: List<String>
    ): List<String> {

        return names
            .filter { name ->

                name.isNotBlank() &&
                        CanonicalNormalizer.allTokensMatchWords(
                            name,
                            question
                        )
            }
            .sortedByDescending {
                it.length
            }
    }
}
