package com.example.boxmanagernew.ui.premium

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.premium.ArchivioCompletoAccess
import com.example.boxmanagernew.domain.premium.ArchivioCompletoCopy
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.common.BaseActivity

class ArchivioCompletoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_archivio_completo)

        setupAppShell()
        setupBottomNav()

        val feature =
            parseFeature(
                intent.getStringExtra(EXTRA_FEATURE)
            )

        if (feature == null) {
            finish()
            return
        }

        setupPageHeader(
            ArchivioCompletoCopy.featureTitle(feature),
            ArchivioCompletoCopy.PAGE_SUBTITLE
        )

        val access =
            ArchivioCompletoAccess(this)

        val remaining =
            access.remaining(feature)

        val canTry =
            access.canTrial(feature)

        findViewById<TextView>(R.id.textPreviewBody).text =
            PremiumCopyFormatter.formatPitch(
                ArchivioCompletoCopy.pitch(feature),
                includeCallToAction = canTry
            )

        findViewById<TextView>(R.id.textTrialLine).text =
            ArchivioCompletoCopy.trialLine(feature, remaining)

        val packageHint =
            findViewById<TextView>(R.id.textLockedFooter)

        packageHint.text =
            ArchivioCompletoCopy.PACKAGE_BUY_HINT

        packageHint.visibility =
            if (canTry) View.GONE else View.VISIBLE

        val buttonPrimary =
            findViewById<Button>(R.id.buttonTry)

        buttonPrimary.text =
            ArchivioCompletoCopy.primaryButton(canTry)

        buttonPrimary.visibility = View.VISIBLE

        buttonPrimary.setOnClickListener {
            if (canTry) {
                access.markPreviewSeen(feature)
                val proceed = ArchivioCompletoNav.pending
                ArchivioCompletoNav.pending = null
                finish()
                proceed?.invoke()
            } else {
                Toast.makeText(
                    this,
                    ArchivioCompletoCopy.BUY_NOT_READY,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        findViewById<Button>(R.id.buttonDebugUnlock).visibility =
            View.GONE

        findViewById<Button>(R.id.buttonClose).text =
            ArchivioCompletoCopy.BUTTON_CLOSE

        findViewById<Button>(R.id.buttonClose).setOnClickListener {
            ArchivioCompletoNav.pending = null
            finish()
        }

        onBackPressedDispatcher.addCallback(this) {
            ArchivioCompletoNav.pending = null
            finish()
        }
    }

    private fun parseFeature(raw: String?): PremiumFeature? {
        return PremiumFeature.entries.find { it.name == raw }
    }

    companion object {
        const val EXTRA_FEATURE = "feature"
        const val EXTRA_NAV_TAB = "nav_tab"
    }
}
