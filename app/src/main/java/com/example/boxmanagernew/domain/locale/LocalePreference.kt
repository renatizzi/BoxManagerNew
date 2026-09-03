package com.example.boxmanagernew.domain.locale

/**
 * Scelta lingua 3.6.6 — solo IT + EN in V1.
 * Default italiano; i dati utente (nomi archivio) non si traducono.
 */
object LocalePreference {

    const val PREFS = "boxmanager_settings"

    const val KEY_LANGUAGE = "app_language"

    const val IT = "it"

    const val EN = "en"

    fun resolve(stored: String?): String {
        val value = stored?.trim()?.lowercase().orEmpty()
        return when {
            value == EN || value.startsWith("$EN-") || value.startsWith("${EN}_") ->
                EN
            else ->
                IT
        }
    }

    fun isEnglish(stored: String?): Boolean {
        return resolve(stored) == EN
    }
}
