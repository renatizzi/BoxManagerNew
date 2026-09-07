package com.example.boxmanagernew.ui.qr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.boxmanagernew.domain.qr.LabelSheetSpec
import java.io.ByteArrayOutputStream

/**
 * PDF batch da bitmap QR + permanentId (REQUISITI_QR_AVANZATO T2).
 * Non modifica [QrLabelPdf] V1 (singola view).
 */
object QrBatchPdf {

    data class Label(
        val permanentId: String,
        val qrBitmap: Bitmap
    )

    fun toBytes(
        labels: List<Label>,
        spec: LabelSheetSpec
    ): ByteArray {
        if (labels.isEmpty()) {
            return ByteArray(0)
        }

        val document = PdfDocument()
        val pages = spec.pageCount(labels.size)
        var index = 0

        for (pageIndex in 0 until pages) {
            val pageInfo =
                PdfDocument.PageInfo.Builder(
                    spec.pageWidthPt,
                    spec.pageHeightPt,
                    pageIndex + 1
                ).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawColor(Color.WHITE)

            val innerW = spec.pageWidthPt - 2 * spec.marginPt
            val innerH = spec.pageHeightPt - 2 * spec.marginPt
            val cellW = innerW.toFloat() / spec.cols
            val cellH = innerH.toFloat() / spec.rows

            for (row in 0 until spec.rows) {
                for (col in 0 until spec.cols) {
                    if (index >= labels.size) {
                        break
                    }
                    val label = labels[index++]
                    val left = spec.marginPt + col * cellW
                    val top = spec.marginPt + row * cellH
                    drawLabel(
                        canvas = canvas,
                        label = label,
                        left = left,
                        top = top,
                        cellW = cellW,
                        cellH = cellH
                    )
                }
            }

            document.finishPage(page)
        }

        val bytes = ByteArrayOutputStream()
        document.writeTo(bytes)
        document.close()
        return bytes.toByteArray()
    }

    private fun drawLabel(
        canvas: Canvas,
        label: Label,
        left: Float,
        top: Float,
        cellW: Float,
        cellH: Float
    ) {
        val pad = 6f
        val textPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textAlign = Paint.Align.CENTER
                typeface = Typeface.MONOSPACE
                textSize = (cellH * 0.08f).coerceIn(8f, 14f)
            }
        val textHeight = textPaint.textSize * 1.4f
        val qrArea = (minOf(cellW, cellH - textHeight) - 2 * pad).coerceAtLeast(24f)

        val qrLeft = left + (cellW - qrArea) / 2f
        val qrTop = top + pad
        val src = label.qrBitmap
        val dest =
            android.graphics.RectF(
                qrLeft,
                qrTop,
                qrLeft + qrArea,
                qrTop + qrArea
            )
        canvas.drawBitmap(src, null, dest, null)

        val textY = qrTop + qrArea + textHeight
        canvas.drawText(
            label.permanentId,
            left + cellW / 2f,
            textY.coerceAtMost(top + cellH - pad),
            textPaint
        )
    }
}
