package com.example.boxmanagernew.domain.search.model

data class SearchSatisfiabilityResult(

    val finalClassification: SearchClassification,

    val satisfiableByEngineA: Boolean,

    val satisfiableByEngineB: Boolean,

    val requiresClarification: Boolean,

    val clarificationType: SearchClarificationType,

    val matchedPatternId: String?
)