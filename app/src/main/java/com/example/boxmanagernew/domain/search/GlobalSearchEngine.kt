package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchInterpretation
import com.example.boxmanagernew.domain.search.model.SearchQuestionPattern
import com.example.boxmanagernew.domain.search.model.SearchSatisfiability
import com.example.boxmanagernew.domain.search.model.SearchStrategy

class GlobalSearchEngine {

    private val synonymRepository =
        SynonymRepository()

    private val questionRepository =
        SearchQuestionRepository()

    private val stopWords =
        setOf(
            "a",
            "ad",
            "al",
            "alla",
            "allo",
            "che",
            "con",
            "da",
            "del",
            "della",
            "dello",
            "dei",
            "degli",
            "delle",
            "di",
            "dove",
            "e",
            "gli",
            "ha",
            "hai",
            "ho",
            "i",
            "il",
            "in",
            "l",
            "la",
            "le",
            "li",
            "lo",
            "messo",
            "nel",
            "nella",
            "nello",
            "nei",
            "negli",
            "nelle",
            "per",
            "quale",
            "quali",
            "quanto",
            "quanti",
            "sei",
            "si",
            "sono",
            "su",
            "tra",
            "un",
            "una",
            "uno"
        )

    fun determineStrategy(
        question: String
    ): SearchStrategy {

        if (
            question.isBlank()
        ) {

            return SearchStrategy.FALLBACK
        }

        return SearchStrategy.DIRECT_MATCH
    }

    fun extractFocusTerms(
        question: String
    ): List<String> {

        return question
            .lowercase()
            .replace(
                Regex("[^a-zàèéìòù0-9 ]"),
                " "
            )
            .split("\\s+".toRegex())
            .filter {
                it.isNotBlank()
            }
            .filterNot {
                stopWords.contains(it)
            }
    }

    fun identifyCoreEntities(
        question: String
    ): Set<CoreEntityType> {

        return extractFocusTerms(
            question
        )
            .mapNotNull {

                synonymRepository
                    .getCoreEntityType(it)
            }
            .toSet()
    }

    fun detectPattern(
        question: String
    ): SearchQuestionPattern? {

        val normalizedQuestion =
            question.lowercase()

        return questionRepository
            .getPatterns()
            .firstOrNull { pattern ->

                pattern.variants.any { variant ->

                    normalizedQuestion.contains(
                        variant.lowercase()
                    )
                }
            }
    }

    fun interpret(
        question: String
    ): SearchInterpretation? {

        return detectPattern(
            question
        )?.interpretation
    }

    fun determineFulcrum(
        question: String
    ): SearchFulcrum? {

        return detectPattern(
            question
        )?.dominantFulcrum
    }

    fun determineSatisfiability(
        question: String
    ): SearchSatisfiability? {

        return detectPattern(
            question
        )?.satisfiability
    }

    fun determineClassification(
        question: String
    ): SearchClassification? {

        return detectPattern(
            question
        )?.classification
    }
}