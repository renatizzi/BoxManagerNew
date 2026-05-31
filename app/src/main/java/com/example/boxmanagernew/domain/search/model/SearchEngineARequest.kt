package com.example.boxmanagernew.domain.search.model

data class SearchEngineARequest(

    val fulcrumResult: SearchFulcrumResult,

    val searchText: String,

    val finalClassification: SearchClassification
)