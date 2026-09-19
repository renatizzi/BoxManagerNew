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
 * PDF batch da bitmap QR + nome contenitore + permanentId (REQUISITI_QR_AVANZATO T2).
 * B-QR-BATCH-BOX-NAME: nome sempre in grassetto sopra al codice.
 * B-QR-BATCH-CODE-FONT: permanentId più piccolo (demarcazione / taglio).
 */
object QrBatchPdf {

    data class Label(
        val permanentId: String,
        val boxName: String,
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
        val namePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = (cellH * 0.075f).coerceIn(9f, 13f)
            }
        val codePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textAlign = Paint.Align.CENTER
                typeface = Typeface.MONOSPACE
                // Più piccolo del nome: meno sovrapposizione / linea di taglio più chiara.
                textSize = (cellH * 0.045f).coerceIn(6f, 9f)
            }

        val nameLine = namePaint.textSize * 1.25f
        val codeLine = codePaint.textSize * 1.35f
        val qrArea =
            (minOf(cellW, cellH - nameLine - codeLine) - 2 * pad)
                .coerceAtLeast(20f)

        val nameY = top + pad + namePaint.textSize
        val displayName =
            label.boxName.trim().ifBlank { label.permanentId }
        canvas.drawText(
            ellipsize(displayName, namePaint, cellW - 2 * pad),
            left + cellW / 2f,
            nameY,
            namePaint
        )

        val qrLeft = left + (cellW - qrArea) / 2f
        val qrTop = nameY + pad
        val src = label.qrBitmap
        val dest =
            android.graphics.RectF(
                qrLeft,
                qrTop,
                qrLeft + qrArea,
                qrTop + qrArea
            )
        canvas.drawBitmap(src, null, dest, null)

        val codeY =
            (qrTop + qrArea + codeLine)
                .coerceAtMost(top + cellH - pad)
        canvas.drawText(
            label.permanentId,
            left + cellW / 2f,
            codeY,
            codePaint
        )
    }

    private fun ellipsize(
        text: String,
        paint: Paint,
        maxWidth: Float
    ): String {
        if (paint.measureText(text) <= maxWidth) {
            return text
        }
        val ellipsis = "…"
        var end = text.length
        while (end > 0) {
            val candidate = text.substring(0, end) + ellipsis
            if (paint.measureText(candidate) <= maxWidth) {
                return candidate
            }
            end--
        }
        return ellipsis
    }
}
