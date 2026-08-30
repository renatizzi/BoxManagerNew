package com.example.boxmanagernew.domain.family

/**
 * Testi UI unione famiglia (flavor famiglia).
 * Colori card: palette Impostazioni via [accentCardTexts].
 */
object FamilyMergeCopy {

    const val UTILITY_CARD_LABEL = "🤝 Unione famiglia"

    const val PAGE_TITLE = "Unione famiglia"

    const val PAGE_SUBTITLE =
        "Un unico archivio domestico da aggiornare periodicamente"

    const val INTRO =
        "Invia o ricevi un file con categorie, luoghi, contenitori e oggetti. " +
            "Serve a ripartire il censimento in famiglia e a tenere un unico " +
            "archivio domestico aggiornato. Non sostituisce Importa dati né " +
            "Esporta dati dell'inventario V1."

    const val BUTTON_SEND = "📤 Invia unione"

    const val BUTTON_RECEIVE = "📥 Ricevi unione"

    const val MSG_EXPORT_COMPLETED = "Salvataggio completato."

    const val MSG_RECEIVE_COMPLETED = "Ricezione completata."

    const val MSG_READ_FAILED = "Impossibile leggere il file."

    const val MSG_FOLDER_INACCESSIBLE =
        "Cartella non accessibile. Scegli di nuovo la cartella."

    const val MSG_WRITE_FAILED = "Salvataggio non riuscito. Riprovare."

    /** Testi card che ricevono il colore accent della palette Impostazioni. */
    fun accentCardTexts(): List<String> = listOf(
        UTILITY_CARD_LABEL,
        BUTTON_SEND,
        BUTTON_RECEIVE
    )
}
