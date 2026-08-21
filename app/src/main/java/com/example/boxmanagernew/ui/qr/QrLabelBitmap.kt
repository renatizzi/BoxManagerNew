package com.example.boxmanagernew.ui.qr

import android.graphics.Bitmap
import android.graphics.Color
import com.example.boxmanagernew.domain.qr.QrLabelMatrix
import com.google.zxing.common.BitMatrix

object QrLabelBitmap {

    fun render(
        payload: String,
        size: Int = QrLabelMatrix.DEFAULT_SIZE
    ): Bitmap {

        return toBitmap(QrLabelMatrix.encode(payload, size))
    }

    private fun toBitmap(matrix: BitMatrix): Bitmap {

        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        var index = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[index++] =
                    if (matrix.get(x, y)) {
                        Color.BLACK
                    } else {
                        Color.WHITE
                    }
            }
        }

        return Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        ).also { bitmap ->
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }
}
