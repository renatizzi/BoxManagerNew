package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchNormalizedToken

class SearchCoreNormalizer {

    fun normalize(
        token: String
    ): SearchNormalizedToken {

        val normalizedToken =
            when (token.lowercase()) {

                "oggetto",
                "oggetti",
                "articolo",
                "articoli",
                "elemento",
                "elementi",
                "prodotto",
                "prodotti",
                "cosa",
                "cose" -> "oggetto"

                "box",
                "boxes",
                "contenitore",
                "contenitori",
                "scatola",
                "scatole",
                "scatolone",
                "scatoloni",
                "pacco",
                "pacchi",
                "borsa",
                "borse",
                "borsone",
                "borsoni",
                "busta",
                "buste" -> "contenitore"

                "categoria",
                "categorie",
                "classificazione",
                "classificazioni",
                "classe",
                "classi" -> "categoria"

                "posizione",
                "posizioni",
                "luogo",
                "luoghi",
                "ubicazione",
                "ubicazioni",
                "collocazione",
                "collocazioni",
                "posto",
                "posti" -> "posizione"

                else -> token
            }

        return SearchNormalizedToken(
            originalToken = token,
            normalizedToken = normalizedToken,
            isCoreEntityToken =
                normalizedToken != token ||
                        normalizedToken in setOf(
                    "oggetto",
                    "contenitore",
                    "categoria",
                    "posizione"
                )
        )
    }
}