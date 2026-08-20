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
    fun namedBoxOpensThatContainer() {

        val plan =
            planner.plan(
                "Trova Cassetta 1",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("Cassetta 1", plan.boxTerms)
        assertEquals("", plan.locationTerms)
    }

    @Test
    fun trovaBoxProvaOpensProvaContainers() {

        val plan =
            planner.plan(
                "Trova box prova",
                index
            )

        val names =
            SearchConfiguration.splitLocationTerms(
                plan.boxTerms
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertTrue(names.contains("prova"))
        assertTrue(names.contains("box prova"))
        assertTrue(names.contains("prova 1"))
        assertTrue(names.contains("prova 2"))
    }

    @Test
    fun trovaContenitoreProvaMatchesProvaFamily() {

        val plan =
            planner.plan(
                "Trova contenitore prova",
                index
            )

        val names =
            SearchConfiguration.splitLocationTerms(
                plan.boxTerms
            )

        assertTrue(plan.resolved)
        assertEquals(
            setOf("prova", "box prova", "prova 1", "prova 2"),
            names.toSet()
        )
    }

    @Test
    fun provaUnoDoesNotMatchProvaDue() {

        val plan =
            planner.plan(
                "Trova contenitore prova 1",
                index
            )

        val names =
            SearchConfiguration.splitLocationTerms(
                plan.boxTerms
            )

        assertTrue(plan.resolved)
        assertTrue(names.contains("prova 1"))
        assertFalse(names.contains("prova 2"))
    }

    @Test
    fun boxAliasAloneDoesNotMatchProvaBoxes() {

        val plan =
            planner.plan(
                "Trova contenitore box",
                index
            )

        val names =
            SearchConfiguration.splitLocationTerms(
                plan.boxTerms
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.BOX, plan.fulcrum)
        assertEquals("", plan.categoryTerms)
        assertEquals(
            setOf("box prova", "Box 1", "Box"),
            names.toSet()
        )
    }

    @Test
    fun trovaBoxMatchesSameWholeWordNames() {

        val fromBox =
            planner.plan(
                "Trova box",
                index
            )

        val fromContainer =
            planner.plan(
                "Trova contenitore box",
                index
            )

        assertEquals(
            SearchConfiguration.splitLocationTerms(
                fromContainer.boxTerms
            ).toSet(),
            SearchConfiguration.splitLocationTerms(
                fromBox.boxTerms
            ).toSet()
        )
    }

    @Test
    fun alimentiMatchesAlimentiEBevande() {

        val plan =
            planner.plan(
                "Quali sono i contenitori della categoria Alimenti?",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(
            "Alimenti e Bevande",
            plan.categoryTerms
        )
    }

    @Test
    fun unknownBoxNameStaysUnresolved() {

        val plan =
            planner.plan(
                "Trova box inesistente",
                index
            )

        assertFalse(plan.resolved)
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

    @Test
    fun engineADropsBoxAliasAndDoesNotKeepBox() {

        val response =
            SearchEngineA().execute(
                SearchAnalysisResult(
                    originalQuery =
                        "Trova contenitore box",
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
            "",
            response.operationalQuery
        )
    }
}
