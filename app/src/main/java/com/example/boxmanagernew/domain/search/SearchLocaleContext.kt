package com.example.boxmanagernew.domain.search

/**
 * Ambito della tabella linguistica per un attraversamento 0–10.
 * Default italiano: i test IT esistenti non impostano il locale.
 *
 * [setDisplayLocale] è il locale UI (Impostazioni). Non entra nella
 * pipeline: solo i messaggi 2.6 letti sul main thread (liste vuote).
 */
object SearchLocaleContext {

    private val current =
        ThreadLocal.withInitial {
            SearchLocale.IT
        }

    @Volatile
    private var displayLocale: SearchLocale =
        SearchLocale.IT

    fun locale(): SearchLocale {
        return current.get() ?: SearchLocale.IT
    }

    fun isEnglish(): Boolean {
        return locale() == SearchLocale.EN
    }

    fun displayIsEnglish(): Boolean {
        return displayLocale == SearchLocale.EN
    }

    fun setDisplayLocale(
        locale: SearchLocale
    ) {
        displayLocale = locale
    }

    fun <T> run(
        locale: SearchLocale,
        block: () -> T
    ): T {

        val previous =
            current.get()

        current.set(locale)

        try {
            return block()
        } finally {

            if (previous == null) {
                current.remove()
            } else {
                current.set(previous)
            }
        }
    }
}
