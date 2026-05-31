package com.example.boxmanagernew.domain.search.model

data class SearchCoreNormalizationResult(

    val normalizedQuestion: String,

    val normalizedTokens: List<SearchNormalizedToken>
)