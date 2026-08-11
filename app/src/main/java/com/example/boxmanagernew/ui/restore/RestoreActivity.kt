package com.example.boxmanagernew.ui.restore

import android.os.Bundle
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class RestoreActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_restore)

        setupEdgeToEdge()
        setupTopBar()

        setupPageHeader(
            title = "Ripristino Archivio",
            subtitle = ""
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_UTILITY
        )
    }
}