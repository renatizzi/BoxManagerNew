package com.example.boxmanagernew.ui.common

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.example.boxmanagernew.R

object ThemeManager {

    private const val PREFS =
        "boxmanager_theme"

    private const val KEY_TOPBAR_BG =
        "topbar_bg"

    private const val KEY_TOPBAR_TITLE =
        "topbar_title"

    private const val KEY_TOPBAR_SUBTITLE =
        "topbar_subtitle"

    private const val KEY_BOTTOMNAV_BG =
        "bottomnav_bg"

    private const val KEY_BOTTOMNAV_ACTIVE =
        "bottomnav_active"

    private const val KEY_BOTTOMNAV_INACTIVE =
        "bottomnav_inactive"

    fun initializeDefaults(
        context: Context
    ) {

        val prefs =
            context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )

        if (!prefs.contains(KEY_TOPBAR_BG)) {

            prefs.edit()
                .putInt(
                    KEY_TOPBAR_BG,
                    getDefaultTopBarBackground(
                        context
                    )
                )
                .putInt(
                    KEY_TOPBAR_TITLE,
                    getDefaultTopBarTitle(
                        context
                    )
                )
                .putInt(
                    KEY_TOPBAR_SUBTITLE,
                    getDefaultTopBarSubtitle(
                        context
                    )
                )
                .putInt(
                    KEY_BOTTOMNAV_BG,
                    getDefaultBottomNavBackground(
                        context
                    )
                )
                .putInt(
                    KEY_BOTTOMNAV_ACTIVE,
                    getDefaultBottomNavActive(
                        context
                    )
                )
                .putInt(
                    KEY_BOTTOMNAV_INACTIVE,
                    getDefaultBottomNavInactive(
                        context
                    )
                )
                .apply()
        }
    }

    @ColorInt
    fun getTopBarBackground(
        context: Context
    ): Int {

        return getPrefs(context)
            .getInt(
                KEY_TOPBAR_BG,
                getDefaultTopBarBackground(
                    context
                )
            )
    }

    @ColorInt
    fun getTopBarTitle(
        context: Context
    ): Int {

        return getPrefs(context)
            .getInt(
                KEY_TOPBAR_TITLE,
                getDefaultTopBarTitle(
                    context
                )
            )
    }

    @ColorInt
    fun getTopBarSubtitle(
        context: Context
    ): Int {

        return getPrefs(context)
            .getInt(
                KEY_TOPBAR_SUBTITLE,
                getDefaultTopBarSubtitle(
                    context
                )
            )
    }

    @ColorInt
    fun getBottomNavBackground(
        context: Context
    ): Int {

        return getPrefs(context)
            .getInt(
                KEY_BOTTOMNAV_BG,
                getDefaultBottomNavBackground(
                    context
                )
            )
    }

    @ColorInt
    fun getBottomNavActive(
        context: Context
    ): Int {

        return getPrefs(context)
            .getInt(
                KEY_BOTTOMNAV_ACTIVE,
                getDefaultBottomNavActive(
                    context
                )
            )
    }

    @ColorInt
    fun getBottomNavInactive(
        context: Context
    ): Int {

        return getPrefs(context)
            .getInt(
                KEY_BOTTOMNAV_INACTIVE,
                getDefaultBottomNavInactive(
                    context
                )
            )
    }

    private fun getPrefs(
        context: Context
    ) =
        context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    @ColorInt
    private fun getDefaultTopBarBackground(
        context: Context
    ): Int {

        return ContextCompat.getColor(
            context,
            R.color.primary_gradient_start
        )
    }

    @ColorInt
    private fun getDefaultTopBarTitle(
        context: Context
    ): Int {

        return ContextCompat.getColor(
            context,
            android.R.color.white
        )
    }

    @ColorInt
    private fun getDefaultTopBarSubtitle(
        context: Context
    ): Int {

        return ContextCompat.getColor(
            context,
            android.R.color.white
        )
    }

    @ColorInt
    private fun getDefaultBottomNavBackground(
        context: Context
    ): Int {

        return ContextCompat.getColor(
            context,
            R.color.card_background
        )
    }

    @ColorInt
    private fun getDefaultBottomNavActive(
        context: Context
    ): Int {

        return ContextCompat.getColor(
            context,
            R.color.primary_button
        )
    }

    @ColorInt
    private fun getDefaultBottomNavInactive(
        context: Context
    ): Int {

        return ContextCompat.getColor(
            context,
            R.color.text_secondary
        )
    }
}