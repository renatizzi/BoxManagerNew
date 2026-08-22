package com.example.boxmanagernew.search

import com.example.boxmanagernew.util.CanonicalNormalizer
import com.example.boxmanagernew.util.SimpleSearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimpleSearchTest {

    @Test
    fun needleRequiresThreeNormalizedChars() {

        assertEquals("", SimpleSearch.needle("ge"))
        assertEquals("", SimpleSearch.needle("  ab  "))
        assertEquals("gen", SimpleSearch.needle("Gen"))
        assertEquals("tra", SimpleSearch.needle("TRA"))
    }

    @Test
    fun normalizeMatchesCanonicalNormalizer() {

        val samples =
            listOf(
                "Generico",
                "  Box Prova  ",
                "TRAPANI",
                "cacciaviti",
                "perché",
                "Box-Prova",
                "!!!Hello!!!",
                "prova 1"
            )

        samples.forEach { sample ->

            assertEquals(
                CanonicalNormalizer.normalize(sample),
                SimpleSearch.normalize(sample)
            )
        }
    }

    @Test
    fun inlinePaintsPrefixInsideArchiveWord() {

        assertTrue(
            SimpleSearch.matches("Generico", "gen")
        )

        val painted =
            painted("Generico", "gen")

        assertEquals(listOf("Gen"), painted)

        assertTrue(
            SimpleSearch.matches("trapano", "tra")
        )

        assertEquals(
            listOf("tra"),
            painted("trapano", "tra")
        )
    }

    @Test
    fun vitiPaintsInsideCacciavitiAndNotVite() {

        assertTrue(
            SimpleSearch.matches("cacciaviti", "viti")
        )

        assertEquals(
            listOf("viti"),
            painted("cacciaviti", "viti")
        )

        assertFalse(
            SimpleSearch.matches("vite", "viti")
        )

        assertTrue(
            SimpleSearch.highlightRanges("vite", "viti").isEmpty()
        )
    }

    @Test
    fun twoCharsDoNotFilterOrPaint() {

        assertFalse(
            SimpleSearch.matches("Generico", "ge")
        )

        assertTrue(
            SimpleSearch.highlightRanges("Generico", "ge").isEmpty()
        )
    }

    @Test
    fun paintsEveryOccurrence() {

        assertEquals(
            listOf("viti", "viti"),
            painted("viti e altre viti", "viti")
        )
    }

    @Test
    fun multiWordNeedleKeepsSpaces() {

        assertTrue(
            SimpleSearch.matches("Box Prova", "box pro")
        )

        assertEquals(
            listOf("Box Pro"),
            painted("Box Prova", "box pro")
        )
    }

    @Test
    fun accentInsensitive() {

        assertTrue(
            SimpleSearch.matches("perché", "perche")
        )

        assertEquals(
            listOf("perché"),
            painted("perché", "perche")
        )
    }

    @Test
    fun specialCharactersDoNotEnterTheNeedle() {

        assertEquals(
            "viti",
            SimpleSearch.needle("viti!")
        )

        assertTrue(
            SimpleSearch.matches("cacciaviti", "viti!")
        )

        assertTrue(
            SimpleSearch.matches("box-prova", "box prova")
        )
    }

    @Test
    fun isolatedDigitQueryDoesNotActivate() {

        assertEquals("", SimpleSearch.needle("1"))
        assertFalse(
            SimpleSearch.matches("prova 1", "1")
        )
    }

    private fun painted(
        text: String,
        query: String
    ): List<String> {

        return SimpleSearch
            .highlightRanges(text, query)
            .map { range ->
                text.substring(range.first, range.last + 1)
            }
    }
}
