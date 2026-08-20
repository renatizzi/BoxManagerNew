package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Tabella ufficiale alias Core (Nota 1.3.3 / Matrice Indicatori Core V2).
 * I plurali e le forme senza accento sono espansione di matching, non un altro elenco.
 * "locale" è in più: Allegato 4 e casi fetta 2.
 */
object SearchCoreAliases {

    val objectTerms =
        setOf(
            "oggetto",
            "oggetti",
            "articolo",
            "articoli",
            "elemento",
            "elementi",
            "utensile",
            "utensili",
            "cosa",
            "cose",
            "affare",
            "affari",
            "roba",
            "robe",
            "prodotto",
            "prodotti",
            "arnese",
            "arnesi"
        )

    val boxTerms =
        setOf(
            "contenitore",
            "contenitori",
            "box",
            "boxes",
            "scatola",
            "scatole",
            "scatolone",
            "scatoloni",
            "cassetta",
            "cassette",
            "confezione",
            "confezioni",
            "baule",
            "bauli",
            "busta",
            "buste",
            "bustone",
            "bustoni",
            "cassetto",
            "cassetti",
            "barattolo",
            "barattoli",
            "vaso",
            "vasi",
            "bacinella",
            "bacinelle",
            "recipiente",
            "recipienti",
            "cassa",
            "casse",
            "cassone",
            "cassoni",
            "bidone",
            "bidoni",
            "cassonetto",
            "cassonetti",
            "cassaforte",
            "casseforti",
            "portafoglio",
            "portafogli",
            "portaoggetti",
            "portagioie",
            "portadocumenti",
            "container",
            "containers",
            "involucro",
            "involucri",
            "custodia",
            "custodie",
            "cover",
            "covers",
            "imballaggio",
            "imballaggi"
        )

    val locationTerms =
        setOf(
            "posizione",
            "posizioni",
            "luogo",
            "luoghi",
            "posto",
            "posti",
            "ubicazione",
            "ubicazioni",
            "sito",
            "siti",
            "area",
            "aree",
            "zona",
            "zone",
            "perimetro",
            "perimetri",
            "spazio",
            "spazi",
            "ambiente",
            "ambienti",
            "città",
            "citta",
            "paese",
            "paesi",
            "località",
            "localita",
            "punto",
            "punti",
            "locale",
            "locali"
        )

    val categoryTerms =
        setOf(
            "categoria",
            "categorie",
            "classe",
            "classi",
            "classificazione",
            "classificazioni",
            "gruppo",
            "gruppi",
            "aggregato",
            "aggregati",
            "raggruppamento",
            "raggruppamenti",
            "specie",
            "famiglia",
            "famiglie",
            "ordine",
            "ordini",
            "divisione",
            "divisioni",
            "grado",
            "gradi",
            "fascia",
            "fasce",
            "tipo",
            "tipi",
            "tipologia",
            "tipologie",
            "qualità",
            "qualita",
            "genere",
            "generi"
        )

    fun isObjectAlias(
        token: String
    ): Boolean =
        matches(token, objectTerms)

    fun isBoxAlias(
        token: String
    ): Boolean =
        matches(token, boxTerms)

    fun isLocationAlias(
        token: String
    ): Boolean =
        matches(token, locationTerms)

    fun isCategoryAlias(
        token: String
    ): Boolean =
        matches(token, categoryTerms)

    fun canonicalToken(
        token: String
    ): String? {

        return when {

            isObjectAlias(token) ->
                "oggetto"

            isBoxAlias(token) ->
                "contenitore"

            isCategoryAlias(token) ->
                "categoria"

            isLocationAlias(token) ->
                "posizione"

            else ->
                null
        }
    }

    fun coreEntityType(
        token: String
    ): CoreEntityType? {

        return when (
            canonicalToken(token)
        ) {

            "oggetto" ->
                CoreEntityType.OBJECT

            "contenitore" ->
                CoreEntityType.BOX

            "posizione" ->
                CoreEntityType.LOCATION

            "categoria" ->
                CoreEntityType.CATEGORY

            else ->
                null
        }
    }

    private fun matches(
        token: String,
        terms: Set<String>
    ): Boolean {

        val lower =
            token.lowercase()

        if (lower in terms) {
            return true
        }

        return CanonicalNormalizer.normalize(
            token
        ) in terms
    }
}
