package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchNormalizedQuestion

class SearchNormalizer {

    fun normalize(
        question: String
    ): SearchNormalizedQuestion {

        val normalizedQuestion =
            question
                .trim()
                .lowercase()
                .replace(
                    Regex("\\s+"),
                    " "
                )

        return SearchNormalizedQuestion(
            originalQuestion =
                question,
            normalizedQuestion =
                normalizedQuestion
        )
    }
}