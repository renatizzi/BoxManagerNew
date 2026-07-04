package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchivePath
import com.example.boxmanagernew.domain.search.model.SearchArchivePathStep
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation

class SearchArchiveTransformationResolver {

    fun resolve(
        path: SearchArchivePath
    ): SearchArchiveTransformation {

        val steps = path.steps

        if (steps.size < 2) {
            return SearchArchiveTransformation.NONE
        }

        return when (steps.take(2)) {

            listOf(
                SearchArchivePathStep.OBJECT,
                SearchArchivePathStep.BOX
            ) ->
                SearchArchiveTransformation.OBJECT_TO_BOX

            listOf(
                SearchArchivePathStep.BOX,
                SearchArchivePathStep.LOCATION
            ) ->
                SearchArchiveTransformation.BOX_TO_LOCATION

            listOf(
                SearchArchivePathStep.BOX,
                SearchArchivePathStep.CATEGORY
            ) ->
                SearchArchiveTransformation.BOX_TO_CATEGORY

            listOf(
                SearchArchivePathStep.LOCATION,
                SearchArchivePathStep.BOX
            ) ->
                SearchArchiveTransformation.LOCATION_TO_BOX

            listOf(
                SearchArchivePathStep.CATEGORY,
                SearchArchivePathStep.BOX
            ) ->
                SearchArchiveTransformation.CATEGORY_TO_BOX

            listOf(
                SearchArchivePathStep.OBJECT,
                SearchArchivePathStep.LOCATION
            ) ->
                SearchArchiveTransformation.OBJECT_TO_LOCATION

            listOf(
                SearchArchivePathStep.OBJECT,
                SearchArchivePathStep.CATEGORY
            ) ->
                SearchArchiveTransformation.OBJECT_TO_CATEGORY

            else ->
                SearchArchiveTransformation.NONE
        }
    }
}