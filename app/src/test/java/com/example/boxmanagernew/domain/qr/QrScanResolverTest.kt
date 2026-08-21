package com.example.boxmanagernew.domain.qr

import com.example.boxmanagernew.domain.model.Box
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BoxQrPayloadTest {

    @Test
    fun encode_containsOnlyPermanentIdEnvelope() {

        val encoded = BoxQrPayload.encode("perm-1")

        assertTrue(encoded.contains("\"src\":\"boxmanager\""))
        assertTrue(encoded.contains("\"ver\":1"))
        assertTrue(encoded.contains("\"id\":\"perm-1\""))
        assertTrue(!encoded.contains("category"))
        assertTrue(!encoded.contains("position"))
    }

    @Test
    fun parse_roundtrip_returnsSamePermanentId() {

        val encoded = BoxQrPayload.encode("perm-round")

        val parsed = BoxQrPayload.parse(encoded)

        assertEquals(
            BoxQrPayload.Parse.Identified("perm-round"),
            parsed
        )
    }

    @Test
    fun parse_ignoresUnknownFieldsAndAcceptsHigherVersion() {

        val raw =
            """{"src":"boxmanager","ver":2,"id":"perm-2","user":"x"}"""

        assertEquals(
            BoxQrPayload.Parse.Identified("perm-2"),
            BoxQrPayload.parse(raw)
        )
    }

    @Test
    fun parse_blank_isUnreadable() {

        assertEquals(
            BoxQrPayload.Parse.Unreadable,
            BoxQrPayload.parse("  ")
        )
        assertEquals(
            BoxQrPayload.Parse.Unreadable,
            BoxQrPayload.parse(null)
        )
    }

    @Test
    fun parse_foreignPayload_isNotBoxManager() {

        assertEquals(
            BoxQrPayload.Parse.NotBoxManager,
            BoxQrPayload.parse("https://example.com")
        )
        assertEquals(
            BoxQrPayload.Parse.NotBoxManager,
            BoxQrPayload.parse("{\"src\":\"other\",\"id\":\"perm-1\"}")
        )
        assertEquals(
            BoxQrPayload.Parse.NotBoxManager,
            BoxQrPayload.parse("perm-1")
        )
    }
}

class QrScanResolverTest {

    private val box = Box(
        id = 7,
        name = "Cavi",
        categoryId = 1,
        position = "Garage",
        lastModified = 1L,
        permanentId = "perm-7"
    )

    @Test
    fun validQr_opensContainer() {

        val outcome =
            QrScanResolver.resolve(
                BoxQrPayload.encode("perm-7")
            ) { id ->
                box.takeIf { it.permanentId == id }
            }

        assertEquals(QrScanOutcome.OpenContainer(box), outcome)
        assertEquals(null, QrScanResolver.message(outcome))
    }

    @Test
    fun boxManagerQr_unknownId_isContainerMissing() {

        val outcome =
            QrScanResolver.resolve(
                BoxQrPayload.encode("missing-id")
            ) { null }

        assertEquals(QrScanOutcome.ContainerMissing, outcome)
        assertEquals(
            QrConfiguration.MSG_BOX_MISSING,
            QrScanResolver.message(outcome)
        )
    }

    @Test
    fun foreignQr_isUnrecognized() {

        val outcome =
            QrScanResolver.resolve("WIFI:T:WPA;S:x;;") { box }

        assertEquals(QrScanOutcome.Unrecognized, outcome)
        assertEquals(
            QrConfiguration.MSG_UNRECOGNIZED,
            QrScanResolver.message(outcome)
        )
    }

    @Test
    fun emptyRead_isReadError() {

        val outcome = QrScanResolver.resolve(null) { box }

        assertEquals(QrScanOutcome.ReadError, outcome)
        assertEquals(
            QrConfiguration.MSG_READ_ERROR,
            QrScanResolver.message(outcome)
        )
    }

    @Test
    fun deleteMessage_isExactNotaText() {

        assertEquals(
            "Se elimini il contenitore, l'etichetta QR non sarà più utilizzabile. Confermi l'eliminazione?",
            QrConfiguration.MSG_DELETE
        )
    }
}
