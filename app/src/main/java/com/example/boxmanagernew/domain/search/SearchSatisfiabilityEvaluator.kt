package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchSatisfiability
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

/**
 * Fase 7: soddisfacibilità dopo entità, fulcro, percorso e trasformazione.
 */
class SearchSatisfiabilityEvaluator {

    fun evaluate(
        normalizedQuestion: String,
        indicators: Map<String, Set<String>>,
        transformation: SearchArchiveTransformation,
        navigationSatisfiable: Boolean
    ): SearchSatisfiabilityResult {

        if (
            SearchF8Pattern.matches(
                normalizedQuestion
            )
        ) {

            return SearchSatisfiabilityResult(
                finalClassification =
                    SearchClassification.ENGINE_B,
                satisfiableByEngineA = false,
                satisfiableByEngineB = true,
                requiresClarification = false,
                clarificationType =
                    SearchClarificationType.NONE,
                matchedPatternId =
                    SearchF8Pattern.ID
            )
        }

        if (
            SearchF7Pattern.matches(
                normalizedQuestion
            ) ||
            (
                hasDuplicateConfronto(
                    indicators
                ) &&
                    !navigationSatisfiable &&
                    transformation !=
                    SearchArchiveTransformation.OBJECT_TO_LOCATION &&
                    !SearchF8Pattern.isOfficialFamily(
                        normalizedQuestion
                    )
            )
        ) {

            return SearchSatisfiabilityResult(
                finalClassification =
                    SearchClassification.ENGINE_B,
                satisfiableByEngineA = false,
                satisfiableByEngineB = true,
                requiresClarification = false,
                clarificationType =
                    SearchClarificationType.NONE,
                matchedPatternId =
                    SearchF7Pattern.ID
            )
        }

        if (
            navigationSatisfiable &&
            isNavigationTransformation(
                transformation
            )
        ) {

            return SearchSatisfiabilityResult(
                finalClassification =
                    SearchClassification.ENGINE_A,
                satisfiableByEngineA = true,
                satisfiableByEngineB = false,
                requiresClarification = false,
                clarificationType =
                    SearchClarificationType.NONE,
                matchedPatternId = null
            )
        }

        if (
            !navigationSatisfiable &&
            isInventoryTransformation(
                transformation
            ) &&
            isSingleCoreAlias(
                indicators
            )
        ) {

            return SearchSatisfiabilityResult(
                finalClassification =
                    SearchClassification.ENGINE_A,
                satisfiableByEngineA = true,
                satisfiableByEngineB = false,
                requiresClarification = false,
                clarificationType =
                    SearchClarificationType.NONE,
                matchedPatternId = null
            )
        }

        if (
            isLegacyInterrogationTransformation(
                transformation
            )
        ) {

            return SearchSatisfiabilityResult(
                finalClassification =
                    SearchClassification.ENGINE_B,
                satisfiableByEngineA = false,
                satisfiableByEngineB = true,
                requiresClarification = false,
                clarificationType =
                    SearchClarificationType.NONE,
                matchedPatternId = null
            )
        }

        return SearchSatisfiabilityResult(
            finalClassification =
                SearchClassification.ENGINE_A,
            satisfiableByEngineA = false,
            satisfiableByEngineB = false,
            requiresClarification = false,
            clarificationType =
                SearchClarificationType.NONE,
            matchedPatternId = null
        )
    }

    fun satisfiabilityOf(
        result: SearchSatisfiabilityResult
    ): SearchSatisfiability {

        return when {

            result.satisfiableByEngineA ->
                SearchSatisfiability.SATISFIABLE_BY_ENGINE_A

            result.satisfiableByEngineB ->
                SearchSatisfiability.REQUIRES_ENGINE_B

            else ->
                SearchSatisfiability.UNSATISFIABLE
        }
    }

    private fun hasDuplicateConfronto(
        indicators: Map<String, Set<String>>
    ): Boolean {

        val confronto =
            indicators[
                SearchLexicalIndicatorMatrix.CONFRONTO
            ].orEmpty()

        return confronto.any { term ->

            DUPLICATE_CONFRONTO.any { official ->

                com.example.boxmanagernew.util.CanonicalNormalizer
                    .wholeWordMatches(
                        term,
                        official
                    )
            }
        }
    }

    private fun isSingleCoreAlias(
        indicators: Map<String, Set<String>>
    ): Boolean {

        val hasBox =
            indicators[
                SearchLexicalIndicatorMatrix.BOX
            ].orEmpty().isNotEmpty()

        val hasObject =
            indicators[
                SearchLexicalIndicatorMatrix.OBJECT
            ].orEmpty().isNotEmpty()

        val hasCategory =
            indicators[
                SearchLexicalIndicatorMatrix.CATEGORY
            ].orEmpty().isNotEmpty()

        val hasLocation =
            indicators[
                SearchLexicalIndicatorMatrix.LOCATION
            ].orEmpty().any { term ->
                SearchCoreAliases.isLocationAlias(
                    term
                )
            }

        return listOf(
            hasBox,
            hasObject,
            hasCategory,
            hasLocation
        ).count { present ->
            present
        } == 1
    }

    private fun isInventoryTransformation(
        transformation: SearchArchiveTransformation
    ): Boolean {

        return transformation ==
                SearchArchiveTransformation.NONE ||
                transformation ==
                SearchArchiveTransformation.CATEGORY_TO_BOX ||
                transformation ==
                SearchArchiveTransformation.LOCATION_TO_BOX
    }

    private fun isNavigationTransformation(
        transformation: SearchArchiveTransformation
    ): Boolean {

        return transformation ==
                SearchArchiveTransformation.OBJECT_TO_BOX ||
                transformation ==
                SearchArchiveTransformation.LOCATION_TO_BOX ||
                transformation ==
                SearchArchiveTransformation.CATEGORY_TO_BOX ||
                transformation ==
                SearchArchiveTransformation.NONE
    }

    private fun isLegacyInterrogationTransformation(
        transformation: SearchArchiveTransformation
    ): Boolean {

        return transformation ==
                SearchArchiveTransformation.OBJECT_TO_LOCATION ||
                transformation ==
                SearchArchiveTransformation.OBJECT_TO_CATEGORY ||
                transformation ==
                SearchArchiveTransformation.BOX_TO_LOCATION ||
                transformation ==
                SearchArchiveTransformation.BOX_TO_CATEGORY
    }

    companion object {

        private val DUPLICATE_CONFRONTO =
            setOf(
                "uguale",
                "stesso",
                "duplicato",
                "doppione"
            )
    }
}
