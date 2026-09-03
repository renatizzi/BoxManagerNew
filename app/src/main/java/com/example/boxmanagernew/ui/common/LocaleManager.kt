package com.example.boxmanagernew.ui.common

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.boxmanagernew.domain.locale.LocalePreference

/**
 * Applica la lingua persistita (3.6.6) via AppCompat per-app locales.
 * Persistenza UI 3.6.6. La Ricerca avanzata riceve lo stesso tag
 * (SearchLocale) in GlobalSearchActivity; niente traduttore EN→IT.
 */
object LocaleManager {

    fun storedTag(context: Context): String {
        val prefs =
            context.getSharedPreferences(
                LocalePreference.PREFS,
                Context.MODE_PRIVATE
            )
        val stored =
            prefs.getString(
                LocalePreference.KEY_LANGUAGE,
                null
            )
        return LocalePreference.resolve(stored)
    }

    fun applyStored(context: Context) {
        apply(context, storedTag(context), persist = false)
    }

    fun setLanguage(
        context: Context,
        languageTag: String
    ) {
        apply(context, languageTag, persist = true)
    }

    private fun apply(
        context: Context,
        languageTag: String,
        persist: Boolean
    ) {
        val resolved =
            LocalePreference.resolve(languageTag)

        if (persist || !hasStoredValue(context)) {
            context.getSharedPreferences(
                LocalePreference.PREFS,
                Context.MODE_PRIVATE
            ).edit()
                .putString(
                    LocalePreference.KEY_LANGUAGE,
                    resolved
                )
                .apply()
        }

        val locales =
            LocaleListCompat.forLanguageTags(resolved)
        val currentTags =
            AppCompatDelegate.getApplicationLocales()
                .toLanguageTags()
        if (currentTags != resolved) {
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }

    private fun hasStoredValue(context: Context): Boolean {
        return context.getSharedPreferences(
            LocalePreference.PREFS,
            Context.MODE_PRIVATE
        ).contains(LocalePreference.KEY_LANGUAGE)
    }
}
