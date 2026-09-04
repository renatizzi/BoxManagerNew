package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.InventoryListRouter
import com.example.boxmanagernew.domain.search.InventoryListTarget
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import com.example.boxmanagernew.domain.search.model.SearchArchiveBoxRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * EN inventory: (1) router con SearchLocaleContext EN;
 * (2) function words EN devono lasciare riconoscere i luoghi nominati.
 */
class SearchEnInventoryAuditTest {

    private val dispatcher =
        GlobalSearchDispatcher()

    private val labIndex =
        SearchArchiveIndex(
            locations = listOf("Cellar", "Garage"),
            categories = listOf(
                "Tools, Instruments and Hardware",
                "Food and Beverages"
            ),
            objects = listOf(
                "Box",
                "Trapano elettrico",
                "Vite"
            ),
            boxes = listOf(
                "Box",
                "box prova",
                "Box 1",
                "Cassetta 1",
                "prova"
            ),
            boxRecords = listOf(
                SearchArchiveBoxRecord(
                    name = "Box",
                    categoryName = "Tools, Instruments and Hardware",
                    locationName = "Cellar"
                ),
                SearchArchiveBoxRecord(
                    name = "box prova",
                    categoryName = "Tools, Instruments and Hardware",
                    locationName = "Cellar"
                ),
                SearchArchiveBoxRecord(
                    name = "Box 1",
                    categoryName = "Tools, Instruments and Hardware",
                    locationName = "Cellar"
                ),
                SearchArchiveBoxRecord(
                    name = "Cassetta 1",
                    categoryName = "Tools, Instruments and Hardware",
                    locationName = "Cellar"
                ),
                SearchArchiveBoxRecord(
                    name = "prova",
                    categoryName = "Food and Beverages",
                    locationName = "Cellar"
                )
            ),
            objectRecords = listOf(
                SearchArchiveObjectRecord(
                    name = "Box",
                    boxName = "Cassetta 1",
                    boxCategory = "Tools, Instruments and Hardware",
                    boxLocation = "Cellar"
                ),
                SearchArchiveObjectRecord(
                    name = "Trapano elettrico",
                    boxName = "Box 1",
                    boxCategory = "Tools, Instruments and Hardware",
                    boxLocation = "Cellar"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "Cassetta 1",
                    boxCategory = "Tools, Instruments and Hardware",
                    boxLocation = "Cellar"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "prova",
                    boxCategory = "Food and Beverages",
                    boxLocation = "Cellar"
                )
            )
        )

    @Test
    fun auditEnDump() {
        val out = StringBuilder()
        listOf(
            "What are the categories in use?",
            "List of all the objects",
            "List of the objects in the archive",
            "What objects do I have in the cellar?",
            "What are the places in use?",
            "List of all the containers"
        ).forEach { q ->
            val r =
                dispatcher.dispatch(
                    q,
                    labIndex,
                    SearchLocale.EN
                )
            val inv =
                if (
                    r.requestType ==
                    SearchRequestType.ARCHIVE_NAVIGATION
                ) {
                    SearchLocaleContext.run(
                        SearchLocale.EN
                    ) {
                        InventoryListRouter.target(
                            r,
                            q
                        )
                    }
                } else {
                    null
                }
            out.appendLine("==== $q")
            out.appendLine(
                "  type=${r.requestType} transform=${r.archiveTransformation} " +
                    "loc=[${r.locationTerms}] inv=$inv"
            )
        }
        File("/opt/cursor/artifacts/search_audit_en.txt")
            .writeText(out.toString())
        assertTrue(out.isNotEmpty())
    }

    @Test
    fun enObjectsInventory_withoutLocale_opensBoxes_withLocale_opensObjects() {
        val q =
            "List of all the objects"
        val r =
            dispatcher.dispatch(
                q,
                labIndex,
                SearchLocale.EN
            )
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            r.requestType
        )
        assertEquals(
            InventoryListTarget.BOXES,
            InventoryListRouter.target(r, q)
        )
        assertEquals(
            InventoryListTarget.OBJECTS,
            SearchLocaleContext.run(
                SearchLocale.EN
            ) {
                InventoryListRouter.target(r, q)
            }
        )
    }

    @Test
    fun enObjectsInCellar_recognizesLocationAndOpensObjectsReport() {
        val q =
            "What objects do I have in the cellar?"
        val r =
            dispatcher.dispatch(
                q,
                labIndex,
                SearchLocale.EN
            )
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            r.requestType
        )
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            r.archiveTransformation
        )
        assertEquals(
            "Cellar",
            r.locationTerms
        )
        assertEquals(
            InventoryListTarget.OBJECTS,
            SearchLocaleContext.run(
                SearchLocale.EN
            ) {
                InventoryListRouter.target(r, q)
            }
        )
        // Senza contesto EN resterebbe BOXES / null sbagliato per oggetti.
        assertNotEquals(
            InventoryListTarget.OBJECTS,
            InventoryListRouter.target(r, q)
        )
    }

    @Test
    fun enCategoriesAndPlaces_typeOnly() {
        SearchLocaleContext.run(
            SearchLocale.EN
        ) {
            val catQ =
                "What are the categories in use?"
            val cat =
                dispatcher.dispatch(
                    catQ,
                    labIndex,
                    SearchLocale.EN
                )
            assertEquals(
                InventoryListTarget.CATEGORIES,
                InventoryListRouter.target(
                    cat,
                    catQ
                )
            )
            val placesQ =
                "What are the places in use?"
            val places =
                dispatcher.dispatch(
                    placesQ,
                    labIndex,
                    SearchLocale.EN
                )
            assertEquals(
                InventoryListTarget.LOCATIONS,
                InventoryListRouter.target(
                    places,
                    placesQ
                )
            )
        }
    }
}
