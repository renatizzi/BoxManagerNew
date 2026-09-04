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

    /**
     * Tag effettivo per UI e Ricerca avanzata.
     * Preferisce le application locales AppCompat (lingua delle schermate),
     * altrimenti la preferenza Impostazioni. Non usa la lingua di sistema
     * delle Resources da sola (telefono EN non deve forzare la ricerca EN
     * se Impostazioni è IT).
     */
    fun resolveUiTag(
        applicationLocalesTags: String?,
        stored: String?
    ): String {

        firstLanguageTag(
            applicationLocalesTags
        )?.let { tag ->
            return resolve(tag)
        }

        return resolve(stored)
    }

    private fun firstLanguageTag(
        tags: String?
    ): String? {

        return tags
            ?.split(",", ";")
            ?.map { part ->
                part.trim()
            }
            ?.firstOrNull { part ->
                part.isNotEmpty()
            }
    }
}
