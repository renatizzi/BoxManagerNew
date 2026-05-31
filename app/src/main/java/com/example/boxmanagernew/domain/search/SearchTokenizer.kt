package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchNormalizedQuestion
import com.example.boxmanagernew.domain.search.model.SearchTokenizedQuestion

class SearchTokenizer {

    fun tokenize(
        question: SearchNormalizedQuestion
    ): SearchTokenizedQuestion {

        val tokens =
            question.normalizedQuestion
                .split("\\s+".toRegex())
                .filter {
                    it.isNotBlank()
                }

        return SearchTokenizedQuestion(
            normalizedQuestion =
                question.normalizedQuestion,
            tokens =
                tokens
        )
    }
}