package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.util.CanonicalNormalizer

object ObjectSearchMatcher {

    private val ignoredWords =
        setOf(
            "a","ad","da","di","del","della",
            "dello","dei","degli","con",
            "per","in","su","al","alla"
        )

    fun matches(
        typeName: String,
        description: String?,
        query: String
    ): Boolean {

        val tokens =
            CanonicalNormalizer.wordTokens(
                query
            )
                .filter { token ->
                    token.isNotBlank() &&
                            token !in ignoredWords
                }

        if (tokens.isEmpty()) {
            return false
        }

        val haystackWords =
            CanonicalNormalizer.wordTokens(
                "$typeName ${description.orEmpty()}"
            )

        return tokens.all { token ->

            haystackWords.any { word ->

                CanonicalNormalizer.wholeWordMatches(
                    token,
                    word
                )
            }
        }
    }
}
