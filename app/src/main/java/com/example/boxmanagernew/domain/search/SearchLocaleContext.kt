package com.example.boxmanagernew.domain.search

/**
 * Ambito della tabella linguistica per un attraversamento 0–10.
 * Default italiano: i test IT esistenti non impostano il locale.
 */
object SearchLocaleContext {

    private val current =
        ThreadLocal.withInitial {
            SearchLocale.IT
        }

    fun locale(): SearchLocale {
        return current.get() ?: SearchLocale.IT
    }

    fun isEnglish(): Boolean {
        return locale() == SearchLocale.EN
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
