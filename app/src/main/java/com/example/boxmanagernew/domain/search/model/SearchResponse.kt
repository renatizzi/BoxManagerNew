package com.example.boxmanagernew.domain.search.model

data class SearchResponse(

    val success: Boolean,

    val message: String,

    val requiresClarification: Boolean = false,

    val clarificationType: SearchClarificationType =
        SearchClarificationType.NONE
)