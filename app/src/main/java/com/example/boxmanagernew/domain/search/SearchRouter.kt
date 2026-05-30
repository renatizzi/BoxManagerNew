package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchEngineType
import com.example.boxmanagernew.domain.search.model.SearchRoutingResult

class SearchRouter {

    fun route(
        analysis: SearchAnalysisResult
    ): SearchRoutingResult {

        val hasRecognizedEntities =
            analysis.recognizedEntities.isNotEmpty()

        val isFallback =
            analysis.patternId == null &&
                    !hasRecognizedEntities

        val requiresClarification =
            analysis.patternId == null &&
                    hasRecognizedEntities

        val clarificationType =
            if (requiresClarification) {

                SearchClarificationType.GENERIC_REQUEST

            } else {

                SearchClarificationType.NONE
            }

        val engineType =
            when (
                analysis.classification
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
                requiresClarification,
            clarificationType =
                clarificationType,
            isFallback =
                isFallback
        )
    }
}