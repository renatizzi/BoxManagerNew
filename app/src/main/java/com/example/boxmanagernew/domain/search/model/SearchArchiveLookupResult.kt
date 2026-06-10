package com.example.boxmanagernew.domain.search.model

data class SearchArchiveLookupResult(

    val scopeMatches:
    List<SearchArchiveScopeMatch>,

    val hasMatches: Boolean =
        scopeMatches.isNotEmpty()
)