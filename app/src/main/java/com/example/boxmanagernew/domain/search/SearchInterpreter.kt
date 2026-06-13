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

    fun buildM3Marker(
        result: SearchCoreNormalizationResult,
        interpretation: SearchInterpretation
    ): String {

        return "[M3] " +
                "QUESTION=${result.normalizedQuestion} " +
                "INTERPRETATION=$interpretation"
    }
}