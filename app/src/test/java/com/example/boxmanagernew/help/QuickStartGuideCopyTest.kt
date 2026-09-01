package com.example.boxmanagernew.help

import com.example.boxmanagernew.domain.help.QuickStartGuideCopy
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickStartGuideCopyTest {

    @Test
    fun header_keepsTitleAndUsesRequestedIntroAndHeading() {
        assertEquals("Guida rapida", QuickStartGuideCopy.PAGE_TITLE)
        assertEquals(
            "BoxManager, un'app che ti aiuta a cercare ciò che non trovi...",
            QuickStartGuideCopy.PAGE_SUBTITLE
        )
        assertEquals(
            "Come usare BoxManager in tre mosse",
            QuickStartGuideCopy.WORKFLOW_TITLE
        )
        assertEquals(
            "1. Configura",
            QuickStartGuideCopy.Phase.CONFIG.numberedLabel()
        )
        assertEquals(
            "2. Censisci",
            QuickStartGuideCopy.Phase.CENSUS.numberedLabel()
        )
        assertEquals(
            "3. Utilizza",
            QuickStartGuideCopy.Phase.USAGE.numberedLabel()
        )
    }

    @Test
    fun sections_coverSixStepsInThreePhases() {
        assertEquals(6, QuickStartGuideCopy.sections.size)
        assertEquals(
            1,
            QuickStartGuideCopy.sections.count {
                it.phase == QuickStartGuideCopy.Phase.CONFIG
            }
        )
        assertEquals(
            2,
            QuickStartGuideCopy.sections.count {
                it.phase == QuickStartGuideCopy.Phase.CENSUS
            }
        )
        assertEquals(
            3,
            QuickStartGuideCopy.sections.count {
                it.phase == QuickStartGuideCopy.Phase.USAGE
            }
        )
    }

    @Test
    fun footer_mentionsPremiumRestrictions() {
        assertTrue(
            QuickStartGuideCopy.FOOTER_NOTE.contains("premium")
        )
        assertTrue(
            QuickStartGuideCopy.FOOTER_NOTE.contains("restrizioni temporali")
        )
    }

    @Test
    fun csvFootnote_alignsWithImportConfiguration() {
        val footnote = QuickStartGuideCopy.CSV_FOOTNOTE

        assertTrue(footnote.startsWith("(*)"))
        assertTrue(footnote.contains(ImportConfiguration.SEPARATOR))
        assertTrue(footnote.contains(ImportConfiguration.FILE_NAME))
        assertTrue(footnote.contains(ImportConfiguration.FORMAT_NAME))
        assertTrue(footnote.contains(ImportConfiguration.SECTION_BOXES))
        assertTrue(footnote.contains(ImportConfiguration.SECTION_OBJECTS))
        assertTrue(footnote.contains(ImportConfiguration.PRE_IMPORT_PREFIX))
        assertTrue(footnote.contains(ViewOutputConfiguration.EXPORT_FILE_PREFIX))
        assertTrue(footnote.contains("Stesso schema del modello Importa dati"))
    }

    @Test
    fun settingsSection_mentionsPrivacyAndArchivioCondiviso() {
        val settings =
            QuickStartGuideCopy.sections.single {
                it.number == 1
            }
        val body = settings.bullets.joinToString(" ")

        assertEquals("Impostazioni", settings.title)
        assertTrue(body.contains("Privacy"))
        assertTrue(body.contains("Archivio Condiviso"))
    }

    @Test
    fun contextualToolsSection_referencesCsvFootnote() {
        val tools =
            QuickStartGuideCopy.sections.single {
                it.number == 6
            }

        assertEquals("Strumenti contestuali", tools.title)
        assertEquals(
            "Gli elementi visualizzati nelle liste possono essere",
            tools.bodyIntro
        )
        assertEquals(2, tools.bullets.size)
        assertEquals("stampati.", tools.bullets[0])
        assertTrue(
            tools.bullets[1].startsWith("esportati su file CSV")
        )
        assertTrue(
            tools.bullets[1].contains("(*)")
        )
        assertTrue(
            tools.bodyClosing.orEmpty().contains("etichetta QR")
        )
    }

    @Test
    fun playUtility_doesNotMentionFamilyShare() {
        val utility =
            QuickStartGuideCopy.sections.single {
                it.number == 5
            }

        assertFalse(
            utility.bullets.any { it.contains("Condividi Archivio") }
        )
        assertTrue(
            utility.bullets.any { it.contains("(*)") }
        )
    }

    @Test
    fun familyBetaSections_addCondividiArchivioAndRestoreWarning() {
        val sections =
            QuickStartGuideCopy.sectionsFor(includeFamilyBeta = true)

        assertEquals(6, sections.size)
        assertFalse(sections.any { it.number == 8 })

        val utility = sections.single { it.number == 5 }
        assertTrue(
            utility.bullets.any { it.contains("Condividi Archivio") }
        )
        assertTrue(
            utility.bullets.any { it.contains("non Ripristino") }
        )
    }
}
