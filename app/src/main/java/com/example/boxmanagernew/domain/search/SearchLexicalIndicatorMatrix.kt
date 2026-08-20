package com.example.boxmanagernew.domain.search

class SearchLexicalIndicatorMatrix {

    companion object {

        const val SIMPLE_SEARCH =
            "SIMPLE_SEARCH"

        const val LOCALIZATION =
            "LOCALIZATION"

        const val RELATION =
            "RELATION"

        const val AMBIGUITY =
            "AMBIGUITY"

        private val SIMPLE_SEARCH_INDICATORS =
            setOf(
                "cerca",
                "mostra",
                "dammi",
                "dimmi",
                "trova",
                "dove"
            )

        private val LOCALIZATION_INDICATORS =
            setOf(
                "in",
                "posizione",
                "luogo",
                "posto",
                "locale",
                "sito",
                "dove"
            )

        private val RELATION_INDICATORS =
            setOf(
                "quale",
                "intersezione",
                "e",
                "con",
                "sia"
            )

        private val AMBIGUITY_INDICATORS =
            setOf(
                "materiale",
                "cosa",
                "documento"
            )
    }

    fun findIndicators(
        normalizedQuestion: String
    ): Map<String, Set<String>> {

        val question =
            normalizedQuestion.lowercase()

        return mapOf(
            SIMPLE_SEARCH to
                    SIMPLE_SEARCH_INDICATORS.filter {
                        question.contains(it)
                    }.toSet(),

            LOCALIZATION to
                    LOCALIZATION_INDICATORS.filter {
                        question.contains(it)
                    }.toSet(),

            RELATION to
                    RELATION_INDICATORS.filter {
                        question.contains(it)
                    }.toSet(),

            AMBIGUITY to
                    AMBIGUITY_INDICATORS.filter {
                        question.contains(it)
                    }.toSet()
        )
    }

    fun hasMultiEntityIndicators(
        indicators: Map<String, Set<String>>
    ): Boolean {

        return indicators
            .count {
                it.value.isNotEmpty()
            } > 1
    }
}