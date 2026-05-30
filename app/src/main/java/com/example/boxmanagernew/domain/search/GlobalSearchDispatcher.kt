package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchResponse

class GlobalSearchDispatcher(

    private val engine: GlobalSearchEngine =
        GlobalSearchEngine(),

    private val router: SearchRouter =
        SearchRouter()
) {

    fun dispatch(
        question: String
    ): SearchResponse {

        val routingResult =
            router.route(
                engine.analyze(
                    question
                )
            )

        if (
            routingResult.isFallback
        ) {

            return SearchResponse(
                success = false,
                message =
                    "Non ho compreso la richiesta."
            )
        }

        if (
            routingResult.requiresClarification
        ) {

            return SearchResponse(
                success = false,
                message =
                    "Puoi formulare la richiesta in modo più preciso?",
                requiresClarification = true,
                clarificationType =
                    SearchClarificationType.GENERIC_REQUEST
            )
        }

        return SearchResponse(
            success = true,
            message =
                "Richiesta riconosciuta e instradata verso il motore corretto."
        )
    }
}