package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchCoreNormalizationResult
import com.example.boxmanagernew.domain.search.model.SearchTokenizedQuestion

class SearchCoreNormalizationPipeline {

    private val coreNormalizer =
        SearchCoreNormalizer()

    fun normalize(
        question: SearchTokenizedQuestion
    ): SearchCoreNormalizationResult {

        val normalizedTokens =
            question.tokens.map {

                coreNormalizer.normalize(it)
            }

        val normalizedQuestion =
            normalizedTokens.joinToString(
                separator = " "
            ) {

                it.normalizedToken
            }

        return SearchCoreNormalizationResult(
            normalizedQuestion =
                normalizedQuestion,
            normalizedTokens =
                normalizedTokens
        )
    }
}