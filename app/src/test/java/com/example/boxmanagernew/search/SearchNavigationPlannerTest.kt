package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchEngineA
import com.example.boxmanagernew.domain.search.SearchNavigationPlanner
import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchNavigationPlannerTest {

    private val planner =
        SearchNavigationPlanner()

    private val index =
        SearchArchiveIndex(
            locations = listOf("Cantina", "Mansarda"),
            categories = listOf(
                "Ferramenta",
                "Generico",
                "Miscellanea"
            ),
            objects = listOf("Vite"),
            boxes = listOf("Cassetta 1")
        )

    @Test
    fun locationQuestionOpensContainerList() {

        val plan =
            planner.plan(
                "Fammi vedere tutto quello che ho in cantina",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Cantina", plan.locationTerms)
    }

    @Test
    fun twoPlacesOpenContainersInEitherLocation() {

        val plan =
            planner.plan(
                "Fammi vedere cosa ho in cantina e in mansarda",
                index
            )

        val names =
            SearchConfiguration.splitLocationTerms(
                plan.locationTerms
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals(
            setOf("Cantina", "Mansarda"),
            names.toSet()
        )
    }

    @Test
    fun placesWithoutANameStayUnresolved() {

        val plan =
            planner.plan(
                "Fammi vedere i luoghi",
                index
            )

        assertFalse(plan.resolved)
    }

    @Test
    fun localeBeforePlaceOpensSameContainersAsPlace() {

        val plan =
            planner.plan(
                "Fammi vedere cosa ho nel locale cantina",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Cantina", plan.locationTerms)
    }

    @Test
    fun sitoBeforePlaceOpensSameContainersAsPlace() {

        val plan =
            planner.plan(
                "Cosa ho nel sito mansarda",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Mansarda", plan.locationTerms)
    }

    @Test
    fun zonaBeforePlaceOpensSameContainersAsPlace() {

        val plan =
            planner.plan(
                "Fammi vedere cosa ho nella zona cantina",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Cantina", plan.locationTerms)
    }

    @Test
    fun postoWithoutAPlaceNameStaysOnObjectPath() {

        val plan =
            planner.plan(
                "In quale posto custodisco le viti?",
                index
            )

        assertFalse(plan.resolved)
    }

    @Test
    fun categoryWordWithoutNameOpensLocationContainers() {

        val plan =
            planner.plan(
                "Fammi vedere le categorie dei contenitori che ho in cantina",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Cantina", plan.locationTerms)
        assertEquals("", plan.categoryTerms)
    }

    @Test
    fun namedCategoryOpensContainersOfThatCategory() {

        val plan =
            planner.plan(
                "Quali sono i contenitori della categoria Generico?",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Generico", plan.categoryTerms)
    }

    @Test
    fun objectsOfCategoryOpenSameContainers() {

        val fromObjects =
            planner.plan(
                "Fammi vedere tutti gli oggetti che fanno parte della categoria Generico",
                index
            )

        val fromContainers =
            planner.plan(
                "Quali sono i contenitori della categoria Generico?",
                index
            )

        assertTrue(fromObjects.resolved)
        assertEquals(SearchFulcrum.BOX, fromObjects.fulcrum)
        assertEquals(
            fromContainers.categoryTerms,
            fromObjects.categoryTerms
        )
    }

    @Test
    fun miscellaneaCategoryOpensItsContainers() {

        val plan =
            planner.plan(
                "Cerca i contenitori che fanno parte della categoria Miscellanea",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Miscellanea", plan.categoryTerms)
    }

    @Test
    fun objectQuestionStaysOnExistingPath() {

        val plan =
            planner.plan(
                "Dove si trovano le viti?",
                index
            )

        assertFalse(plan.resolved)
    }

    @Test
    fun containerObjectQuestionStaysOnExistingPath() {

        val plan =
            planner.plan(
                "fammi vedere quali contenitori contengono viti",
                index
            )

        assertFalse(plan.resolved)
    }

    @Test
    fun engineADropsListScopeWordsFromLocationQuestion() {

        val response =
            SearchEngineA().execute(
                SearchAnalysisResult(
                    originalQuery =
                        "Fammi vedere tutto quello che ho in cantina",
                    operationalQuery = null,
                    interpretation = null,
                    recognizedEntities = emptySet(),
                    dominantFulcrum = SearchFulcrum.BOX,
                    satisfiability = null,
                    classification = null,
                    patternId = null
                )
            )

        assertEquals(
            "cantina",
            response.operationalQuery
        )
    }
}
