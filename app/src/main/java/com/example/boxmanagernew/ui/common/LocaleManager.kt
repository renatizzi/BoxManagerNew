package com.example.boxmanagernew.ui.common

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.boxmanagernew.domain.locale.LocalePreference
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext

/**
 * Applica la lingua persistita (3.6.6) via AppCompat per-app locales.
 * Ricerca avanzata e Impostazioni usano [effectiveTag] / [searchLocale]:
 * stessa fonte della UI (AppCompat), con heal delle preferenze se diverge.
 * Niente traduttore EN→IT.
 */
object LocaleManager {

    fun storedTag(context: Context): String {
        return LocalePreference.resolve(
            rawStored(context)
        )
    }

    /**
     * Lingua effettiva delle schermate: AppCompat application locales
     * se impostate, altrimenti preferenza Impostazioni.
     * Allinea le prefs se AppCompat e prefs divergono (CK2).
     */
    fun effectiveTag(context: Context): String {

        val applicationTags =
            AppCompatDelegate
                .getApplicationLocales()
                .toLanguageTags()

        val stored =
            rawStored(context)

        val resolved =
            LocalePreference.resolveUiTag(
                applicationTags,
                stored
            )

        healStoredIfNeeded(
            context,
            resolved
        )

        return resolved
    }

    /**
     * Locale Pipeline 0–10 + messaggi 2.6: stesso tag della UI.
     */
    fun searchLocale(context: Context): SearchLocale {

        val locale =
            SearchLocale.fromTag(
                effectiveTag(context)
            )

        SearchLocaleContext.setDisplayLocale(
            locale
        )

        return locale
    }

    fun applyStored(context: Context) {

        val applicationTags =
            AppCompatDelegate
                .getApplicationLocales()
                .toLanguageTags()

        val resolved =
            LocalePreference.resolveUiTag(
                applicationTags,
                rawStored(context)
            )

        apply(
            context,
            resolved,
            persist = true
        )
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

        SearchLocaleContext.setDisplayLocale(
            SearchLocale.fromTag(resolved)
        )

        if (persist || !hasStoredValue(context)) {
            writeStored(
                context,
                resolved
            )
        }

        val locales =
            LocaleListCompat.forLanguageTags(resolved)

        val currentTags =
            AppCompatDelegate.getApplicationLocales()
                .toLanguageTags()

        if (
            LocalePreference.resolve(currentTags) != resolved ||
            currentTags.isBlank()
        ) {
            AppCompatDelegate.setApplicationLocales(
                locales
            )
        }
    }

    private fun healStoredIfNeeded(
        context: Context,
        resolved: String
    ) {

        if (
            storedTag(context) == resolved &&
            hasStoredValue(context)
        ) {
            return
        }

        writeStored(
            context,
            resolved
        )
    }

    private fun writeStored(
        context: Context,
        resolved: String
    ) {

        context.getSharedPreferences(
            LocalePreference.PREFS,
            Context.MODE_PRIVATE
        ).edit()
            .putString(
                LocalePreference.KEY_LANGUAGE,
                resolved
            )
            .commit()
    }

    private fun rawStored(
        context: Context
    ): String? {

        return context.getSharedPreferences(
            LocalePreference.PREFS,
            Context.MODE_PRIVATE
        ).getString(
            LocalePreference.KEY_LANGUAGE,
            null
        )
    }

    private fun hasStoredValue(context: Context): Boolean {
        return context.getSharedPreferences(
            LocalePreference.PREFS,
            Context.MODE_PRIVATE
        ).contains(LocalePreference.KEY_LANGUAGE)
    }

    /**
     * Tag lingua dalle Resources del Context (diagnostica / test).
     * Non è la fonte primaria: un telefono EN non deve forzare EN
     * se Impostazioni/AppCompat dicono IT.
     */
    fun resourceLanguageTag(context: Context): String? {

        val configuration =
            context.resources.configuration

        return if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        ) {
            val locales =
                configuration.locales

            if (locales.isEmpty) {
                null
            } else {
                locales[0].toLanguageTag()
            }
        } else {
            @Suppress("DEPRECATION")
            configuration.locale?.toLanguageTag()
                ?: configuration.locale?.language
        }
    }
}
