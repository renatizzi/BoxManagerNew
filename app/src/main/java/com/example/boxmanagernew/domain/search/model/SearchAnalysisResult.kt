package com.example.boxmanagernew.domain.search.model

data class SearchAnalysisResult(

    val originalQuery: String,

    val operationalQuery: String? = null,

    val interpretation: SearchInterpretation?,

    val recognizedEntities: Set<CoreEntityType>,

    val dominantFulcrum: SearchFulcrum?,

    val satisfiability: SearchSatisfiability?,

    val classification: SearchClassification?,

    val patternId: String?
)