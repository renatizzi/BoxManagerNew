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
| **Branch tipico** | `cursor/bc-export-csv-v2-8f03` (B2) |
| **Build play** | **`v. 1.3.11`** (vc **15**) dopo merge B2 su `main` |
| **A3** | **FATTO** 1.3.9 |
| **B1** | **FATTO** SI device Renato 19/09 — play **1.3.10 ok** |

---

## Micro-step B–C (ordine)

| # | Scope | Requisiti | Stato |
|---|--------|-----------|-------|
| **B1** | Export **selezione** contenitori (+ oggetti); profili Intero / Selezione | R2, R3 | **FATTO** SI 19/09 `1.3.10 ok` |
| **B2** | CSV **v2** da Utility (id stabili) + `Modello_Importazione` aggiornato; V1 resta importabile; Esporta vista resta V1 | R7, R4 CSV | **IN CORSO** → ritest **1.3.11** |
| **B3** | Import: validazione dati **estesa** | R5 | Dopo B2 device |
| **B4** | Import: report errori **riga-per-riga** | R6 | Dopo B3 |

Fuori prima fetta (R8 / §4): picker categorie/posizioni; apply parziale; .xlsx nativo.

---

## Ritest B2 (dopo merge `main`) — topbar `v. 1.3.11`

1. `git checkout main` + `git pull origin main` + Run **`playDebug`**
2. Topbar **`v. 1.3.11`**
3. Utility → Esporta → CSV: file con `formato;BoxManager_Import;2` e colonne `permanentId` / `objectPermanentId`
4. Importa → Genera Modello: stesso schema v2
5. Importa un CSV **v1** legacy: ancora ok
6. Rispondi `1.3.11 ok` o KO

---

## Incolla in chat (nuova sessione)

```
Continua B–C Export/Import da docs/famiglia/PROMPT_CONTINUITA_BC_EXPORT.md.
Priorità: micro-step in corso; non aprire Utility UI / Premium / QR senza SI.
Identità: una sola BoxManager. Test telefono solo su main.
```
