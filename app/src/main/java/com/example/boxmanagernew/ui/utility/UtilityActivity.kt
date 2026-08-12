package com.example.boxmanagernew.ui.utility

import android.content.Intent
import android.os.Bundle
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.backup.BackupActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.importdata.ImportActivity
import com.example.boxmanagernew.ui.qr.QRActivity
import com.example.boxmanagernew.ui.restore.RestoreActivity
import com.google.android.material.card.MaterialCardView

class UtilityActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_utility)

        setupEdgeToEdge()

        setupTopBar()

        setupPageHeader(
            title = "Utility",
            subtitle = "Strumenti di supporto alla gestione dell'archivio"
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_UTILITY
        )

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

            startActivity(
                Intent(
                    this,
                    ImportActivity::class.java
                )
            )
        }

        findViewById<MaterialCardView>(
            R.id.btnQr
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    QRActivity::class.java
                )
            )
        }
    }
}