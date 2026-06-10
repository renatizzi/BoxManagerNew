package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchQuestionPattern
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityInput
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchSatisfiabilityEvaluatorV2 {

    fun evaluate(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult {

        val s1Result =
            evaluateS1(input)

        if (s1Result != null) {
            return s1Result
        }

        val s2Result =
            evaluateS2(input)

        if (s2Result != null) {
            return s2Result
        }

        val s4Result =
            evaluateS4(input)

        if (s4Result != null) {
            return s4Result
        }

        return evaluateS3()
    }

    private fun evaluateS1(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult? {

        val recognizedEntities =
            input
                .recognizedEntitiesResult
                .recognizedEntities

        val hasRecognizedEntities =
            recognizedEntities.isNotEmpty()

        val hasFulcrum =
            input.fulcrumResult.fulcrum != null

        if (
            !hasRecognizedEntities ||
            !hasFulcrum
        ) {
            return null
        }

        return SearchSatisfiabilityResult(
            finalClassification =
                SearchClassification.ENGINE_A,
            satisfiableByEngineA =
                true,
            satisfiableByEngineB =
                false,
            requiresClarification =
                false,
            clarificationType =
                SearchClarificationType.NONE,
            matchedPatternId =
                findMatchingPattern(input)?.id
        )
    }

    private fun evaluateS2(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult? {

        val fulcrum =
            input.fulcrumResult.fulcrum
                ?: return null

        val entityTypes =
            input
                .recognizedEntitiesResult
                .recognizedEntities
                .map {
                    it.entityType
                }
                .toSet()

        val isF1 =
            fulcrum == SearchFulcrum.OBJECT &&
                    entityTypes.contains(
                        CoreEntityType.OBJECT
                    )

        val isF2 =
            fulcrum == SearchFulcrum.BOX &&
                    entityTypes.containsAll(
                        setOf(
                            CoreEntityType.BOX,
                            CoreEntityType.OBJECT
                        )
                    )

        val isF3 =
            fulcrum == SearchFulcrum.LOCATION &&
                    entityTypes.containsAll(
                        setOf(
                            CoreEntityType.LOCATION,
                            CoreEntityType.BOX
                        )
                    )

        val isF4 =
            fulcrum == SearchFulcrum.BOX &&
                    entityTypes.contains(
                        CoreEntityType.OBJECT
                    )

        if (
            !isF1 &&
            !isF2 &&
            !isF3 &&
            !isF4
        ) {
            return null
        }

        return SearchSatisfiabilityResult(
            finalClassification =
                SearchClassification.ENGINE_A,
            satisfiableByEngineA =
                true,
            satisfiableByEngineB =
                false,
            requiresClarification =
                false,
            clarificationType =
                SearchClarificationType.NONE,
            matchedPatternId =
                findMatchingPattern(input)?.id
        )
    }

    private fun evaluateS4(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult? {

        /*
         * S4 predisposta secondo Blueprint.
         * Nessuna chiarificazione preventiva.
         * Attivazione reale rinviata alla disponibilità
         * di evidenze concorrenti sufficienti.
         */

        return null
    }

    private fun evaluateS3(
    ): SearchSatisfiabilityResult {

        return SearchSatisfiabilityResult(
            finalClassification =
                SearchClassification.ENGINE_B,
            satisfiableByEngineA =
                false,
            satisfiableByEngineB =
                true,
            requiresClarification =
                false,
            clarificationType =
                SearchClarificationType.NONE,
            matchedPatternId =
                null
        )
    }

    private fun findMatchingPattern(
        input: SearchSatisfiabilityInput
    ): SearchQuestionPattern? {

        val fulcrum =
            input.fulcrumResult.fulcrum

        return input
            .matchedPatterns
            .firstOrNull {

                it.dominantFulcrum ==
                        fulcrum
            }
    }

    private fun findLexicalIndicators(
        input: SearchSatisfiabilityInput
    ): List<String> {

        return emptyList()
    }
}