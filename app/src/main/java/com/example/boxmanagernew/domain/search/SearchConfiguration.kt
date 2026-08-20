package com.example.boxmanagernew.domain.search

/**
 * Messaggi utente della Ricerca Interrogativa (catalogo 2.6).
 */
object SearchConfiguration {

    const val MSG_NOT_UNDERSTOOD =
        "Non ho compreso la richiesta."

    const val MSG_CLARIFY =
        "Puoi formulare la richiesta in modo più preciso?"

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
