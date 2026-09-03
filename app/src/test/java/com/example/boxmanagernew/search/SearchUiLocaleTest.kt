package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchLanguageTablesEn
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

/**
 * M2c: messaggi 2.6 sul main thread seguono il locale UI, non solo
 * il ThreadLocal del dispatcher (che si pulisce a fine dispatch).
 */
class SearchUiLocaleTest {

    private val dispatcher =
        GlobalSearchDispatcher()

    private val index =
        SearchArchiveIndex(
            objects = listOf("Trapano elettrico", "Box"),
            boxes = listOf("Box", "Box 1")
        )

    @Before
    fun italianDisplay() {
        SearchLocaleContext.setDisplayLocale(
            SearchLocale.IT
        )
    }

    @After
    fun restoreItalianDisplay() {
        SearchLocaleContext.setDisplayLocale(
            SearchLocale.IT
        )
    }

    @Test
    fun defaultDisplay_keepsItalianMessages() {
        assertEquals(
            "Nessun risultato trovato.",
            SearchConfiguration.MSG_NO_RESULTS
        )
        assertEquals(
            "Non ho compreso la richiesta.",
            SearchConfiguration.MSG_NOT_UNDERSTOOD
        )
        assertEquals(
            "Questo tipo di richiesta non è ancora disponibile.",
            SearchConfiguration.MSG_INTERROGATION_UNAVAILABLE
        )
        assertFalse(
            SearchLocaleContext.displayIsEnglish()
        )
    }

    @Test
    fun displayEnglish_showsEnglishMessagesWithoutDispatcherThreadLocal() {
        SearchLocaleContext.setDisplayLocale(
            SearchLocale.EN
        )

        assertEquals(
            SearchLanguageTablesEn.MSG_NO_RESULTS,
            SearchConfiguration.MSG_NO_RESULTS
        )
        assertEquals(
            SearchLanguageTablesEn.MSG_NOT_UNDERSTOOD,
            SearchConfiguration.MSG_NOT_UNDERSTOOD
        )
        assertEquals(
            SearchLanguageTablesEn.MSG_CLARIFY,
            SearchConfiguration.MSG_CLARIFY
        )
        assertEquals(
            SearchLanguageTablesEn.MSG_INTERROGATION_UNAVAILABLE,
            SearchConfiguration.MSG_INTERROGATION_UNAVAILABLE
        )
    }

    @Test
    fun englishDispatch_doesNotLeaveThreadLocalEnglish() {
        dispatcher.dispatch(
            "Find box",
            index,
            SearchLocale.EN
        )

        assertEquals(
            "Nessun risultato trovato.",
            SearchConfiguration.MSG_NO_RESULTS
        )
    }

    @Test
    fun englishDispatch_thenDisplayEnglish_keepsEnglishEmptyState() {
        dispatcher.dispatch(
            "Find box",
            index,
            SearchLocale.EN
        )

        SearchLocaleContext.setDisplayLocale(
            SearchLocale.EN
        )

        assertEquals(
            SearchLanguageTablesEn.MSG_NO_RESULTS,
            SearchConfiguration.MSG_NO_RESULTS
        )
    }
}
