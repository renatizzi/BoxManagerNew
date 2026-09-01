package com.example.boxmanagernew.ui.common

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.boxdetail.BoxDetailActivity
import com.example.boxmanagernew.ui.backup.BackupActivity
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.dashboard.DashboardActivity
import com.example.boxmanagernew.ui.globalsearch.GlobalSearchActivity
import com.example.boxmanagernew.ui.importdata.ImportActivity
import com.example.boxmanagernew.ui.qr.QRActivity
import com.example.boxmanagernew.ui.qr.QrLabelActivity
import com.example.boxmanagernew.ui.restore.RestoreActivity
import com.example.boxmanagernew.ui.search.SearchResultActivity
import com.example.boxmanagernew.ui.premium.ArchivioCompletoActivity
import com.example.boxmanagernew.ui.settings.LocationsActivity
import com.example.boxmanagernew.ui.settings.SettingsActivity
import com.example.boxmanagernew.ui.utility.UtilityActivity
import com.example.boxmanagernew.ui.family.FamilyCatalogActivity

object BottomNavManager {

    const val TAB_DASHBOARD = 0
    const val TAB_BOXES = 1
    const val TAB_CATEGORIES = 2
    const val TAB_UTILITY = 3
    const val TAB_SETTINGS = 4

    /**
     * Tab padre AppShell per ogni schermata con barra inferiore.
     * Le pagine secondarie restano ancorate alla sezione di provenienza.
     */
    fun tabFor(activity: Activity): Int? {
        return when (activity) {
            is DashboardActivity,
            is GlobalSearchActivity ->
                TAB_DASHBOARD

            is MainActivity,
            is BoxDetailActivity,
            is SearchResultActivity ->
                TAB_BOXES

            is CategoriesActivity ->
                TAB_CATEGORIES

            is UtilityActivity,
            is BackupActivity,
            is RestoreActivity,
            is ImportActivity,
            is FamilyCatalogActivity,
            is QRActivity,
            is QrLabelActivity ->
                TAB_UTILITY

            is SettingsActivity,
            is LocationsActivity ->
                TAB_SETTINGS

            is ArchivioCompletoActivity ->
                activity.intent.getIntExtra(
                    ArchivioCompletoActivity.EXTRA_NAV_TAB,
                    TAB_DASHBOARD
                )

            is com.example.boxmanagernew.ui.help.QuickStartGuideActivity ->
                activity.intent.getIntExtra(
                    com.example.boxmanagernew.ui.help.QuickStartGuideActivity.EXTRA_NAV_TAB,
                    TAB_DASHBOARD
                )

            else ->
                null
        }
    }

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

                if (activity !is DashboardActivity) {

                    openActivity(
                        activity,
                        DashboardActivity::class.java
                    )
                }

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

                if (activity !is CategoriesActivity) {

                    openActivity(
                        activity,
                        CategoriesActivity::class.java
                    )
                }

                return@setOnClickListener
            }

            openActivity(
                activity,
                CategoriesActivity::class.java
            )
        }

        navUtility?.setOnClickListener {

            if (currentTab == TAB_UTILITY) {

                if (activity !is UtilityActivity) {

                    openActivity(
                        activity,
                        UtilityActivity::class.java
                    )
                }

                return@setOnClickListener
            }

            openActivity(
                activity,
                UtilityActivity::class.java
            )
        }

        navSettings?.setOnClickListener {

            if (currentTab == TAB_SETTINGS) {

                if (activity !is SettingsActivity) {

                    openActivity(
                        activity,
                        SettingsActivity::class.java
                    )
                }

                return@setOnClickListener
            }

            openActivity(
                activity,
                SettingsActivity::class.java
            )
        }

        applyShellColors(
            activity
        )

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

    private fun applyShellColors(
        activity: Activity
    ) {

        val bottomNav =
            activity.findViewById<LinearLayout>(
                R.id.bottomNav
            )

        bottomNav?.setBackgroundColor(
            ThemeManager.getBottomNavBackground(
                activity
            )
        )
    }

    private fun updateSelection(
        activity: Activity,
        currentTab: Int
    ) {

        val activeColor =
            ThemeManager.getBottomNavActive(
                activity
            )

        val inactiveColor =
            ThemeManager.getBottomNavInactive(
                activity
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
}