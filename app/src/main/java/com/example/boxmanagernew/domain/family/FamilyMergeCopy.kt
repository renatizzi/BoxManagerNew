package com.example.boxmanagernew.domain.family

/**
 * Testi UI condivisione archivio (flavor famiglia).
 * Colori card: palette Impostazioni via [accentCardTexts].
 */
object FamilyMergeCopy {

    const val UTILITY_CARD_LABEL = "📤 Condividi Archivio"

    const val PAGE_TITLE = "Condivisione Archivio"

    const val PAGE_SUBTITLE =
        "Tabelle condivise e archivio domestico in famiglia"

    const val INTRO =
        "Aggiorna periodicamente e condividi con i tuoi familiari i dati del tuo " +
            "archivio usando i tasti \"Invia\" e \"Ricevi\". Non sostituisce " +
            "Importa ed Esporta Dati."

    const val SECTION_SHARED_TABLES = "Tabelle condivise (categorie e posizioni)"

    const val SECTION_SHARED_TABLES_HINT =
        "Usa il tasto \"Invia\" per esportare e condividere con i tuoi familiari solo " +
            "le categorie e le posizioni abituali. Usa il tasto \"Ricevi\" per " +
            "ripristinarle sul tuo archivio."

    const val BUTTON_SEND_SHARED_TABLES = "📤 Invia tabelle condivise"

    const val BUTTON_RECEIVE_SHARED_TABLES = "📥 Ricevi tabelle condivise"

    const val SECTION_ARCHIVE = "Archivio (contenitori e oggetti)"

    const val SECTION_ARCHIVE_HINT =
        "Usa il tasto \"Invia\" per esportare e condividere con i tuoi familiari tutti i " +
            "dati del tuo archivio. Usa il tasto \"Ricevi\" per unire al tuo archivio " +
            "quello condiviso, riconoscendo gli stessi contenitori e oggetti."

    const val BUTTON_SEND = "📤 Invia Archivio"

    const val BUTTON_RECEIVE = "📥 Ricevi Archivio"

    const val MSG_EXPORT_COMPLETED = "Salvataggio completato."

    fun buildExportSummary(
        folderName: String,
        fileName: String
    ): String = buildString {
        appendLine(MSG_EXPORT_COMPLETED)
        if (folderName.isNotBlank()) {
            appendLine("Cartella: $folderName")
        }
        append("Nome file: $fileName")
    }

    const val MSG_RECEIVE_COMPLETED = "Ricezione completata."

    const val MSG_READ_FAILED = "Impossibile leggere il file."

    const val MSG_FOLDER_INACCESSIBLE =
        "Cartella non accessibile. Scegli di nuovo la cartella."

    const val MSG_WRITE_FAILED = "Salvataggio non riuscito. Riprovare."

    /** Testi card che ricevono il colore accent della palette Impostazioni. */
    fun accentCardTexts(): List<String> = listOf(
        UTILITY_CARD_LABEL,
        BUTTON_SEND_SHARED_TABLES,
        BUTTON_RECEIVE_SHARED_TABLES,
        BUTTON_SEND,
        BUTTON_RECEIVE
    )
}
