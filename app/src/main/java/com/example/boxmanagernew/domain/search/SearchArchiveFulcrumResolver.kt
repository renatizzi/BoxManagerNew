package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchivalHits
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchFulcrumResult
import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Fase 4: fulcro dalle entità in archivio, non dalla formulazione.
 */
class SearchArchiveFulcrumResolver {

    fun resolve(
        question: String,
        hits: SearchArchivalHits
    ): SearchFulcrumResult {

        val categoryNames =
            hits.categories.filterNot { name ->
                SearchCoreAliases.isCategoryAlias(
                    name
                )
            }

        val locationNames =
            hits.locations.filterNot { name ->
                SearchCoreAliases.isLocationAlias(
                    name
                )
            }

        val objectNames =
            hits.objects.filterNot { name ->
                SearchCoreAliases.isLocationAlias(name) ||
                        SearchCoreAliases.isCategoryAlias(name)
            }

        val hasObjectAlias =
            CanonicalNormalizer.wordTokens(
                question
            ).any { token ->
                SearchCoreAliases.isObjectAlias(
                    token
                )
            }

        val extraBoxQualifier =
            CanonicalNormalizer.wordTokens(
                question
            ).any { token ->

                SearchCoreAliases.isBoxAlias(
                    token
                ) &&
                        objectNames.none { name ->

                            SearchNameMatcher.wholeWordInName(
                                name,
                                token
                            )
                        }
            }

        if (categoryNames.isNotEmpty()) {

            return SearchFulcrumResult(
                fulcrum = SearchFulcrum.CATEGORY,
                reason = "ARCHIVE_CATEGORY"
            )
        }

        if (
            hasObjectAlias &&
            objectNames.isNotEmpty()
        ) {

            return SearchFulcrumResult(
                fulcrum = SearchFulcrum.OBJECT,
                reason = "ARCHIVE_OBJECT"
            )
        }

        if (
            hits.boxes.isNotEmpty() &&
            (
                objectNames.isEmpty() ||
                        extraBoxQualifier
            )
        ) {

            return SearchFulcrumResult(
                fulcrum = SearchFulcrum.BOX,
                reason = "ARCHIVE_BOX_NAME"
            )
        }

        if (
            locationNames.isNotEmpty() &&
            objectNames.isEmpty()
        ) {

            return SearchFulcrumResult(
                fulcrum = SearchFulcrum.LOCATION,
                reason = "ARCHIVE_LOCATION"
            )
        }

        if (objectNames.isNotEmpty()) {

            return SearchFulcrumResult(
                fulcrum = SearchFulcrum.OBJECT,
                reason = "ARCHIVE_OBJECT"
            )
        }

        return SearchFulcrumResult(
            fulcrum = null,
            reason = "NO_FULCRUM"
        )
    }
}
