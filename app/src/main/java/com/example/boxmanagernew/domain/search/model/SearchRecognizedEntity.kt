package com.example.boxmanagernew.domain.search.model

data class SearchRecognizedEntity(

    val entityType: CoreEntityType,

    val scope: SearchArchiveScope,

    val matchCount: Int
)