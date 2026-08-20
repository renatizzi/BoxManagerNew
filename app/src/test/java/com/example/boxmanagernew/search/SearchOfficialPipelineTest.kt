package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchOfficialPipelineTest {

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
            objects = listOf("Vite", "Box"),
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

    @Test
    fun trovaContenitoreBoxUsesSameNamesAsTrovaBox() {

        val fromBox =
            dispatcher.dispatch(
                "Trova box",
                index
            )

        val fromContainer =
            dispatcher.dispatch(
                "Trova contenitore box",
                index
            )

        val expected =
            setOf("box prova", "Box 1", "Box")

        assertTrue(fromBox.success)
        assertTrue(fromContainer.success)
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            fromContainer.requestType
        )
        assertEquals(
            SearchArchiveTransformation.NONE,
            fromContainer.archiveTransformation
        )
        assertEquals(
            expected,
            SearchConfiguration.splitLocationTerms(
                fromBox.boxTerms
            ).toSet()
        )
        assertEquals(
            expected,
            SearchConfiguration.splitLocationTerms(
                fromContainer.boxTerms
            ).toSet()
        )
        assertEquals("", fromContainer.categoryTerms)
        assertEquals("", fromContainer.objectTerms)
    }

    @Test
    fun trovaTuttiIContenitoriDiNomeBoxMatchesSameSix() {

        val response =
            dispatcher.dispatch(
                "Trova tutti i contenitori di nome box",
                index
            )

        assertTrue(response.success)
        assertEquals(
            setOf("box prova", "Box 1", "Box"),
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        )
    }

    @Test
    fun locationQuestionOpensContainers() {

        val response =
            dispatcher.dispatch(
                "Fammi vedere tutto quello che ho in cantina",
                index
            )

        assertTrue(response.success)
        assertEquals("Cantina", response.locationTerms)
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun namedCategoryOpensContainers() {

        val response =
            dispatcher.dispatch(
                "Quali sono i contenitori della categoria Generico?",
                index
            )

        assertTrue(response.success)
        assertEquals("Generico", response.categoryTerms)
        assertEquals(
            SearchArchiveTransformation.CATEGORY_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun trovaContenitoreProvaKeepsProvaFamily() {

        val response =
            dispatcher.dispatch(
                "Trova contenitore prova",
                index
            )

        assertTrue(response.success)
        assertEquals(
            setOf("prova", "box prova", "prova 1", "prova 2"),
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        )
    }

    @Test
    fun objectQuestionUsesObjectToBox() {

        val response =
            dispatcher.dispatch(
                "Dove si trovano le viti?",
                index
            )

        assertTrue(response.success)
        assertEquals("Vite", response.objectTerms)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
    }
}
