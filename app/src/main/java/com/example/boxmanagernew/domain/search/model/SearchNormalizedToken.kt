package com.example.boxmanagernew.domain.search.model

data class SearchNormalizedToken(

    val originalToken: String,

    val normalizedToken: String,

    val isCoreEntityToken: Boolean
)