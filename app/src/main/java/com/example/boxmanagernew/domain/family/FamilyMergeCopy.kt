package com.example.boxmanagernew.domain.family

/**
 * Testi UI condivisione archivio (flavor famiglia).
 * Colori card: palette Impostazioni via [accentCardTexts].
 */
object FamilyMergeCopy {

    const val UTILITY_CARD_LABEL = "🤝 Condivisione Archivio"

    const val PAGE_TITLE = "Condivisione Archivio"

    const val PAGE_SUBTITLE =
        "Un unico archivio domestico da aggiornare periodicamente"

    const val INTRO =
        "Condividi il tuo archivio personale con i tuoi familiari. Serve ad " +
            "agevolare il censimento iniziale dei contenitori e degli oggetti " +
            "in modo da fare riferimento ad un unico archivio condiviso e " +
            "aggiornato \"della casa\". Non sostituisce le funzioni di Importa " +
            "ed Esporta dati."

    const val HINT_FOLDER =
        "Invia: salva nella cartella di Backup (come Esporta). Ricevi: scegli " +
            "il file CSV; se hai già scelto la cartella di Backup, il selettore " +
            "file si apre lì."

    const val BUTTON_SEND = "📤 Invia Archivio"

    const val BUTTON_RECEIVE = "📥 Ricevi Archivio"

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
