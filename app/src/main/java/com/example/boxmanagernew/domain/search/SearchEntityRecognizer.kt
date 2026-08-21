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

        val hits =
            lookupResult.hits

        val recognizedEntities =
            listOf(
                entity(
                    CoreEntityType.OBJECT,
                    SearchArchiveScope.OBJECT,
                    hits.objects
                ),
                entity(
                    CoreEntityType.BOX,
                    SearchArchiveScope.BOX,
                    hits.boxes
                ),
                entity(
                    CoreEntityType.LOCATION,
                    SearchArchiveScope.LOCATION,
                    hits.locations
                ),
                entity(
                    CoreEntityType.CATEGORY,
                    SearchArchiveScope.CATEGORY,
                    hits.categories
                )
            ).filterNotNull()

        return SearchRecognizedEntitiesResult(
            recognizedEntities =
                recognizedEntities
        )
    }

    private fun entity(
        type: CoreEntityType,
        scope: SearchArchiveScope,
        keys: List<String>
    ): SearchRecognizedEntity? {

        if (keys.isEmpty()) {
            return null
        }

        return SearchRecognizedEntity(
            entityType = type,
            scope = scope,
            matchCount = keys.size,
            keys = keys
        )
    }

    fun buildT2Marker(
        result: SearchRecognizedEntitiesResult
    ): String {

        return "[T2] ENTITIES=" +
                result.recognizedEntities.joinToString(
                    separator = "|"
                ) {
                    it.entityType.name
                }
    }
}