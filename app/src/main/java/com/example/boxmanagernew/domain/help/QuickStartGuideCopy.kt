package com.example.boxmanagernew.domain.help

/**
 * Guida rapida in-app. Non fa parte del catalogo 2.6.
 */
object QuickStartGuideCopy {

    const val PAGE_TITLE =
        "Guida rapida"

    const val PAGE_SUBTITLE =
        "Come orientarsi in BoxManager"

    const val WORKFLOW_TITLE =
        "Il workflow in sintesi"

    const val WORKFLOW_LINE =
        "Configura → Censisci → Utilizza"

    const val INTRO =
        "BoxManager organizza contenitori e oggetti. " +
            "Segui le tre macro-fasi nell'ordine indicato."

    enum class Phase(
        val label: String,
        val colorRes: Int
    ) {
        CONFIG(
            "Configura",
            com.example.boxmanagernew.R.color.guide_phase_config
        ),
        CENSUS(
            "Censisci",
            com.example.boxmanagernew.R.color.guide_phase_census
        ),
        USAGE(
            "Utilizza",
            com.example.boxmanagernew.R.color.guide_phase_usage
        )
    }

    data class Section(
        val phase: Phase,
        val number: Int,
        val title: String,
        val bullets: List<String>
    )

    val sections: List<Section> =
        listOf(
            Section(
                phase = Phase.CONFIG,
                number = 1,
                title = "Impostazioni",
                bullets = listOf(
                    "Definisci i luoghi abituali di custodia (casa, garage, cantina…).",
                    "Scegli il tema colore dell'interfaccia.",
                    "Il nome utente è facoltativo: serve solo come etichetta locale."
                )
            ),
            Section(
                phase = Phase.CENSUS,
                number = 2,
                title = "Categorie",
                bullets = listOf(
                    "Parti dalle categorie predefinite.",
                    "Puoi rinominarle, aggiungerne o eliminarle.",
                    "Servono a classificare i contenitori."
                )
            ),
            Section(
                phase = Phase.CENSUS,
                number = 3,
                title = "Contenitori e oggetti",
                bullets = listOf(
                    "Registra i contenitori e assegna categoria e posizione.",
                    "Apri un contenitore per aggiungere gli oggetti al suo interno.",
                    "Così sai cosa c'è e dove si trova."
                )
            ),
            Section(
                phase = Phase.USAGE,
                number = 4,
                title = "Dashboard",
                bullets = listOf(
                    "Vedi i totali dell'archivio a colpo d'occhio.",
                    "Ricerca semplice: digita o usa il microfono sulla vista corrente.",
                    "Ricerca avanzata (linguaggio naturale): da Archivio completo."
                )
            ),
            Section(
                phase = Phase.USAGE,
                number = 5,
                title = "Utility",
                bullets = listOf(
                    "Backup e Ripristino per salvare l'archivio su file.",
                    "Importa ed Esporta dati in CSV.",
                    "Codice QR per aprire un contenitore dalla fotocamera."
                )
            ),
            Section(
                phase = Phase.USAGE,
                number = 6,
                title = "Strumenti contestuali",
                bullets = listOf(
                    "Nelle liste puoi selezionare più elementi.",
                    "Stampa, esporta CSV o etichetta QR su ciò che hai selezionato.",
                    "Utile per inventari mirati e stampa di etichette."
                )
            )
        )

    const val FOOTER_NOTE =
        "Ricerca avanzata, QR, import ed export richiedono Archivio completo " +
            "(prova a tempo, poi rinnovo tramite condivisione o codice)."
}
