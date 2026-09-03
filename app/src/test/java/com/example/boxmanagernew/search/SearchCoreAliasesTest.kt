package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.SearchCoreAliases
import com.example.boxmanagernew.domain.search.SearchCoreNormalizer
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import com.example.boxmanagernew.domain.search.model.CoreEntityType
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchCoreAliasesTest {

    private val normalizer =
        SearchCoreNormalizer()

    @Test
    fun officialObjectAliasesMapToOggetto() {

        listOf(
            "utensile",
            "arnese",
            "roba",
            "affare",
            "articolo"
        ).forEach { alias ->

            assertEquals(
                "oggetto",
                normalizer.normalize(alias).normalizedToken
            )
        }
    }

    @Test
    fun officialBoxAliasesMapToContenitore() {

        listOf(
            "cassetta",
            "baule",
            "barattolo",
            "cassaforte",
            "imballaggio",
            "cover",
            "armadio",
            "guardaroba",
            "stipo",
            "libreria",
            "scaffale"
        ).forEach { alias ->

            assertEquals(
                "contenitore",
                normalizer.normalize(alias).normalizedToken
            )
        }
    }

    @Test
    fun officialLocationAliasesMapToPosizione() {

        listOf(
            "ubicazione",
            "zona",
            "perimetro",
            "ambiente",
            "località",
            "punto"
        ).forEach { alias ->

            assertEquals(
                "posizione",
                normalizer.normalize(alias).normalizedToken
            )
        }
    }

    @Test
    fun officialCategoryAliasesMapToCategoria() {

        listOf(
            "classificazione",
            "gruppo",
            "fascia",
            "tipologia",
            "qualità",
            "genere"
        ).forEach { alias ->

            assertEquals(
                "categoria",
                normalizer.normalize(alias).normalizedToken
            )
        }
    }

    @Test
    fun officialAliasesResolveToCoreEntity() {

        assertEquals(
            CoreEntityType.OBJECT,
            SearchCoreAliases.coreEntityType("utensile")
        )

        assertEquals(
            CoreEntityType.BOX,
            SearchCoreAliases.coreEntityType("cassetta")
        )

        assertEquals(
            CoreEntityType.LOCATION,
            SearchCoreAliases.coreEntityType("sito")
        )

        assertEquals(
            CoreEntityType.CATEGORY,
            SearchCoreAliases.coreEntityType("fascia")
        )
    }

    @Test
    fun englishOfficialAliasesMapToItalianCanonicalCores() {

        SearchLocaleContext.run(
            SearchLocale.EN
        ) {

            assertEquals(
                "oggetto",
                SearchCoreAliases.canonicalToken("item")
            )
            assertEquals(
                "oggetto",
                SearchCoreAliases.canonicalToken("tool")
            )
            assertEquals(
                "contenitore",
                SearchCoreAliases.canonicalToken("crate")
            )
            assertEquals(
                "contenitore",
                SearchCoreAliases.canonicalToken("closet")
            )
            assertEquals(
                "posizione",
                SearchCoreAliases.canonicalToken("room")
            )
            assertEquals(
                "posizione",
                SearchCoreAliases.canonicalToken("spot")
            )
            assertEquals(
                "categoria",
                SearchCoreAliases.canonicalToken("tier")
            )
            assertEquals(
                "categoria",
                SearchCoreAliases.canonicalToken("type")
            )
            assertEquals(
                CoreEntityType.OBJECT,
                SearchCoreAliases.coreEntityType("item")
            )
            assertEquals(
                CoreEntityType.BOX,
                SearchCoreAliases.coreEntityType("container")
            )
            assertEquals(
                null,
                SearchCoreAliases.coreEntityType("locale")
            )
        }
    }
}
