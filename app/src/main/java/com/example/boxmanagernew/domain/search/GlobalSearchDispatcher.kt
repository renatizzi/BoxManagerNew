package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchRoutingResult

class GlobalSearchDispatcher(

    private val engine: GlobalSearchEngine =
        GlobalSearchEngine()
) {

    fun dispatch(
        question: String
    ): SearchRoutingResult {

        return engine.route(
            question
        )
    }
}