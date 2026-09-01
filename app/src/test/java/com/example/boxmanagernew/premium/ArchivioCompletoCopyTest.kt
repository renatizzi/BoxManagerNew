package com.example.boxmanagernew.premium

import com.example.boxmanagernew.domain.premium.ArchivioCompletoCopy
import com.example.boxmanagernew.domain.premium.PremiumFeature
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchivioCompletoCopyTest {

    @Test
    fun familyShare_isPremiumTitleArchivioCondiviso() {
        assertEquals(
            "Archivio condiviso",
            ArchivioCompletoCopy.featureTitle(PremiumFeature.FAMILY_SHARE)
        )
        val pitch =
            ArchivioCompletoCopy.pitch(PremiumFeature.FAMILY_SHARE)
        assertTrue(pitch.lead.contains("famiglia"))
        assertTrue(pitch.example!!.contains("Invia"))
    }

    @Test
    fun advancedFunctionsList_addsArchivioCondivisoOnlyWhenAsked() {
        assertTrue(
            ArchivioCompletoCopy.advancedFunctionsList(true)
                .contains("Archivio condiviso")
        )
        assertFalse(
            ArchivioCompletoCopy.advancedFunctionsList(false)
                .contains("Archivio condiviso")
        )
        assertTrue(
            ArchivioCompletoCopy.packageShareHint(14, 7, 1, true)
                .contains("Archivio condiviso")
        )
        assertFalse(
            ArchivioCompletoCopy.packageShareHint(14, 7, 1, false)
                .contains("Archivio condiviso")
        )
    }
}
