package com.example.boxmanagernew.family

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class FamilyShareLayoutParityTest {

    @Test
    fun familyShareCards_matchUtilityCardStyle() {
        val utility = loadLayout("activity_utility.xml")
        val family = loadLayout("activity_family_catalog.xml")

        val utilityCard = firstCard(utility, "btnBackup")
        val familyIds = listOf(
            "btnExportSharedTables",
            "btnImportSharedTables",
            "btnExportMerge",
            "btnImportMerge"
        )
        val familyCards = familyIds.map { firstCard(family, it) }

        for (card in familyCards) {
            assertEquals(
                "180dp",
                card.getAttribute("android:layout_height")
            )
            assertEquals(
                utilityCard.getAttribute("android:layout_height"),
                card.getAttribute("android:layout_height")
            )
            assertEquals("6dp", card.getAttribute("android:layout_margin"))
            assertEquals(
                utilityCard.getAttribute("android:layout_margin"),
                card.getAttribute("android:layout_margin")
            )
            assertEquals(
                "@color/elevated_surface",
                card.getAttribute("app:cardBackgroundColor")
            )
            assertEquals("16dp", card.getAttribute("app:cardCornerRadius"))
            assertEquals("5dp", card.getAttribute("app:cardElevation"))
            assertEquals("0dp", card.getAttribute("app:strokeWidth"))
        }

        val familyTexts = listOf(
            "textExportSharedTables",
            "textImportSharedTables",
            "textExportMerge",
            "textImportMerge"
        ).map { firstElementById(family, it) }

        for (text in familyTexts) {
            assertEquals("center", text.getAttribute("android:gravity"))
            assertEquals("20sp", text.getAttribute("android:textSize"))
            assertEquals("bold", text.getAttribute("android:textStyle"))
            assertEquals(
                "match_parent",
                text.getAttribute("android:layout_height")
            )
        }
    }

    @Test
    fun familySharePage_keepsTwoSectionsWithoutFolderBrowse() {
        val xml = layoutFile("activity_family_catalog.xml").readText()
        assertTrue(xml.contains("btnExportSharedTables"))
        assertTrue(xml.contains("btnImportSharedTables"))
        assertTrue(xml.contains("btnExportMerge"))
        assertTrue(xml.contains("btnImportMerge"))
        assertFalse(xml.contains("SFOGLIA", ignoreCase = true))
        assertFalse(xml.contains("btnBrowse", ignoreCase = true))
        assertTrue(xml.contains("android:padding=\"16dp\""))
    }

    @Test
    fun familyShareDimens_matchUtilityHeightOnPhoneAndTablet() {
        assertEquals(
            "180dp",
            dimenValue("values/dimens.xml", "family_share_button_min_height")
        )
        assertEquals(
            "20sp",
            dimenValue("values/dimens.xml", "family_share_button_text")
        )
        assertEquals(
            "180dp",
            dimenValue(
                "values-sw600dp/dimens.xml",
                "family_share_button_min_height"
            )
        )
        assertEquals(
            "20sp",
            dimenValue("values-sw600dp/dimens.xml", "family_share_button_text")
        )
    }

    private fun firstCard(root: Element, id: String): Element =
        firstElementById(root, id)

    private fun firstElementById(root: Element, id: String): Element {
        val matches = mutableListOf<Element>()
        collectElements(root, matches)
        return matches.first { element ->
            element.getAttribute("android:id") == "@+id/$id"
        }
    }

    private fun collectElements(node: Node, out: MutableList<Element>) {
        if (node is Element) {
            out.add(node)
        }
        val children = node.childNodes
        for (i in 0 until children.length) {
            collectElements(children.item(i), out)
        }
    }

    private fun loadLayout(name: String): Element {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        return factory.newDocumentBuilder()
            .parse(layoutFile(name))
            .documentElement
    }

    private fun dimenValue(relativeRes: String, name: String): String {
        val factory = DocumentBuilderFactory.newInstance()
        val root = factory.newDocumentBuilder()
            .parse(resFile(relativeRes))
            .documentElement
        val nodes = root.getElementsByTagName("dimen")
        for (i in 0 until nodes.length) {
            val element = nodes.item(i) as Element
            if (element.getAttribute("name") == name) {
                return element.textContent.trim()
            }
        }
        error("Dimen $name assente in $relativeRes")
    }

    private fun layoutFile(name: String): File = resFile("layout/$name")

    private fun resFile(relative: String): File {
        val candidates = listOf(
            File("src/main/res/$relative"),
            File("app/src/main/res/$relative")
        )
        return candidates.firstOrNull { it.isFile }
            ?: error("Risorsa non trovata: $relative")
    }
}
