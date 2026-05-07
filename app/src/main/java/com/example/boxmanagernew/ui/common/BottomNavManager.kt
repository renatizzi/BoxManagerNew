package com.example.boxmanagernew.ui.common

import android.app.Activity
import android.content.Intent
import android.view.View
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.dashboard.DashboardActivity
import com.example.boxmanagernew.ui.settings.SettingsActivity
import com.example.boxmanagernew.ui.utility.UtilityActivity

object BottomNavManager {

    const val TAB_DASHBOARD = 0
    const val TAB_BOXES = 1
    const val TAB_CATEGORIES = 2
    const val TAB_UTILITY = 3
    const val TAB_SETTINGS = 4

    fun setup(activity: Activity, currentTab: Int) {

        val navDashboard = activity.findViewById<View>(R.id.navDashboard)
        val navBoxes = activity.findViewById<View>(R.id.navBoxes)
        val navCategories = activity.findViewById<View>(R.id.navCategories)
        val navUtility = activity.findViewById<View>(R.id.navUtility)
        val navSettings = activity.findViewById<View>(R.id.navSettings)

        navDashboard?.setOnClickListener {

            if (currentTab == TAB_DASHBOARD) return@setOnClickListener

            val intent = Intent(activity, DashboardActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            activity.startActivity(intent)
        }

        navBoxes?.setOnClickListener {

            if (currentTab == TAB_BOXES) {

                if (activity !is MainActivity) {

                    val intent = Intent(activity, MainActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }

                    activity.startActivity(intent)
                }

                return@setOnClickListener
            }

            val intent = Intent(activity, MainActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            activity.startActivity(intent)
        }

        navCategories?.setOnClickListener {

            if (currentTab == TAB_CATEGORIES) return@setOnClickListener

            val intent = Intent(activity, CategoriesActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            activity.startActivity(intent)
        }

        navUtility?.setOnClickListener {

            if (currentTab == TAB_UTILITY) return@setOnClickListener

            val intent = Intent(activity, UtilityActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            activity.startActivity(intent)
        }

        navSettings?.setOnClickListener {

            if (currentTab == TAB_SETTINGS) return@setOnClickListener

            val intent = Intent(activity, SettingsActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            activity.startActivity(intent)
        }

        updateSelection(activity, currentTab)
    }

    private fun updateSelection(activity: Activity, currentTab: Int) {

        val tabs = listOf(
            activity.findViewById<View>(R.id.navDashboard),
            activity.findViewById<View>(R.id.navBoxes),
            activity.findViewById<View>(R.id.navCategories),
            activity.findViewById<View>(R.id.navUtility),
            activity.findViewById<View>(R.id.navSettings)
        )

        tabs.forEachIndexed { index, view ->
            view?.alpha = if (index == currentTab) 1f else 0.5f
        }
    }
}