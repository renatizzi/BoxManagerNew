package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Matrice 3.3.5 + alias Core tabella 1.3.3.
 * Gli esempi di 3.3.5 non sostituiscono le quattro righe Core.
 * Confronto e aggregazione: elenco della Nota, non ricomposto.
 */
class SearchLexicalIndicatorMatrix {

    companion object {

        const val OBJECT =
            "OBJECT"

        const val BOX =
            "BOX"

        const val LOCATION =
            "LOCATION"

        const val CATEGORY =
            "CATEGORY"

        const val CONFRONTO =
            "CONFRONTO"

        const val AGGREGAZIONE =
            "AGGREGAZIONE"

        const val SIMPLE_SEARCH =
            AGGREGAZIONE

        const val LOCALIZATION =
            LOCATION

        const val RELATION =
            CONFRONTO

        const val AMBIGUITY =
            CONFRONTO

        private val CONFRONTO_TERMS =
            setOf(
                "uguale",
                "stesso",
                "duplicato",
                "doppione",
                "diverso",
                "differente",
                "confronto"
            )

        private val AGGREGAZIONE_TERMS =
            setOf(
                "tutti",
                "elenco",
                "quali"
            )

        private val LOCATION_CLUES =
            setOf(
                "dove"
            )

        fun isOfficialIndicator(
            token: String
        ): Boolean {

            if (
                SearchCoreAliases.isObjectAlias(token) ||
                        SearchCoreAliases.isBoxAlias(token) ||
                        SearchCoreAliases.isLocationAlias(token) ||
                        SearchCoreAliases.isCategoryAlias(token)
            ) {
                return true
            }

            return presentIn(
                LOCATION_CLUES,
                token
            ) ||
                    presentIn(
                        CONFRONTO_TERMS,
                        token
                    ) ||
                    presentIn(
                        AGGREGAZIONE_TERMS,
                        token
                    )
        }

        private fun presentIn(
            terms: Set<String>,
            token: String
        ): Boolean {

            return terms.any { term ->

                CanonicalNormalizer.wholeWordMatches(
                    token,
                    term
                )
            }
        }
    }

    fun findIndicators(
        normalizedQuestion: String
    ): Map<String, Set<String>> {

        val tokens =
            CanonicalNormalizer.wordTokens(
                normalizedQuestion
            )

        return mapOf(
            OBJECT to
                    matched(
                        SearchCoreAliases.objectTerms,
                        tokens
                    ),
            BOX to
                    matched(
                        SearchCoreAliases.boxTerms,
                        tokens
                    ),
            LOCATION to
                    matched(
                        SearchCoreAliases.locationTerms +
                                LOCATION_CLUES,
                        tokens
                    ),
            CATEGORY to
                    matched(
                        SearchCoreAliases.categoryTerms,
                        tokens
                    ),
            CONFRONTO to
                    matched(
                        CONFRONTO_TERMS,
                        tokens
                    ),
            AGGREGAZIONE to
                    matched(
                        AGGREGAZIONE_TERMS,
                        tokens
                    )
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

    private fun matched(
        terms: Set<String>,
        tokens: List<String>
    ): Set<String> {

        return terms.filter { term ->

            tokens.any { token ->

                CanonicalNormalizer.wholeWordMatches(
                    token,
                    term
                )
            }
        }.toSet()
    }
}
