package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchEngineARequest
import com.example.boxmanagernew.domain.search.model.SearchFulcrumResult
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult

class SearchEngineARequestBuilder {

    fun build(
        searchText: String,
        fulcrumResult: SearchFulcrumResult,
        satisfiabilityResult: SearchSatisfiabilityResult
    ): SearchEngineARequest {

        return SearchEngineARequest(
            fulcrumResult = fulcrumResult,
            searchText = searchText,
            finalClassification =
                satisfiabilityResult.finalClassification
        )
    }
}