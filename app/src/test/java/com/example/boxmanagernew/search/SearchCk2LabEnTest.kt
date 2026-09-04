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

    /**
     * Characterization: EN questions under IT locale reproduce CK2 device KOs
     * (empty boxTerms → UI INVENTORY_BOX = all 5; F7 where → no results).
     * Lab + SearchLocale.EN passes ck2_01..10; this isolates the locale mismatch.
     */
    @Test
    fun ck2_repro_enQuestionsUnderItLocale_matchDeviceKos() {
        val findContainer =
            dispatcher.dispatch(
                "Find container box",
                labIndex,
                SearchLocale.IT
            )
        println(
            "CK2|REPRO_IT|Find container box|" +
                "success=${findContainer.success}|" +
                "boxTermsBlank=${findContainer.boxTerms.isBlank()}|" +
                "type=${findContainer.requestType}|" +
                "clarify=${findContainer.requiresClarification}\n" +
                findContainer.debugMarker
        )
        assertTrue(findContainer.success)
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            findContainer.requestType
        )
        assertTrue(
            "device: empty boxTerms → all 5 inventory",
            findContainer.boxTerms.isBlank()
        )

        val f7Dup =
            dispatcher.dispatch(
                "Search all the containers that contain duplicates",
                labIndex,
                SearchLocale.IT
            )
        println(
            "CK2|REPRO_IT|F7 duplicates|type=${f7Dup.requestType}|" +
                "boxTermsBlank=${f7Dup.boxTerms.isBlank()}|" +
                "pattern line=${f7Dup.debugMarker?.lineSequence()?.firstOrNull { it.startsWith("[PATTERN]") }}\n" +
                f7Dup.debugMarker
        )
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            f7Dup.requestType
        )
        assertTrue(f7Dup.boxTerms.isBlank())

        val f7Where =
            dispatcher.dispatch(
                "Where do I find the same type of objects",
                labIndex,
                SearchLocale.IT
            )
        println(
            "CK2|REPRO_IT|F7 where|success=${f7Where.success}|" +
                "type=${f7Where.requestType}|msg=${f7Where.message}\n" +
                f7Where.debugMarker
        )
        assertFalse(f7Where.success)
        assertTrue(
            f7Where.message.contains(
                "Nessun risultato",
                ignoreCase = true
            ) ||
                f7Where.message.contains(
                    "No results",
                    ignoreCase = true
                )
        )

        val f8 =
            dispatcher.dispatch(
                "Search the containers with a different category that contain the same type of object",
                labIndex,
                SearchLocale.IT
            )
        println(
            "CK2|REPRO_IT|F8|type=${f8.requestType}|" +
                "boxTermsBlank=${f8.boxTerms.isBlank()}\n" +
                f8.debugMarker
        )
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            f8.requestType
        )
        assertTrue(f8.boxTerms.isBlank())

        // Find box without object Box + IT → inventory (no R19), matches device #1
        val noObjectBox =
            labIndex.copy(
                objects = listOf(
                    "Trapano elettrico",
                    "Vite"
                ),
                objectRecords =
                    labIndex.objectRecords.filterNot {
                        it.name.equals("Box", ignoreCase = true)
                    }
            )
        val findBoxIt =
            dispatcher.dispatch(
                "Find box",
                noObjectBox,
                SearchLocale.IT
            )
        println(
            "CK2|REPRO_IT|Find box noObjBox|" +
                "clarify=${findBoxIt.requiresClarification}|" +
                "boxTermsBlank=${findBoxIt.boxTerms.isBlank()}|" +
                "success=${findBoxIt.success}\n" +
                findBoxIt.debugMarker
        )
        assertFalse(findBoxIt.requiresClarification)
        assertTrue(findBoxIt.success)
        assertTrue(findBoxIt.boxTerms.isBlank())
    }
}

