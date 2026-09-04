package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchLanguageTablesEn
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.model.SearchArchiveBoxRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * CK2 lab archive (Renato device): exactamente 5 contenitori + 4 oggetti.
 * Riproduce i KO EN Find box / Find container box / F7 / F8.
 */
class SearchCk2LabEnTest {

    private val dispatcher =
        GlobalSearchDispatcher()

    private val catAttrezzi =
        "Attrezzi, Strumenti e Ferramenta"

    private val catAlimenti =
        "Alimenti e Bevande"

    /** Archivio minimo CK2 — solo questi nomi. */
    private val labIndex =
        SearchArchiveIndex(
            locations = listOf("Cantina"),
            categories = listOf(
                catAttrezzi,
                catAlimenti
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
                    categoryName = catAttrezzi,
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "box prova",
                    categoryName = catAttrezzi,
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "Box 1",
                    categoryName = catAttrezzi,
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "Cassetta 1",
                    categoryName = catAttrezzi,
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "prova",
                    categoryName = catAlimenti,
                    locationName = "Cantina"
                )
            ),
            objectRecords = listOf(
                SearchArchiveObjectRecord(
                    name = "Box",
                    boxName = "Cassetta 1",
                    boxCategory = catAttrezzi,
                    boxLocation = "Cantina"
                ),
                SearchArchiveObjectRecord(
                    name = "Trapano elettrico",
                    boxName = "Box 1",
                    boxCategory = catAttrezzi,
                    boxLocation = "Cantina"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "Cassetta 1",
                    boxCategory = catAttrezzi,
                    boxLocation = "Cantina"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "prova",
                    boxCategory = catAlimenti,
                    boxLocation = "Cantina"
                )
            )
        )

    private fun dispatchEn(
        question: String
    ) =
        dispatcher.dispatch(
            question,
            labIndex,
            SearchLocale.EN
        )

    private fun dump(
        label: String,
        question: String
    ) {
        val response =
            dispatchEn(question)
        val boxSet =
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        println(
            "CK2|$label|success=${response.success}" +
                "|clarify=${response.requiresClarification}" +
                "|type=${response.requestType}" +
                "|transform=${response.archiveTransformation}" +
                "|boxTerms=$boxSet" +
                "|obj=${response.objectTerms}" +
                "|msg=${response.message.take(80)}" +
                "|resultBoxes=${response.resultBoxNames}" +
                "\n${response.debugMarker}"
        )
    }

    @Test
    fun ck2_01_findBox_asksR19Clarification() {
        dump("01", "Find box")
        val response =
            dispatchEn("Find box")
        assertFalse(
            "must not open inventory",
            response.success
        )
        assertTrue(
            "R19 clarification required",
            response.requiresClarification
        )
        assertTrue(
            response.message.contains(
                "object",
                ignoreCase = true
            )
        )
        assertTrue(
            response.message.contains(
                "container",
                ignoreCase = true
            )
        )
    }

    @Test
    fun ck2_02_findContainerBox_namedBoxesOnly() {
        dump("02", "Find container box")
        val response =
            dispatchEn("Find container box")
        val boxes =
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        assertTrue(response.success)
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            response.requestType
        )
        assertEquals(
            SearchArchiveTransformation.NONE,
            response.archiveTransformation
        )
        assertEquals(
            setOf("Box", "box prova", "Box 1"),
            boxes
        )
        assertFalse(
            "must not dump all 5",
            boxes.containsAll(
                setOf("Cassetta 1", "prova")
            )
        )
    }

    @Test
    fun ck2_03_whereIsTrapano_objectToBox() {
        dump("03", "Where is the trapano elettrico?")
        val response =
            dispatchEn(
                "Where is the trapano elettrico?"
            )
        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
    }

    @Test
    fun ck2_04_f7_sameTypeOfObjects() {
        val question =
            "Where do I find the same type of objects"
        dump("04", question)
        val response =
            dispatchEn(question)
        assertFalse(response.requiresClarification)
        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertTrue(response.success)
        assertTrue(
            response.message.startsWith(
                SearchLanguageTablesEn.F7_HEADING
            )
        )
        assertTrue(
            response.resultBoxNames.containsAll(
                listOf("Cassetta 1", "prova")
            )
        )
    }

    @Test
    fun ck2_05_f8_differentCategorySameType() {
        val question =
            "Search the containers with a different category that contain the same type of object"
        dump("05", question)
        val response =
            dispatchEn(question)
        assertFalse(response.requiresClarification)
        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertTrue(response.success)
        assertTrue(
            response.message.startsWith(
                SearchLanguageTablesEn.F8_HEADING
            )
        )
        assertTrue(
            response.message.contains("Vite")
        )
        assertTrue(
            response.resultBoxNames.containsAll(
                listOf("Cassetta 1", "prova")
            )
        )
        assertEquals(
            5,
            labIndex.boxes.size
        )
        assertFalse(
            "must not open all 5 as Engine A inventory",
            response.requestType ==
                SearchRequestType.ARCHIVE_NAVIGATION
        )
    }

    @Test
    fun ck2_06_inOrderToFindTrapano_ok() {
        dump(
            "06",
            "In order to find the trapano elettrico"
        )
        val response =
            dispatchEn(
                "In order to find the trapano elettrico"
            )
        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun ck2_07_f7_containersWithDuplicates() {
        val question =
            "Search all the containers that contain duplicates"
        dump("07", question)
        val response =
            dispatchEn(question)
        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertTrue(response.success)
        assertTrue(
            response.message.startsWith(
                SearchLanguageTablesEn.F7_HEADING
            )
        )
        assertTrue(
            response.resultBoxNames.containsAll(
                listOf("Cassetta 1", "prova")
            )
        )
    }

    @Test
    fun ck2_08_f8_differentCategoryIdentical() {
        val question =
            "Find containers with a different category and identical objects"
        dump("08", question)
        val response =
            dispatchEn(question)
        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertTrue(response.success)
        assertTrue(
            response.message.startsWith(
                SearchLanguageTablesEn.F8_HEADING
            )
        )
    }

    @Test
    fun ck2_09_f7_listIdenticalObjects() {
        val question =
            "List of the containers that have identical objects"
        dump("09", question)
        val response =
            dispatchEn(question)
        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertTrue(response.success)
        assertTrue(
            response.message.startsWith(
                SearchLanguageTablesEn.F7_HEADING
            )
        )
    }

    @Test
    fun ck2_10_findTrapano_ok() {
        dump("10", "Find the trapano elettrico")
        val response =
            dispatchEn(
                "Find the trapano elettrico"
            )
        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
    }
}
