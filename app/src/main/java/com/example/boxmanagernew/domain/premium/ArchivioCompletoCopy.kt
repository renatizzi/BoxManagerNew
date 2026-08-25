package com.example.boxmanagernew.domain.premium

/**
 * Testi dello sblocco commerciale. Non fanno parte del catalogo 2.6.
 */
object ArchivioCompletoCopy {

    const val PAGE_SUBTITLE =
        "Funzione a pagamento"

    const val BUTTON_TRY =
        "PROVA"

    const val BUTTON_BUY =
        "ACQUISTA"

    const val BUTTON_CLOSE =
        "CHIUDI"

    const val SETTINGS_UNLOCK_TITLE =
        "Archivio completo (prova)"

    const val SETTINGS_UNLOCK_HINT =
        "Solo in questa installazione di prova: attiva per usare ricerca avanzata, QR, import ed export senza limiti. Disattiva per rivedere i lucchetti."

    const val SETTINGS_RESET_TRIALS =
        "Azzera le prove"

    const val PACKAGE_BUY_HINT =
        "Archivio completo sblocca ricerca avanzata, codice QR, import ed export. Tocca Acquista per attivarle tutte."

    const val BUY_NOT_READY =
        "L'acquisto sarà disponibile con Google Play."

    data class FeaturePitch(
        val lead: String,
        val example: String?,
        val callToAction: String? = null
    )

    fun featureTitle(feature: PremiumFeature): String {
        return when (feature) {
            PremiumFeature.ADVANCED_SEARCH ->
                "Ricerca avanzata"
            PremiumFeature.QR_SCAN ->
                "Codice QR"
            PremiumFeature.QR_LABEL ->
                "Etichetta QR"
            PremiumFeature.IMPORT ->
                "Importa dati"
            PremiumFeature.EXPORT ->
                "Esporta dati"
        }
    }

    fun pitch(feature: PremiumFeature): FeaturePitch {
        return when (feature) {
            PremiumFeature.ADVANCED_SEARCH ->
                FeaturePitch(
                    lead =
                        "Questa potentissima funzione ti consente di fare ricerche articolate usando il linguaggio naturale.",
                    example =
                        "Cosa ho in garage e in cantina?",
                    callToAction = "Provala!"
                )

            PremiumFeature.QR_SCAN ->
                FeaturePitch(
                    lead =
                        "Con una sola inquadratura apri il contenitore giusto, anche in cantina o in garage, senza cercare a mano.",
                    example =
                        "etichetta sulla scatola → scheda del contenitore",
                    callToAction = "Provala!"
                )

            PremiumFeature.QR_LABEL ->
                FeaturePitch(
                    lead =
                        "Genera etichette con QR da stampare e attaccare su ogni contenitore.",
                    example =
                        "BOX 12 — Ferramenta — Garage",
                    callToAction = "Provala!"
                )

            PremiumFeature.IMPORT ->
                FeaturePitch(
                    lead =
                        "Insieme a Esporta dati, questa funzione ti consente di gestire facilmente archivi già utilizzati sul tuo dispositivo.",
                    example =
                        "importa in formato CSV il contenuto di un archivio locale gestito su foglio elettronico"
                )

            PremiumFeature.EXPORT ->
                FeaturePitch(
                    lead =
                        "Insieme a Importa dati, questa funzione ti consente di gestire facilmente archivi già utilizzati sul tuo dispositivo.",
                    example =
                        "esporta in formato CSV il contenuto parziale o totale del mio archivio su foglio elettronico",
                    callToAction = "Provala!"
                )
        }
    }

    fun trialLine(
        feature: PremiumFeature,
        remaining: Int
    ): String {
        if (feature.trialLimit == 0) {
            return "Nessuna prova gratuita su file reali."
        }
        return if (remaining <= 0) {
            "Hai esaurito le prove a disposizione."
        } else if (remaining == 1) {
            "Hai ancora 1 prova a disposizione."
        } else {
            "Hai ancora $remaining prove a disposizione."
        }
    }

    fun primaryButton(
        canTry: Boolean
    ): String {
        return if (canTry) {
            BUTTON_TRY
        } else {
            BUTTON_BUY
        }
    }
}
