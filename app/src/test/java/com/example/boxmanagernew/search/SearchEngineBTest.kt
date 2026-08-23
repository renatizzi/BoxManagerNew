package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchEngineB
import com.example.boxmanagernew.domain.search.SearchF7Pattern
import com.example.boxmanagernew.domain.search.SearchF8Pattern
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveQuery
import com.example.boxmanagernew.domain.search.model.SearchArchiveQueryOperation
import com.example.boxmanagernew.domain.search.model.CoreEntityType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchEngineBTest {

    private val engine =
        SearchEngineB()

    @Test
    fun singleRecord_isNotDuplicate() {

        val response =
            engine.execute(
                SearchEngineB.f7Query(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "Cassetta 1"
                        )
                    )
                )
            )

        assertFalse(response.success)
        assertEquals(
            SearchConfiguration.MSG_NO_RESULTS,
            response.message
        )
    }

    @Test
    fun accentAndApostrophe_countAsSameName() {

        val response =
            engine.execute(
                SearchEngineB.f7Query(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Caffè",
                            boxName = "Prova 2"
                        ),
                        SearchArchiveObjectRecord(
                            name = "CAFFE'",
                            boxName = "prova"
                        )
                    )
                )
            )

        assertTrue(response.success)
        assertTrue(response.message.contains("Prova 2"))
        assertTrue(response.message.contains("prova"))
    }

    @Test
    fun singularAndPlural_countAsSameName() {

        val response =
            engine.execute(
                SearchEngineB.f7Query(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "Cassetta 1"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Viti",
                            boxName = "prova"
                        )
                    )
                )
            )

        assertTrue(response.success)
        assertTrue(response.message.contains("Cassetta 1"))
        assertTrue(response.message.contains("prova"))
    }

    @Test
    fun multiWord_doesNotInflectTokens() {

        val response =
            engine.execute(
                SearchEngineB.f7Query(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Trapano elettrico",
                            boxName = "Box 1"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Trapani elettrici",
                            boxName = "Box 2"
                        )
                    )
                )
            )

        assertFalse(response.success)
        assertEquals(
            SearchConfiguration.MSG_NO_RESULTS,
            response.message
        )
    }

    @Test
    fun otherQuery_staysUnavailable() {

        val response =
            engine.execute(
                SearchArchiveQuery(
                    operation =
                        SearchArchiveQueryOperation.COUNT,
                    targetEntities =
                        setOf(
                            CoreEntityType.BOX
                        )
                ),
                SearchArchiveIndex()
            )

        assertFalse(response.success)
        assertEquals(
            SearchConfiguration.MSG_INTERROGATION_UNAVAILABLE,
            response.message
        )
    }

    @Test
    fun heading_isOfficialF7Variant() {

        assertEquals(
            "Elenco dei contenitori che hanno oggetti uguali",
            SearchF7Pattern.VARIANTS[2]
        )
    }

    @Test
    fun f8DifferentCategories_listsBothBoxes() {

        val response =
            engine.execute(
                SearchEngineB.f8Query(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "Cassetta 1",
                            boxCategory = "Ferramenta"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "prova",
                            boxCategory = "Alimenti e Bevande"
                        )
                    )
                )
            )

        assertTrue(response.success)
        assertTrue(
            response.message.startsWith(
                "Elenco dei contenitori che hanno categoria diversa e contengono oggetti uguali"
            )
        )
        assertTrue(response.message.contains("Cassetta 1"))
        assertTrue(response.message.contains("prova"))
    }

    @Test
    fun objectLocationDifferentPlaces_listsBothBoxes() {

        val response =
            engine.execute(
                SearchEngineB.objectLocationQuery(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "Cassetta 1",
                            boxLocation = "Cantina"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "prova",
                            boxLocation = "Mansarda"
                        )
                    )
                )
            )

        assertTrue(response.success)
        assertTrue(response.message.contains("Vite: Cassetta 1, prova"))
        assertFalse(
            response.message.startsWith(
                "Elenco dei contenitori che hanno oggetti uguali"
            )
        )
    }

    @Test
    fun objectLocationSamePlace_isNotAHit() {

        val response =
            engine.execute(
                SearchEngineB.objectLocationQuery(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "Cassetta 1",
                            boxLocation = "Cantina"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "prova",
                            boxLocation = "Cantina"
                        )
                    )
                )
            )

        assertFalse(response.success)
        assertEquals(
            SearchConfiguration.MSG_NO_RESULTS,
            response.message
        )
    }

    @Test
    fun f8OnlyCrossCategoryPair_ignoresOtherSameCategoryDuplicates() {

        val response =
            engine.execute(
                SearchEngineB.f8Query(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Box",
                            boxName = "BOX",
                            boxCategory = "Ferramenta"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Box",
                            boxName = "prova 4",
                            boxCategory = "Alimenti e Bevande"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "Cassetta 1",
                            boxCategory = "Ferramenta"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Viti",
                            boxName = "prova",
                            boxCategory = "Ferramenta"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Trapano elettrico",
                            boxName = "Box 1",
                            boxCategory = "Miscellanea"
                        )
                    )
                )
            )

        assertTrue(response.success)
        val listed =
            response.message
                .lineSequence()
                .drop(1)
                .filter { it.isNotBlank() }
                .toList()
        assertEquals(
            listOf("Box: BOX, prova 4"),
            listed
        )
    }

    @Test
    fun f8SameCategory_isNotAHit() {

        val response =
            engine.execute(
                SearchEngineB.f8Query(),
                SearchArchiveIndex(
                    objectRecords = listOf(
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "Cassetta 1",
                            boxCategory = "Ferramenta"
                        ),
                        SearchArchiveObjectRecord(
                            name = "Vite",
                            boxName = "prova",
                            boxCategory = "Ferramenta"
                        )
                    )
                )
            )

        assertFalse(response.success)
        assertEquals(
            SearchConfiguration.MSG_NO_RESULTS,
            response.message
        )
    }

    @Test
    fun heading_isOfficialF8Variant() {

        assertEquals(
            "Elenco dei contenitori che hanno categoria diversa e contengono oggetti uguali",
            SearchF8Pattern.VARIANTS[6]
        )
    }
}
