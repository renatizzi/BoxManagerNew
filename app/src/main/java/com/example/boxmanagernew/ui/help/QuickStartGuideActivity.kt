package com.example.boxmanagernew.ui.help

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.help.QuickStartGuideCopy
import com.example.boxmanagernew.ui.common.BaseActivity
import com.google.android.material.card.MaterialCardView

class QuickStartGuideActivity : BaseActivity() {

    companion object {
        const val EXTRA_NAV_TAB = "nav_tab"
    }

    private lateinit var chipConfig: TextView
    private lateinit var chipCensus: TextView
    private lateinit var chipUsage: TextView
    private lateinit var sectionsContainer: LinearLayout
    private lateinit var guideScroll: ScrollView

    private var highlightedPhase =
        QuickStartGuideCopy.Phase.CONFIG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_start_guide)

        setupAppShell()
        setupBottomNav()

        setupPageHeader(
            title = QuickStartGuideCopy.PAGE_TITLE,
            subtitle = QuickStartGuideCopy.PAGE_SUBTITLE
        )

        chipConfig = findViewById(R.id.chipConfig)
        chipCensus = findViewById(R.id.chipCensus)
        chipUsage = findViewById(R.id.chipUsage)
        sectionsContainer = findViewById(R.id.sectionsContainer)
        guideScroll = findViewById(R.id.guideScroll)

        bindWorkflow()
        bindSections()
        bindScrollHighlight()
    }

    private fun bindWorkflow() {
        findViewById<TextView>(R.id.textWorkflowTitle).text =
            QuickStartGuideCopy.WORKFLOW_TITLE

        refreshChipHighlight()

        findViewById<TextView>(R.id.textFooterNote).text =
            QuickStartGuideCopy.FOOTER_NOTE
    }

    private fun refreshChipHighlight() {
        stylePhaseChip(
            chipConfig,
            QuickStartGuideCopy.Phase.CONFIG
        )
        stylePhaseChip(
            chipCensus,
            QuickStartGuideCopy.Phase.CENSUS
        )
        stylePhaseChip(
            chipUsage,
            QuickStartGuideCopy.Phase.USAGE
        )
    }

    private fun stylePhaseChip(
        view: TextView,
        phase: QuickStartGuideCopy.Phase
    ) {
        view.text = phase.numberedLabel()
        val active = phase == highlightedPhase
        val color =
            ContextCompat.getColor(this, phase.colorRes)
        val density = resources.displayMetrics.density
        val background =
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f * density
                setColor(color)
                if (active) {
                    setStroke(
                        (3 * density).toInt(),
                        Color.WHITE
                    )
                }
            }
        view.background = background
        view.alpha = if (active) 1f else 0.45f
    }

    private fun bindSections() {
        QuickStartGuideCopy.sectionsFor(BuildConfig.FAMILY_BETA)
            .forEach { section ->
                sectionsContainer.addView(createSectionCard(section))
            }
    }

    private fun bindScrollHighlight() {
        guideScroll.setOnScrollChangeListener { _, _, _, _, _ ->
            updateHighlightedPhase()
        }
        guideScroll.post {
            updateHighlightedPhase()
        }
    }

    private fun updateHighlightedPhase() {
        val threshold =
            (12 * resources.displayMetrics.density).toInt()
        val scrollLocation = IntArray(2)
        guideScroll.getLocationOnScreen(scrollLocation)
        val viewportTop = scrollLocation[1]

        var phase = QuickStartGuideCopy.Phase.CONFIG
        for (index in 0 until sectionsContainer.childCount) {
            val child = sectionsContainer.getChildAt(index)
            val tagged =
                child.tag as? QuickStartGuideCopy.Phase
                    ?: continue
            val childLocation = IntArray(2)
            child.getLocationOnScreen(childLocation)
            if (childLocation[1] <= viewportTop + threshold) {
                phase = tagged
            }
        }

        if (phase != highlightedPhase) {
            highlightedPhase = phase
            refreshChipHighlight()
        }
    }

    private fun createSectionCard(
        section: QuickStartGuideCopy.Section
    ): View {
        val card =
            layoutInflater.inflate(
                R.layout.item_quick_guide_section,
                sectionsContainer,
                false
            ) as MaterialCardView

        card.tag = section.phase

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
