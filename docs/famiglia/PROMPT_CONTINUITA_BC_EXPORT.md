# Prompt di continuità — B–C Export/Import avanzati (19/09/2026)

**Ingresso unico** per questa linea (chiusa). Prossimo filone: [PROMPT_CONTINUITA_UTILITY_UI.md](PROMPT_CONTINUITA_UTILITY_UI.md).

**Identità:** una sola **BoxManager**. Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

**Test telefono:** solo su `main` — `.cursor/rules/test-telefono-main.mdc`.

**Fonte requisiti (congelata):** [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md).

### Backlog aperto (in evidenza — non è questa fetta)

Fonte viva: [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md).

| ID | Indicazione |
|----|-------------|
| **B-UTILITY-BUTTON-HEIGHT** | → presa in carico [PROMPT_CONTINUITA_UTILITY_UI.md](PROMPT_CONTINUITA_UTILITY_UI.md) |
| **B-UTILITY-RIPRISTINA-VISIBLE** | Legato ad altezza tile — stessa fetta Utility |
| **B-PREMIUM-UNIFY-PAGE** | Pagina premium unica — **Aperto** 18/09 |
| **B-QR-BATCH-*** | QR batch — **Aperto** |

---

## Stato — **CHIUSO**

| Voce | Valore |
|------|--------|
| **B1** | **FATTO** SI `1.3.10 ok` |
| **B2** | **FATTO** SI `1.3.11 ok` |
| **B3** | **FATTO** SI `1.3.13 ok` |
| **B4** | **FATTO** SI Renato 19/09 (dialog + resto ok); nome file report = `REPORT_ERRORI_ddMMyy_HHmm.csv` (no ritest) |
| **Build play a chiusura** | **`v. 1.3.15`** (+ fix nome report in **1.3.16** con Utility) |

Report errori CSV: prefisso **`REPORT_ERRORI_`** (non `IMPORT_ERRORI_`, per non confondere con i file di import).

---

## Incolla in chat (nuova sessione — Utility)

```
Continua Utility UI da docs/famiglia/PROMPT_CONTINUITA_UTILITY_UI.md.
Priorità: B-UTILITY-BUTTON-HEIGHT (+ Ripristina visibile); poi Premium/QR solo con SI.
Identità: una sola BoxManager. Test telefono solo su main.
```
