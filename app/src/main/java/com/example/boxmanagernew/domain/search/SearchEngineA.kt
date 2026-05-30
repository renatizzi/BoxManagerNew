package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchResponse

class SearchEngineA {

    fun execute(
        analysis: SearchAnalysisResult
    ): SearchResponse {

        return SearchResponse(
            success = true,
            message =
                "Motore A eseguito. Pattern rilevato: ${analysis.patternId ?: "N/D"}"
        )
    }
}