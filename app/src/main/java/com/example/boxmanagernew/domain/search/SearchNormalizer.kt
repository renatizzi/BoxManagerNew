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
                    Regex("\\bdov['’]è\\b"),
                    "dove è"
                )
                .replace(
                    Regex("\\bqual è\\b"),
                    "quale è"
                )
                .replace(
                    Regex("\\bcom['’]è\\b"),
                    "come è"
                )
                .replace(
                    Regex("\\bcos['’]è\\b"),
                    "cosa è"
                )
                .replace(
                    Regex("\\bc['’]è\\b"),
                    "ci è"
                )
                .replace(
                    Regex("\\bs['’]è\\b"),
                    "si è"
                )
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