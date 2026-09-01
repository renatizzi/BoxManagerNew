package com.example.boxmanagernew.help

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class QuickStartGuideLayoutTest {

    @Test
    fun processBlock_isStickyOutsideScroll() {
        val xml = layoutSource()
        val processIndex = xml.indexOf("android:id=\"@+id/processBlock\"")
        val scrollTagIndex = xml.indexOf("<ScrollView")
        val scrollIdIndex = xml.indexOf("android:id=\"@+id/guideScroll\"")
        assertTrue(processIndex > 0)
        assertTrue(scrollTagIndex > 0)
        assertTrue(scrollIdIndex > scrollTagIndex)
        assertTrue(processIndex < scrollTagIndex)
    }

    @Test
    fun processChips_haveNumbersAndArrows_withoutRedundantLine() {
        val xml = layoutSource()
        assertTrue(xml.contains("android:id=\"@+id/chipConfig\""))
        assertTrue(xml.contains("android:id=\"@+id/chipCensus\""))
        assertTrue(xml.contains("android:id=\"@+id/chipUsage\""))
        assertTrue(xml.contains("android:id=\"@+id/arrowConfigCensus\""))
        assertTrue(xml.contains("android:id=\"@+id/arrowCensusUsage\""))
        assertTrue(xml.contains("android:text=\"→\""))
        assertTrue(xml.contains("android:textSize=\"14sp\""))
        assertFalse(xml.contains("textWorkflowLine"))
        assertFalse(xml.contains("textIntro"))
    }

    private fun layoutSource(): String {
        val relative =
            "app/src/main/res/layout/activity_quick_start_guide.xml"
        val candidates = listOf(
            File(relative.removePrefix("app/")),
            File(relative)
        )
        return candidates.first { it.isFile }.readText()
    }
}
