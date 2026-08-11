package com.example.boxmanagernew.ui.importdata

import android.os.Bundle
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class ImportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_import)

        setupEdgeToEdge()
        setupTopBar()

        setupPageHeader(
            title = "Importa Dati",
            subtitle = ""
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_UTILITY
        )
    }
}