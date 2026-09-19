package com.example.boxmanagernew.ui.utility

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Utility: tile QR BATCH accanto a Codice QR; griglia 4×2 con weight;
 * ETICHETTE QR solo in selectionBar (non sempre sulla lista).
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
    fun utilityActivity_opensMainWithQrBatchPick() {
        val src = File("src/main/java/com/example/boxmanagernew/ui/utility/UtilityActivity.kt")
            .readText()
        assertTrue(src.contains("MainActivity.intentQrBatchPick"))
        assertTrue(src.contains("PremiumFeature.QR_LABEL"))
        assertTrue(src.contains("msg_qr_batch_no_boxes"))
    }

    @Test
    fun strings_qrBatchItAndEn() {
        val it = File("src/main/res/values/strings.xml").readText()
        val en = File("src/main/res/values-en/strings.xml").readText()
        assertTrue(it.contains("name=\"utility_qr_batch\""))
        assertTrue(en.contains("name=\"utility_qr_batch\""))
        assertTrue(it.contains("QR BATCH"))
        assertTrue(en.contains("QR BATCH"))
        assertTrue(it.contains("name=\"msg_qr_batch_select_containers\""))
        assertTrue(en.contains("Select the containers"))
        assertTrue(it.contains("Seleziona i contenitori"))
        assertTrue(en.contains("name=\"msg_qr_batch_no_boxes\""))
    }
}
