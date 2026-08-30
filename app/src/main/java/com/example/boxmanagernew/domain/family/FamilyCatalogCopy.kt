package com.example.boxmanagernew.domain.family

/**
 * Testi UI catalogo famiglia (flavor famiglia).
 * Colori card: palette Impostazioni via [accentCardTexts].
 */
object FamilyCatalogCopy {

    const val UTILITY_CARD_LABEL = "📋 Catalogo Famiglia"

    const val PAGE_TITLE = "Catalogo Famiglia"

    const val PAGE_SUBTITLE =
        "Catalogo condiviso e unione inventario tra familiari"

    const val INTRO =
        "Allinea prima categorie e luoghi con il Catalogo, poi unisci " +
            "contenitori e oggetti per ID stabili con l'Inventario. " +
            "Serve a ripartire il censimento in famiglia: non sostituisce " +
            "Importa dati né Esporta dati dell'inventario V1."

    const val SECTION_CATALOG = "Catalogo"

    const val BUTTON_SEND = "📤 Invia Catalogo"

    const val BUTTON_RECEIVE = "📥 Ricevi Catalogo"

    const val SECTION_INVENTORY = "Inventario"

    const val BUTTON_SEND_INVENTORY = "📤 Invia Inventario"

    const val BUTTON_RECEIVE_INVENTORY = "📥 Ricevi Inventario"

    const val SECTION_CATALOG_HINT =
        "Condividi categorie e luoghi: primo passo del merge famiglia."

    const val SECTION_INVENTORY_HINT =
        "Unisce contenitori e oggetti per ID stabili tra i familiari."

    const val MSG_EXPORT_COMPLETED = "Salvataggio completato."

    const val MSG_FOLDER_INACCESSIBLE =
        "Cartella non accessibile. Scegli di nuovo la cartella."

    /** Testi card che ricevono il colore accent della palette Impostazioni. */
    fun accentCardTexts(): List<String> = listOf(
        UTILITY_CARD_LABEL,
        BUTTON_SEND,
        BUTTON_RECEIVE,
        BUTTON_SEND_INVENTORY,
        BUTTON_RECEIVE_INVENTORY
    )
}
