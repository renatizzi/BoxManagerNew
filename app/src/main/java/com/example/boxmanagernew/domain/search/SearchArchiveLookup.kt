package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchiveLookupResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveScope
import com.example.boxmanagernew.domain.search.model.SearchArchiveScopeMatch

class SearchArchiveLookup(

    private val gateway: SearchArchiveGateway =
        SearchArchiveGateway()
) {

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

        val matches =
            mutableListOf<SearchArchiveScopeMatch>()

        if (
            gateway.hasMatches(
                normalized
            )
        ) {

            matches.add(
                SearchArchiveScopeMatch(
                    scope =
                        SearchArchiveScope.OBJECT,
                    matchCount = 1
                )
            )
        }

        return SearchArchiveLookupResult(
            scopeMatches = matches
        )
    }
}