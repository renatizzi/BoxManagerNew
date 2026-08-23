package com.example.boxmanagernew

import android.app.Application
import com.example.boxmanagernew.ui.common.ThemeManager

class BoxManagerApplication : Application() {

    override fun onCreate() {

        super.onCreate()

        ThemeManager.applyStoredNightMode(
            this
        )
    }
}
