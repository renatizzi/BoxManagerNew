package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchQuestionPattern
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

        val candidatePatterns =
            findCandidatePatterns(input)

        if (
            candidatePatterns.isEmpty()
        ) {
            return null
        }

        val dominantPattern =
            selectDominantPattern(
                candidates = candidatePatterns,
                input = input
            )

        return SearchSatisfiabilityResult(
            finalClassification =
                dominantPattern.classification,
            satisfiableByEngineA =
                dominantPattern.supportsEngineA,
            satisfiableByEngineB =
                dominantPattern.classification ==
                        SearchClassification.ENGINE_B,
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

    private fun findCandidatePatterns(
        input: SearchSatisfiabilityInput
    ): List<SearchQuestionPattern> {

        val fulcrum =
            input.fulcrumResult.fulcrum

        val entityTypes =
            input
                .recognizedEntitiesResult
                .recognizedEntities
                .map {
                    it.entityType
                }
                .toSet()

        return input
            .matchedPatterns
            .filter { pattern ->

                pattern.dominantFulcrum ==
                        fulcrum &&
                        entityTypes.containsAll(
                            pattern.involvedEntities
                        )
            }
    }

    private fun selectDominantPattern(
        candidates: List<SearchQuestionPattern>,
        input: SearchSatisfiabilityInput
    ): SearchQuestionPattern {

        return candidates
            .maxByOrNull {
                calculateEvidenceScore(
                    pattern = it,
                    input = input
                )
            }
            ?: candidates.first()
    }

    private fun calculateEvidenceScore(
        pattern: SearchQuestionPattern,
        input: SearchSatisfiabilityInput
    ): Int {

        var score = 0

        if (
            findLexicalIndicators(input)
                .isNotEmpty()
        ) {
            score += INDICATORS_WEIGHT
        }

        if (
            pattern.involvedEntities
                .isNotEmpty()
        ) {
            score += ENTITIES_WEIGHT
        }

        if (
            pattern.dominantFulcrum != null
        ) {
            score += FULCRUM_WEIGHT
        }

        if (
            pattern.supportsEngineA ||
            pattern.classification ==
            SearchClassification.ENGINE_B
        ) {
            score += SATISFIABILITY_WEIGHT
        }

        return score
    }

    private fun findLexicalIndicators(
        input: SearchSatisfiabilityInput
    ): List<String> {

        return input.lexicalIndicators
    }
}