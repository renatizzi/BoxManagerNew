package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.EmptyBoxesInventoryCue
import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.InventoryListRouter
import com.example.boxmanagernew.domain.search.InventoryListTarget
import com.example.boxmanagernew.domain.search.SearchF7Pattern
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import com.example.boxmanagernew.domain.search.model.SearchArchiveBoxRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import com.example.boxmanagernew.domain.search.model.SearchResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Verifica end-to-end: domanda → tipo pipeline → target UI.
 * Archivio lab allineato a CK2 (Cantina + contenitori/oggetti).
 */
class SearchInventoryAuditTest {

    private val dispatcher =
        GlobalSearchDispatcher()

    private val labIndex =
        SearchArchiveIndex(
            locations = listOf("Cantina", "Garage", "Soffitta"),
            categories = listOf(
                "Attrezzi, Strumenti e Ferramenta",
                "Alimenti e Bevande",
                "Miscellanea",
                "Foto e Video"
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
                    categoryName = "Attrezzi, Strumenti e Ferramenta",
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "box prova",
                    categoryName = "Attrezzi, Strumenti e Ferramenta",
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "Box 1",
                    categoryName = "Attrezzi, Strumenti e Ferramenta",
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "Cassetta 1",
                    categoryName = "Attrezzi, Strumenti e Ferramenta",
                    locationName = "Cantina"
                ),
                SearchArchiveBoxRecord(
                    name = "prova",
                    categoryName = "Alimenti e Bevande",
                    locationName = "Cantina"
                )
            ),
            objectRecords = listOf(
                SearchArchiveObjectRecord(
                    name = "Box",
                    boxName = "Cassetta 1",
                    boxCategory = "Attrezzi, Strumenti e Ferramenta",
                    boxLocation = "Cantina"
                ),
                SearchArchiveObjectRecord(
                    name = "Trapano elettrico",
                    boxName = "Box 1",
                    boxCategory = "Attrezzi, Strumenti e Ferramenta",
                    boxLocation = "Cantina"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "Cassetta 1",
                    boxCategory = "Attrezzi, Strumenti e Ferramenta",
                    boxLocation = "Cantina"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "prova",
                    boxCategory = "Alimenti e Bevande",
                    boxLocation = "Cantina"
                )
            )
        )

    private data class Case(
        val question: String,
        val expectedUi: String,
        val locale: SearchLocale = SearchLocale.IT
    )

    @Test
    fun auditRenatoFourPlusCk2() {
        val cases =
            listOf(
                // Renato — 4 domande ritest
                Case(
                    "Quali sono le categorie in uso?",
                    "CATEGORIES_USED_FILTER"
                ),
                Case(
                    "Elenco degli oggetti in archivio",
                    "SEARCH_RESULT_ALL_OBJECTS"
                ),
                Case(
                    "Elenca tutti gli oggetti in archivio",
                    "SEARCH_RESULT_ALL_OBJECTS"
                ),
                Case(
                    "Quali oggetti ho in cantina?",
                    "SEARCH_RESULT_OBJECTS_IN_LOCATION"
                ),
                Case(
                    "Quali sono i luoghi in uso?",
                    "LOCATIONS_USED_FILTER"
                ),
                // Inventario correlato
                Case(
                    "Elenco di tutte le posizioni",
                    "LOCATIONS_USED_FILTER"
                ),
                Case(
                    "Elenco delle categorie usate",
                    "CATEGORIES_USED_FILTER"
                ),
                Case(
                    "Fammi vedere i contenitori vuoti",
                    "MAIN_EMPTY_CONTAINERS"
                ),
                Case(
                    "Elenco dei contenitori che sono in cantina",
                    "MAIN_FILTERED_LOCATION"
                ),
                // Motore B / F7
                Case(
                    "Dove trovo lo stesso tipo di oggetti",
                    "MAIN_CONTAINERS_F7_LIST"
                ),
                // CK2 EN (locale EN)
                Case(
                    "Find box",
                    "CARD_CLARIFY",
                    SearchLocale.EN
                ),
                Case(
                    "Find container box",
                    "MAIN_FILTERED_BOX",
                    SearchLocale.EN
                ),
                Case(
                    "Where is the trapano elettrico?",
                    "MAIN_FILTERED_OBJECT",
                    SearchLocale.EN
                ),
                Case(
                    "Where do I find the same type of objects",
                    "MAIN_CONTAINERS_F7_LIST",
                    SearchLocale.EN
                ),
                Case(
                    "Search the containers with a different category that contain the same type of object",
                    "CARD_MOTORE_B_MSG_PRINT",
                    SearchLocale.EN
                ),
                Case(
                    "In order to find the trapano elettrico",
                    "MAIN_FILTERED_OBJECT",
                    SearchLocale.EN
                ),
                Case(
                    "Search all the containers that contain duplicates",
                    "MAIN_CONTAINERS_F7_LIST",
                    SearchLocale.EN
                ),
                Case(
                    "Find containers with a different category and identical objects",
                    "CARD_MOTORE_B_MSG_PRINT",
                    SearchLocale.EN
                ),
                Case(
                    "List of the containers that have identical objects",
                    "MAIN_CONTAINERS_F7_LIST",
                    SearchLocale.EN
                ),
                Case(
                    "Find the trapano elettrico",
                    "MAIN_FILTERED_OBJECT",
                    SearchLocale.EN
                )
            )

        val out = StringBuilder()
        val failures = mutableListOf<String>()

        cases.forEach { c ->
            val r =
                dispatcher.dispatch(
                    c.question,
                    labIndex,
                    c.locale
                )
            val inv =
                if (
                    r.requestType ==
                    SearchRequestType.ARCHIVE_NAVIGATION
                ) {
                    SearchLocaleContext.run(c.locale) {
                        InventoryListRouter.target(r, c.question)
                    }
                } else {
                    null
                }
            val ui =
                SearchLocaleContext.run(c.locale) {
                    describeUi(r.requestType, inv, r)
                }
            out.appendLine("==== [${c.locale}] ${c.question}")
            out.appendLine(
                "  type=${r.requestType} success=${r.success} " +
                    "clarify=${r.requiresClarification}"
            )
            out.appendLine(
                "  transform=${r.archiveTransformation} " +
                    "fulcrum=${r.dominantFulcrum} pattern=${r.matchedPatternId}"
            )
            out.appendLine(
                "  obj=[${r.objectTerms}] box=[${r.boxTerms}] " +
                    "loc=[${r.locationTerms}] cat=[${r.categoryTerms}]"
            )
            out.appendLine(
                "  resultBoxes=${r.resultBoxNames} " +
                    "resultObjs=${r.resultObjectNames}"
            )
            out.appendLine(
                "  inventoryTarget=$inv emptyCue=${EmptyBoxesInventoryCue.matches(c.question)}"
            )
            out.appendLine(
                "  UI=$ui expected=${c.expectedUi}"
            )
            if (ui != c.expectedUi) {
                failures.add(
                    "${c.question}: got=$ui expected=${c.expectedUi}"
                )
            }
        }

        File("/opt/cursor/artifacts/search_audit_renato.txt")
            .writeText(out.toString())
        File("/tmp/search_audit_renato.txt")
            .writeText(out.toString())

        assertTrue(
            "Audit failures:\n" + failures.joinToString("\n"),
            failures.isEmpty()
        )
        assertEquals(20, cases.size)
    }

    private fun describeUi(
        type: SearchRequestType?,
        inv: InventoryListTarget?,
        r: SearchResponse
    ): String {
        if (r.requiresClarification) {
            return "CARD_CLARIFY"
        }
        if (
            type == SearchRequestType.ARCHIVE_QUERY
        ) {
            return if (
                r.matchedPatternId == SearchF7Pattern.ID &&
                r.success &&
                r.resultBoxNames.isNotEmpty()
            ) {
                "MAIN_CONTAINERS_F7_LIST"
            } else if (r.success && r.resultBoxNames.isNotEmpty()) {
                "CARD_MOTORE_B_MSG_PRINT"
            } else {
                "CARD_MSG"
            }
        }
        if (
            type == SearchRequestType.ARCHIVE_NAVIGATION
        ) {
            return when (inv) {
                InventoryListTarget.CATEGORIES ->
                    "CATEGORIES_USED_FILTER"
                InventoryListTarget.LOCATIONS ->
                    "LOCATIONS_USED_FILTER"
                InventoryListTarget.OBJECTS ->
                    if (r.locationTerms.isNotBlank()) {
                        "SEARCH_RESULT_OBJECTS_IN_LOCATION"
                    } else {
                        "SEARCH_RESULT_ALL_OBJECTS"
                    }
                InventoryListTarget.BOXES ->
                    "MAIN_ALL_CONTAINERS"
                InventoryListTarget.EMPTY_BOXES ->
                    "MAIN_EMPTY_CONTAINERS"
                null ->
                    when {
                        r.objectTerms.isNotBlank() ->
                            "MAIN_FILTERED_OBJECT"
                        r.boxTerms.isNotBlank() ->
                            "MAIN_FILTERED_BOX"
                        r.locationTerms.isNotBlank() ->
                            "MAIN_FILTERED_LOCATION"
                        r.categoryTerms.isNotBlank() ->
                            "MAIN_FILTERED_CATEGORY"
                        else ->
                            "MAIN_FILTERED"
                    }
            }
        }
        return "CARD_FAIL_OR_UNKNOWN"
    }
}
