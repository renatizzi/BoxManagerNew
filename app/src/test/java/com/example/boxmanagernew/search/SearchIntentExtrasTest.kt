package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.SearchConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Regressione Renato KO oggetti: se domanda e needle condividono
 * la chiave Intent, putExtra(domanda) sovrascrive il filtro vuoto
 * e SimpleSearch svuota SearchResultActivity.
 */
class SearchIntentExtrasTest {

    @Test
    fun searchQuestionKey_isDistinctFromDashboardNeedle() {
        assertEquals(
            "dashboardSearchQuery",
            SearchConfiguration.EXTRA_DASHBOARD_SEARCH_QUERY
        )
        assertEquals(
            "advancedSearchQuestion",
            SearchConfiguration.EXTRA_SEARCH_QUESTION
        )
        assertNotEquals(
            SearchConfiguration.EXTRA_DASHBOARD_SEARCH_QUERY,
            SearchConfiguration.EXTRA_SEARCH_QUESTION
        )
    }

    @Test
    fun objectsIntent_emptyNeedleSurvivesQuestionExtra() {
        // Simula putExtra ordine di GlobalSearchActivity OBJECTS.
        val extras =
            linkedMapOf<String, String>()

        extras[
            SearchConfiguration.EXTRA_DASHBOARD_SEARCH_QUERY
        ] = ""

        extras[
            SearchConfiguration.EXTRA_SEARCH_QUESTION
        ] = "Elenco degli oggetti in archivio"

        assertEquals(
            "",
            extras[
                SearchConfiguration.EXTRA_DASHBOARD_SEARCH_QUERY
            ]
        )
        assertEquals(
            "Elenco degli oggetti in archivio",
            extras[
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            ]
        )
    }
}
