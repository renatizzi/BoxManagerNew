package com.example.boxmanagernew.domain.qr

import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.junit.Assert.assertEquals
import org.junit.Test

class QrLabelMatrixTest {

    @Test
    fun encodedMatrix_roundtripsPermanentIdPayload() {

        val payload = BoxQrPayload.encode("label-perm-id")
        val matrix = QrLabelMatrix.encode(payload, 320)

        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        var index = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[index++] =
                    if (matrix.get(x, y)) {
                        0xFF000000.toInt()
                    } else {
                        0xFFFFFFFF.toInt()
                    }
            }
        }

        val decoded = QRCodeReader().decode(
            BinaryBitmap(
                HybridBinarizer(
                    RGBLuminanceSource(width, height, pixels)
                )
            )
        )

        assertEquals(payload, decoded.text)
        assertEquals(
            BoxQrPayload.Parse.Identified("label-perm-id"),
            BoxQrPayload.parse(decoded.text)
        )
    }
}
