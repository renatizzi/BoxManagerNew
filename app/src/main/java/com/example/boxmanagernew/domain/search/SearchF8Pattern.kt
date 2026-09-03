package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * PATTERN_008 / F8 — Allegato 1 §1.1 e Matrice Test Ricerca.
 * Elenco intero. Motore B: stesso tipo di oggetto, categorie diverse.
 */
object SearchF8Pattern {

    const val ID =
        "PATTERN_008"

    val VARIANTS: List<String>
        get() =
            if (SearchLocaleContext.isEnglish()) {
                SearchLanguageTablesEn.f8Variants
            } else {
                variantsIt
            }

    val HEADING: String
        get() =
            if (SearchLocaleContext.isEnglish()) {
                SearchLanguageTablesEn.F8_HEADING
            } else {
                "Elenco dei contenitori che hanno categoria diversa e contengono oggetti uguali"
            }

    private val variantsIt =
        listOf(
            "Cerca i contenitori con categoria diversa che contengono lo stesso tipo di oggetto",
            "Quali contenitori hanno categoria diversa e contengono oggetti uguali",
            "Trova contenitori con categoria diversa e oggetti uguali",
            "Elenco contenitori con categoria diversa e oggetti uguali",
            "Quali contenitori hanno una categoria diversa e contengono oggetti uguali",
            "Trova i contenitori con categoria diversa e con oggetti uguali",
            "Elenco dei contenitori che hanno categoria diversa e contengono oggetti uguali"
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

        if (
            VARIANTS.any { variant ->

                SearchNormalizer()
                    .normalize(
                        variant
                    )
                    .normalizedQuestion == question
            }
        ) {

            return true
        }

        return isOfficialFamily(
            question
        )
    }

    fun isOfficialFamily(
        normalizedQuestion: String
    ): Boolean {

        val tokens =
            SearchNormalizer()
                .normalize(
                    normalizedQuestion
                )
                .normalizedQuestion
                .split(" ")
                .filter { token ->
                    token.isNotBlank()
                }

        val hasCategory =
            tokens.any { token ->
                SearchCoreAliases.isCategoryAlias(
                    token
                )
            }

        val hasDiverso =
            tokens.any { token ->
                CanonicalNormalizer.wholeWordMatches(
                    token,
                    "diverso"
                ) ||
                    CanonicalNormalizer.wholeWordMatches(
                        token,
                        "diversa"
                    ) ||
                    token.equals("different", ignoreCase = true)
            }

        val hasSameObject =
            tokens.any { token ->
                CanonicalNormalizer.wholeWordMatches(
                    token,
                    "uguale"
                ) ||
                    CanonicalNormalizer.wholeWordMatches(
                        token,
                        "stesso"
                    ) ||
                    token.equals("identical", ignoreCase = true) ||
                    token.equals("same", ignoreCase = true)
            }

        return hasCategory &&
            hasDiverso &&
            hasSameObject
    }
}
