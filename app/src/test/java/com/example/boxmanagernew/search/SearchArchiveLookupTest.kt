package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.SearchArchiveLookup
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchArchiveLookupTest {

    private val index =
        SearchArchiveIndex(
            objects = listOf("Vite", "Box"),
            boxes = listOf(
                "prova",
                "UtenzeBox",
                "Box 1",
                "Box1 - Cartone",
                "BOX_VUOTO"
            )
        )

    @Test
    fun boxInArchiveNamesIsRecognizedAsBoxNotOnlyObjectStub() {

        val result =
            SearchArchiveLookup().lookup(
                "Trova contenitore box",
                index
            )

        val boxMatch =
            result.scopeMatches.first {
                it.scope == SearchArchiveScope.BOX
            }

        assertTrue(result.hasMatches)
        assertEquals(1, boxMatch.matchCount)
    }

    @Test
    fun twoObjectNamesAreRecognizedIndependently() {

        val result =
            SearchArchiveLookup().lookup(
                "Trova viti e trapano elettrico",
                SearchArchiveIndex(
                    objects = listOf(
                        "Vite",
                        "Trapano elettrico",
                        "Box"
                    )
                )
            )

        assertEquals(
            setOf("Vite", "Trapano elettrico"),
            result.hits.objects.toSet()
        )
    }
}
