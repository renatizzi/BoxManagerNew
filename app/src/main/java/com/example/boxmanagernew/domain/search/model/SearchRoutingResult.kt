package com.example.boxmanagernew.domain.search.model

data class SearchRoutingResult(

    val analysis: SearchAnalysisResult,

    val engineType: SearchEngineType,

    val requiresClarification: Boolean,

    val clarificationType: SearchClarificationType,

    val isFallback: Boolean
)