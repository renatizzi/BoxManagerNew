package com.example.boxmanagernew.ui.utility

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Utility: tile QR BATCH; dialog intro su Utility (non riquadro warning lista);
 * ETICHETTE QR solo in selectionBar.
 */
class UtilityQrBatchLayoutTest {

    @Test
    fun utilityLayout_hasQrBatchBesideQr_andWeightRows() {
        val layout = File("src/main/res/layout/activity_utility.xml").readText()
        assertTrue(layout.contains("@+id/btnQrBatch"))
        assertTrue(layout.contains("@string/utility_qr_batch"))
        assertTrue(layout.contains("android:layout_weight=\"1\""))
        assertFalse(layout.contains("android:layout_height=\"92dp\""))
        val qrIdx = layout.indexOf("@+id/btnQr\"")
        val batchIdx = layout.indexOf("@+id/btnQrBatch")
        val familyIdx = layout.indexOf("@+id/btnFamilyCatalog")
        assertTrue(qrIdx >= 0 && batchIdx > qrIdx)
        assertTrue(familyIdx > batchIdx)
    }

    @Test
    fun mainLayout_qrLabelsOnlyInsideSelectionBar() {
        val layout = File("src/main/res/layout/activity_main.xml").readText()
        val selectionStart = layout.indexOf("@+id/selectionBar")
        val selectionEnd = layout.indexOf("<!-- LISTA -->", selectionStart)
        assertTrue(selectionStart >= 0 && selectionEnd > selectionStart)
        val selectionBlock = layout.substring(selectionStart, selectionEnd)
        assertTrue(selectionBlock.contains("@+id/btnQrSelected"))
        val afterList = layout.substring(selectionEnd)
        assertFalse(afterList.contains("@+id/btnQrSelected"))
    }

    @Test
    fun utilityActivity_showsDialogThenOpensMain() {
        val src = File("src/main/java/com/example/boxmanagernew/ui/utility/UtilityActivity.kt")
            .readText()
        assertTrue(src.contains("msg_qr_batch_intro"))
        assertTrue(src.contains("AlertDialog.Builder"))
        assertTrue(src.contains("PremiumFeature.QR_LABEL"))
        assertTrue(src.contains("MainActivity::class.java"))
        assertFalse(src.contains("intentQrBatchPick"))
        assertFalse(src.contains("msg_qr_batch_select_containers"))
    }

    @Test
    fun mainActivity_noStickyContextForQrBatch() {
        val src = File("src/main/java/com/example/boxmanagernew/MainActivity.kt").readText()
        assertFalse(src.contains("stickyContextMessage"))
        assertFalse(src.contains("EXTRA_QR_BATCH_PICK"))
        assertFalse(src.contains("applyQrBatchPickIntent"))
    }

    @Test
    fun strings_qrBatchIntroItAndEn() {
        val it = File("src/main/res/values/strings.xml").readText()
        val en = File("src/main/res/values-en/strings.xml").readText()
        assertTrue(it.contains("name=\"msg_qr_batch_intro\""))
        assertTrue(en.contains("name=\"msg_qr_batch_intro\""))
        assertTrue(it.contains("stampa o l\\'esportazione"))
        assertTrue(en.contains("print or export labels"))
        assertFalse(it.contains("name=\"msg_qr_batch_select_containers\""))
    }
}
