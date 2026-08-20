package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.util.CanonicalNormalizer

data class SearchNavigationPlan(

    val resolved: Boolean,

    val fulcrum: SearchFulcrum? = null,

    val locationTerms: String = "",

    val categoryTerms: String = "",

    val boxTerms: String = "",

    val objectTerms: String = ""
)

class SearchNavigationPlanner(

    private val archivalLookup: SearchArchivalLookup =
        SearchArchivalLookup()
) {

    fun plan(
        question: String,
        index: SearchArchiveIndex
    ): SearchNavigationPlan {

        val hits =
            archivalLookup.find(
                question,
                index
            )

        val objects =
            hits.objects.filterNot { name ->
                SearchCoreAliases.isBoxAlias(name) ||
                        SearchCoreAliases.isLocationAlias(name) ||
                        SearchCoreAliases.isCategoryAlias(name)
            }

        val looksForContainerName =
            CanonicalNormalizer.wordTokens(
                question
            ).any { token ->
                SearchCoreAliases.isBoxAlias(
                    token
                )
            }

        val locationTerms =
            SearchConfiguration.packLocationTerms(
                hits.locations.filterNot { name ->
                    isGenericLocationWord(name)
                }
            )

        val categoryTerms =
            SearchConfiguration.packLocationTerms(
                hits.categories.filterNot { name ->
                    SearchCoreAliases.isCategoryAlias(
                        name
                    )
                }
            )

        val boxTerms =
            SearchConfiguration.packLocationTerms(
                hits.boxes.filterNot { name ->
                    hits.locations.any { location ->
                        CanonicalNormalizer.allTokensMatchWords(
                            name,
                            location
                        ) &&
                                CanonicalNormalizer.allTokensMatchWords(
                                    location,
                                    name
                                )
                    }
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
            boxTerms.isNotBlank() &&
            (
                objects.isEmpty() ||
                        looksForContainerName
            )
        ) {

            return SearchNavigationPlan(
                resolved = true,
                fulcrum = SearchFulcrum.BOX,
                boxTerms = boxTerms
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
}
