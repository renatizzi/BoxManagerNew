package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.util.CanonicalNormalizer

data class SearchNavigationPlan(

    val resolved: Boolean,

    val fulcrum: SearchFulcrum? = null,

    val locationTerms: String = "",

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

        val locationTerms =
            SearchConfiguration.packLocationTerms(
                locations.filterNot { name ->
                    isGenericLocationWord(name)
                }
            )

        if (asksForCategoryList(question)) {

            if (locationTerms.isBlank()) {
                return SearchNavigationPlan(
                    resolved = false
                )
            }

            return SearchNavigationPlan(
                resolved = true,
                fulcrum = SearchFulcrum.CATEGORY,
                locationTerms = locationTerms
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

    private fun asksForCategoryList(
        question: String
    ): Boolean {

        val normalized =
            CanonicalNormalizer.normalize(
                question
            )

        return normalized.contains("categoria") ||
                normalized.contains("categorie")
    }

    private fun isGenericLocationWord(
        name: String
    ): Boolean {

        val normalized =
            CanonicalNormalizer.normalize(
                name
            )

        return normalized == "luogo" ||
                normalized == "luoghi" ||
                normalized == "posizione" ||
                normalized == "posizioni" ||
                normalized == "posto" ||
                normalized == "posti"
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
