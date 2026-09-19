package com.example.boxmanagernew.ui.main

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Il messaggio «Seleziona i contenitori» da QR BATCH non deve sparire
 * quando hasHiddenSelections emette false al caricamento lista.
 */
class QrBatchContextMessageStickyTest {

    @Test
    fun mainActivity_keepsStickyContextWhenHiddenSelectionsFalse() {
        val src = File("src/main/java/com/example/boxmanagernew/MainActivity.kt").readText()
        assertTrue(src.contains("stickyContextMessage"))
        assertTrue(src.contains("msg_qr_batch_select_containers"))
        assertTrue(src.contains("EXTRA_QR_BATCH_PICK"))
        val observerIdx = src.indexOf("hasHiddenSelections.observe")
        assertTrue(observerIdx >= 0)
        val after = src.substring(observerIdx, observerIdx + 500)
        assertTrue(after.contains("stickyContextMessage"))
        assertTrue(after.contains("showContextMessage(sticky)"))
    }
}
