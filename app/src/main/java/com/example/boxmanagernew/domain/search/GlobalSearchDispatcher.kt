package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchRoutingResult

class GlobalSearchDispatcher(

    private val engine: GlobalSearchEngine =
        GlobalSearchEngine(),

    private val router: SearchRouter =
        SearchRouter()
) {

    fun dispatch(
        question: String
    ): SearchRoutingResult {

        return router.route(
            engine.analyze(
                question
            )
        )
    }
}