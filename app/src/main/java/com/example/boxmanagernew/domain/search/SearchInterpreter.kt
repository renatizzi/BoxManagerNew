package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchCoreNormalizationResult
import com.example.boxmanagernew.domain.search.model.SearchInterpretation

class SearchInterpreter {

    fun interpret(
        result: SearchCoreNormalizationResult
    ): SearchInterpretation {

        val normalizedQuestion =
            result.normalizedQuestion

        return when {

            normalizedQuestion.contains(
                "oggetto"
            ) -> {

                SearchInterpretation.FIND_OBJECT
            }

            normalizedQuestion.contains(
                "contenitore"
            ) -> {

                SearchInterpretation.FIND_BOX
            }

            normalizedQuestion.contains(
                "categoria"
            ) -> {

                SearchInterpretation.FIND_CATEGORY
            }

            normalizedQuestion.contains(
                "posizione"
            ) -> {

                SearchInterpretation.FIND_LOCATION
            }

            else -> {

                SearchInterpretation.UNKNOWN
            }
        }
    }

    fun buildD1Marker(
        interpretation: SearchInterpretation
    ): String {

        return "[D1] INTERPRETATION=$interpretation"
    }

    fun buildD2Marker(
        result: SearchCoreNormalizationResult,
        interpretation: SearchInterpretation
    ): String {

        return "[D2] " +
                "QUESTION=${result.normalizedQuestion} " +
                "REASON=$interpretation"
    }
}