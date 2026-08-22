package com.example.boxmanagernew.viewoutput

import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

class ViewOutputConfigurationTest {

    @Test
    fun filterLine_quotesQueryAndUsesDateDashTime() {

        val now = GregorianCalendar(
            2026,
            Calendar.AUGUST,
            22,
            14,
            5
        ).time

        assertEquals(
            "Lista filtrata per \"viti\" il 22/08/2026 - 14:05",
            ViewOutputConfiguration.filterLine("viti", now)
        )
    }

    @Test
    fun countLines_matchOnScreenCards() {

        assertEquals(
            "N. Contenitori: 3",
            ViewOutputConfiguration.countBoxes(3)
        )
        assertEquals(
            "N. Oggetti: 12",
            ViewOutputConfiguration.countObjects(12)
        )
        assertEquals(
            "N. Categorie: 4",
            ViewOutputConfiguration.countCategories(4)
        )
        assertEquals(
            "N. Posizioni: 5",
            ViewOutputConfiguration.countLocations(5)
        )
    }

    @Test
    fun objectPageTitles_matchScreenHeaders() {

        assertEquals(
            "Lista Oggetti - Contenuto del box Cantina",
            ViewOutputConfiguration.objectsInBoxTitle("Cantina")
        )
        assertEquals(
            "Lista Oggetti Trovati - Risultati ricerca archivio",
            ViewOutputConfiguration.PAGE_TITLE_FOUND_OBJECTS
        )
        assertEquals(
            "Categorie - Classificazione Contenitori",
            ViewOutputConfiguration.PAGE_TITLE_CATEGORIES
        )
        assertEquals(
            "Posizione - Luoghi abituali di custodia",
            ViewOutputConfiguration.PAGE_TITLE_LOCATIONS
        )
    }

    @Test
    fun csvFileName_keepsOrAddsCsv() {

        val now = GregorianCalendar(
            2026,
            Calendar.AUGUST,
            22,
            16,
            20
        ).time

        assertEquals(
            "ESPORTA_220826_1620.csv",
            ViewOutputConfiguration.csvFileName("", now)
        )
        assertEquals(
            "ESPORTA_220826_1620.csv",
            ViewOutputConfiguration.proposedFileName(now)
        )
        assertEquals(
            "Lista_Cantina.csv",
            ViewOutputConfiguration.csvFileName("Lista_Cantina")
        )
        assertEquals(
            "Lista.csv",
            ViewOutputConfiguration.csvFileName("Lista.csv")
        )
    }

    @Test
    fun csvNamesMatch_treatsCsvExtensionAsSameFile() {

        assertEquals(
            true,
            ViewOutputConfiguration.csvNamesMatch(
                "Esporta.csv",
                "Esporta"
            )
        )
        assertEquals(
            true,
            ViewOutputConfiguration.csvNamesMatch(
                "Esporta.csv",
                "esporta.CSV"
            )
        )
        assertEquals(
            true,
            ViewOutputConfiguration.csvNamesMatch(
                "Esporta.csv.csv",
                "Esporta.csv"
            )
        )
        assertEquals(
            false,
            ViewOutputConfiguration.csvNamesMatch(
                "Esporta.csv",
                "Lista.csv"
            )
        )
    }

    @Test
    fun exportFilePrompt_confirmOrReplace() {

        assertEquals(
            "Confermi?",
            ViewOutputConfiguration.exportFilePrompt(false)
        )
        assertEquals(
            "File già esistente. Sostituirlo?",
            ViewOutputConfiguration.exportFilePrompt(true)
        )
    }
}
