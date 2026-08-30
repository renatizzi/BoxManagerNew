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
        "Prima allinea le tue categorie e posizioni alle tabelle condivise, " +
            "poi aggiorna periodicamente contenitori e oggetti. Non sostituisce " +
            "Importa ed Esporta dati."

    const val SECTION_SHARED_TABLES = "Tabelle condivise (categorie e posizioni)"

    const val SECTION_SHARED_TABLES_HINT =
        "Passo 1: invia o ricevi le tabelle condivise in famiglia. " +
            "Passo 3: ripristinale sulle tabelle locali dopo reinstallazione " +
            "o per correggere errori."

    const val BUTTON_SEND_SHARED_TABLES = "📤 Invia tabelle condivise"

    const val BUTTON_RECEIVE_SHARED_TABLES = "📥 Ricevi tabelle condivise"

    const val SECTION_ARCHIVE = "Archivio (contenitori e oggetti)"

    const val SECTION_ARCHIVE_HINT =
        "Passo 2: condividi periodicamente l'archivio censito."

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
        BUTTON_SEND_SHARED_TABLES,
        BUTTON_RECEIVE_SHARED_TABLES,
        BUTTON_SEND,
        BUTTON_RECEIVE
    )
}
