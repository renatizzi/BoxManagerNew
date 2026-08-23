package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType

/**
 * Messaggi utente della Ricerca Interrogativa (catalogo 2.6).
 */
object SearchConfiguration {

    const val MSG_NOT_UNDERSTOOD =
        "Non ho compreso la richiesta."

    const val MSG_CLARIFY =
        "Puoi formulare la richiesta in modo più preciso?"

    private const val MSG_HOMONYM_CLARIFY_PREFIX =
        "Riformula la domanda in modo che sia chiaro se ti riferisci"

    fun homonymClarifyMessage(
        cores: Set<CoreEntityType>
    ): String {

        val phrases =
            listOf(
                CoreEntityType.OBJECT to
                    "a un oggetto",
                CoreEntityType.BOX to
                    "a un contenitore",
                CoreEntityType.LOCATION to
                    "a una posizione",
                CoreEntityType.CATEGORY to
                    "a una categoria"
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

        val joined =
            if (phrases.size == 2) {
                "${phrases[0]} o ${phrases[1]}"
            } else {
                phrases.dropLast(1).joinToString(
                    ", "
                ) + " o " + phrases.last()
            }

        return "$MSG_HOMONYM_CLARIFY_PREFIX $joined."
    }

    const val MSG_NO_RESULTS =
        "Nessun risultato trovato."

    const val MSG_INTERROGATION_UNAVAILABLE =
        "Questo tipo di richiesta non è ancora disponibile."

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
