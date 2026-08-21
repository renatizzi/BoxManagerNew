package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.ObjectSearchMatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchEngineA
import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.util.CanonicalNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancedObjectMatchTest {

    @Test
    fun vitiMatchesSingularViteAndNotCacciaviti() {

        assertTrue(
            CanonicalNormalizer.wholeWordMatches(
                "viti",
                "vite"
            )
        )

        assertTrue(
            CanonicalNormalizer.wholeWordMatches(
                "vite",
                "viti"
            )
        )

        assertFalse(
            CanonicalNormalizer.wholeWordMatches(
                "viti",
                "cacciaviti"
            )
        )

        assertFalse(
            CanonicalNormalizer.wholeWordMatches(
                "viti",
                "cacciavite"
            )
        )
    }

    @Test
    fun allTokensMatchWordsAcceptsSingularVite() {

        assertTrue(
            CanonicalNormalizer.allTokensMatchWords(
                "viti",
                "vite"
            )
        )

        assertFalse(
            CanonicalNormalizer.allTokensMatchWords(
                "viti",
                "cacciaviti"
            )
        )
    }

    @Test
    fun objectSearchUsesDescriptionLikeSimpleSearch() {

        assertTrue(
            ObjectSearchMatcher.matches(
                "Utensile",
                "viti da legno",
                "viti"
            )
        )

        assertTrue(
            ObjectSearchMatcher.matches(
                "Trapano",
                "elettrico a batteria",
                "trapano elettrico"
            )
        )

        assertFalse(
            ObjectSearchMatcher.matches(
                "Cacciaviti",
                "a stella",
                "viti"
            )
        )
    }

    @Test
    fun matchingWordRangesHighlightsSingularVite() {

        val ranges =
            CanonicalNormalizer.matchingWordRanges(
                "vite",
                "viti"
            )

        assertEquals(
            1,
            ranges.size
        )

        assertEquals(
            0,
            ranges.first().first
        )

        assertTrue(
            CanonicalNormalizer.matchingWordRanges(
                "cacciaviti",
                "viti"
            ).isEmpty()
        )
    }

    @Test
    fun engineAKeepsObjectTermFromContainerQuestion() {

        val response =
            SearchEngineA().execute(
                analysis(
                    "fammi vedere quali contenitori contengono viti"
                )
            )

        assertEquals(
            "viti",
            response.operationalQuery
        )
    }

    @Test
    fun engineAKeepsObjectTermFromWhereQuestion() {

        val withArticle =
            SearchEngineA().execute(
                analysis(
                    "Dove si trovano le viti?"
                )
            )

        val withoutArticle =
            SearchEngineA().execute(
                analysis(
                    "Dove si trovano viti?"
                )
            )

        assertEquals(
            "viti",
            withArticle.operationalQuery
        )

        assertEquals(
            "viti",
            withoutArticle.operationalQuery
        )
    }

    @Test
    fun engineADropsBeingVerbAfterDove() {

        val withApostrophe =
            SearchEngineA().execute(
                analysis(
                    "Dov'è il trapano elettrico?"
                )
            )

        val withoutApostrophe =
            SearchEngineA().execute(
                analysis(
                    "Dove è il trapano elettrico?"
                )
            )

        val curlyApostrophe =
            SearchEngineA().execute(
                analysis(
                    "Dov’è il trapano elettrico?"
                )
            )

        assertEquals(
            "trapano elettrico",
            withApostrophe.operationalQuery
        )

        assertEquals(
            "trapano elettrico",
            withoutApostrophe.operationalQuery
        )

        assertEquals(
            "trapano elettrico",
            curlyApostrophe.operationalQuery
        )
    }

    @Test
    fun engineAKeepsObjectFromTrovaQuestion() {

        val response =
            SearchEngineA().execute(
                analysis(
                    "Trova il trapano elettrico?"
                )
            )

        assertEquals(
            "trapano elettrico",
            response.operationalQuery
        )
    }

    @Test
    fun engineADropsPlaceSynonymsAndKeepsObject() {

        val withPosto =
            SearchEngineA().execute(
                analysis(
                    "In quale posto custodisco le viti?"
                )
            )

        val whereQuestion =
            SearchEngineA().execute(
                analysis(
                    "Dove si trovano le viti?"
                )
            )

        assertEquals(
            "viti",
            withPosto.operationalQuery
        )

        assertEquals(
            whereQuestion.operationalQuery,
            withPosto.operationalQuery
        )

        val withZona =
            SearchEngineA().execute(
                analysis(
                    "In quale zona custodisco le viti?"
                )
            )

        assertEquals(
            "viti",
            withZona.operationalQuery
        )
    }

    @Test
    fun packedTermsMatchEitherObjectNotBothOnOneRow() {

        val packed =
            SearchConfiguration.packLocationTerms(
                listOf("Vite", "Trapano elettrico")
            )

        assertTrue(
            ObjectSearchMatcher.matchesAnyPackedTerm(
                "Vite",
                null,
                packed
            )
        )

        assertTrue(
            ObjectSearchMatcher.matchesAnyPackedTerm(
                "Trapano elettrico",
                "a batteria",
                packed
            )
        )

        assertFalse(
            ObjectSearchMatcher.matches(
                "Vite",
                null,
                SearchConfiguration.locationHighlightQuery(
                    packed
                )
            )
        )
    }

    private fun analysis(
        question: String
    ): SearchAnalysisResult {

        return SearchAnalysisResult(
            originalQuery = question,
            operationalQuery = null,
            interpretation = null,
            recognizedEntities = emptySet(),
            dominantFulcrum = SearchFulcrum.OBJECT,
            satisfiability = null,
            classification = null,
            patternId = null
        )
    }
}
