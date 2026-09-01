package com.example.boxmanagernew.help

import com.example.boxmanagernew.domain.help.QuickStartGuideCopy
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import org.junit.Assert.assertEquals
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
    fun sections_coverSevenStepsInThreePhases() {
        assertEquals(7, QuickStartGuideCopy.sections.size)
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
            4,
            QuickStartGuideCopy.sections.count {
                it.phase == QuickStartGuideCopy.Phase.USAGE
            }
        )
    }

    @Test
    fun footer_mentionsArchivioCompleto() {
        assertTrue(
            QuickStartGuideCopy.FOOTER_NOTE.contains("Archivio completo")
        )
    }

    @Test
    fun csvSection_alignsWithImportConfiguration() {
        val csvSection =
            QuickStartGuideCopy.sections.single {
                it.number == 7
            }
        val body =
            (
                csvSection.bullets +
                    listOfNotNull(csvSection.spreadsheetExample)
                ).joinToString(" ")

        assertEquals("Import ed export CSV", csvSection.title)
        assertEquals(
            QuickStartGuideCopy.CSV_EXAMPLE_TITLE,
            csvSection.spreadsheetExampleTitle
        )
        assertTrue(
            body.contains("Excel") || body.contains("Fogli")
        )
        assertTrue(body.contains(ImportConfiguration.FILE_NAME))
        assertTrue(body.contains(ImportConfiguration.FORMAT_NAME))
        assertTrue(body.contains(ImportConfiguration.SECTION_BOXES))
        assertTrue(body.contains(ImportConfiguration.SECTION_OBJECTS))
        assertTrue(body.contains(ImportConfiguration.PRE_IMPORT_PREFIX))
        assertTrue(body.contains(ViewOutputConfiguration.EXPORT_FILE_PREFIX))
        assertTrue(body.contains("Scatola garage"))
        assertTrue(body.contains("Trapano"))
        ImportConfiguration.BOX_HEADER_FIELDS.forEach { column ->
            assertTrue(body.contains(column))
        }
        ImportConfiguration.OBJECT_HEADER_FIELDS.forEach { column ->
            assertTrue(body.contains(column))
        }
    }

    @Test
    fun familyBetaSections_addSetupFamiglia() {
        val sections =
            QuickStartGuideCopy.sectionsFor(includeFamilyBeta = true)
        assertEquals(8, sections.size)
        val family = sections.single { it.number == 8 }
        assertEquals("Setup famiglia (beta)", family.title)
        assertTrue(
            family.bullets.any { it.contains("Catalogo Famiglia") }
        )
        val utility = sections.single { it.number == 5 }
        assertTrue(
            utility.bullets.any { it.contains("Catalogo Famiglia") }
        )
        assertTrue(
            utility.bullets.any { it.contains("Inventario Famiglia") }
        )
    }
}
