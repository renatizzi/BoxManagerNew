package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchSatisfiability
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchSatisfiabilityEvaluator {

    fun evaluate(
        analysis: SearchAnalysisResult
    ): SearchSatisfiabilityResult {

        val hasRecognizedEntities =
            analysis.recognizedEntities.isNotEmpty()

        val isPatternMissing =
            analysis.patternId == null

        val isFallback =
            isPatternMissing &&
                    !hasRecognizedEntities

        val requiresClarification =
            isPatternMissing &&
                    hasRecognizedEntities

        val clarificationType =
            if (requiresClarification) {
                SearchClarificationType.GENERIC_REQUEST
            } else {
                SearchClarificationType.NONE
            }

        val finalClassification =
            when (analysis.satisfiability) {

                SearchSatisfiability.REQUIRES_ENGINE_B ->
                    SearchClassification.ENGINE_B

                SearchSatisfiability.SATISFIABLE_BY_ENGINE_A ->
                    SearchClassification.ENGINE_A

                SearchSatisfiability.UNSATISFIABLE ->
                    SearchClassification.ENGINE_A

                null ->
                    SearchClassification.ENGINE_A
            }

        return SearchSatisfiabilityResult(
            finalClassification =
                finalClassification,
            satisfiableByEngineA =
                analysis.satisfiability ==
                        SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
            satisfiableByEngineB =
                analysis.satisfiability ==
                        SearchSatisfiability.REQUIRES_ENGINE_B,
            requiresClarification =
                requiresClarification,
            clarificationType =
                clarificationType,
            matchedPatternId =
                analysis.patternId
        )
    }
}