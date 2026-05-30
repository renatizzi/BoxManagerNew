package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchEngineType
import com.example.boxmanagernew.domain.search.model.SearchResponse

class GlobalSearchDispatcher(

    private val engine: GlobalSearchEngine =
        GlobalSearchEngine(),

    private val router: SearchRouter =
        SearchRouter(),

    private val engineA: SearchEngineA =
        SearchEngineA()
) {

    fun dispatch(
        question: String
    ): SearchResponse {

        val analysis =
            engine.analyze(
                question
            )

        val routingResult =
            router.route(
                analysis
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

        return when (
            routingResult.engineType
        ) {

            SearchEngineType.ENGINE_A ->
                engineA.execute(
                    analysis
                )

            SearchEngineType.ENGINE_B ->
                SearchResponse(
                    success = false,
                    message =
                        "Motore B non ancora disponibile."
                )
        }
    }
}