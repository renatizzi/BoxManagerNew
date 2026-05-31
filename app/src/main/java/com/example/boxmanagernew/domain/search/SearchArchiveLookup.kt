package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveLookupResult

class SearchArchiveLookup {

    fun lookup(
        searchText: String
    ): SearchArchiveLookupResult {

        return SearchArchiveLookupResult(
            scopeMatches = emptyList()
        )
    }
}