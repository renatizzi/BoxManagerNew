package com.example.boxmanagernew.ui.dashboard

import android.os.Bundle
import android.widget.TextView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        setupEdgeToEdge()

        setupTopBar()

        findViewById<TextView>(R.id.textTitle).text =
            "Dashboard"

        findViewById<TextView>(R.id.textSubtitle).text =
            "Panoramica Archivio"

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_DASHBOARD
        )
    }
}