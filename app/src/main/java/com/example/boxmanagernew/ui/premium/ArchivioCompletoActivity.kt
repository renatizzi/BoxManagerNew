package com.example.boxmanagernew.ui.premium

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.premium.ArchivioCompletoAccess
import com.example.boxmanagernew.domain.premium.ArchivioCompletoCopy
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.domain.premium.ShareActionResult
import com.example.boxmanagernew.ui.common.BaseActivity

class ArchivioCompletoActivity : BaseActivity() {

    private lateinit var access: ArchivioCompletoAccess

    private val shareLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            proceedIfOpen()
        }

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

        access = ArchivioCompletoAccess(this)

        if (access.isOpen()) {
            proceedIfOpen()
            return
        }

        setupPageHeader(
            ArchivioCompletoCopy.featureTitle(this, feature),
            ArchivioCompletoCopy.pageSubtitle(this)
        )

        findViewById<TextView>(R.id.textPreviewBody).text =
            PremiumCopyFormatter.formatPitch(
                this,
                ArchivioCompletoCopy.pitch(this, feature),
                includeCallToAction = false
            )

        findViewById<TextView>(R.id.textTrialLine).text =
            ArchivioCompletoCopy.packageShareHint(
                this,
                access.trialDays(),
                access.shareBonusDays(),
                access.shareFriendsRequired()
            )

        findViewById<TextView>(R.id.textLockedFooter).text =
            ArchivioCompletoCopy.codeHint(this)

        findViewById<EditText>(R.id.editUnlockCode).hint =
            ArchivioCompletoCopy.unlockCodeHint(this)

        val buttonShare =
            findViewById<Button>(R.id.buttonTry)

        buttonShare.text =
            ArchivioCompletoCopy.buttonShare(this)

        buttonShare.setOnClickListener {
            shareForBonus()
        }

        findViewById<Button>(R.id.buttonDebugUnlock).visibility =
            android.view.View.GONE

        findViewById<Button>(R.id.buttonRedeemCode).text =
            ArchivioCompletoCopy.buttonRedeem(this)

        findViewById<Button>(R.id.buttonRedeemCode).setOnClickListener {
            redeemCode()
        }

        findViewById<Button>(R.id.buttonClose).text =
            ArchivioCompletoCopy.buttonClose(this)

        findViewById<Button>(R.id.buttonClose).setOnClickListener {
            ArchivioCompletoNav.pending = null
            finish()
        }

        onBackPressedDispatcher.addCallback(this) {
            ArchivioCompletoNav.pending = null
            finish()
        }
    }

    private fun shareForBonus() {
        when (access.registerShareAction()) {
            ShareActionResult.GRANTED ->
                Toast.makeText(
                    this,
                    ArchivioCompletoCopy.shareGranted(
                        this,
                        access.shareBonusDays()
                    ),
                    Toast.LENGTH_SHORT
                ).show()

            ShareActionResult.PROGRESS ->
                Toast.makeText(
                    this,
                    ArchivioCompletoCopy.shareProgressLine(
                        this,
                        access.shareProgress(),
                        access.shareFriendsRequired()
                    ),
                    Toast.LENGTH_SHORT
                ).show()

            ShareActionResult.COOLDOWN ->
                Toast.makeText(
                    this,
                    ArchivioCompletoCopy.shareCooldownLine(
                        this,
                        access.cooldownRemainingMs(),
                        access.shareBonusDays()
                    ),
                    Toast.LENGTH_LONG
                ).show()
        }

        val playUrl =
            "https://play.google.com/store/apps/details?id=" +
                BuildConfig.APPLICATION_ID

        val send =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    ArchivioCompletoCopy.shareMessage(this@ArchivioCompletoActivity, playUrl)
                )
            }

        shareLauncher.launch(
            Intent.createChooser(
                send,
                ArchivioCompletoCopy.buttonShare(this)
            )
        )
    }

    private fun redeemCode() {
        val raw =
            findViewById<EditText>(R.id.editUnlockCode)
                .text
                .toString()

        if (!access.redeemCode(raw)) {
            Toast.makeText(
                this,
                ArchivioCompletoCopy.codeKo(this),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Toast.makeText(
            this,
            ArchivioCompletoCopy.codeOk(this),
            Toast.LENGTH_SHORT
        ).show()

        proceedIfOpen()
    }

    private fun proceedIfOpen() {
        if (!ArchivioCompletoAccess(this).isOpen()) {
            return
        }
        val proceed = ArchivioCompletoNav.pending
        ArchivioCompletoNav.pending = null
        finish()
        proceed?.invoke()
    }

    private fun parseFeature(raw: String?): PremiumFeature? {
        return PremiumFeature.entries.find { it.name == raw }
    }

    companion object {
        const val EXTRA_FEATURE = "feature"
        const val EXTRA_NAV_TAB = "nav_tab"
    }
}
