package com.example.boxmanagernew.ui.utility

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

class UtilityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_utility)

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
            "Utility"

        findViewById<TextView>(R.id.textSubtitle).text =
            "Import / Export Archivio"

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_UTILITY
        )
    }
}