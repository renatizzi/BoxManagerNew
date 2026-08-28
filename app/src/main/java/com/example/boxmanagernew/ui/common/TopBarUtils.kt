package com.example.boxmanagernew.ui.common

import android.app.Activity
import android.content.Context
import android.widget.TextView
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
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
            buildSubtitle()

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
            "v. ${BuildConfig.VERSION_NAME}"

        versionView.setTextColor(
            ThemeManager.getTopBarSubtitle(
                context
            )
        )
    }

    fun buildSubtitle(
        context: Context
    ): String {

        return "${DEFAULT_USERNAME} - ${getCurrentDateTime()}"
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