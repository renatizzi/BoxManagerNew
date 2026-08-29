package com.example.boxmanagernew.ui.help

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.help.QuickStartGuideCopy
import com.example.boxmanagernew.ui.common.BaseActivity
import com.google.android.material.card.MaterialCardView

class QuickStartGuideActivity : BaseActivity() {

    companion object {
        const val EXTRA_NAV_TAB = "nav_tab"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_start_guide)

        setupAppShell()
        setupBottomNav()

        setupPageHeader(
            title = QuickStartGuideCopy.PAGE_TITLE,
            subtitle = QuickStartGuideCopy.PAGE_SUBTITLE
        )

        bindWorkflow()
        bindSections()
    }

    private fun bindWorkflow() {
        findViewById<TextView>(R.id.textWorkflowTitle).text =
            QuickStartGuideCopy.WORKFLOW_TITLE

        findViewById<TextView>(R.id.textWorkflowLine).text =
            QuickStartGuideCopy.WORKFLOW_LINE

        findViewById<TextView>(R.id.textIntro).text =
            QuickStartGuideCopy.INTRO

        stylePhaseChip(
            findViewById(R.id.chipConfig),
            QuickStartGuideCopy.Phase.CONFIG
        )
        stylePhaseChip(
            findViewById(R.id.chipCensus),
            QuickStartGuideCopy.Phase.CENSUS
        )
        stylePhaseChip(
            findViewById(R.id.chipUsage),
            QuickStartGuideCopy.Phase.USAGE
        )

        findViewById<TextView>(R.id.textFooterNote).text =
            QuickStartGuideCopy.FOOTER_NOTE
    }

    private fun stylePhaseChip(
        view: TextView,
        phase: QuickStartGuideCopy.Phase
    ) {
        view.text = phase.label
        val color =
            ContextCompat.getColor(this, phase.colorRes)
        val background =
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f * resources.displayMetrics.density
                setColor(color)
            }
        view.background = background
    }

    private fun bindSections() {
        val container =
            findViewById<LinearLayout>(R.id.sectionsContainer)

        QuickStartGuideCopy.sections.forEach { section ->
            container.addView(createSectionCard(section))
        }
    }

    private fun createSectionCard(
        section: QuickStartGuideCopy.Section
    ): View {
        val card =
            layoutInflater.inflate(
                R.layout.item_quick_guide_section,
                findViewById(R.id.sectionsContainer),
                false
            ) as MaterialCardView

        val phaseColor =
            ContextCompat.getColor(
                this,
                section.phase.colorRes
            )

        card.findViewById<View>(R.id.phaseStripe)
            .setBackgroundColor(phaseColor)

        card.findViewById<TextView>(R.id.textPhaseLabel).apply {
            text = section.phase.label
            setTextColor(phaseColor)
        }

        card.findViewById<TextView>(R.id.textSectionTitle).text =
            "${section.number}. ${section.title}"

        card.findViewById<TextView>(R.id.textSectionBody).text =
            section.bullets.joinToString("\n") { "• $it" }

        val exampleTitle =
            card.findViewById<TextView>(R.id.textExampleTitle)
        val exampleBody =
            card.findViewById<TextView>(R.id.textSpreadsheetExample)

        if (
            section.spreadsheetExampleTitle != null &&
            section.spreadsheetExample != null
        ) {
            exampleTitle.text = section.spreadsheetExampleTitle
            exampleTitle.visibility = View.VISIBLE
            exampleBody.text = section.spreadsheetExample
            exampleBody.visibility = View.VISIBLE
        } else {
            exampleTitle.visibility = View.GONE
            exampleBody.visibility = View.GONE
        }

        return card
    }
}
