package com.example.boxmanagernew.viewoutput.print

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.boxmanagernew.R
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot
import com.example.boxmanagernew.viewoutput.model.NameListStyle
import com.example.boxmanagernew.viewoutput.model.ViewPrintHeader
import java.io.ByteArrayOutputStream

data class ViewPrintResult(
    val bytes: ByteArray,
    val pageCount: Int
)

object ViewPrintPdf {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 48f
    private const val ICON = 14f

    fun toBytes(
        context: Context,
        snapshot: ContainerViewSnapshot,
        header: ViewPrintHeader
    ): ViewPrintResult {

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.BLACK
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 15f
            color = android.graphics.Color.BLACK
        }
        val countPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.BLACK
        }
        val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.BLACK
        }
        val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 11f
            color = android.graphics.Color.BLACK
        }
        val objectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 14f
            color = android.graphics.Color.BLACK
        }
        val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 1f
        }

        val placeIcon = bitmap(context, R.drawable.ic_place)
        val objectIcon = bitmap(context, R.drawable.ic_object_view)
        val categoryIcons = mutableMapOf<Int, Bitmap?>()

        val document = PdfDocument()
        var pageNumber = 1
        var page = startPage(document, pageNumber)
        var canvas = page.canvas
        var y = MARGIN + titlePaint.textSize

        fun newPage() {
            document.finishPage(page)
            pageNumber++
            page = startPage(document, pageNumber)
            canvas = page.canvas
            y = MARGIN + titlePaint.textSize
        }

        fun ensureSpace(needed: Float) {
            if (y + needed > PAGE_HEIGHT - MARGIN) {
                newPage()
            }
        }

        fun drawWrapped(
            text: String,
            paint: Paint,
            startX: Float
        ) {
            wrap(text, paint, PAGE_WIDTH - MARGIN - startX).forEach { line ->
                ensureSpace(paint.textSize + 4f)
                canvas.drawText(line, startX, y, paint)
                y += paint.textSize + 4f
            }
        }

        fun drawIconLine(
            firstIcon: Bitmap?,
            firstText: String,
            secondIcon: Bitmap?,
            secondText: String,
            paint: Paint
        ) {
            ensureSpace(paint.textSize + 6f)
            var x = MARGIN
            if (firstIcon != null) {
                val top = y - ICON + 2f
                canvas.drawBitmap(
                    firstIcon,
                    null,
                    RectF(x, top, x + ICON, top + ICON),
                    null
                )
                x += ICON + 4f
            }
            val gap = " - "
            val firstWidth = paint.measureText(firstText)
            canvas.drawText(firstText, x, y, paint)
            x += firstWidth
            canvas.drawText(gap, x, y, paint)
            x += paint.measureText(gap)
            if (secondIcon != null) {
                val top = y - ICON + 2f
                canvas.drawBitmap(
                    secondIcon,
                    null,
                    RectF(x, top, x + ICON, top + ICON),
                    null
                )
                x += ICON + 4f
            }
            val restWidth = PAGE_WIDTH - MARGIN - x
            wrap(secondText, paint, restWidth).forEachIndexed { index, line ->
                if (index == 0) {
                    canvas.drawText(line, x, y, paint)
                    y += paint.textSize + 4f
                } else {
                    ensureSpace(paint.textSize + 4f)
                    canvas.drawText(line, MARGIN + ICON + 4f, y, paint)
                    y += paint.textSize + 4f
                }
            }
        }

        drawWrapped(header.title, titlePaint, MARGIN)
        y += 6f
        drawWrapped(header.filterLine, linePaint, MARGIN)
        y += 4f
        drawWrapped(header.countLine, countPaint, MARGIN)
        y += 10f
        ensureSpace(12f)
        canvas.drawLine(
            MARGIN,
            y,
            PAGE_WIDTH - MARGIN,
            y,
            rulePaint
        )
        y += 14f

        if (header.nameListStyle == NameListStyle.CATEGORY_GROUPS ||
            header.nameListStyle == NameListStyle.PLACE_GROUPS
        ) {
            snapshot.boxes.forEach { group ->
                val headerIcon = when (header.nameListStyle) {
                    NameListStyle.PLACE_GROUPS -> placeIcon
                    NameListStyle.CATEGORY_GROUPS ->
                        categoryIcons.getOrPut(group.categoryIconRes) {
                            if (group.categoryIconRes == 0) {
                                null
                            } else {
                                bitmap(context, group.categoryIconRes)
                            }
                        }
                    else -> null
                }
                ensureSpace(boxPaint.textSize + 10f)
                val top = y - ICON + 2f
                var textX = MARGIN
                if (headerIcon != null) {
                    canvas.drawBitmap(
                        headerIcon,
                        null,
                        RectF(MARGIN, top, MARGIN + ICON, top + ICON),
                        null
                    )
                    textX = MARGIN + ICON + 4f
                }
                drawWrapped(group.name, boxPaint, textX)
                y += 4f
                group.objects.forEach { boxLine ->
                    ensureSpace(objectPaint.textSize + 8f)
                    drawWrapped(
                        boxLine.name,
                        objectPaint,
                        MARGIN + ICON + 4f
                    )
                    y += 4f
                }
                y += 8f
            }
        } else if (header.nameListStyle != NameListStyle.NESTED) {
            snapshot.boxes.forEach { row ->
                val icon = when (header.nameListStyle) {
                    NameListStyle.PLACE_ICON -> placeIcon
                    NameListStyle.CATEGORY_ICON ->
                        categoryIcons.getOrPut(row.categoryIconRes) {
                            if (row.categoryIconRes == 0) {
                                null
                            } else {
                                bitmap(context, row.categoryIconRes)
                            }
                        }
                    else -> null
                }
                ensureSpace(boxPaint.textSize + 10f)
                val top = y - ICON + 2f
                var textX = MARGIN
                if (icon != null) {
                    canvas.drawBitmap(
                        icon,
                        null,
                        RectF(MARGIN, top, MARGIN + ICON, top + ICON),
                        null
                    )
                    textX = MARGIN + ICON + 4f
                }
                drawWrapped(row.name, boxPaint, textX)
                y += 8f
            }
        } else {
        snapshot.boxes.forEach { box ->
            ensureSpace(boxPaint.textSize + metaPaint.textSize + 24f)
            drawWrapped(box.name, boxPaint, MARGIN)
            val categoryIcon = categoryIcons.getOrPut(box.categoryIconRes) {
                if (box.categoryIconRes == 0) {
                    null
                } else {
                    bitmap(context, box.categoryIconRes)
                }
            }
            drawIconLine(
                categoryIcon,
                box.category,
                placeIcon,
                box.position,
                metaPaint
            )
            y += 8f
            box.objects.forEach { obj ->
                val quantity = "Q.${obj.quantity}"
                val body = if (obj.description.isBlank()) {
                    "${obj.name} - $quantity"
                } else {
                    "${obj.name} - ${obj.description} - $quantity"
                }
                ensureSpace(objectPaint.textSize + 8f)
                val top = y - ICON + 2f
                if (objectIcon != null) {
                    canvas.drawBitmap(
                        objectIcon,
                        null,
                        RectF(MARGIN, top, MARGIN + ICON, top + ICON),
                        null
                    )
                }
                drawWrapped(
                    body,
                    objectPaint,
                    MARGIN + ICON + 4f
                )
                y += 8f
            }
            if (header.showBlockSubtotals) {
                y += 4f
                drawWrapped(
                    ViewOutputConfiguration.countObjects(
                        context,
                        box.objects.size
                    ),
                    countPaint,
                    MARGIN
                )
            }
            y += 6f
        }
        }

        document.finishPage(page)
        val bytes = ByteArrayOutputStream()
        document.writeTo(bytes)
        document.close()
        return ViewPrintResult(
            bytes = bytes.toByteArray(),
            pageCount = pageNumber
        )
    }

    private fun bitmap(
        context: Context,
        resId: Int
    ): Bitmap? {

        val drawable =
            ContextCompat.getDrawable(context, resId)
                ?: return null

        val size = (ICON * 2).toInt().coerceAtLeast(24)
        return drawable.toBitmap(size, size)
    }

    private fun startPage(
        document: PdfDocument,
        number: Int
    ): PdfDocument.Page {
        val info = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH,
            PAGE_HEIGHT,
            number
        ).create()
        val page = document.startPage(info)
        page.canvas.drawColor(android.graphics.Color.WHITE)
        return page
    }

    private fun wrap(
        text: String,
        paint: Paint,
        maxWidth: Float
    ): List<String> {
        if (text.isEmpty()) {
            return listOf("")
        }
        if (maxWidth <= 0f) {
            return listOf(text)
        }
        val words = text.split(' ')
        val lines = mutableListOf<String>()
        var current = StringBuilder()
        words.forEach { word ->
            val candidate =
                if (current.isEmpty()) word
                else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current = StringBuilder(candidate)
            } else {
                if (current.isNotEmpty()) {
                    lines.add(current.toString())
                }
                current = StringBuilder(word)
            }
        }
        if (current.isNotEmpty()) {
            lines.add(current.toString())
        }
        return lines
    }
}
