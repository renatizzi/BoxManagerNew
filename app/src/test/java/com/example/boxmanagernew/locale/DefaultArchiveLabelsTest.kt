package com.example.boxmanagernew.locale

import com.example.boxmanagernew.domain.locale.DefaultArchiveLabels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultArchiveLabelsTest {

    @Test
    fun seedCounts_matchAppDatabase() {
        assertEquals(16, DefaultArchiveLabels.categoryItToEn.size)
        assertEquals(3, DefaultArchiveLabels.locationItToEn.size)
    }

    @Test
    fun italianKeys_coverOfficialSeeds() {
        assertTrue(
            DefaultArchiveLabels.categoryItToEn.containsKey(
                "Attrezzi, Strumenti e Ferramenta"
            )
        )
        assertTrue(
            DefaultArchiveLabels.categoryItToEn.containsKey(
                "Alimenti e Bevande"
            )
        )
        assertTrue(
            DefaultArchiveLabels.locationItToEn.containsKey(
                "Cantina"
            )
        )
        assertEquals(
            "Basement",
            DefaultArchiveLabels.locationItToEn["Cantina"]
        )
        assertEquals(
            "Garage",
            DefaultArchiveLabels.locationItToEn["Garage"]
        )
    }
}
