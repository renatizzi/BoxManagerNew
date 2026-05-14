package com.example.boxmanagernew.ui.common

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
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

    fun setup(
        activity: Activity,
        currentTab: Int
    ) {

        val navDashboard =
            activity.findViewById<View>(
                R.id.navDashboard
            )

        val navBoxes =
            activity.findViewById<View>(
                R.id.navBoxes
            )

        val navCategories =
            activity.findViewById<View>(
                R.id.navCategories
            )

        val navUtility =
            activity.findViewById<View>(
                R.id.navUtility
            )

        val navSettings =
            activity.findViewById<View>(
                R.id.navSettings
            )

        navDashboard?.setOnClickListener {

            if (currentTab == TAB_DASHBOARD) {
                return@setOnClickListener
            }

            openActivity(
                activity,
                DashboardActivity::class.java
            )
        }

        navBoxes?.setOnClickListener {

            if (currentTab == TAB_BOXES) {

                if (activity !is MainActivity) {

                    openActivity(
                        activity,
                        MainActivity::class.java
                    )
                }

                return@setOnClickListener
            }

            openActivity(
                activity,
                MainActivity::class.java
            )
        }

        navCategories?.setOnClickListener {

            if (currentTab == TAB_CATEGORIES) {
                return@setOnClickListener
            }

            openActivity(
                activity,
                CategoriesActivity::class.java
            )
        }

        navUtility?.setOnClickListener {

            if (currentTab == TAB_UTILITY) {
                return@setOnClickListener
            }

            openActivity(
                activity,
                UtilityActivity::class.java
            )
        }

        navSettings?.setOnClickListener {

            if (currentTab == TAB_SETTINGS) {
                return@setOnClickListener
            }

            openActivity(
                activity,
                SettingsActivity::class.java
            )
        }

        updateSelection(
            activity,
            currentTab
        )
    }

    private fun openActivity(
        activity: Activity,
        target: Class<*>
    ) {

        val intent =
            Intent(activity, target).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        activity.startActivity(intent)
    }

    private fun updateSelection(
        activity: Activity,
        currentTab: Int
    ) {

        val activeColor =
            resolveThemeColor(
                activity,
                R.attr.bottomNavActiveColor
            )

        val inactiveColor =
            resolveThemeColor(
                activity,
                R.attr.bottomNavInactiveColor
            )

        val tabs = listOf(
            activity.findViewById<TextView>(
                R.id.navDashboard
            ),
            activity.findViewById<TextView>(
                R.id.navBoxes
            ),
            activity.findViewById<TextView>(
                R.id.navCategories
            ),
            activity.findViewById<TextView>(
                R.id.navUtility
            ),
            activity.findViewById<TextView>(
                R.id.navSettings
            )
        )

        tabs.forEachIndexed { index, view ->

            view ?: return@forEachIndexed

            val selected =
                index == currentTab

            view.setTextColor(
                if (selected) {
                    activeColor
                } else {
                    inactiveColor
                }
            )

            view.setTypeface(
                null,
                if (selected) {
                    Typeface.BOLD
                } else {
                    Typeface.NORMAL
                }
            )

            view.alpha = 1f
        }
    }

    private fun resolveThemeColor(
        activity: Activity,
        attr: Int
    ): Int {

        val typedValue =
            TypedValue()

        activity.theme.resolveAttribute(
            attr,
            typedValue,
            true
        )

        return if (typedValue.resourceId != 0) {

            ContextCompat.getColor(
                activity,
                typedValue.resourceId
            )

        } else {

            typedValue.data
        }
    }
}