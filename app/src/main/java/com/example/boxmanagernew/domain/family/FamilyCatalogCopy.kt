package com.example.boxmanagernew.domain.family

/**
 * Testi UI catalogo famiglia (flavor famiglia).
 * Colori card: palette Impostazioni via [accentCardTexts].
 */
object FamilyCatalogCopy {

    const val UTILITY_CARD_LABEL = "📋 Catalogo famiglia"

    const val PAGE_TITLE = "Catalogo Famiglia"

    const val PAGE_SUBTITLE =
        "Categorie e luoghi di custodia condivisi"

    const val INTRO =
        "Allinea categorie e luoghi tra i familiari. " +
            "Non sostituisce Importa dati né Esporta dati dell'inventario."

    const val BUTTON_SEND = "📤 Invia catalogo"

    const val BUTTON_RECEIVE = "📥 Ricevi catalogo"

    /** Testi card che ricevono il colore accent della palette Impostazioni. */
    fun accentCardTexts(): List<String> = listOf(
        UTILITY_CARD_LABEL,
        BUTTON_SEND,
        BUTTON_RECEIVE
    )
}
