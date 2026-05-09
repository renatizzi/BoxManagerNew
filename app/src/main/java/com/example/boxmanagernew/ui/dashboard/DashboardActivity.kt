package com.example.boxmanagernew.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.TopBarUtils

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_dashboard)

        val root = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        TopBarUtils.bindTitle(
            findViewById(R.id.textGlobalTitle)
        )

        TopBarUtils.bindSubtitle(
            this,
            findViewById(R.id.textGlobalSubtitle)
        )

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