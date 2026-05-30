package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType

class SynonymRepository {

    fun getCoreEntityType(
        term: String
    ): CoreEntityType? {

        return when (
            term.lowercase()
        ) {

            "oggetto",
            "oggetti",
            "articolo",
            "articoli",
            "elemento",
            "elementi",
            "prodotto",
            "prodotti",
            "cosa",
            "cose" ->

                CoreEntityType.OBJECT

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
            "buste" ->

                CoreEntityType.BOX

            "categoria",
            "categorie",
            "classificazione",
            "classificazioni",
            "classe",
            "classi" ->

                CoreEntityType.CATEGORY

            "posizione",
            "posizioni",
            "luogo",
            "luoghi",
            "ubicazione",
            "ubicazioni",
            "collocazione",
            "collocazioni",
            "posto",
            "posti" ->

                CoreEntityType.LOCATION

            else -> null
        }
    }
}