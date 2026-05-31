package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchFulcrumResult
import com.example.boxmanagernew.domain.search.model.SearchRecognizedEntitiesResult

class SearchFulcrumResolver {

    fun resolve(
        recognizedEntitiesResult: SearchRecognizedEntitiesResult
    ): SearchFulcrumResult {

        val recognizedEntities =
            recognizedEntitiesResult.recognizedEntities

        val objectOrBox =
            recognizedEntities.firstOrNull {
                it.entityType == CoreEntityType.OBJECT ||
                        it.entityType == CoreEntityType.BOX
            }

        if (objectOrBox != null) {

            return SearchFulcrumResult(
                fulcrum =
                    when (objectOrBox.entityType) {

                        CoreEntityType.OBJECT ->
                            SearchFulcrum.OBJECT

                        else ->
                            SearchFulcrum.BOX
                    },
                reason =
                    "OBJECT_OR_BOX_PRIORITY"
            )
        }

        val location =
            recognizedEntities.firstOrNull {
                it.entityType ==
                        CoreEntityType.LOCATION
            }

        if (location != null) {

            return SearchFulcrumResult(
                fulcrum =
                    SearchFulcrum.LOCATION,
                reason =
                    "LOCATION_PRIORITY"
            )
        }

        val category =
            recognizedEntities.firstOrNull {
                it.entityType ==
                        CoreEntityType.CATEGORY
            }

        if (category != null) {

            return SearchFulcrumResult(
                fulcrum =
                    SearchFulcrum.CATEGORY,
                reason =
                    "CATEGORY_PRIORITY"
            )
        }

        return SearchFulcrumResult(
            fulcrum = null,
            reason = "NO_FULCRUM"
        )
    }
}