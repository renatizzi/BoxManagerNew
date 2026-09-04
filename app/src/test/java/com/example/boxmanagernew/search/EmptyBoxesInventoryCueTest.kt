package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.EmptyBoxesInventoryCue
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmptyBoxesInventoryCueTest {

    @Test
    fun italianEmptyContainers_matches() {
        SearchLocaleContext.run(SearchLocale.IT) {
            assertTrue(
                EmptyBoxesInventoryCue.matches(
                    "Fammi vedere i contenitori vuoti"
                )
            )
            assertTrue(
                EmptyBoxesInventoryCue.matches(
                    "Trova i box vuoti"
                )
            )
        }
    }

    @Test
    fun inventoryWithoutEmptyCue_doesNotMatch() {
        SearchLocaleContext.run(SearchLocale.IT) {
            assertFalse(
                EmptyBoxesInventoryCue.matches(
                    "Elenco di tutti i contenitori"
                )
            )
            assertFalse(
                EmptyBoxesInventoryCue.matches(
                    "Elenco di tutte le posizioni"
                )
            )
        }
    }

    @Test
    fun englishEmptyContainers_matches() {
        SearchLocaleContext.run(SearchLocale.EN) {
            assertTrue(
                EmptyBoxesInventoryCue.matches(
                    "Show me the empty containers"
                )
            )
        }
    }
}
