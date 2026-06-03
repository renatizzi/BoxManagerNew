package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveLookupResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveScope
import com.example.boxmanagernew.domain.search.model.SearchArchiveScopeMatch

class SearchArchiveLookup {

    fun lookup(
        searchText: String
    ): SearchArchiveLookupResult {

        val normalized =
            searchText
                .trim()
                .lowercase()

        if (
            normalized.isBlank()
        ) {

            return SearchArchiveLookupResult(
                scopeMatches = emptyList()
            )
        }

        return SearchArchiveLookupResult(
            scopeMatches =
                listOf(
                    SearchArchiveScopeMatch(
                        scope =
                            SearchArchiveScope.OBJECT,
                        matchCount = 1
                    )
                )
        )
    }
}