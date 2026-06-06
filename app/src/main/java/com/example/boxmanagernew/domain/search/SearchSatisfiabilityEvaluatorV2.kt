package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityInput
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchSatisfiabilityEvaluatorV2 {

    fun evaluate(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult {

        val recognizedEntities =
            input
                .recognizedEntitiesResult
                .recognizedEntities

        val hasRecognizedEntities =
            recognizedEntities.isNotEmpty()

        val hasFulcrum =
            input.fulcrumResult.fulcrum != null

        val satisfiableByEngineA =
            hasRecognizedEntities &&
                    hasFulcrum

        return SearchSatisfiabilityResult(
            finalClassification =
                if (satisfiableByEngineA) {
                    SearchClassification.ENGINE_A
                } else {
                    SearchClassification.ENGINE_B
                },
            satisfiableByEngineA =
                satisfiableByEngineA,
            satisfiableByEngineB =
                !satisfiableByEngineA,
            requiresClarification =
                false,
            clarificationType =
                SearchClarificationType.NONE,
            matchedPatternId =
                null
        )
    }
}