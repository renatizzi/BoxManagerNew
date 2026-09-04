package com.example.boxmanagernew.ui.common

import android.app.Activity
import android.content.Context
import android.widget.TextView
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TopBarUtils {

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

        bindVersion(context)

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

        // Come 1.2: titolo in-app sempre "BoxManager".
        // app_name è il launcher; stesso prodotto BoxManager (anche flavor sviluppo).
        titleView.text =
            context.getString(R.string.topbar_app_title)

        titleView.setTextColor(
            ThemeManager.getTopBarTitle(
                context
            )
        )
    }

    fun bindVersion(
        context: Context
    ) {

        val activity =
            context as? Activity
                ?: return

        val versionView =
            activity.findViewById<TextView>(
                R.id.textAppVersion
            )
                ?: return

        versionView.text =
            context.getString(
                R.string.topbar_version_prefix,
                BuildConfig.VERSION_NAME
            )

        versionView.setTextColor(
            ThemeManager.getTopBarSubtitle(
                context
            )
        )
    }

    fun resolvedUsername(context: Context): String {
        val stored = context
            .getSharedPreferences(
                SettingsActivity.PREFS,
                Context.MODE_PRIVATE
            )
            .getString(SettingsActivity.KEY_USERNAME, "")
            ?.trim()
            .orEmpty()
        return stored.ifEmpty {
            context.getString(R.string.topbar_default_username)
        }
    }

    fun buildSubtitle(context: Context): String {
        return "${resolvedUsername(context)} - ${getCurrentDateTime()}"
    }

    /** Retrocompat: senza Context resta il default. */
    fun buildSubtitle(): String {
        return "Utente - ${getCurrentDateTime()}"
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
