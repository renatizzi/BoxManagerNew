package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Inventario Motore A: quale lista anagrafica aprire a video.
 * Navigazione nominata (chiavi archivio) resta lista Contenitori.
 */
enum class InventoryListTarget {
    BOXES,
    EMPTY_BOXES,
    OBJECTS,
    CATEGORIES,
    LOCATIONS
}

object InventoryListRouter {

    fun target(
        response: SearchResponse,
        question: String
    ): InventoryListTarget? {

        if (
            response.objectTerms.isNotBlank() ||
            response.boxTerms.isNotBlank() ||
            response.categoryTerms.isNotBlank()
        ) {
            return null
        }

        if (
            EmptyBoxesInventoryCue.matches(
                question
            )
        ) {
            return InventoryListTarget.EMPTY_BOXES
        }

        return when (
            response.archiveTransformation
        ) {

            SearchArchiveTransformation.CATEGORY_TO_BOX ->
                InventoryListTarget.CATEGORIES

            SearchArchiveTransformation.LOCATION_TO_BOX ->
                if (
                    response.locationTerms.isBlank()
                ) {
                    InventoryListTarget.LOCATIONS
                } else {
                    null
                }

            SearchArchiveTransformation.NONE ->
                objectOrBoxInventory(
                    question
                )

            else ->
                null
        }
    }

    private fun objectOrBoxInventory(
        question: String
    ): InventoryListTarget {

        val normalized =
            SearchNormalizer()
                .normalize(question)
                .normalizedQuestion

        val tokens =
            CanonicalNormalizer.wordTokens(
                normalized
            )

        val objectCue =
            tokens.any { token ->
                SearchCoreAliases.isObjectAlias(
                    token
                )
            }

        val boxCue =
            tokens.any { token ->
                SearchCoreAliases.isBoxAlias(
                    token
                )
            }

        return when {

            objectCue && !boxCue ->
                InventoryListTarget.OBJECTS

            else ->
                InventoryListTarget.BOXES
        }
    }
}
