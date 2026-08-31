package com.example.boxmanagernew.ui.common

import android.content.Context

/**
 * Origine censimento (Nota B0 §7): username Impostazioni, non un secondo
 * «membro famiglia». Fallback UI «Utente» se il nome è vuoto.
 */
object CreatedByResolver {

    const val FALLBACK = "Utente"

    fun current(context: Context): String {
        return TopBarUtils.resolvedUsername(context)
    }

    fun normalize(value: String?): String {
        return value?.trim().orEmpty()
    }

    fun display(value: String?): String {
        return normalize(value).ifEmpty { FALLBACK }
    }
}
