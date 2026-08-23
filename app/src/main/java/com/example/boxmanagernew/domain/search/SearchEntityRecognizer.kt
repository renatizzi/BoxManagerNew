package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchArchivalHits
import com.example.boxmanagernew.domain.search.model.SearchArchiveLookupResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveScope
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntitiesResult
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntity

class SearchEntityRecognizer {

    fun recognize(
        lookupResult: SearchArchiveLookupResult,
        indicators: Map<String, Set<String>> =
            emptyMap()
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
                .toMutableList()

        val hasCategoryAlias =
            indicators[
                SearchLexicalIndicatorMatrix.CATEGORY
            ].orEmpty().isNotEmpty()

        val hasBoxAlias =
            indicators[
                SearchLexicalIndicatorMatrix.BOX
            ].orEmpty().isNotEmpty()

        val hasObjectAlias =
            indicators[
                SearchLexicalIndicatorMatrix.OBJECT
            ].orEmpty().isNotEmpty()

        val hasLocationAlias =
            indicators[
                SearchLexicalIndicatorMatrix.LOCATION
            ].orEmpty().any { term ->
                SearchCoreAliases.isLocationAlias(
                    term
                )
            }

        if (hasCategoryAlias && hasBoxAlias) {

            addTypeOnly(
                recognizedEntities,
                CoreEntityType.CATEGORY,
                SearchArchiveScope.CATEGORY
            )
            addTypeOnly(
                recognizedEntities,
                CoreEntityType.BOX,
                SearchArchiveScope.BOX
            )

            if (hasObjectAlias) {

                addTypeOnly(
                    recognizedEntities,
                    CoreEntityType.OBJECT,
                    SearchArchiveScope.OBJECT
                )
            }
        }

        if (hasLocationAlias && hasBoxAlias) {

            addTypeOnly(
                recognizedEntities,
                CoreEntityType.LOCATION,
                SearchArchiveScope.LOCATION
            )
            addTypeOnly(
                recognizedEntities,
                CoreEntityType.BOX,
                SearchArchiveScope.BOX
            )

            if (hasObjectAlias) {

                addTypeOnly(
                    recognizedEntities,
                    CoreEntityType.OBJECT,
                    SearchArchiveScope.OBJECT
                )
            }
        }

        if (
            !hasNamedArchiveKey(
                hits
            )
        ) {

            if (
                hasBoxAlias &&
                !hasCategoryAlias &&
                !hasLocationAlias &&
                !hasObjectAlias
            ) {

                addTypeOnly(
                    recognizedEntities,
                    CoreEntityType.BOX,
                    SearchArchiveScope.BOX
                )
            }

            if (
                hasObjectAlias &&
                !hasCategoryAlias &&
                !hasLocationAlias &&
                !hasBoxAlias
            ) {

                addTypeOnly(
                    recognizedEntities,
                    CoreEntityType.OBJECT,
                    SearchArchiveScope.OBJECT
                )
            }

            if (
                hasCategoryAlias &&
                !hasBoxAlias
            ) {

                addTypeOnly(
                    recognizedEntities,
                    CoreEntityType.CATEGORY,
                    SearchArchiveScope.CATEGORY
                )
            }

            if (
                hasLocationAlias &&
                !hasBoxAlias
            ) {

                addTypeOnly(
                    recognizedEntities,
                    CoreEntityType.LOCATION,
                    SearchArchiveScope.LOCATION
                )
            }
        }

        return SearchRecognizedEntitiesResult(
            recognizedEntities =
                recognizedEntities
        )
    }

    private fun hasNamedArchiveKey(
        hits: SearchArchivalHits
    ): Boolean {

        val namedObjects =
            hits.objects.any { name ->
                !SearchCoreAliases.isLocationAlias(
                    name
                ) &&
                    !SearchCoreAliases.isCategoryAlias(
                        name
                    )
            }

        val namedCategories =
            hits.categories.any { name ->
                !SearchCoreAliases.isCategoryAlias(
                    name
                )
            }

        val namedLocations =
            hits.locations.any { name ->
                !SearchCoreAliases.isLocationAlias(
                    name
                )
            }

        val namedBoxes =
            hits.boxes.isNotEmpty()

        return namedObjects ||
            namedCategories ||
            namedLocations ||
            namedBoxes
    }

    private fun addTypeOnly(
        recognizedEntities: MutableList<SearchRecognizedEntity>,
        type: CoreEntityType,
        scope: SearchArchiveScope
    ) {

        if (
            recognizedEntities.any { entity ->
                entity.entityType == type
            }
        ) {
            return
        }

        recognizedEntities.add(
            SearchRecognizedEntity(
                entityType = type,
                scope = scope,
                matchCount = 0,
                keys = emptyList()
            )
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