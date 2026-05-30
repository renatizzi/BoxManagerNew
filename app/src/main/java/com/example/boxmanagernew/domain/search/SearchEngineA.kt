package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchResponse

class SearchEngineA {

    fun execute(
        analysis: SearchAnalysisResult
    ): SearchResponse {

        val message =
            when (
                analysis.patternId
            ) {

                "find_object" ->
                    "Ricerca oggetto riconosciuta."

                "box_contents" ->
                    "Richiesta contenuto contenitore riconosciuta."

                "boxes_in_location" ->
                    "Ricerca contenitori per posizione riconosciuta."

                else ->
                    "Richiesta semplice riconosciuta."
            }

        return SearchResponse(
            success = true,
            message = message
        )
    }
}