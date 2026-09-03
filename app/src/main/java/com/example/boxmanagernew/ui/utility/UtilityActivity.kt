package com.example.boxmanagernew.ui.utility

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.backup.BackupActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.family.FamilyCatalogActivity
import com.example.boxmanagernew.ui.importdata.ImportActivity
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import com.example.boxmanagernew.ui.qr.QRActivity
import com.example.boxmanagernew.ui.restore.RestoreActivity
import com.google.android.material.card.MaterialCardView

class UtilityActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_utility)

        setupAppShell()

        setupPageHeader(
            title = getString(R.string.page_utility_title),
            subtitle = getString(R.string.page_utility_subtitle)
        )

        setupBottomNav()

        findViewById<MaterialCardView>(
            R.id.btnBackup
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    BackupActivity::class.java
                )
            )
        }

        findViewById<MaterialCardView>(
            R.id.btnRestore
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RestoreActivity::class.java
                )
            )
        }

        findViewById<MaterialCardView>(
            R.id.btnImport
        ).setOnClickListener {

            ArchivioCompletoNav.start(
                this,
                PremiumFeature.IMPORT,
                Intent(
                    this,
                    ImportActivity::class.java
                )
            )
        }

        findViewById<MaterialCardView>(
            R.id.btnQr
        ).setOnClickListener {

            ArchivioCompletoNav.start(
                this,
                PremiumFeature.QR_SCAN,
                Intent(
                    this,
                    QRActivity::class.java
                )
            )
        }

        val familyRow = findViewById<View>(R.id.rowFamilyCatalog)
        val familyButton =
            findViewById<MaterialCardView>(R.id.btnFamilyCatalog)
        if (BuildConfig.FAMILY_BETA) {
            familyRow.visibility = View.VISIBLE
            familyButton.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        FamilyCatalogActivity::class.java
                    )
                )
            }
        } else {
            familyRow.visibility = View.GONE
        }
    }
}
