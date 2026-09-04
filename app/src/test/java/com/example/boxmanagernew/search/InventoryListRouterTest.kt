package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.InventoryListRouter
import com.example.boxmanagernew.domain.search.InventoryListTarget
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import com.example.boxmanagernew.domain.search.model.SearchResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InventoryListRouterTest {

    @Test
    fun categoryInventory_opensCategories() {
        assertEquals(
            InventoryListTarget.CATEGORIES,
            InventoryListRouter.target(
                nav(SearchArchiveTransformation.CATEGORY_TO_BOX),
                "Elenco di tutte le categorie"
            )
        )
    }

    @Test
    fun locationInventory_opensLocations() {
        assertEquals(
            InventoryListTarget.LOCATIONS,
            InventoryListRouter.target(
                nav(SearchArchiveTransformation.LOCATION_TO_BOX),
                "Elenco di tutte le posizioni"
            )
        )
    }

    @Test
    fun objectInventory_opensObjects() {
        SearchLocaleContext.run(SearchLocale.IT) {
            assertEquals(
                InventoryListTarget.OBJECTS,
                InventoryListRouter.target(
                    nav(SearchArchiveTransformation.NONE),
                    "Elenco di tutti gli oggetti"
                )
            )
        }
    }

    @Test
    fun boxInventory_opensBoxes() {
        SearchLocaleContext.run(SearchLocale.IT) {
            assertEquals(
                InventoryListTarget.BOXES,
                InventoryListRouter.target(
                    nav(SearchArchiveTransformation.NONE),
                    "Elenco di tutti i contenitori"
                )
            )
        }
    }

    @Test
    fun emptyBoxes_opensEmptyFilter() {
        SearchLocaleContext.run(SearchLocale.IT) {
            assertEquals(
                InventoryListTarget.EMPTY_BOXES,
                InventoryListRouter.target(
                    nav(SearchArchiveTransformation.NONE),
                    "Fammi vedere i contenitori vuoti"
                )
            )
        }
    }

    @Test
    fun namedNavigation_staysOnContainers() {
        assertNull(
            InventoryListRouter.target(
                nav(
                    SearchArchiveTransformation.OBJECT_TO_BOX,
                    objectTerms = "Trapano"
                ),
                "Trova il trapano elettrico"
            )
        )
    }

    private fun nav(
        transformation: SearchArchiveTransformation,
        objectTerms: String = "",
        locationTerms: String = "",
        categoryTerms: String = "",
        boxTerms: String = ""
    ) =
        SearchResponse(
            success = true,
            message = "ENGINE_A_RESULT",
            requestType =
                SearchRequestType.ARCHIVE_NAVIGATION,
            archiveTransformation =
                transformation,
            objectTerms = objectTerms,
            locationTerms = locationTerms,
            categoryTerms = categoryTerms,
            boxTerms = boxTerms
        )
}
