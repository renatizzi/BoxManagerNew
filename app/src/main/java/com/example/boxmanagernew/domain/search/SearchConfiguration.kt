package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType

/**
 * Messaggi utente della Ricerca Interrogativa (catalogo 2.6).
 */
object SearchConfiguration {

    private const val MSG_NOT_UNDERSTOOD_IT =
        "Non ho compreso la richiesta."

    private const val MSG_CLARIFY_IT =
        "Puoi formulare la richiesta in modo più preciso?"

    private const val MSG_HOMONYM_CLARIFY_PREFIX_IT =
        "Riformula la domanda in modo che sia chiaro se ti riferisci"

    private const val MSG_NO_RESULTS_IT =
        "Nessun risultato trovato."

    private const val MSG_INTERROGATION_UNAVAILABLE_IT =
        "Questo tipo di richiesta non è ancora disponibile."

    /**
     * Pipeline ThreadLocal **oppure** locale UI (liste vuote sul main thread).
     * I test della Pipeline restano IT: display locale default IT.
     */
    private fun english(): Boolean {
        return SearchLocaleContext.isEnglish() ||
            SearchLocaleContext.displayIsEnglish()
    }

    val MSG_NOT_UNDERSTOOD: String
        get() =
            if (english()) {
                SearchLanguageTablesEn.MSG_NOT_UNDERSTOOD
            } else {
                MSG_NOT_UNDERSTOOD_IT
            }

    val MSG_CLARIFY: String
        get() =
            if (english()) {
                SearchLanguageTablesEn.MSG_CLARIFY
            } else {
                MSG_CLARIFY_IT
            }

    val MSG_NO_RESULTS: String
        get() =
            if (english()) {
                SearchLanguageTablesEn.MSG_NO_RESULTS
            } else {
                MSG_NO_RESULTS_IT
            }

    val MSG_INTERROGATION_UNAVAILABLE: String
        get() =
            if (english()) {
                SearchLanguageTablesEn.MSG_INTERROGATION_UNAVAILABLE
            } else {
                MSG_INTERROGATION_UNAVAILABLE_IT
            }

    fun homonymClarifyMessage(
        cores: Set<CoreEntityType>
    ): String {

        val inEnglish =
            english()

        val phrases =
            listOf(
                CoreEntityType.OBJECT to
                    if (inEnglish) {
                        SearchLanguageTablesEn.PHRASE_OBJECT
                    } else {
                        "a un oggetto"
                    },
                CoreEntityType.BOX to
                    if (inEnglish) {
                        SearchLanguageTablesEn.PHRASE_BOX
                    } else {
                        "a un contenitore"
                    },
                CoreEntityType.LOCATION to
                    if (inEnglish) {
                        SearchLanguageTablesEn.PHRASE_LOCATION
                    } else {
                        "a una posizione"
                    },
                CoreEntityType.CATEGORY to
                    if (inEnglish) {
                        SearchLanguageTablesEn.PHRASE_CATEGORY
                    } else {
                        "a una categoria"
                    }
            ).mapNotNull { (core, phrase) ->

                if (core in cores) {
                    phrase
                } else {
                    null
                }
            }

        if (phrases.size < 2) {
            return MSG_CLARIFY
        }

        val orWord =
            if (inEnglish) {
                "or"
            } else {
                "o"
            }

        val joined =
            if (phrases.size == 2) {
                "${phrases[0]} $orWord ${phrases[1]}"
            } else {
                phrases.dropLast(1).joinToString(
                    ", "
                ) + " $orWord " + phrases.last()
            }

        val prefix =
            if (inEnglish) {
                SearchLanguageTablesEn.MSG_HOMONYM_PREFIX
            } else {
                MSG_HOMONYM_CLARIFY_PREFIX_IT
            }

        return "$prefix $joined."
    }

    const val EXTRA_SEARCH_QUESTION =
        "dashboardSearchQuery"

    const val EXTRA_OBJECT_TERMS =
        "advancedObjectTerms"

    const val EXTRA_SEARCH_QUERY =
        "searchQuery"

    const val EXTRA_ADVANCED_OBJECT_MATCH =
        "advancedObjectMatch"

    const val EXTRA_LOCATION_TERMS =
        "advancedLocationTerms"

    const val EXTRA_CATEGORY_TERMS =
        "advancedCategoryTerms"

    const val EXTRA_BOX_TERMS =
        "advancedBoxTerms"

    const val EXTRA_HIGHLIGHT_TERMS =
        "advancedHighlightTerms"

    const val EXTRA_INVENTORY_LIST =
        "advancedInventoryList"

    const val INVENTORY_BOX =
        "BOX"

    const val INVENTORY_OBJECT =
        "OBJECT"

    const val INVENTORY_CATEGORY =
        "CATEGORY"

    const val INVENTORY_LOCATION =
        "LOCATION"

    const val LOCATION_TERMS_SEPARATOR =
        "\u001f"

    fun packLocationTerms(
        names: List<String>
    ): String {

        return names
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(
                LOCATION_TERMS_SEPARATOR
            )
    }

    fun splitLocationTerms(
        packed: String
    ): List<String> {

        if (packed.isBlank()) {
            return emptyList()
        }

        return packed
            .split(LOCATION_TERMS_SEPARATOR)
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    fun locationHighlightQuery(
        packed: String
    ): String {

        return splitLocationTerms(
            packed
        ).joinToString(" ")
    }
}
