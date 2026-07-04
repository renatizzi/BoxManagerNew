package com.example.boxmanagernew.domain.search.model

data class SearchArchiveQuery(

    val operation: SearchArchiveQueryOperation,

    val targetEntities: Set<CoreEntityType>,

    val filters: List<String> = emptyList()
)