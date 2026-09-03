package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchF7Pattern
import com.example.boxmanagernew.domain.search.SearchF8Pattern
import com.example.boxmanagernew.domain.search.SearchLanguageTablesEn
import com.example.boxmanagernew.domain.search.SearchLexicalIndicatorMatrix
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import com.example.boxmanagernew.domain.search.SearchNormalizer
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
 * Campione EN 0–10 (S1–S3). Stesso archivio di [SearchOfficialPipelineTest].
 * Nomi d'archivio non tradotti.
 */
class SearchOfficialPipelineEnTest {

    private val dispatcher =
        GlobalSearchDispatcher()

    private val index =
        SearchArchiveIndex(
            locations = listOf("Cantina", "Mansarda"),
            categories = listOf(
                "Ferramenta",
                "Generico",
                "Miscellanea",
                "Alimenti e Bevande",
                "Contenitori"
            ),
            objects = listOf("Vite", "Trapano elettrico", "Box"),
            boxes = listOf(
                "Cassetta 1",
                "prova",
                "box prova",
                "prova 1",
                "prova 2",
                "Box 1",
                "Box1",
                "Box",
                "Box1 - Cartone",
                "Box9 - Plastica",
                "BOX_VUOTO",
                "UtenzeBox",
                "NastroBox"
            )
        )

    private fun dispatchEn(
        question: String,
        archive: SearchArchiveIndex = index
    ) =
        dispatcher.dispatch(
            question,
            archive,
            SearchLocale.EN
        )

    @Test
    fun findBoxAsksClarificationWhenObjectAndContainerShareKey() {

        val response =
            dispatchEn(
                "Find box"
            )

        assertFalse(response.success)
        assertTrue(response.requiresClarification)
        assertEquals(
            "Rephrase the question so it is clear whether you mean an object or a container.",
            response.message
        )
    }

    @Test
    fun findContainerBoxUsesNamedContainers() {

        val response =
            dispatchEn(
                "Find container box"
            )

        val expected =
            setOf("box prova", "Box 1", "Box")

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
            expected,
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        )
        assertEquals("", response.categoryTerms)
        assertEquals("", response.objectTerms)
        assertEquals(
            "box",
            response.highlightTerms
        )
    }

    @Test
    fun whereIsTrapanoElettricoOpensObjectToBox() {

        val response =
            dispatchEn(
                "Where is the trapano elettrico?"
            )

        assertTrue(
            response.debugMarker.orEmpty(),
            response.success
        )
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            response.requestType
        )
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
        assertFalse(response.requiresClarification)
    }

    @Test
    fun inOrderToFindTrapanoDoesNotTreatOrderAsCategory() {

        val normalized =
            SearchLocaleContext.run(
                SearchLocale.EN
            ) {
                SearchNormalizer().normalize(
                    "In order to find the trapano elettrico"
                )
            }

        assertEquals(
            "trapano elettrico",
            normalized.normalizedQuestion
        )

        val indicators =
            SearchLocaleContext.run(
                SearchLocale.EN
            ) {
                SearchLexicalIndicatorMatrix()
                    .findIndicators(
                        normalized.normalizedQuestion
                    )
            }

        assertTrue(
            indicators[
                SearchLexicalIndicatorMatrix.CATEGORY
            ].orEmpty().isEmpty()
        )

        val response =
            dispatchEn(
                "In order to find the trapano elettrico"
            )

        assertTrue(response.success)
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            response.requestType
        )
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
    fun f7OfficialWhereVariant_doesNotAddLocationFromWhereClue() {

        val response =
            dispatchEn(
                SearchLanguageTablesEn.f7Variants[3],
                duplicateArchive()
            )

        val entitiesLine =
            response.debugMarker.orEmpty()
                .lineSequence()
                .first {
                    it.startsWith(
                        "[M3] ENTITIES="
                    )
                }

        assertFalse(
            entitiesLine,
            entitiesLine.contains(
                "LOCATION"
            )
        )
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
    fun f7OfficialVariants_routeToEngineB() {

        val archive =
            duplicateArchive()

        SearchLocaleContext.run(
            SearchLocale.EN
        ) {
            SearchF7Pattern.VARIANTS.forEach { question ->

                val response =
                    dispatchEn(
                        question,
                        archive
                    )

                assertFalse(
                    question,
                    response.requiresClarification
                )
                assertEquals(
                    question,
                    SearchRequestType.ARCHIVE_QUERY,
                    response.requestType
                )
                assertTrue(
                    question,
                    response.success
                )
                assertTrue(
                    question,
                    response.message.startsWith(
                        SearchF7Pattern.HEADING
                    )
                )
                assertEquals(
                    question,
                    SearchF7Pattern.ID,
                    response.debugMarker
                        .orEmpty()
                        .lineSequence()
                        .first {
                            it.startsWith(
                                "[PATTERN]"
                            )
                        }
                        .substringAfter(
                            "[PATTERN] "
                        )
                )
            }
        }
    }

    @Test
    fun f8OfficialFirstVariant_sameObjectOnBoxCategory() {

        val response =
            dispatchEn(
                SearchLanguageTablesEn.f8Variants[0],
                crossCategoryArchive()
            )

        assertFalse(response.requiresClarification)
        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_CATEGORY,
            response.archiveTransformation
        )
        assertTrue(response.success)
        assertTrue(
            response.message.startsWith(
                SearchLanguageTablesEn.F8_HEADING
            )
        )
        assertTrue(
            response.message.contains(
                "Vite: Cassetta 1, prova"
            )
        )
    }

    @Test
    fun englishF8VariantsDoNotUseItalianIndexSix() {

        SearchLocaleContext.run(
            SearchLocale.EN
        ) {
            assertEquals(
                4,
                SearchF8Pattern.VARIANTS.size
            )
            assertEquals(
                SearchLanguageTablesEn.F8_HEADING,
                SearchF8Pattern.HEADING
            )
        }
    }

    private fun duplicateArchive() =
        index.copy(
            objectRecords = listOf(
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "Cassetta 1"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "prova"
                ),
                SearchArchiveObjectRecord(
                    name = "Trapano elettrico",
                    boxName = "Box 1"
                )
            )
        )

    private fun crossCategoryArchive() =
        index.copy(
            boxRecords = listOf(
                SearchArchiveBoxRecord(
                    name = "Cassetta 1",
                    categoryName = "Ferramenta"
                ),
                SearchArchiveBoxRecord(
                    name = "prova",
                    categoryName = "Alimenti e Bevande"
                ),
                SearchArchiveBoxRecord(
                    name = "Box 1",
                    categoryName = "Ferramenta"
                )
            ),
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
                ),
                SearchArchiveObjectRecord(
                    name = "Trapano elettrico",
                    boxName = "Box 1",
                    boxCategory = "Ferramenta"
                )
            )
        )
}
