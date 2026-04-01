package com.example.boxmanagernew.ui.common

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.dashboard.DashboardActivity
import com.example.boxmanagernew.ui.settings.SettingsActivity

class BottomNavManager {

    companion object {

        const val TAB_DASHBOARD = 0
        const val TAB_BOXES = 1
        const val TAB_CATEGORIES = 2
        const val TAB_SETTINGS = 3

        fun setup(activity: Activity, activeTab: Int) {

            val navDashboard = activity.findViewById<TextView>(R.id.navDashboard)
            val navBoxes = activity.findViewById<TextView>(R.id.navBoxes)
            val navCategories = activity.findViewById<TextView>(R.id.navCategories)
            val navSettings = activity.findViewById<TextView>(R.id.navSettings)

            val tabs = listOf(navDashboard, navBoxes, navCategories, navSettings)

            tabs.forEach {
                it.setTextColor(Color.parseColor("#888888"))
                it.setTypeface(null, Typeface.NORMAL)
            }

            val active = when (activeTab) {
                TAB_DASHBOARD -> navDashboard
                TAB_BOXES -> navBoxes
                TAB_CATEGORIES -> navCategories
                TAB_SETTINGS -> navSettings
                else -> navBoxes
            }

            active.setTextColor(Color.parseColor("#2196F3"))
            active.setTypeface(null, Typeface.BOLD)

            navDashboard.setOnClickListener {
                if (activeTab != TAB_DASHBOARD) {
                    activity.startActivity(Intent(activity, DashboardActivity::class.java))
                    activity.finish()
                }
            }

            navBoxes.setOnClickListener {
                if (activeTab != TAB_BOXES) {
                    activity.startActivity(Intent(activity, MainActivity::class.java))
                    activity.finish()
                }
            }

            navCategories.setOnClickListener {
                if (activeTab != TAB_CATEGORIES) {
                    activity.startActivity(Intent(activity, CategoriesActivity::class.java))
                    activity.finish()
                }
            }

            navSettings.setOnClickListener {
                if (activeTab != TAB_SETTINGS) {
                    activity.startActivity(Intent(activity, SettingsActivity::class.java))
                    activity.finish()
                }
            }
        }
    }
}