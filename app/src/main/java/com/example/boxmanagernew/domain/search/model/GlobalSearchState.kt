package com.example.boxmanagernew.domain.search.model

data class GlobalSearchState(

    val currentQuestion: String = "",

    val clarificationCount: Int = 0,

    val isProcessing: Boolean = false,

    val lastResponse: String = "",

    val recognizedEntities: Set<CoreEntityType> = emptySet(),

    val interpretation: SearchInterpretation? = null,

    val dominantFulcrum: SearchFulcrum? = null,

    val clarificationType: SearchClarificationType =
        SearchClarificationType.NONE,

    val satisfiability: SearchSatisfiability? = null,

    val classification: SearchClassification? = null,

    val selectedEngine: SearchEngineType? = null,

    val detectedPatternId: String? = null
)