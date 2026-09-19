package com.example.boxmanagernew.ui.utility

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.backup.BackupActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.exportdata.ExportDataActivity
import com.example.boxmanagernew.ui.family.FamilyCatalogActivity
import com.example.boxmanagernew.ui.importdata.ImportActivity
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import com.example.boxmanagernew.ui.qr.QRActivity
import com.example.boxmanagernew.ui.restore.RestoreActivity
import com.example.boxmanagernew.ui.trash.TrashActivity
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            R.id.btnExport
        ).setOnClickListener {

            ArchivioCompletoNav.start(
                this,
                PremiumFeature.EXPORT,
                Intent(
                    this,
                    ExportDataActivity::class.java
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

        findViewById<MaterialCardView>(
            R.id.btnQrBatch
        ).setOnClickListener {
            openQrBatchPick()
        }

        findViewById<MaterialCardView>(
            R.id.btnTrash
        ).setOnClickListener {
            ArchivioCompletoNav.start(
                this,
                PremiumFeature.TRASH,
                Intent(
                    this,
                    TrashActivity::class.java
                )
            )
        }

        val familyButton =
            findViewById<MaterialCardView>(R.id.btnFamilyCatalog)
        val spacerTrash =
            findViewById<View>(R.id.spacerTrashRow)
        if (BuildConfig.FAMILY_BETA) {
            familyButton.visibility = View.VISIBLE
            spacerTrash.visibility = View.GONE
            familyButton.setOnClickListener {
                ArchivioCompletoNav.start(
                    this,
                    PremiumFeature.ARCHIVE_SHARE,
                    Intent(
                        this,
                        FamilyCatalogActivity::class.java
                    )
                )
            }
        } else {
            familyButton.visibility = View.GONE
            spacerTrash.visibility = View.VISIBLE
        }
    }

    private fun openQrBatchPick() {
        lifecycleScope.launch {
            val count =
                withContext(Dispatchers.IO) {
                    DatabaseProvider
                        .getDatabase(applicationContext)
                        .boxDao()
                        .getAllSync()
                        .size
                }
            if (count <= 0) {
                Toast.makeText(
                    this@UtilityActivity,
                    getString(R.string.msg_qr_batch_no_boxes),
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }
            ArchivioCompletoNav.start(
                this@UtilityActivity,
                PremiumFeature.QR_LABEL,
                MainActivity.intentQrBatchPick(this@UtilityActivity)
            )
        }
    }
}
