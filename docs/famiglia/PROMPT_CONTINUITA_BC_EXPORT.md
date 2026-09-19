# Prompt di continuità — B–C Export/Import avanzati (19/09/2026)

**Ingresso unico** per questa linea. Non mescolare con Utility UI backlog, Motore B, Dashboard (D).

**Identità:** una sola **BoxManager**. Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

**Test telefono:** solo su `main` — `.cursor/rules/test-telefono-main.mdc`.

**Fonte requisiti (congelata):** [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md).  
**Assessment (storico):** [ASSESSMENT_EXPORT_IMPORT_AVANZATO.md](ASSESSMENT_EXPORT_IMPORT_AVANZATO.md).

### Backlog aperto (in evidenza — non è questa fetta)

Fonte viva: [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md).

| ID | Indicazione |
|----|-------------|
| **B-UTILITY-BUTTON-HEIGHT** | Ridurre altezza bottoni Utility — **Aperto** 18/09 |
| **B-UTILITY-RIPRISTINA-VISIBLE** | Ripristina non visibile senza scroll — **Aperto** 18/09 |
| **B-PREMIUM-UNIFY-PAGE** | Pagina premium unica — **Aperto** 18/09 |
| **B-UI-BUTTONS-ACCESSO-RAPIDO** | Bottoni Accesso rapido — **Aperto** |
| **B-QR-BATCH-CODE-FONT** / **B-QR-BATCH-BOX-NAME** | QR batch — **Aperto** |
| *(dopo B–C)* | Sequenza: Utility UI → Premium unify → QR batch (SI Renato 18/09) |

---

## Stato

| Voce | Valore |
|------|--------|
| **SI apertura codice** | Renato 18/09 — roadmap dopo A3 |
| **Branch tipico** | `cursor/bc-import-error-report-8f03` (B4) |
| **Build play** | **`v. 1.3.15`** (vc **19**) — testo dialog promemoria |
| **B1** | **FATTO** SI `1.3.10 ok` |
| **B2** | **FATTO** SI `1.3.11 ok` |
| **B3** | **FATTO** SI `1.3.13 ok` |
| **B4** | Report errori → ritest dialog **1.3.15** |

---

## Micro-step B–C (ordine)

| # | Scope | Requisiti | Stato |
|---|--------|-----------|-------|
| **B1** | Export selezione | R2, R3 | **FATTO** `1.3.10 ok` |
| **B2** | CSV v2 Utility + Modello; V1 importabile | R7, R4 | **FATTO** `1.3.11 ok` |
| **B3** | Import: validazione estesa | R5 | **FATTO** `1.3.13 ok` |
| **B4** | Import: report errori riga-per-riga (schermata + CSV `IMPORT_ERRORI_…`) | R6 | **IN CORSO** → ritest **1.3.15** (testo dialog) |

**Nota ritest:** il campo Quantità **in app** è solo numerico (`inputType=number`) — non si digita `abc` lì. Il KO quantità alfabetica si prepara **nel file CSV** (editor testo / foglio), non nel form Oggetto. Fixture pronta: `docs/famiglia/fixtures/IMPORT_TEST_ERRORI.csv`.

Dialog salvataggio (SI Renato 19/09): titolo «Importazione non riuscita»; messaggio «ATTENZIONE: questo file non può essere importato… Vuoi salvare un promemoria?»

---

## Ritest B4 dialog (dopo merge `main`) — topbar `v. 1.3.15`

1. `git checkout main` + `git pull origin main` + Run **`playDebug`**
2. Topbar **`v. 1.3.15`**
3. Importa `IMPORT_TEST_ERRORI.csv` (o altro CSV KO) → blocco + dettaglio riga
4. Dialog: testo chiaro **ATTENZIONE… Vuoi salvare un promemoria?** — SI = salva CSV; NO = chiudi
5. Rispondi `1.3.15 ok` o KO

---

## Incolla in chat (nuova sessione)

```
Continua B–C Export/Import da docs/famiglia/PROMPT_CONTINUITA_BC_EXPORT.md.
Priorità: micro-step in corso; non aprire Utility UI / Premium / QR senza SI.
Identità: una sola BoxManager. Test telefono solo su main.
```
