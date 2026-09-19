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
| **Branch tipico** | `cursor/bc-import-validazione-8f03` (B3) |
| **Build play** | **`v. 1.3.12`** (vc **16**) dopo merge B3 su `main` |
| **B1** | **FATTO** SI `1.3.10 ok` |
| **B2** | **FATTO** SI `1.3.11 ok` |

---

## Micro-step B–C (ordine)

| # | Scope | Requisiti | Stato |
|---|--------|-----------|-------|
| **B1** | Export selezione | R2, R3 | **FATTO** `1.3.10 ok` |
| **B2** | CSV v2 Utility + Modello; V1 importabile | R7, R4 | **FATTO** `1.3.11 ok` |
| **B3** | Import: validazione estesa (quantità, lunghezze, duplicati file) + ZIP/id già presenti | R5 | **IN CORSO** → ritest **1.3.12** |
| **B4** | Import: report errori riga-per-riga | R6 | Dopo B3 device |

---

## Ritest B3 (dopo merge `main`) — topbar `v. 1.3.12`

1. `git checkout main` + `git pull origin main` + Run **`playDebug`**
2. Topbar **`v. 1.3.12`**
3. Importa CSV valido → anteprima ok (come prima)
4. CSV con quantità `abc` o `-1` → blocco + messaggio quantità
5. CSV con nome > 100 caratteri → blocco
6. CSV con due contenitori omonimi → blocco duplicato
7. CSV v1 e ZIP v2 validi → ancora ok
8. Rispondi `1.3.12 ok` o KO

---

## Incolla in chat (nuova sessione)

```
Continua B–C Export/Import da docs/famiglia/PROMPT_CONTINUITA_BC_EXPORT.md.
Priorità: micro-step in corso; non aprire Utility UI / Premium / QR senza SI.
Identità: una sola BoxManager. Test telefono solo su main.
```
