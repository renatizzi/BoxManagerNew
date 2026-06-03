package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveLookupResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveScope
import com.example.boxmanagernew.domain.search.model.SearchArchiveScopeMatch
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntitiesResult
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntity
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityInput
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchSatisfiabilityEvaluator(

    private val evaluatorV2:
    SearchSatisfiabilityEvaluatorV2 =
        SearchSatisfiabilityEvaluatorV2()
) {

    fun evaluate(
        analysis: SearchAnalysisResult
    ): SearchSatisfiabilityResult {

        val recognizedEntities =
            analysis.recognizedEntities.map {

                SearchRecognizedEntity(
                    entityType = it,
                    scope =
                        when (it) {

                            CoreEntityType.OBJECT ->
                                SearchArchiveScope.OBJECT

                            CoreEntityType.BOX ->
                                SearchArchiveScope.BOX

                            CoreEntityType.LOCATION ->
                                SearchArchiveScope.LOCATION

                            CoreEntityType.CATEGORY ->
                                SearchArchiveScope.CATEGORY
                        },
                    matchCount = 1
                )
            }

        val recognizedEntitiesResult =
            SearchRecognizedEntitiesResult(
                recognizedEntities =
                    recognizedEntities
            )

        val lookupResult =
            SearchArchiveLookupResult(
                scopeMatches =
                    recognizedEntities.map {

                        SearchArchiveScopeMatch(
                            scope = it.scope,
                            matchCount = it.matchCount
                        )
                    }
            )

        val fulcrumResult =
            SearchFulcrumResolver()
                .resolve(
                    SearchEntityRecognizer()
                        .recognize(
                            lookupResult
                        )
                )

        return evaluatorV2.evaluate(
            SearchSatisfiabilityInput(
                fulcrumResult =
                    fulcrumResult,
                recognizedEntitiesResult =
                    recognizedEntitiesResult
            )
        )
    }
}