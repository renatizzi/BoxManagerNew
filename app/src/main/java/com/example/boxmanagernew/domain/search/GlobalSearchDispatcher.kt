package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchStrategy

class GlobalSearchDispatcher(

    private val engine: GlobalSearchEngine =
        GlobalSearchEngine()
) {

    fun dispatch(
        question: String
    ): SearchStrategy {

        return engine.determineStrategy(
            question
        )
    }
}