package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Inventario Motore A (type-only, senza chiave nominata): report ad hoc
 * con layout delle liste predefinite, contenuto filtrato.
 *
 * - CATEGORY → Categorie **usate** (nei contenitori), non l'anagrafica intera
 * - OBJECT → report oggetti (SearchResult), non lista contenitori
 * - LOCATION type-only → Posizioni usate
 * - LOCATION nominata + alias OBJECT → report oggetti filtrati per luogo
 * - BOX / vuoti → Contenitori
 *
 * Navigazione nominata (termini object/box/category) → null (lista Contenitori).
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
                when {
                    response.locationTerms.isBlank() ->
                        InventoryListTarget.LOCATIONS

                    // «Quali oggetti ho in cantina?» → report oggetti, non lista Contenitori.
                    objectOnlyCue(question) ->
                        InventoryListTarget.OBJECTS

                    else ->
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

        return when {

            objectOnlyCue(question) ->
                InventoryListTarget.OBJECTS

            else ->
                InventoryListTarget.BOXES
        }
    }

    private fun objectOnlyCue(
        question: String
    ): Boolean {

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

        return objectCue && !boxCue
    }
}
