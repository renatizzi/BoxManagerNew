package com.example.boxmanagernew.domain.search

/**
 * PATTERN_007 / F7 — Allegato 1 §1.1 e Matrice Test Ricerca.
 * Elenco intero, non ricomposto.
 */
object SearchF7Pattern {

    const val ID =
        "PATTERN_007"

    val VARIANTS: List<String>
        get() =
            if (SearchLocaleContext.isEnglish()) {
                SearchLanguageTablesEn.f7Variants
            } else {
                variantsIt
            }

    val HEADING: String
        get() =
            if (SearchLocaleContext.isEnglish()) {
                SearchLanguageTablesEn.F7_HEADING
            } else {
                "Elenco dei contenitori che hanno oggetti uguali"
            }

    private val variantsIt =
        listOf(
            "Cerca tutti i contenitori che contengono doppioni",
            "In quali contenitori ci sono oggetti uguali",
            "Elenco dei contenitori che hanno oggetti uguali",
            "Dove trovo lo stesso tipo di oggetti",
            "Trova i contenitori che hanno almeno un oggetto uguale"
        )

    fun matches(
        normalizedQuestion: String
    ): Boolean {

        val question =
            SearchNormalizer()
                .normalize(
                    normalizedQuestion
                )
                .normalizedQuestion

        return VARIANTS.any { variant ->

            SearchNormalizer()
                .normalize(
                    variant
                )
                .normalizedQuestion == question
        }
    }
}
