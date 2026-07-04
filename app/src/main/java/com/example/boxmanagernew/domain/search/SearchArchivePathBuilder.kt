package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchArchivePath
import com.example.boxmanagernew.domain.search.model.SearchArchivePathStep
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntitiesResult

class SearchArchivePathBuilder {

    fun build(
        recognizedEntities: SearchRecognizedEntitiesResult,
        fulcrum: SearchFulcrum?
    ): SearchArchivePath {

        val steps = mutableListOf<SearchArchivePathStep>()

        when (fulcrum) {

            SearchFulcrum.OBJECT ->
                steps += SearchArchivePathStep.OBJECT

            SearchFulcrum.BOX ->
                steps += SearchArchivePathStep.BOX

            SearchFulcrum.LOCATION ->
                steps += SearchArchivePathStep.LOCATION

            SearchFulcrum.CATEGORY ->
                steps += SearchArchivePathStep.CATEGORY

            null -> {}
        }

        recognizedEntities.recognizedEntities.forEach {

            val step =
                when (it.entityType) {

                    CoreEntityType.OBJECT ->
                        SearchArchivePathStep.OBJECT

                    CoreEntityType.BOX ->
                        SearchArchivePathStep.BOX

                    CoreEntityType.LOCATION ->
                        SearchArchivePathStep.LOCATION

                    CoreEntityType.CATEGORY ->
                        SearchArchivePathStep.CATEGORY
                }

            if (!steps.contains(step)) {
                steps += step
            }
        }

        return SearchArchivePath(steps)
    }
}