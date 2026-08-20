package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchivePath
import com.example.boxmanagernew.domain.search.model.SearchArchivePathStep
import com.example.boxmanagernew.domain.search.model.SearchFulcrum

/**
 * Fase 5: percorso V1 verso la lista Contenitori.
 */
class SearchArchivePathBuilder {

    fun build(
        fulcrum: SearchFulcrum?
    ): SearchArchivePath {

        val steps =
            when (fulcrum) {

                SearchFulcrum.OBJECT ->
                    listOf(
                        SearchArchivePathStep.OBJECT,
                        SearchArchivePathStep.BOX
                    )

                SearchFulcrum.LOCATION ->
                    listOf(
                        SearchArchivePathStep.LOCATION,
                        SearchArchivePathStep.BOX
                    )

                SearchFulcrum.CATEGORY ->
                    listOf(
                        SearchArchivePathStep.CATEGORY,
                        SearchArchivePathStep.BOX
                    )

                SearchFulcrum.BOX ->
                    listOf(
                        SearchArchivePathStep.BOX
                    )

                null ->
                    emptyList()
            }

        return SearchArchivePath(
            steps = steps
        )
    }
}
