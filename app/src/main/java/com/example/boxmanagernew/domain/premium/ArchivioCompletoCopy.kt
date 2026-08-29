package com.example.boxmanagernew.domain.premium

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Testi dello sblocco commerciale. Non fanno parte del catalogo 2.6.
 */
object ArchivioCompletoCopy {

    const val PAGE_SUBTITLE =
        "Funzione avanzata"

    const val BUTTON_SHARE =
        "CONDIVIDI"

    const val BUTTON_REDEEM =
        "USA CODICE"

    const val BUTTON_CLOSE =
        "CHIUDI"

    const val SETTINGS_UNLOCK_TITLE =
        "Archivio completo (prova)"

    const val SETTINGS_UNLOCK_HINT =
        "Solo in questa installazione di prova: attiva per usare ricerca avanzata, QR, import ed export senza limiti. Disattiva e usa i tasti sotto per simulare fine prova o ricominciare il periodo di prova."

    const val SETTINGS_EXPIRE_TRIAL =
        "Simula fine prova"

    const val SETTINGS_RESTART_TRIAL =
        "Ricomincia il periodo di prova"

    const val SETTINGS_PARAMS_TITLE =
        "Parametri Archivio completo"

    const val SETTINGS_PARAMS_HINT =
        "Solo per l'amministratore: modifica prova, rinnovo e amici richiesti. I valori valgono su questo dispositivo."

    const val SETTINGS_PARAMS_SAVE =
        "SALVA PARAMETRI"

    const val SETTINGS_PARAMS_SAVED =
        "Parametri salvati."

    const val SETTINGS_PARAM_TRIAL =
        "Periodo di prova (giorni)"

    const val SETTINGS_PARAM_BONUS =
        "Rinnovo per condivisione (giorni)"

    const val SETTINGS_PARAM_FRIENDS =
        "Amici da condividere"

    const val CODE_HINT =
        "Hai un codice?"

    const val SETTINGS_CODE_TITLE =
        "Archivio completo"

    const val SETTINGS_CODE_HINT =
        "Inserisci il codice tester o amico per sbloccare le funzioni avanzate."

    const val SETTINGS_CODE_ACTIVE =
        "Archivio completo attivo su questo dispositivo."

    const val CODE_OK =
        "Archivio completo sbloccato su questo dispositivo."

    const val CODE_KO =
        "Codice non valido."

    const val UNLOCK_CODE_HINT =
        "Codice"

    fun packageShareHint(
        trialDays: Int,
        bonusDays: Int,
        friends: Int
    ): String {
        val friendPart =
            if (friends <= 1) {
                "Condividi BoxManager con un amico"
            } else {
                "Condividi BoxManager con $friends amici"
            }
        return "Il periodo di prova di $trialDays giorni è terminato. " +
            "$friendPart: ottieni altri $bonusDays giorni su ricerca avanzata, QR, import ed export."
    }

    fun shareGranted(bonusDays: Int): String {
        return "Hai altri $bonusDays giorni di Archivio completo."
    }

    fun shareProgressLine(
        done: Int,
        required: Int
    ): String {
        return "Condivisione $done di $required. Continua con un altro amico."
    }

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
                        "Cosa ho in garage e in cantina?"
                )

            PremiumFeature.QR_SCAN ->
                FeaturePitch(
                    lead =
                        "Con una sola inquadratura apri il contenitore giusto, anche in cantina o in garage, senza cercare a mano.",
                    example =
                        "etichetta sulla scatola → scheda del contenitore"
                )

            PremiumFeature.QR_LABEL ->
                FeaturePitch(
                    lead =
                        "Genera etichette con QR da stampare e attaccare su ogni contenitore.",
                    example =
                        "BOX 12 — Ferramenta — Garage"
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
                        "esporta in formato CSV il contenuto parziale o totale del mio archivio su foglio elettronico"
                )
        }
    }

    fun trialStatusLine(
        remainingDays: Int,
        accessUntil: Long
    ): String {
        if (remainingDays <= 0) {
            return "Il periodo di prova è terminato."
        }
        val until =
            formatDay(accessUntil)
        return if (remainingDays == 1) {
            "Archivio completo in prova fino a $until (scade entro 24 ore)."
        } else {
            "Archivio completo in prova ancora $remainingDays giorni (fino a $until)."
        }
    }

    fun shareCooldownLine(
        remainingMs: Long,
        bonusDays: Int
    ): String {
        val hours =
            ((remainingMs + 3_599_999L) / 3_600_000L).toInt()
        return if (hours <= 1) {
            "Potrai ottenere altri $bonusDays giorni tra circa un'ora."
        } else {
            "Potrai ottenere altri $bonusDays giorni tra circa $hours ore."
        }
    }

    fun shareMessage(playUrl: String): String {
        return "Sto usando BoxManager per organizzare contenitori e oggetti. " +
            "Provalo: $playUrl"
    }

    fun formatDay(epochMs: Long): String {
        val fmt =
            SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
        return fmt.format(Date(epochMs))
    }

    data class FeaturePitch(
        val lead: String,
        val example: String?,
        val callToAction: String? = null
    )
}
