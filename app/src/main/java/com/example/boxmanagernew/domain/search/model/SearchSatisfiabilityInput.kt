package com.example.boxmanagernew.domain.search.model

data class SearchSatisfiabilityInput(

    val originalQuestion: String,

    val interpretation:
    SearchInterpretation?,

    val fulcrumResult:
    SearchFulcrumResult,

    val recognizedEntitiesResult:
    SearchRecognizedEntitiesResult,

    val matchedPatterns:
    List<SearchQuestionPattern> = emptyList(),

    val lexicalIndicators:
    List<String> = emptyList()
)