package com.example.boxmanagernew.domain.search.model

@Deprecated(
    message = "Usare SearchArchiveScopeMatch"
)
data class SearchArchiveMatch(

    val entityType: CoreEntityType,

    val matchCount: Int
)