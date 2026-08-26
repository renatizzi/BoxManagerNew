package com.example.boxmanagernew.privacy

import com.example.boxmanagernew.domain.privacy.PrivacyPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyPolicyTest {

    @Test
    fun publicUrl_isHttpsGithubPagesPrivacy() {
        assertEquals(
            "https://renatizzi.github.io/BoxManagerNew/privacy/",
            PrivacyPolicy.PUBLIC_URL
        )
        assertTrue(PrivacyPolicy.PUBLIC_URL.startsWith("https://"))
    }

    @Test
    fun cameraRationale_statesOnDeviceQrOnly() {
        assertTrue(
            PrivacyPolicy.CAMERA_RATIONALE.contains("fotocamera")
        )
        assertTrue(
            PrivacyPolicy.CAMERA_RATIONALE.contains("codice QR")
        )
        assertTrue(
            PrivacyPolicy.CAMERA_RATIONALE.contains("non vengono salvate")
        )
        assertTrue(
            PrivacyPolicy.CAMERA_RATIONALE.contains("né inviate")
        )
    }

    @Test
    fun cameraDenied_isDistinctFromReadError() {
        assertTrue(PrivacyPolicy.MSG_CAMERA_DENIED.isNotBlank())
        assertTrue(
            PrivacyPolicy.MSG_CAMERA_DENIED.contains("Permesso fotocamera")
        )
    }
}
