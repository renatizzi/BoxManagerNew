package com.example.boxmanagernew.domain.search.model

data class SearchSatisfiabilityInput(

    val fulcrumResult: SearchFulcrumResult,

    val recognizedEntitiesResult:
    SearchRecognizedEntitiesResult,

    val matchedPatterns:
    List<SearchQuestionPattern> = emptyList()
)