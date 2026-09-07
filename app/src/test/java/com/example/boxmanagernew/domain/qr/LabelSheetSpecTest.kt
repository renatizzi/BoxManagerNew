package com.example.boxmanagernew.domain.qr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class LabelSheetSpecTest {

    @Test
    fun labelsPerPage_matchesGrid() {
        assertEquals(1, LabelSheetSpec.ONE_PER_PAGE.labelsPerPage)
        assertEquals(4, LabelSheetSpec.A4_2X2.labelsPerPage)
        assertEquals(6, LabelSheetSpec.A4_2X3.labelsPerPage)
    }

    @Test
    fun pageCount_ceilDivision() {
        assertEquals(0, LabelSheetSpec.ONE_PER_PAGE.pageCount(0))
        assertEquals(3, LabelSheetSpec.ONE_PER_PAGE.pageCount(3))
        assertEquals(1, LabelSheetSpec.A4_2X2.pageCount(4))
        assertEquals(2, LabelSheetSpec.A4_2X2.pageCount(5))
        assertEquals(2, LabelSheetSpec.A4_2X3.pageCount(7))
    }

    @Test
    fun presets_includeL0AndL1() {
        val presets = LabelSheetSpec.presets()
        assertTrue(presets.contains(LabelSheetSpec.ONE_PER_PAGE))
        assertTrue(presets.contains(LabelSheetSpec.A4_2X2))
        assertTrue(presets.contains(LabelSheetSpec.A4_2X3))
    }
}

class QrBatchFileNamesTest {

    @Test
    fun proposed_matchesQrStampPattern() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.SEPTEMBER, 7, 17, 5, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val name = QrBatchFileNames.proposed(cal.time)
        assertTrue(
            name.matches(Regex("""QR_\d{6}_\d{4}\.pdf"""))
        )
        assertTrue(name.startsWith("QR_"))
        assertTrue(name.endsWith(".pdf"))
    }

    @Test
    fun force_alwaysPdfExtension() {
        assertEquals("QR_test.pdf", QrBatchFileNames.force("QR_test"))
        assertEquals("QR_test.pdf", QrBatchFileNames.force("QR_test.PDF"))
        assertEquals("QR_test.pdf", QrBatchFileNames.force("QR_test.txt"))
    }
}
