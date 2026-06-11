package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchQuestionPattern
import com.example.boxmanagernew.domain.search.model.SearchSatisfiability
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityInput
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchSatisfiabilityEvaluatorV2 {

    companion object {

        private const val INDICATORS_WEIGHT = 25
        private const val ENTITIES_WEIGHT = 25
        private const val FULCRUM_WEIGHT = 25
        private const val SATISFIABILITY_WEIGHT = 25
    }

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

        return null
    }

    private fun evaluateS2(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult? {

        if (
            input.matchedPatterns.isEmpty()
        ) {
            return null
        }

        val dominantPattern =
            selectDominantPattern(
                patterns =
                    input.matchedPatterns,
                input = input
            )

        return SearchSatisfiabilityResult(
            finalClassification =
                dominantPattern.classification,
            satisfiableByEngineA =
                dominantPattern.supportsEngineA,
            satisfiableByEngineB =
                dominantPattern.satisfiability ==
                        SearchSatisfiability.REQUIRES_ENGINE_B,
            requiresClarification =
                false,
            clarificationType =
                SearchClarificationType.NONE,
            matchedPatternId =
                dominantPattern.id
        )
    }

    private fun evaluateS4(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult? {

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

    private fun selectDominantPattern(
        patterns: List<SearchQuestionPattern>,
        input: SearchSatisfiabilityInput
    ): SearchQuestionPattern {

        return patterns
            .maxByOrNull {
                calculateEvidenceScore(
                    pattern = it,
                    input = input
                )
            }
            ?: patterns.first()
    }

    private fun calculateEvidenceScore(
        pattern: SearchQuestionPattern,
        input: SearchSatisfiabilityInput
    ): Int {

        var score = 0

        if (
            input.lexicalIndicators.any {
                pattern.variants.contains(it)
            }
        ) {
            score += INDICATORS_WEIGHT
        }

        val recognizedEntities =
            input
                .recognizedEntitiesResult
                .recognizedEntities
                .map {
                    it.entityType
                }
                .toSet()

        if (
            recognizedEntities.containsAll(
                pattern.involvedEntities
            )
        ) {
            score += ENTITIES_WEIGHT
        }

        if (
            input.fulcrumResult.fulcrum ==
            pattern.dominantFulcrum
        ) {
            score += FULCRUM_WEIGHT
        }

        val satisfiable =
            when (
                pattern.satisfiability
            ) {

                SearchSatisfiability
                    .SATISFIABLE_BY_ENGINE_A -> {

                    pattern.supportsEngineA
                }

                SearchSatisfiability
                    .REQUIRES_ENGINE_B -> {

                    pattern.classification ==
                            SearchClassification.ENGINE_B
                }

                SearchSatisfiability
                    .UNSATISFIABLE -> {

                    true
                }
            }

        if (satisfiable) {
            score += SATISFIABILITY_WEIGHT
        }

        return score
    }
}