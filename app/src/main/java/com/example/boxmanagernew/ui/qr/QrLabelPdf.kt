package com.example.boxmanagernew.ui.qr

import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.view.View
import java.io.ByteArrayOutputStream

object QrLabelPdf {

    private const val PAGE_WIDTH = 298
    private const val PAGE_HEIGHT = 420

    fun toBytes(labelView: View): ByteArray {

        ensureLaidOut(labelView)

        val document = PdfDocument()
        val pageInfo =
            PdfDocument.PageInfo.Builder(
                PAGE_WIDTH,
                PAGE_HEIGHT,
                1
            ).create()

        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawColor(Color.WHITE)

        val viewWidth = labelView.width.coerceAtLeast(1).toFloat()
        val viewHeight = labelView.height.coerceAtLeast(1).toFloat()
        val scale = minOf(
            PAGE_WIDTH / viewWidth,
            PAGE_HEIGHT / viewHeight
        )
        val dx = (PAGE_WIDTH - viewWidth * scale) / 2f
        val dy = (PAGE_HEIGHT - viewHeight * scale) / 2f

        canvas.translate(dx, dy)
        canvas.scale(scale, scale)
        labelView.draw(canvas)

        document.finishPage(page)

        val bytes = ByteArrayOutputStream()
        document.writeTo(bytes)
        document.close()
        return bytes.toByteArray()
    }

    private fun ensureLaidOut(view: View) {

        if (view.width > 0 && view.height > 0) {
            return
        }

        val width = view.resources.displayMetrics.widthPixels.coerceAtLeast(1)
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(
                width,
                View.MeasureSpec.EXACTLY
            )
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
