package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchNormalizedQuestion

class SearchNormalizer {

    fun normalize(
        question: String
    ): SearchNormalizedQuestion {

        val unified =
            question
                .trim()
                .lowercase()
                .replace('’', '\'')
                .replace('‘', '\'')
                .replace('ʼ', '\'')
                .replace('`', '\'')
                .replace('´', '\'')

        val normalizedQuestion =
            unified
                .replace(
                    wordRegex("dov'\\s*[èeé]'?"),
                    "dove è"
                )
                .replace(
                    wordRegex("qual\\s+[èeé]"),
                    "quale è"
                )
                .replace(
                    wordRegex("qual\\s+e'"),
                    "quale è"
                )
                .replace(
                    wordRegex("com'\\s*[èeé]"),
                    "come è"
                )
                .replace(
                    wordRegex("com'\\s*e'"),
                    "come è"
                )
                .replace(
                    wordRegex("cos'\\s*[èeé]"),
                    "cosa è"
                )
                .replace(
                    wordRegex("cos'\\s*e'"),
                    "cosa è"
                )
                .replace(
                    wordRegex("c'\\s*[èeé]"),
                    "ci è"
                )
                .replace(
                    wordRegex("c'\\s*e'"),
                    "ci è"
                )
                .replace(
                    wordRegex("s'\\s*[èeé]"),
                    "si è"
                )
                .replace(
                    wordRegex("s'\\s*e'"),
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

    private fun wordRegex(
        core: String
    ): Regex {

        return Regex(
            "(?:^|(?<=\\s))$core(?=\\s|[?.,;:!']|$)"
        )
    }
}
