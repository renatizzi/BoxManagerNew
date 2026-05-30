package com.example.boxmanagernew.domain.search.model

data class SearchQuestionPattern(

    val id: String,

    val variants: List<String>,

    val involvedEntities: Set<CoreEntityType>,

    val interpretation: SearchInterpretation,

    val dominantFulcrum: SearchFulcrum,

    val clarificationType: SearchClarificationType,

    val satisfiability: SearchSatisfiability,

    val classification: SearchClassification,

    val dominantStrategy: SearchStrategy,

    val supportsEngineA: Boolean,

    val expectedOutput: String
)