package com.example.boxmanagernew.ui.common

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
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

    private const val KEY_BOTTOMNAV_ACTIVE =
        "bottomnav_active"

    private const val KEY_ACCENT =
        "accent"

    private const val KEY_ACCENT_DARK =
        "accent_dark"

    private const val KEY_CURRENT_PALETTE =
        "current_palette"

    private const val KEY_NIGHT_MODE =
        "night_mode"

    private const val NIGHT_UNSET =
        -1

    private const val NIGHT_NO =
        0

    private const val NIGHT_YES =
        1

    const val PALETTE_ORANGE =
        "palette_orange"

    const val PALETTE_BLUE =
        "palette_blue"

    const val PALETTE_GREEN =
        "palette_green"

    fun initializeDefaults(
        context: Context
    ) {

        val prefs =
            getPrefs(context)

        if (
            !prefs.contains(
                KEY_CURRENT_PALETTE
            )
        ) {

            applyPalette(
                context,
                PALETTE_ORANGE
            )
        }
    }

    fun applyPalette(
        context: Context,
        palette: String
    ) {

        val prefs =
            getPrefs(context)

        val topBar =
            when (palette) {

                PALETTE_BLUE ->
                    0xFF1565C0.toInt()

                PALETTE_GREEN ->
                    0xFF2E7D32.toInt()

                else ->
                    0xFFE67E22.toInt()
            }

        val accent =
            when (palette) {

                PALETTE_BLUE ->
                    0xFF42A5F5.toInt()

                PALETTE_GREEN ->
                    0xFF66BB6A.toInt()

                else ->
                    0xFFF39C12.toInt()
            }

        val accentDark =
            when (palette) {

                PALETTE_BLUE ->
                    0xFF0D47A1.toInt()

                PALETTE_GREEN ->
                    0xFF1B5E20.toInt()

                else ->
                    0xFFA04000.toInt()
            }

        prefs.edit()
            .putString(
                KEY_CURRENT_PALETTE,
                palette
            )
            .putInt(
                KEY_TOPBAR_BG,
                topBar
            )
            .putInt(
                KEY_TOPBAR_TITLE,
                0xFFFFFFFF.toInt()
            )
            .putInt(
                KEY_TOPBAR_SUBTITLE,
                0xFFFFFFFF.toInt()
            )
            .putInt(
                KEY_BOTTOMNAV_ACTIVE,
                accentDark
            )
            .putInt(
                KEY_ACCENT,
                accent
            )
            .putInt(
                KEY_ACCENT_DARK,
                accentDark
            )
            .apply()
    }

    fun getCurrentPalette(
        context: Context
    ): String {

        return getPrefs(context)
            .getString(
                KEY_CURRENT_PALETTE,
                PALETTE_ORANGE
            )
            ?: PALETTE_ORANGE
    }

    fun applyStoredNightMode(
        context: Context
    ) {

        val stored =
            getPrefs(context)
                .getInt(
                    KEY_NIGHT_MODE,
                    NIGHT_UNSET
                )

        if (stored == NIGHT_UNSET) {
            return
        }

        val mode =
            if (stored == NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

        if (
            AppCompatDelegate
                .getDefaultNightMode() != mode
        ) {

            AppCompatDelegate
                .setDefaultNightMode(
                    mode
                )
        }
    }

    fun isNightModeEnabled(
        context: Context
    ): Boolean {

        val stored =
            getPrefs(context)
                .getInt(
                    KEY_NIGHT_MODE,
                    NIGHT_UNSET
                )

        return when (stored) {

            NIGHT_YES ->
                true

            NIGHT_NO ->
                false

            else ->
                (
                        context.resources
                            .configuration.uiMode and
                                Configuration.UI_MODE_NIGHT_MASK
                        ) == Configuration.UI_MODE_NIGHT_YES
        }
    }

    fun setNightMode(
        context: Context,
        night: Boolean
    ) {

        if (
            isNightModeEnabled(context) == night &&
                    AppCompatDelegate.getDefaultNightMode() ==
                    if (night) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
        ) {
            return
        }

        val stored =
            if (night) {
                NIGHT_YES
            } else {
                NIGHT_NO
            }

        val mode =
            if (night) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

        getPrefs(context)
            .edit()
            .putInt(
                KEY_NIGHT_MODE,
                stored
            )
            .apply()

        if (
            AppCompatDelegate
                .getDefaultNightMode() != mode
        ) {

            AppCompatDelegate
                .setDefaultNightMode(
                    mode
                )
        }
    }

    @ColorInt
    fun getAccentColor(
        context: Context
    ): Int =
        getPrefs(context)
            .getInt(
                KEY_ACCENT,
                getDefaultAccent(context)
            )

    @ColorInt
    fun getAccentDarkColor(
        context: Context
    ): Int =
        getPrefs(context)
            .getInt(
                KEY_ACCENT_DARK,
                getDefaultAccentDark(context)
            )

    @ColorInt
    fun getTopBarBackground(
        context: Context
    ): Int =
        getPrefs(context)
            .getInt(
                KEY_TOPBAR_BG,
                getDefaultTopBarBackground(context)
            )

    @ColorInt
    fun getTopBarTitle(
        context: Context
    ): Int =
        getPrefs(context)
            .getInt(
                KEY_TOPBAR_TITLE,
                getDefaultTopBarTitle(context)
            )

    @ColorInt
    fun getTopBarSubtitle(
        context: Context
    ): Int =
        getPrefs(context)
            .getInt(
                KEY_TOPBAR_SUBTITLE,
                getDefaultTopBarSubtitle(context)
            )

    @ColorInt
    fun getBottomNavBackground(
        context: Context
    ): Int =
        getDefaultBottomNavBackground(
            context
        )

    @ColorInt
    fun getBottomNavActive(
        context: Context
    ): Int =
        getPrefs(context)
            .getInt(
                KEY_BOTTOMNAV_ACTIVE,
                getDefaultBottomNavActive(context)
            )

    @ColorInt
    fun getBottomNavInactive(
        context: Context
    ): Int =
        getDefaultBottomNavInactive(
            context
        )

    private fun getPrefs(
        context: Context
    ) =
        context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    @ColorInt
    private fun getDefaultAccent(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            R.color.primary_button_light
        )

    @ColorInt
    private fun getDefaultAccentDark(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            R.color.primary_button
        )

    @ColorInt
    private fun getDefaultTopBarBackground(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            R.color.primary_gradient_start
        )

    @ColorInt
    private fun getDefaultTopBarTitle(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            android.R.color.white
        )

    @ColorInt
    private fun getDefaultTopBarSubtitle(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            android.R.color.white
        )

    @ColorInt
    private fun getDefaultBottomNavBackground(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            R.color.card_background
        )

    @ColorInt
    private fun getDefaultBottomNavActive(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            R.color.primary_button
        )

    @ColorInt
    private fun getDefaultBottomNavInactive(
        context: Context
    ) =
        ContextCompat.getColor(
            context,
            R.color.text_secondary
        )
}