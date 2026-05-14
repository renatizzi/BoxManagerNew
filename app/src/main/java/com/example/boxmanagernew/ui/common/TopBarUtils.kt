package com.example.boxmanagernew.ui.common

import android.content.Context
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.example.boxmanagernew.ui.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TopBarUtils {

    private const val DEFAULT_USERNAME =
        "Utente"

    private const val APP_TITLE =
        "BoxManager"

    private const val DATE_PATTERN =
        "dd/MM/yyyy HH:mm"

    fun bindTopBar(
        context: Context,
        titleView: TextView,
        subtitleView: TextView
    ) {

        bindTitle(
            context,
            titleView
        )

        bindSubtitle(
            context,
            subtitleView
        )
    }

    fun bindSubtitle(
        context: Context,
        subtitleView: TextView
    ) {

        subtitleView.text =
            buildSubtitle(context)

        subtitleView.setTextColor(
            ThemeManager.getTopBarSubtitle(
                context
            )
        )
    }

    fun bindTitle(
        context: Context,
        titleView: TextView
    ) {

        titleView.text =
            APP_TITLE

        titleView.setTextColor(
            ThemeManager.getTopBarTitle(
                context
            )
        )
    }

    fun buildSubtitle(
        context: Context
    ): String {

        return "${getUsername(context)} - ${getCurrentDateTime()}"
    }

    private fun getUsername(
        context: Context
    ): String {

        val prefs =
            context.getSharedPreferences(
                SettingsActivity.PREFS,
                Context.MODE_PRIVATE
            )

        return prefs.getString(
            SettingsActivity.KEY_USERNAME,
            ""
        )?.trim()
            ?.ifBlank {
                DEFAULT_USERNAME
            }
            ?: DEFAULT_USERNAME
    }

    private fun getCurrentDateTime(): String {

        return SimpleDateFormat(
            DATE_PATTERN,
            Locale.getDefault()
        ).format(
            Date()
        )
    }
}