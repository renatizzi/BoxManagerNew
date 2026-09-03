package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.locale.LocalePreference

/**
 * Locale della Ricerca avanzata. Default IT.
 * Non è un traduttore di frasi (S1).
 */
enum class SearchLocale {
    IT,
    EN;

    companion object {

        fun fromTag(
            tag: String?
        ): SearchLocale {

            return if (
                LocalePreference.isEnglish(
                    tag
                )
            ) {
                EN
            } else {
                IT
            }
        }
    }
}
