package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveLookupResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveScope
import com.example.boxmanagernew.domain.search.model.SearchArchiveScopeMatch

class SearchArchiveLookup(

    private val gateway: SearchArchiveGateway =
        SearchArchiveGateway(),

    private val archivalLookup: SearchArchivalLookup =
        SearchArchivalLookup()
) {

    fun lookup(
        searchText: String,
        index: SearchArchiveIndex? = null,
        indicators: Map<String, Set<String>> =
            emptyMap()
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

        if (
            index != null
        ) {

            return lookupInArchive(
                searchText,
                index,
                indicators
            )
        }

        return stubLookup(
            normalized
        )
    }

    private fun lookupInArchive(
        searchText: String,
        index: SearchArchiveIndex,
        indicators: Map<String, Set<String>>
    ): SearchArchiveLookupResult {

        val hits =
            archivalLookup.find(
                searchText,
                index,
                indicators
            )

        val matches =
            mutableListOf<SearchArchiveScopeMatch>()

        addScope(
            matches,
            SearchArchiveScope.OBJECT,
            hits.objects.size
        )

        addScope(
            matches,
            SearchArchiveScope.BOX,
            hits.boxes.size
        )

        addScope(
            matches,
            SearchArchiveScope.LOCATION,
            hits.locations.size
        )

        addScope(
            matches,
            SearchArchiveScope.CATEGORY,
            hits.categories.size
        )

        return SearchArchiveLookupResult(
            scopeMatches = matches,
            hits = hits
        )
    }

    fun homonymCoresForClarification(
        question: String,
        index: SearchArchiveIndex
    ): Set<CoreEntityType> {

        return archivalLookup.homonymCoresForClarification(
            question,
            index
        )
    }

    fun needsHomonymClarification(
        question: String,
        index: SearchArchiveIndex
    ): Boolean {

        return homonymCoresForClarification(
            question,
            index
        ).size >= 2
    }

    private fun stubLookup(
        normalized: String
    ): SearchArchiveLookupResult {

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

    private fun addScope(
        matches: MutableList<SearchArchiveScopeMatch>,
        scope: SearchArchiveScope,
        matchCount: Int
    ) {

        if (
            matchCount > 0
        ) {

            matches.add(
                SearchArchiveScopeMatch(
                    scope = scope,
                    matchCount = matchCount
                )
            )
        }
    }
}
