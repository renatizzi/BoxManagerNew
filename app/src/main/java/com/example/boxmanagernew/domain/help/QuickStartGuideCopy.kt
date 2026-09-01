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
        "BoxManager, un'app che ti aiuta a cercare ciò che non trovi..."

    const val WORKFLOW_TITLE =
        "Come usare BoxManager in tre mosse"

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
        );

        fun numberedLabel(): String {
            return "${ordinal + 1}. $label"
        }
    }

    data class Section(
        val phase: Phase,
        val number: Int,
        val title: String,
        val bullets: List<String>
    )

    val sections: List<Section>
        get() = sectionsFor(includeFamilyBeta = false)

    fun sectionsFor(includeFamilyBeta: Boolean): List<Section> {
        return listOf(
            Section(
                phase = Phase.CONFIG,
                number = 1,
                title = "Impostazioni",
                bullets = listOf(
                    "Inserisci il tuo nome che apparirà nella topbar. " +
                        "Se usi Archivio Condiviso, sarà annotato come ultimo " +
                        "familiare che ha modificato un determinato box e/o " +
                        "il suo contenuto.",
                    "Scegli il tema colore dell'interfaccia.",
                    "Informativa sulla Privacy.",
                    "Definisci i luoghi abituali di custodia che, insieme " +
                        "alle categorie, costituiranno le tue tabelle di " +
                        "riferimento (da salvare su cartella diversa da app " +
                        "ed eventualmente ripristinare all'occorrenza)."
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
                bullets = utilityBullets(includeFamilyBeta)
            ),
            Section(
                phase = Phase.USAGE,
                number = 6,
                title = "Strumenti contestuali",
                bullets = listOf(
                    "Gli elementi visualizzati nelle liste possono essere stampati.",
                    "Esportati su file CSV per interagire con file esterni (*).",
                    "Per i contenitori è possibile richiedere la stampa " +
                        "dell'etichetta QR."
                )
            )
        )
    }

    private fun utilityBullets(
        includeFamilyBeta: Boolean
    ): List<String> {

        return buildList {
            add(
                "Backup e Ripristino per salvare l'archivio " +
                    "(usa cartella diversa da app)."
            )
            add(
                "Importa dati da file CSV per interagire con file esterni (*)."
            )
            add(
                "Codice QR per vedere il contenuto di un box " +
                    "usando la fotocamera."
            )
            if (includeFamilyBeta) {
                add(
                    "Condividi Archivio per aggiornare e condividere con i " +
                        "tuoi familiari i dati del tuo archivio."
                )
                add(
                    "Per unire i dati usa Invia/Ricevi Archivio, non " +
                        "Ripristino (sostituisce tutto l'archivio)."
                )
            }
        }
    }

    val CSV_FOOTNOTE: String
        get() = buildString {
            appendLine(
                "(*) I file di import ed export devono essere in formato CSV " +
                    "(con separatore \"${ImportConfiguration.SEPARATOR}\")."
            )
            appendLine()
            appendLine(
                "Formato file Importa dati (${ImportConfiguration.FILE_NAME})"
            )
            appendLine()
            appendLine("Intestazione:")
            appendLine(
                "formato${ImportConfiguration.SEPARATOR}" +
                    "${ImportConfiguration.FORMAT_NAME}" +
                    ImportConfiguration.SEPARATOR +
                    ImportConfiguration.FORMAT_VERSION
            )
            appendLine(
                "sezione${ImportConfiguration.SEPARATOR}" +
                    ImportConfiguration.SECTION_BOXES
            )
            appendLine(
                ImportConfiguration.COL_NAME +
                    " ${ImportConfiguration.COL_CATEGORY} " +
                    ImportConfiguration.COL_POSITION
            )
            appendLine(
                "sezione${ImportConfiguration.SEPARATOR}" +
                    ImportConfiguration.SECTION_OBJECTS
            )
            appendLine(
                "${ImportConfiguration.COL_NAME} (oggetto) " +
                    "${ImportConfiguration.COL_BOX} " +
                    "${ImportConfiguration.COL_DESCRIPTION} (oggetto) " +
                    "${ImportConfiguration.COL_QUANTITY} (oggetto)"
            )
            appendLine()
            appendLine(
                "Le relative categorie e posizioni (obbligatorie) devono " +
                    "essere preliminarmente create in archivio. In fase di " +
                    "Import l'app salva un backup automatico " +
                    "(${ImportConfiguration.PRE_IMPORT_PREFIX}ddMMyy_HHmm) " +
                    "da ripristinare in caso di necessità."
            )
            appendLine()
            appendLine(
                "Formato file Esporta dati " +
                    "(${ViewOutputConfiguration.EXPORT_FILE_PREFIX}ddMMyy_HHmm.csv)"
            )
            append("• Stesso schema del modello Importa dati.")
        }

    const val FOOTER_NOTE =
        "Ricerca avanzata, QR, import ed export richiedono Archivio completo " +
            "(prova a tempo, poi rinnovo tramite condivisione o codice)."
}
