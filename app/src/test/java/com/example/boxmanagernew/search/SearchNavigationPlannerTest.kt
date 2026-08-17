package com.example.boxmanagernew.search

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
            locations = listOf("Cantina"),
            categories = listOf("Ferramenta"),
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
    fun categoryQuestionOpensCategoriesOfLocation() {

        val plan =
            planner.plan(
                "Fammi vedere le categorie dei contenitori che ho in cantina",
                index
            )

        assertTrue(plan.resolved)
        assertEquals(SearchFulcrum.CATEGORY, plan.fulcrum)
        assertEquals("Cantina", plan.locationTerms)
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
