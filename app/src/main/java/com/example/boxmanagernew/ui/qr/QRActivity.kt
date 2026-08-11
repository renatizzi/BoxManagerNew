package com.example.boxmanagernew.ui.qr

import android.os.Bundle
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class QRActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qr)

        setupEdgeToEdge()
        setupTopBar()

        setupPageHeader(
            title = "QR Scanner",
            subtitle = ""
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_UTILITY
        )
    }
}