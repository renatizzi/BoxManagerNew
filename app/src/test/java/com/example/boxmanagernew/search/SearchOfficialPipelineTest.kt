package com.example.boxmanagernew.search

import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchF7Pattern
import com.example.boxmanagernew.domain.search.SearchF8Pattern
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchOfficialPipelineTest {

    private val dispatcher =
        GlobalSearchDispatcher()

    private val index =
        SearchArchiveIndex(
            locations = listOf("Cantina", "Mansarda"),
            categories = listOf(
                "Ferramenta",
                "Generico",
                "Miscellanea",
                "Alimenti e Bevande",
                "Contenitori"
            ),
            objects = listOf("Vite", "Trapano elettrico", "Box"),
            boxes = listOf(
                "Cassetta 1",
                "prova",
                "box prova",
                "prova 1",
                "prova 2",
                "Box 1",
                "Box1",
                "Box",
                "Box1 - Cartone",
                "Box9 - Plastica",
                "BOX_VUOTO",
                "UtenzeBox",
                "NastroBox"
            )
        )

    private fun archiveWithBoxOnAllCores() =
        index.copy(
            locations =
                index.locations + "Box",
            categories =
                index.categories + "Box",
            objectRecords = listOf(
                SearchArchiveObjectRecord(
                    name = "Box",
                    boxName = "Cassetta 1"
                )
            )
        )

    @Test
    fun trovaIlTrapanoElettricoPipelinePhases() {

        val question =
            "TROVA IL TRAPANO ELETTRICO"

        val normalized =
            com.example.boxmanagernew.domain.search.SearchNormalizer()
                .normalize(
                    question
                )

        val indicators =
            com.example.boxmanagernew.domain.search
                .SearchLexicalIndicatorMatrix()
                .findIndicators(
                    normalized.normalizedQuestion
                )

        val response =
            dispatcher.dispatch(
                question,
                index
            )

        assertEquals(
            "trapano elettrico",
            normalized.normalizedQuestion
        )
        assertTrue(
            indicators.values.all { group ->
                group.isEmpty()
            }
        )
        assertTrue(response.success)
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            response.requestType
        )
        assertTrue(
            response.debugMarker.orEmpty().contains(
                "keys=[Trapano elettrico]"
            )
        )
        assertTrue(
            response.debugMarker.orEmpty().contains(
                "[SATISFIABLE] true"
            )
        )
    }

    @Test
    fun trovaBoxAsksClarificationWhenObjectAndContainerShareKey() {

        val response =
            dispatcher.dispatch(
                "Trova box",
                index
            )

        assertFalse(response.success)
        assertTrue(response.requiresClarification)
        assertEquals(
            "Riformula la domanda in modo che sia chiaro se ti riferisci a un oggetto o a un contenitore.",
            response.message
        )
    }

    @Test
    fun trovaBoxAsksClarificationWhenOnlyObjectNamedBox() {

        val archive =
            index.copy(
                objects = listOf(
                    "Vite",
                    "Box"
                ),
                boxes = listOf(
                    "Cassetta 1"
                ),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Box",
                        boxName = "Cassetta 1"
                    )
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova box",
                archive
            )

        assertFalse(
            "Trova box non deve aprire i contenitori dell'oggetto Box. " +
                    "success=${response.success} " +
                    "clarify=${response.requiresClarification} " +
                    "objectTerms=${response.objectTerms} " +
                    "boxTerms=${response.boxTerms} " +
                    "transform=${response.archiveTransformation}\n" +
                    response.debugMarker
            ,
            response.success
        )
        assertTrue(response.requiresClarification)
        assertEquals(
            "Riformula la domanda in modo che sia chiaro se ti riferisci a un oggetto o a un contenitore.",
            response.message
        )
        assertEquals("", response.objectTerms)
        assertEquals("", response.boxTerms)
    }

    @Test
    fun trovaBoxWithoutObjectNamedBoxOpensNamedContainers() {

        val onlyContainers =
            index.copy(
                objects = listOf(
                    "Vite",
                    "Trapano elettrico"
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova box",
                onlyContainers
            )

        assertTrue(response.success)
        assertEquals(
            setOf("box prova", "Box 1", "Box"),
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        )
    }

    @Test
    fun trovaContenitoreBoxUsesNamedContainers() {

        val fromContainer =
            dispatcher.dispatch(
                "Trova contenitore box",
                index
            )

        val expected =
            setOf("box prova", "Box 1", "Box")

        assertTrue(fromContainer.success)
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            fromContainer.requestType
        )
        assertEquals(
            SearchArchiveTransformation.NONE,
            fromContainer.archiveTransformation
        )
        assertEquals(
            expected,
            SearchConfiguration.splitLocationTerms(
                fromContainer.boxTerms
            ).toSet()
        )
        assertEquals("", fromContainer.categoryTerms)
        assertEquals("", fromContainer.objectTerms)
        assertEquals(
            "box",
            fromContainer.highlightTerms
        )
    }

    @Test
    fun trovaOggettoBoxDoesNotOpenOggettiDiValoreCategory() {

        val archive =
            index.copy(
                categories =
                    index.categories +
                            "Oggetti di valore",
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Box",
                        boxName = "Cassetta 1"
                    )
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova oggetto box",
                archive
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            "Box",
            response.objectTerms
        )
        assertEquals(
            "",
            response.categoryTerms
        )
        assertEquals(
            "",
            response.boxTerms
        )
        assertEquals(
            "box",
            response.highlightTerms
        )
    }

    @Test
    fun trovaPosizioneBoxOpensLocationNamedBox() {

        val archive =
            index.copy(
                locations =
                    index.locations + "Box",
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Box",
                        boxName = "Cassetta 1"
                    )
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova posizione box",
                archive
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            "Box",
            response.locationTerms
        )
        assertEquals(
            "",
            response.objectTerms
        )
        assertEquals(
            "",
            response.boxTerms
        )
    }

    @Test
    fun trovaCategoriaBoxOpensCategoryNamedBox() {

        val archive =
            index.copy(
                categories =
                    index.categories + "Box",
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Box",
                        boxName = "Cassetta 1"
                    )
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova categoria box",
                archive
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.CATEGORY_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            "Box",
            response.categoryTerms
        )
        assertEquals(
            "",
            response.objectTerms
        )
        assertEquals(
            "",
            response.boxTerms
        )
    }

    @Test
    fun fourCoreBoxOggettoOpensContainersThatContainObjectBox() {

        val response =
            dispatcher.dispatch(
                "Trova oggetto box",
                archiveWithBoxOnAllCores()
            )

        assertFalse(response.requiresClarification)
        assertTrue(
            "Trova oggetto box deve aprire i contenitori dell'oggetto. " +
                    "success=${response.success} " +
                    "message=${response.message} " +
                    "objectTerms=${response.objectTerms} " +
                    "boxTerms=${response.boxTerms} " +
                    "locationTerms=${response.locationTerms} " +
                    "categoryTerms=${response.categoryTerms} " +
                    "transform=${response.archiveTransformation}\n" +
                    response.debugMarker,
            response.success
        )
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals("Box", response.objectTerms)
        assertEquals("", response.boxTerms)
        assertEquals("", response.locationTerms)
        assertEquals("", response.categoryTerms)
        assertEquals("box", response.highlightTerms)
    }

    @Test
    fun fourCoreBoxContenitoreOpensNamedContainers() {

        val response =
            dispatcher.dispatch(
                "Trova contenitore box",
                archiveWithBoxOnAllCores()
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.NONE,
            response.archiveTransformation
        )
        assertEquals(
            setOf("box prova", "Box 1", "Box"),
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        )
        assertEquals("", response.objectTerms)
    }

    @Test
    fun fourCoreBoxPosizioneOpensLocation() {

        val response =
            dispatcher.dispatch(
                "Trova posizione box",
                archiveWithBoxOnAllCores()
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            response.archiveTransformation
        )
        assertEquals("Box", response.locationTerms)
        assertEquals("", response.objectTerms)
        assertEquals("", response.boxTerms)
        assertEquals("", response.categoryTerms)
    }

    @Test
    fun fourCoreBoxCategoriaOpensCategory() {

        val response =
            dispatcher.dispatch(
                "Trova categoria box",
                archiveWithBoxOnAllCores()
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.CATEGORY_TO_BOX,
            response.archiveTransformation
        )
        assertEquals("Box", response.categoryTerms)
        assertEquals("", response.objectTerms)
        assertEquals("", response.boxTerms)
        assertEquals("", response.locationTerms)
    }

    @Test
    fun fourCoreBoxWithoutSelectorAsksClarification() {

        val response =
            dispatcher.dispatch(
                "Trova box",
                archiveWithBoxOnAllCores()
            )

        assertFalse(response.success)
        assertTrue(response.requiresClarification)
        assertEquals(
            "Riformula la domanda in modo che sia chiaro se ti riferisci a un oggetto, a un contenitore, a una posizione o a una categoria.",
            response.message
        )
    }

    @Test
    fun trapanoElettricoDoesNotMatchCacciaviteElettrico() {

        val archive =
            SearchArchiveIndex(
                objects = listOf(
                    "Trapano elettrico",
                    "Cacciavite elettrico"
                ),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Trapano elettrico",
                        boxName = "Cassetta 1"
                    ),
                    SearchArchiveObjectRecord(
                        name = "Cacciavite elettrico",
                        boxName = "Cassetta 2"
                    )
                ),
                boxes = listOf(
                    "Cassetta 1",
                    "Cassetta 2"
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova il trapano elettrico",
                archive
            )

        assertTrue(response.success)
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
    }

    @Test
    fun trovaTuttiIContenitoriDiNomeBoxMatchesSameSix() {

        val response =
            dispatcher.dispatch(
                "Trova tutti i contenitori di nome box",
                index
            )

        assertTrue(response.success)
        assertEquals(
            setOf("box prova", "Box 1", "Box"),
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        )
    }

    @Test
    fun locationQuestionOpensContainers() {

        val response =
            dispatcher.dispatch(
                "Fammi vedere tutto quello che ho in cantina",
                index
            )

        assertTrue(response.success)
        assertEquals("Cantina", response.locationTerms)
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun namedCategoryOpensContainers() {

        val response =
            dispatcher.dispatch(
                "Quali sono i contenitori della categoria Generico?",
                index
            )

        assertTrue(response.success)
        assertEquals("Generico", response.categoryTerms)
        assertEquals(
            SearchArchiveTransformation.CATEGORY_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun trovaContenitoreProvaKeepsProvaFamily() {

        val response =
            dispatcher.dispatch(
                "Trova contenitore prova",
                index
            )

        assertTrue(response.success)
        assertEquals(
            setOf("prova", "box prova", "prova 1", "prova 2"),
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()
        )
    }

    @Test
    fun provaUnoDoesNotMatchProvaDue() {

        val response =
            dispatcher.dispatch(
                "Trova contenitore prova 1",
                index
            )

        val names =
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()

        assertTrue(response.success)
        assertTrue(names.contains("prova 1"))
        assertTrue(!names.contains("prova 2"))
    }

    @Test
    fun objectQuestionUsesObjectToBox() {

        val response =
            dispatcher.dispatch(
                "Dove si trovano le viti?",
                index
            )

        assertTrue(response.success)
        assertEquals("Vite", response.objectTerms)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun twoObjectsStaySeparateAndUseObjectToBox() {

        val response =
            dispatcher.dispatch(
                "Trova viti e trapano elettrico",
                index
            )

        assertTrue(response.success)
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            response.requestType
        )
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            setOf("Vite", "Trapano elettrico"),
            SearchConfiguration.splitLocationTerms(
                response.objectTerms
            ).toSet()
        )
        assertEquals("", response.boxTerms)
    }

    @Test
    fun objectDescriptionIsInSearchPerimeter() {

        val described =
            SearchArchiveIndex(
                objects = listOf("Utensile"),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Utensile",
                        description = "viti da legno"
                    )
                ),
                boxes = listOf("Cassetta 1"),
                locations = listOf("Cantina")
            )

        val response =
            dispatcher.dispatch(
                "Trova viti",
                described
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            "viti",
            response.objectTerms
        )
    }

    @Test
    fun locationWordInObjectDescriptionDoesNotStealLocation() {

        val described =
            SearchArchiveIndex(
                objects = listOf("Utensile"),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Utensile",
                        description = "ripiano in cantina"
                    )
                ),
                boxes = listOf("Cassetta 1"),
                locations = listOf("Cantina")
            )

        val response =
            dispatcher.dispatch(
                "Fammi vedere tutto quello che ho in cantina",
                described
            )

        assertTrue(response.success)
        assertEquals("Cantina", response.locationTerms)
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            response.archiveTransformation
        )
        assertEquals("", response.objectTerms)
    }

    @Test
    fun specificObjectNameIsNotIntersectedWithShorterVite() {

        val index =
            SearchArchiveIndex(
                objects = listOf(
                    "Vite",
                    "Set di viti a stella"
                ),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        boxName = "Box A"
                    ),
                    SearchArchiveObjectRecord(
                        name = "Set di viti a stella",
                        boxName = "Cassetta 1"
                    )
                ),
                boxes = listOf("Box A", "Cassetta 1")
            )

        val fullName =
            dispatcher.dispatch(
                "Trova set di viti a stella",
                index
            )

        val shortName =
            dispatcher.dispatch(
                "Trova set di viti",
                index
            )

        assertTrue(fullName.success)
        assertTrue(shortName.success)
        assertEquals(
            "Set di viti a stella",
            fullName.objectTerms
        )
        assertEquals(
            "Set di viti a stella",
            shortName.objectTerms
        )
    }

    @Test
    fun descriptionPhraseDoesNotOpenAllViteBoxes() {

        val index =
            SearchArchiveIndex(
                objects = listOf("Vite"),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        boxName = "Box A"
                    ),
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        boxName = "Box B"
                    ),
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        description =
                            "set di viti a stella",
                        boxName = "Cassetta 1"
                    )
                ),
                boxes = listOf(
                    "Box A",
                    "Box B",
                    "Cassetta 1"
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova set di viti a stella",
                index
            )

        assertTrue(response.success)
        assertEquals(
            "set viti stella",
            response.objectTerms
        )
    }

    @Test
    fun trovaVitiKeepsViteWhenDescriptionIsLonger() {

        val index =
            SearchArchiveIndex(
                objects = listOf("Vite"),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        boxName = "Box A"
                    ),
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        description =
                            "set di viti a stella",
                        boxName = "Cassetta 1"
                    )
                ),
                boxes = listOf(
                    "Box A",
                    "Cassetta 1"
                )
            )

        val response =
            dispatcher.dispatch(
                "Trova viti",
                index
            )

        assertTrue(response.success)
        assertEquals(
            "Vite",
            response.objectTerms
        )
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun trapanoElettricoIsOneArchivalKey() {

        val response =
            dispatcher.dispatch(
                "Trova trapano elettrico",
                index
            )

        assertTrue(response.success)
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun twoObjectsInDifferentBoxesStayNavigable() {

        val split =
            SearchArchiveIndex(
                objects = listOf(
                    "Vite",
                    "Trapano elettrico"
                ),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        boxName = "Box A"
                    ),
                    SearchArchiveObjectRecord(
                        name = "Trapano elettrico",
                        boxName = "Box B"
                    )
                ),
                boxes = listOf("Box A", "Box B")
            )

        val response =
            dispatcher.dispatch(
                "Trova viti e trapano elettrico",
                split
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            setOf("Vite", "Trapano elettrico"),
            SearchConfiguration.splitLocationTerms(
                response.objectTerms
            ).toSet()
        )
    }

    @Test
    fun twoObjectsInSameBoxStayNavigable() {

        val together =
            SearchArchiveIndex(
                objects = listOf(
                    "Vite",
                    "Trapano elettrico"
                ),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        boxName = "Cassetta 1"
                    ),
                    SearchArchiveObjectRecord(
                        name = "Trapano elettrico",
                        boxName = "Cassetta 1"
                    )
                ),
                boxes = listOf("Cassetta 1")
            )

        val response =
            dispatcher.dispatch(
                "Trova viti e trapano elettrico",
                together
            )

        assertTrue(response.success)
        assertEquals(
            setOf("Vite", "Trapano elettrico"),
            SearchConfiguration.splitLocationTerms(
                response.objectTerms
            ).toSet()
        )
    }

    @Test
    fun descriptionOnlyObjectWithBoxStaysNavigable() {

        val described =
            SearchArchiveIndex(
                objects = listOf("Utensile"),
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Utensile",
                        description = "viti da legno",
                        boxName = "Cassetta 1"
                    )
                ),
                boxes = listOf("Cassetta 1")
            )

        val response =
            dispatcher.dispatch(
                "Trova viti",
                described
            )

        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals(
            "viti",
            response.objectTerms
        )
    }

    @Test
    fun containersThatContainVitiDoesNotAskClarificationForCategoryNamedContenitori() {

        val response =
            dispatcher.dispatch(
                "fammi vedere quali contenitori contengono viti",
                index
            )

        assertFalse(response.requiresClarification)
        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.OBJECT_TO_BOX,
            response.archiveTransformation
        )
        assertEquals("Vite", response.objectTerms)
    }

    @Test
    fun elencoContenitoriInCantinaUsesLocation() {

        val response =
            dispatcher.dispatch(
                "Elenco dei contenitori che sono in cantina",
                index
            )

        assertFalse(response.requiresClarification)
        assertTrue(response.success)
        assertEquals("Cantina", response.locationTerms)
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun oggettiDellaCategoriaGenericoOpensThoseContainers() {

        val response =
            dispatcher.dispatch(
                "oggetti della categoria Generico",
                index
            )

        assertFalse(response.requiresClarification)
        assertTrue(response.success)
        assertEquals("Generico", response.categoryTerms)
        assertEquals(
            SearchArchiveTransformation.CATEGORY_TO_BOX,
            response.archiveTransformation
        )
    }

    @Test
    fun sitoBoxUsesLocationSelectorEvenWithCosa() {

        val archive =
            archiveWithBoxOnAllCores()

        val response =
            dispatcher.dispatch(
                "Cosa ho nel sito BOX",
                archive
            )

        assertFalse(response.requiresClarification)
        assertTrue(response.success)
        assertEquals(
            SearchArchiveTransformation.LOCATION_TO_BOX,
            response.archiveTransformation
        )
        assertEquals("Box", response.locationTerms)
        assertEquals("", response.objectTerms)
        assertEquals("", response.boxTerms)
    }

    @Test
    fun trovaTrapanoAsksClarificationWhenBoxTrapaniExists() {

        val archive =
            index.copy(
                objects = listOf(
                    "Trapano",
                    "Vite"
                ),
                boxes = index.boxes + "Trapani"
            )

        val response =
            dispatcher.dispatch(
                "Trova trapano",
                archive
            )

        assertFalse(response.success)
        assertTrue(response.requiresClarification)
    }

    @Test
    fun trovaTrapanoElettricoDoesNotClarifyForBoxTrapani() {

        val archive =
            index.copy(
                boxes = index.boxes + "Trapani"
            )

        val response =
            dispatcher.dispatch(
                "Trova il trapano elettrico",
                archive
            )

        assertFalse(response.requiresClarification)
        assertTrue(response.success)
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
    }

    @Test
    fun trovaBoxProvaDoesNotIncludeBoxOnlyNames() {

        val response =
            dispatcher.dispatch(
                "Trova box prova",
                index
            )

        val names =
            SearchConfiguration.splitLocationTerms(
                response.boxTerms
            ).toSet()

        assertTrue(response.success)
        assertEquals(
            setOf("prova", "box prova", "prova 1", "prova 2"),
            names
        )
        assertFalse(names.contains("Box 1"))
        assertFalse(names.contains("Box"))
    }

    @Test
    fun provaUnoHighlightKeepsTheDigit() {

        val response =
            dispatcher.dispatch(
                "Trova contenitore prova 1",
                index
            )

        assertTrue(response.success)
        assertTrue(
            response.highlightTerms.contains("1")
        )
        assertTrue(
            response.highlightTerms.contains("prova")
        )
    }

    @Test
    fun trapanoElettricoDoesNotClarifyAgainstBoxDeiTrapani() {

        val archive =
            index.copy(
                objects = listOf(
                    "Trapano elettrico",
                    "Trapano"
                ),
                boxes = index.boxes +
                        "BOX DEI TRAPANI" +
                        "Trapani"
            )

        val response =
            dispatcher.dispatch(
                "Cerca il trapano elettrico",
                archive
            )

        assertFalse(
            "Chiave composta OBJECT, non omonimo del contenitore. " +
                    "clarify=${response.requiresClarification} " +
                    "message=${response.message} " +
                    "objectTerms=${response.objectTerms}\n" +
                    response.debugMarker,
            response.requiresClarification
        )
        assertTrue(response.success)
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
    }

    @Test
    fun contenitoriDellaCategoriaGenericoDoesNotClarifyObjectCategory() {

        val archive =
            index.copy(
                objects = listOf(
                    "Vite",
                    "Generico"
                )
            )

        val response =
            dispatcher.dispatch(
                "contenitori della categoria Generico",
                archive
            )

        assertFalse(
            "categoria Generico è trasformazione dominante. " +
                    "clarify=${response.requiresClarification} " +
                    "message=${response.message} " +
                    "categoryTerms=${response.categoryTerms} " +
                    "objectTerms=${response.objectTerms}\n" +
                    response.debugMarker,
            response.requiresClarification
        )
        assertTrue(response.success)
        assertEquals("Generico", response.categoryTerms)
        assertEquals(
            SearchArchiveTransformation.CATEGORY_TO_BOX,
            response.archiveTransformation
        )
        assertEquals("", response.objectTerms)
    }

    private fun duplicateArchive() =
        index.copy(
            objectRecords = listOf(
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "Cassetta 1"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "prova"
                ),
                SearchArchiveObjectRecord(
                    name = "Trapano elettrico",
                    boxName = "Box 1"
                )
            )
        )

    @Test
    fun f7OfficialVariants_routeToEngineB_notContainerList() {

        val archive =
            duplicateArchive()

        val heading =
            "Elenco dei contenitori che hanno oggetti uguali"

        SearchF7Pattern.VARIANTS.forEach { question ->

            val response =
                dispatcher.dispatch(
                    question,
                    archive
                )

            assertFalse(
                question,
                response.requiresClarification
            )
            assertEquals(
                question,
                SearchRequestType.ARCHIVE_QUERY,
                response.requestType
            )
            assertTrue(
                question,
                response.success
            )
            assertTrue(
                question,
                response.message.startsWith(
                    heading
                )
            )
            assertTrue(
                question,
                response.message.contains(
                    "Cassetta 1"
                )
            )
            assertTrue(
                question,
                response.message.contains(
                    "prova"
                )
            )
            assertFalse(
                question,
                response.message.contains(
                    "Box 1"
                )
            )
            assertEquals(
                question,
                SearchF7Pattern.ID,
                response.debugMarker
                    .orEmpty()
                    .lineSequence()
                    .first {
                        it.startsWith(
                            "[PATTERN]"
                        )
                    }
                    .substringAfter(
                        "[PATTERN] "
                    )
            )
        }
    }

    @Test
    fun f7Official_doesNotClarifyWhenHomonymExistsButIsNotInQuestion() {

        val archive =
            duplicateArchive().copy(
                locations =
                    index.locations + "Box",
                categories =
                    index.categories + "Box"
            )

        val response =
            dispatcher.dispatch(
                SearchF7Pattern.VARIANTS[3],
                archive
            )

        assertFalse(response.requiresClarification)
        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertTrue(response.success)
    }

    @Test
    fun f7WithoutDuplicates_usesCatalogNoResults() {

        val archive =
            index.copy(
                objectRecords = listOf(
                    SearchArchiveObjectRecord(
                        name = "Vite",
                        boxName = "Cassetta 1"
                    )
                )
            )

        val response =
            dispatcher.dispatch(
                SearchF7Pattern.VARIANTS[0],
                archive
            )

        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertFalse(response.success)
        assertEquals(
            SearchConfiguration.MSG_NO_RESULTS,
            response.message
        )
    }

    @Test
    fun f8OfficialVariants_routeToEngineB_notContainerList() {

        val archive =
            crossCategoryArchive()

        val heading =
            "Elenco dei contenitori che hanno categoria diversa e contengono oggetti uguali"

        SearchF8Pattern.VARIANTS.forEach { question ->

            val response =
                dispatcher.dispatch(
                    question,
                    archive
                )

            assertFalse(
                question,
                response.requiresClarification
            )
            assertEquals(
                question,
                SearchRequestType.ARCHIVE_QUERY,
                response.requestType
            )
            assertTrue(
                question,
                response.success
            )
            assertTrue(
                question,
                response.message.startsWith(
                    heading
                )
            )
            assertTrue(
                question,
                response.message.contains(
                    "Cassetta 1"
                )
            )
            assertTrue(
                question,
                response.message.contains(
                    "prova"
                )
            )
            assertFalse(
                question,
                response.message.contains(
                    "Box 1"
                )
            )
            assertEquals(
                question,
                SearchF8Pattern.ID,
                response.debugMarker
                    .orEmpty()
                    .lineSequence()
                    .first {
                        it.startsWith(
                            "[PATTERN]"
                        )
                    }
                    .substringAfter(
                        "[PATTERN] "
                    )
            )
        }
    }

    @Test
    fun f8FamilyTokens_doNotFallThroughToF7() {

        val archive =
            crossCategoryArchive()

        val response =
            dispatcher.dispatch(
                "F8-01 Cerca i contenitori con categoria diversa che contengono lo stesso tipo di oggetto",
                archive
            )

        assertEquals(
            SearchF8Pattern.ID,
            response.debugMarker
                .orEmpty()
                .lineSequence()
                .first {
                    it.startsWith(
                        "[PATTERN]"
                    )
                }
                .substringAfter(
                    "[PATTERN] "
                )
        )
        assertTrue(response.success)
        assertTrue(
            response.message.contains(
                "Cassetta 1"
            )
        )
        assertFalse(
            response.message.contains(
                "Box 1"
            )
        )
    }

    @Test
    fun f8SameCategory_usesCatalogNoResults() {

        val response =
            dispatcher.dispatch(
                SearchF8Pattern.VARIANTS[0],
                duplicateArchive()
            )

        assertEquals(
            SearchRequestType.ARCHIVE_QUERY,
            response.requestType
        )
        assertFalse(response.success)
        assertEquals(
            SearchConfiguration.MSG_NO_RESULTS,
            response.message
        )
    }

    private fun crossCategoryArchive() =
        index.copy(
            objectRecords = listOf(
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "Cassetta 1",
                    boxCategory = "Ferramenta"
                ),
                SearchArchiveObjectRecord(
                    name = "Vite",
                    boxName = "prova",
                    boxCategory = "Alimenti e Bevande"
                ),
                SearchArchiveObjectRecord(
                    name = "Trapano elettrico",
                    boxName = "Box 1",
                    boxCategory = "Ferramenta"
                )
            )
        )

    @Test
    fun motoreAObjectQuestion_stillOpensNavigation() {

        val response =
            dispatcher.dispatch(
                "TROVA IL TRAPANO ELETTRICO",
                index
            )

        assertTrue(response.success)
        assertEquals(
            SearchRequestType.ARCHIVE_NAVIGATION,
            response.requestType
        )
        assertEquals(
            "Trapano elettrico",
            response.objectTerms
        )
    }
}
