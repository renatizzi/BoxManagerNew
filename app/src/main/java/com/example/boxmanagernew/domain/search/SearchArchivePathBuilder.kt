package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchArchivePath
import com.example.boxmanagernew.domain.search.model.SearchArchivePathStep
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntity

/**
 * Fase 5: percorso da fulcro e entità riconosciute (3.3.6).
 * Relazioni: OBJECT⇄BOX, BOX⇄LOCATION, BOX⇄CATEGORY.
 */
class SearchArchivePathBuilder {

    fun build(
        fulcrum: SearchFulcrum?,
        recognizedEntities: List<SearchRecognizedEntity> =
            emptyList()
    ): SearchArchivePath {

        val hasCategory =
            recognizedEntities.any { entity ->
                entity.entityType ==
                    CoreEntityType.CATEGORY
            }

        val hasObject =
            recognizedEntities.any { entity ->
                entity.entityType ==
                    CoreEntityType.OBJECT
            }

        val hasLocation =
            recognizedEntities.any { entity ->
                entity.entityType ==
                    CoreEntityType.LOCATION
            }

        val steps =
            when (fulcrum) {

                SearchFulcrum.OBJECT ->
                    if (hasCategory) {
                        listOf(
                            SearchArchivePathStep.OBJECT,
                            SearchArchivePathStep.BOX,
                            SearchArchivePathStep.CATEGORY
                        )
                    } else if (hasLocation) {
                        listOf(
                            SearchArchivePathStep.OBJECT,
                            SearchArchivePathStep.BOX,
                            SearchArchivePathStep.LOCATION
                        )
                    } else {
                        listOf(
                            SearchArchivePathStep.OBJECT,
                            SearchArchivePathStep.BOX
                        )
                    }

                SearchFulcrum.LOCATION ->
                    listOf(
                        SearchArchivePathStep.LOCATION,
                        SearchArchivePathStep.BOX
                    )

                SearchFulcrum.CATEGORY ->
                    listOf(
                        SearchArchivePathStep.CATEGORY,
                        SearchArchivePathStep.BOX
                    )

                SearchFulcrum.BOX ->
                    pathFromRecognizedCores(
                        hasObject,
                        hasCategory,
                        hasLocation
                    ).ifEmpty {
                        listOf(
                            SearchArchivePathStep.BOX
                        )
                    }

                null ->
                    pathFromRecognizedCores(
                        hasObject,
                        hasCategory,
                        hasLocation
                    )
            }

        return SearchArchivePath(
            steps = steps
        )
    }

    private fun pathFromRecognizedCores(
        hasObject: Boolean,
        hasCategory: Boolean,
        hasLocation: Boolean
    ): List<SearchArchivePathStep> {

        if (hasObject && hasCategory) {

            return listOf(
                SearchArchivePathStep.OBJECT,
                SearchArchivePathStep.BOX,
                SearchArchivePathStep.CATEGORY
            )
        }

        if (hasObject && hasLocation) {

            return listOf(
                SearchArchivePathStep.OBJECT,
                SearchArchivePathStep.BOX,
                SearchArchivePathStep.LOCATION
            )
        }

        if (hasCategory) {

            return listOf(
                SearchArchivePathStep.BOX,
                SearchArchivePathStep.CATEGORY
            )
        }

        if (hasLocation) {

            return listOf(
                SearchArchivePathStep.BOX,
                SearchArchivePathStep.LOCATION
            )
        }

        return emptyList()
    }
}
