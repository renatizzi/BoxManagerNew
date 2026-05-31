package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityInput
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchSatisfiabilityEvaluatorV2 {

    fun evaluate(
        input: SearchSatisfiabilityInput
    ): SearchSatisfiabilityResult {

        val engineA =
            input.fulcrumResult.fulcrum != null

        return SearchSatisfiabilityResult(
            finalClassification =
                if (engineA) {
                    SearchClassification.ENGINE_A
                } else {
                    SearchClassification.ENGINE_B
                },
            satisfiableByEngineA =
                engineA,
            satisfiableByEngineB =
                !engineA,
            requiresClarification =
                false,
            clarificationType =
                SearchClarificationType.NONE,
            matchedPatternId =
                null
        )
    }
}