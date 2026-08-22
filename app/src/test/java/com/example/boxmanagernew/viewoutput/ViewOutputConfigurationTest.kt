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
    }

    @Test
    fun csvFileName_keepsOrAddsCsv() {

        assertEquals(
            "Esporta.csv",
            ViewOutputConfiguration.csvFileName("")
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
}
