package com.example.boxmanagernew.ui.common

import android.content.Context
import android.widget.TextView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TopBarUtils {

    fun bindSubtitle(
        context: Context,
        subtitleView: TextView
    ) {

        val prefs = context.getSharedPreferences(
            SettingsActivity.PREFS,
            Context.MODE_PRIVATE
        )

        val username =
            prefs.getString(
                SettingsActivity.KEY_USERNAME,
                ""
            )?.trim()
                ?.ifBlank { "Utente" }
                ?: "Utente"

        val now =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

        subtitleView.text =
            "$username - $now"
    }

    fun bindTitle(
        titleView: TextView
    ) {

        titleView.text = "BoxManager"
    }
}