package com.example.boxmanagernew.ui.help

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.ContextCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.help.QuickStartGuideCopy
import java.io.ByteArrayOutputStream

/**
 * PDF a colori della Guida rapida: stessi colori fase e struttura della schermata.
 */
object GuidePrintPdf {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    data class Result(
        val bytes: ByteArray,
        val pageCount: Int
    )

    fun toBytes(
        context: Context,
        includeFamilyBeta: Boolean
    ): Result {
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = ContextCompat.getColor(context, R.color.text_primary)
        }
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f
            color = ContextCompat.getColor(context, R.color.text_primary)
        }
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = ContextCompat.getColor(context, R.color.text_primary)
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 11f
            color = ContextCompat.getColor(context, R.color.text_primary)
        }
        val phasePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = android.graphics.Color.WHITE
        }
        val stripePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val chipBg = Paint(Paint.ANTI_ALIAS_FLAG)

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

        fun ensure(space: Float) {
            if (y + space > PAGE_HEIGHT - MARGIN) {
                newPage()
            }
        }

        fun drawWrapped(
            text: String,
            paint: Paint,
            left: Float = MARGIN,
            maxWidth: Float = PAGE_WIDTH - 2 * MARGIN
        ) {
            val words = text.split(' ')
            var line = ""
            for (word in words) {
                val trial = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(trial) <= maxWidth) {
                    line = trial
                } else {
                    ensure(paint.textSize + 4f)
                    canvas.drawText(line, left, y, paint)
                    y += paint.textSize + 4f
                    line = word
                }
            }
            if (line.isNotEmpty()) {
                ensure(paint.textSize + 4f)
                canvas.drawText(line, left, y, paint)
                y += paint.textSize + 4f
            }
        }

        canvas.drawText(
            QuickStartGuideCopy.pageTitle(context),
            MARGIN,
            y,
            titlePaint
        )
        y += titlePaint.textSize + 6f
        drawWrapped(QuickStartGuideCopy.pageSubtitle(context), subtitlePaint)
        y += 8f

        ensure(18f)
        canvas.drawText(
            QuickStartGuideCopy.workflowTitle(context),
            MARGIN,
            y,
            headingPaint
        )
        y += headingPaint.textSize + 10f

        val chipW = (PAGE_WIDTH - 2 * MARGIN - 16f) / 3f
        var chipX = MARGIN
        QuickStartGuideCopy.Phase.entries.forEach { phase ->
            chipBg.color = ContextCompat.getColor(context, phase.colorRes)
            canvas.drawRoundRect(
                chipX,
                y - 12f,
                chipX + chipW,
                y + 10f,
                8f,
                8f,
                chipBg
            )
            val label = phase.numberedLabel(context)
            val tw = phasePaint.measureText(label)
            canvas.drawText(
                label,
                chipX + (chipW - tw) / 2f,
                y + 3f,
                phasePaint
            )
            chipX += chipW + 8f
        }
        y += 28f

        QuickStartGuideCopy.sectionsFor(context, includeFamilyBeta).forEach { section ->
            ensure(36f)
            val phaseColor =
                ContextCompat.getColor(context, section.phase.colorRes)
            stripePaint.color = phaseColor
            canvas.drawRect(
                MARGIN,
                y - 12f,
                MARGIN + 6f,
                y + 8f,
                stripePaint
            )
            phasePaint.color = phaseColor
            canvas.drawText(
                section.phase.label(context),
                MARGIN + 12f,
                y,
                phasePaint.apply {
                    textSize = 9f
                    color = phaseColor
                }
            )
            y += 14f
            canvas.drawText(
                context.getString(
                    R.string.guide_section_numbered,
                    section.number,
                    section.title
                ),
                MARGIN + 12f,
                y,
                headingPaint
            )
            y += headingPaint.textSize + 6f

            section.bodyIntro?.let {
                drawWrapped(it, bodyPaint, MARGIN + 12f, PAGE_WIDTH - MARGIN - 12f)
                y += 2f
            }
            section.bullets.forEach { bullet ->
                drawWrapped(
                    "• $bullet",
                    bodyPaint,
                    MARGIN + 12f,
                    PAGE_WIDTH - MARGIN - 12f
                )
            }
            section.bodyClosing?.let {
                y += 2f
                drawWrapped(it, bodyPaint, MARGIN + 12f, PAGE_WIDTH - MARGIN - 12f)
            }
            y += 10f
        }

        ensure(24f)
        drawWrapped(
            QuickStartGuideCopy.csvFootnote(context, includeFamilyBeta),
            bodyPaint.apply { textSize = 9.5f },
            MARGIN,
            PAGE_WIDTH - 2 * MARGIN
        )
        y += 8f
        drawWrapped(
            QuickStartGuideCopy.footerNote(context),
            subtitlePaint.apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }
        )

        document.finishPage(page)
        val stream = ByteArrayOutputStream()
        document.writeTo(stream)
        document.close()
        return Result(stream.toByteArray(), pageNumber)
    }

    private fun startPage(
        document: PdfDocument,
        pageNumber: Int
    ): PdfDocument.Page {
        val info =
            PdfDocument.PageInfo.Builder(
                PAGE_WIDTH,
                PAGE_HEIGHT,
                pageNumber
            ).create()
        return document.startPage(info)
    }
}
