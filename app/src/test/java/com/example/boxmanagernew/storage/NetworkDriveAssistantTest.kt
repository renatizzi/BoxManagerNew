package com.example.boxmanagernew.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkDriveAssistantTest {

    @Test
    fun helperPackage_isStablePlayId() {
        assertEquals(
            "com.wa2c.android.cifsdocumentsprovider",
            NetworkDriveAssistant.HELPER_PACKAGE
        )
        assertTrue(
            NetworkDriveAssistant.HELPER_PLAY_WEB.contains(
                NetworkDriveAssistant.HELPER_PACKAGE
            )
        )
    }
}
