package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchArchiveLookupResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveScope
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntitiesResult
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntity

class SearchEntityRecognizer {

    fun recognize(
        lookupResult: SearchArchiveLookupResult
    ): SearchRecognizedEntitiesResult {

        val recognizedEntities =
            lookupResult.scopeMatches.map {

                SearchRecognizedEntity(
                    entityType =
                        when (it.scope) {

                            SearchArchiveScope.OBJECT ->
                                CoreEntityType.OBJECT

                            SearchArchiveScope.BOX ->
                                CoreEntityType.BOX

                            SearchArchiveScope.LOCATION ->
                                CoreEntityType.LOCATION

                            SearchArchiveScope.CATEGORY ->
                                CoreEntityType.CATEGORY
                        },
                    scope =
                        it.scope,
                    matchCount =
                        it.matchCount
                )
            }

        return SearchRecognizedEntitiesResult(
            recognizedEntities =
                recognizedEntities
        )
    }
}