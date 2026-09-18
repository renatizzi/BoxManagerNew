# Prompt di continuità — B–C Export/Import avanzati (18/09/2026)

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
| **Branch tipico** | `cursor/bc-export-selezione-8f03` |
| **Build play** | **`v. 1.3.10`** (vc **14**) su merge `main` |
| **A3** | **FATTO** 1.3.9 |
| **Già shippato (T5/A3)** | Utility Esporta; CSV V1; ZIP+foto V2; Import ZIP; nome datato; riuso cartella |

---

## Micro-step B–C (ordine)

| # | Scope | Requisiti | Stato |
|---|--------|-----------|-------|
| **B1** | Export **selezione** contenitori (+ oggetti collegati); profili Intero / Selezione | R2, R3 | **IN CORSO** |
| **B2** | CSV **v2** da Utility (id stabili) + `Modello_Importazione` aggiornato; V1 resta | R7, R4 CSV | Dopo B1 |
| **B3** | Import: validazione dati **estesa** | R5 | Dopo B2 |
| **B4** | Import: report errori **riga-per-riga** (schermata e/o file) | R6 | Dopo B3 |

Fuori prima fetta (R8 / §4): picker categorie/posizioni; apply parziale; .xlsx nativo.

---

## Incolla in chat (nuova sessione)

```
Continua B–C Export/Import da docs/famiglia/PROMPT_CONTINUITA_BC_EXPORT.md.
Priorità: micro-step in corso; non aprire Utility UI / Premium / QR senza SI.
Identità: una sola BoxManager. Test telefono solo su main.
```
