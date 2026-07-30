package com.example.boxmanagernew.ui.utility

import android.content.Intent
import android.os.Bundle
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.backup.BackupActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class UtilityActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_utility)

        setupEdgeToEdge()

        setupTopBar()

        setupPageHeader(
            title = "Utility",
            subtitle = "Import / Export Archivio"
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_UTILITY
        )

        findViewById<android.widget.Button>(
            R.id.btnBackup
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    BackupActivity::class.java
                )
            )
        }
    }
}