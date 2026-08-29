package com.example.boxmanagernew.domain.help

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration

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
        val bullets: List<String>,
        val spreadsheetExampleTitle: String? = null,
        val spreadsheetExample: String? = null
    )

    const val CSV_EXAMPLE_TITLE =
        "Esempio come appare in Excel o Fogli Google"

    val sections: List<Section>
        get() = sectionsFor(includeFamilyBeta = false)

    fun sectionsFor(includeFamilyBeta: Boolean): List<Section> {
        val base = listOf(
            Section(
                phase = Phase.CONFIG,
                number = 1,
                title = "Impostazioni",
                bullets = listOf(
                    "Definisci i luoghi abituali di custodia (casa, garage, cantina…).",
                    "Scegli il tema colore dell'interfaccia.",
                    "Il nome utente (Impostazioni) compare in topbar e, in beta famiglia, marca chi ha censito contenitori/oggetti."
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
                bullets = buildList {
                    add("Backup e Ripristino per salvare l'archivio su file.")
                    add("Importa ed Esporta dati in CSV (vedi sezione 7).")
                    add("Codice QR per aprire un contenitore dalla fotocamera.")
                    if (includeFamilyBeta) {
                        add(
                            "Catalogo famiglia: condividi categorie e luoghi " +
                                "tra i telefoni (setup una volta)."
                        )
                    }
                }
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
            ),
            Section(
                phase = Phase.USAGE,
                number = 7,
                title = "Import ed export CSV",
                bullets = csvGuideBullets(),
                spreadsheetExampleTitle = CSV_EXAMPLE_TITLE,
                spreadsheetExample = csvSpreadsheetExample()
            )
        )
        if (!includeFamilyBeta) {
            return base
        }
        return base + Section(
            phase = Phase.CONFIG,
            number = 8,
            title = "Setup famiglia (beta)",
            bullets = listOf(
                "Allineate categorie e luoghi con Utility → Catalogo famiglia.",
                "Un familiare esporta il catalogo e lo condivide; gli altri lo importano.",
                "Poi ciascuno censisce contenitori e oggetti; l'unione inventario arriverà in una fetta successiva.",
                "Non usare Ripristino per unire archivi: sostituisce tutto."
            )
        )
    }

    private fun csvGuideBullets(): List<String> {
        val boxColumns =
            ImportConfiguration.BOX_HEADER_FIELDS.joinToString(
                ", "
            )
        val objectColumns =
            ImportConfiguration.OBJECT_HEADER_FIELDS.joinToString(
                ", "
            )
        return listOf(
            "Di solito si lavora con un foglio elettronico: genera " +
                ImportConfiguration.FILE_NAME +
                " dall'app (Utility → Importa dati) oppure " +
                "modifica un file esportato.",
            "Salva come CSV con separatore punto e virgola (.csv): " +
                "in Excel «CSV (delimitato da punto e virgola)», " +
                "in Fogli «Valori separati da virgola» con separatore ;.",
            "Formato ufficiale ${ImportConfiguration.FORMAT_NAME} " +
                "v${ImportConfiguration.FORMAT_VERSION}: prima " +
                "${ImportConfiguration.SECTION_BOXES} " +
                "($boxColumns), poi ${ImportConfiguration.SECTION_OBJECTS} " +
                "($objectColumns). Vedi l'esempio sotto.",
            "Prima di importare: crea in app le categorie e le posizioni " +
                "che scrivi nel foglio; negli oggetti il contenitore deve " +
                "essere già presente nel blocco contenitori o in archivio.",
            "All'import l'app salva un backup " +
                "${ImportConfiguration.PRE_IMPORT_PREFIX}ddMMyy_HHmm " +
                "nella cartella del Backup. Se qualcosa non torna, " +
                "l'archivio non cambia. I duplicati vengono ignorati.",
            "Esporta dalla selezione nelle liste o da Utility: nome " +
                "proposto ${ViewOutputConfiguration.EXPORT_FILE_PREFIX}" +
                "ddMMyy_HHmm.csv, stesso schema del modello."
        )
    }

    private fun csvSpreadsheetExample(): String {
        val boxName = "Scatola garage"
        val category = "Attrezzi"
        val position = "Garage"
        val objectName = "Trapano"
        val description = "Bosch verde"
        val quantity = "1"

        return buildString {
            appendLine(
                "Ogni riga del foglio = una riga del file. " +
                    "Le colonne sono separate dal punto e virgola del CSV."
            )
            appendLine()
            appendLine(
                "     A              B                 C"
            )
            appendLine(
                "1    formato        ${ImportConfiguration.FORMAT_NAME}  " +
                    "${ImportConfiguration.FORMAT_VERSION}"
            )
            appendLine(
                "2    sezione        ${ImportConfiguration.SECTION_BOXES}"
            )
            appendLine(
                "3    ${ImportConfiguration.COL_NAME}           " +
                    "${ImportConfiguration.COL_CATEGORY}         " +
                    ImportConfiguration.COL_POSITION
            )
            appendLine(
                "4    $boxName  $category          $position"
            )
            appendLine()
            appendLine(
                "     A        B              C             D"
            )
            appendLine(
                "5    sezione  ${ImportConfiguration.SECTION_OBJECTS}"
            )
            appendLine(
                "6    ${ImportConfiguration.COL_NAME}     " +
                    "${ImportConfiguration.COL_BOX}    " +
                    "${ImportConfiguration.COL_DESCRIPTION}   " +
                    ImportConfiguration.COL_QUANTITY
            )
            appendLine(
                "7    $objectName  $boxName  $description  $quantity"
            )
        }.trimEnd()
    }

    const val FOOTER_NOTE =
        "Ricerca avanzata, QR, import ed export richiedono Archivio completo " +
            "(prova a tempo, poi rinnovo tramite condivisione o codice)."
}
