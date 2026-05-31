package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchEngineType
import com.example.boxmanagernew.domain.search.model.SearchRoutingResult
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchRouter {

    fun route(
        analysis: SearchAnalysisResult,
        satisfiabilityResult: SearchSatisfiabilityResult
    ): SearchRoutingResult {

        val hasRecognizedEntities =
            analysis.recognizedEntities.isNotEmpty()

        val isFallback =
            analysis.patternId == null &&
                    !hasRecognizedEntities

        val engineType =
            when (
                satisfiabilityResult.finalClassification
            ) {

                SearchClassification.ENGINE_B ->
                    SearchEngineType.ENGINE_B

                else ->
                    SearchEngineType.ENGINE_A
            }

        return SearchRoutingResult(
            analysis = analysis,
            engineType = engineType,
            requiresClarification =
                satisfiabilityResult.requiresClarification,
            clarificationType =
                satisfiabilityResult.clarificationType,
            isFallback =
                isFallback
        )
    }
}