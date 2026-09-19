# Prompt di continuità — A3 Foto oggetto (17/09/2026)

**Ingresso unico** per riprendere **questa** linea (A3 Foto + OCR + T4/T5). Non mescolare con filone M, Motore B, o Export/Import avanzati completi (B–C oltre il minimo T5).

**Identità:** una sola **BoxManager**. Non esiste un’«app famiglia». Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md), `.cursor/rules/identita-app.mdc`.

**Test telefono (SI 14/09 permanente):** solo su `main` — merge + push di `main` **prima** di chiedere `git pull` + Run `playDebug`. Indicare sempre il `versionName` atteso in topbar. Regola: `.cursor/rules/test-telefono-main.mdc`.

### Backlog aperto (in evidenza — non è questa fetta)

Fonte viva: [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md).

| ID | Indicazione |
|----|-------------|
| **B-UTILITY-BUTTON-HEIGHT** | Ridurre altezza bottoni Utility (niente scroll) — **IN CORSO** lote → [PROMPT_CONTINUITA_UI_LOTE.md](PROMPT_CONTINUITA_UI_LOTE.md) |
| **B-UTILITY-RIPRISTINA-VISIBLE** | Tasto Ripristina visibile senza scroll — **IN CORSO** (stessa fetta) |
| **B-PREMIUM-UNIFY-PAGE** | Pagina premium unica (scaduta → CONDIVIDI/CODICE) — **IN CORSO** lote UI |
| **B-PLAY-DEX-R8** | Play DEX obfuscation 1%; enforcement feb 2027 — non bloccante oggi |
| **B-CURSOR-CREDITS** | Ottimizzare sessioni Cursor (crediti) — **Aperto** 16/09 |
| **B-UI-BUTTONS-ACCESSO-RAPIDO** | Caratteri bottoni «azione» = **Ordina** (testo 19/09) — **IN CORSO** lote UI |
| **B-VOICE-MIC-ACCIDENTAL** | Evitare avvio involontario microfono — **IN CORSO** lote UI *(nuovo 19/09)* |
| **B-QR-BATCH-CODE-FONT** | Stampa multietichette: carattere più piccolo per il codice — **IN CORSO** lote UI |
| **B-QR-BATCH-BOX-NAME** | Stampa multietichette: nome contenitore in grassetto sopra al codice — **IN CORSO** lote UI |
| **B-QTY-KPI-SEARCH** | **CONGELATO** — zero codice finché SI apertura |
| *(altre)* | P2 sync continuo; B–C Export/Import avanzati — solo con SI |

---

## Stato al 18/09/2026 (sera — **A3 CHIUSO**)

| Voce | Valore |
|------|--------|
| **Stato fetta** | **FATTO** — SI Renato «A3 OK» 18/09 |
| **Build play** | Topbar **`v. 1.3.9`** (versionCode **13**) su `main` |
| **PR codice** | [#35](https://github.com/renatizzi/BoxManagerNew/pull/35) + hotfix [#36](https://github.com/renatizzi/BoxManagerNew/pull/36) — entrambi su `main` |
| **Decisione Esporta** | Card Utility **tenuta** (R3 B–C + T5); copy ≠ Backup; Esporta vista resta contestuale |
| **A3 codice** | Completo: foto + OCR + T4/T5 + hotfix Utility/EXIF/OCR max 100 |
| **Documento requisiti** | [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) — CONVALIDATO (T1–T8 + §6 OCR) |
| **Prossima fetta** | **B–C** Export/Import avanzati **oppure** backlog (QR batch / UI / **B-PREMIUM-UNIFY-PAGE**) — **solo con SI** |

### KO device 18/09 → risolti in 1.3.9

| # | Voce | Esito chiusura |
|---|------|----------------|
| 6 | Foto scatto EXIF | **OK** (hotfix) |
| 7 | OCR max caratteri | **OK** (max 100) |
| 8 | Premium UI unica | Annotato **B-PREMIUM-UNIFY-PAGE** — fuori A3 |
| — | Utility griglia | **OK** (Condividi/Cestino + Esporta tenuta) |

---

## Cosa è chiuso in codice (su `main` dopo PR #35)

### Foto (T1–T3, UI, premium)
- Galleria / scatto / rimuovi; thumb lista + anteprima; gate `OBJECT_PHOTO`
- Display 800 JPEG 75 / thumb 160 JPEG 70; file per `objectPermanentId`
- Backup ZIP formato **2** + `photos/objects/`; Ripristina REPLACE + foto
- Cestino: hard-delete foto con oggetto

### OCR Descrizione (§6, R-OCR-1…8)
- ML Kit on-device; **solo Descrizione** (Nome invariato da OCR)
- Dopo galleria/scatto: proposta; se Descrizione piena → conferma SI/NO
- HD temporaneo → OCR → mini → elimina HD
- Documento: [ASSESSMENT_OCR_COVER.md](ASSESSMENT_OCR_COVER.md) CONVALIDATO + §6 in REQUISITI

### T4 — Invia / Ricevi Archivio
- Pacchetto **ZIP**: CSV FamilyMerge + `photos/objects/`
- Prima di Invia: dialog riepilogo «Foto: N oggetti, circa X MB»
- Ricevi: ZIP **o** CSV legacy; dopo merge, `mergeFromZipEntries` per id
- Persistenza `.zip` (non forzata a `.csv`) via `ViewExportPersister`

### T5 — Esporta / Importa Dati (minimo Foto)
- Utility → card **Esporta dati** (`ExportDataActivity`)
  - **CSV V1** senza foto (fogli esterni)
  - **ZIP** con CSV **v2** (`permanentId` / `objectPermanentId`) + `photos/objects/`
- Importa: MIME CSV + ZIP; dopo MERGE, riaggancio foto
- **Non** è la fetta completa B–C (selezione contenuti, report riga-per-riga) — quella resta [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) fino a SI apertura

### Unit test
- `FamilyArchiveZipTest`, `ImportDataZipRoundTripTest`, `ImportFileInspectorTest` (ZIP+v2), family/importdata/photo — OK su playDebugUnitTest

---

## Passi (chiusura)

| # | Azione | Chi | Note |
|---|--------|-----|------|
| **1** | Merge PR **#35** su `main` | Agente | **FATTO** |
| **2–3** | Istruzioni + Run `playDebug` 1.3.8 | Renato | **FATTO** |
| **4 / 4b** | KO + hotfix 1.3.9 | Agente + Renato | **FATTO** — PR #36 su `main` |
| **5** | SI «A3 OK» | Renato | **FATTO** 18/09 — PROMEMORIA / CHECKLIST C4 aggiornati |
| **6** | Prossima fetta codice | — | **B–C aperto** SI 18/09 → [PROMPT_CONTINUITA_BC_EXPORT.md](PROMPT_CONTINUITA_BC_EXPORT.md); poi backlog Utility/Premium/QR in sequenza |

### Non fare (salvo SI)
- Riaprire merito R1–R6 / T1–T8 / R-OCR senza SI revisione
- Anticipare Dashboard (D) o B-QTY
- Aprire codice B–C Export avanzato senza SI

---

## Istruzioni operative (storico — A3 chiuso)

Topbar chiusura: **`v. 1.3.9`**. Solo su `main`.

(Elenco ritest usato per SI device — non ripetere salvo regressione.)

1. `git checkout main` + `git pull origin main` + Run **`playDebug`**
2. Topbar **`v. 1.3.9`**
3. Foto / OCR / Premium / Backup / Invia-Ricevi / Esporta-Importa / Cestino — criteri §4

---

## Criterio accettazione (da REQUISITI §4 — chiuso con SI)

1. Foto galleria + scatto; modifica; elimina foto; Salva senza foto OK  
2. Lista: thumb; tap → ingrandimento; senza foto → icona fissa  
3. Backup → Ripristina: foto presenti  
4. Invia → Ricevi: foto per `objectPermanentId`  
5. Esporta ZIP → Importa ZIP: foto; CSV legacy senza foto  
6. Gate premium foto  
7. OCR: proposta Descrizione; Nome invariato; solo mini in archivio  

---

## Sequenza agente (nuova sessione)

1. A3 è **CHIUSO**. Non riprendere codice A3 senza SI revisione.
2. Leggere [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md) backlog aperto.
3. Prossima fetta **solo con SI** Renato (default suggerito: B–C).
4. **Non** aprire B–C / D / B-QTY da soli.

---

## Documenti vincolanti

| File | Ruolo |
|------|--------|
| [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) | Freeze A3 + OCR §6 |
| [ASSESSMENT_OCR_COVER.md](ASSESSMENT_OCR_COVER.md) | Storico OCR CONVALIDATO |
| [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) | B–C completo — prossimo candidato codice |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Backlog + stato Progetto 2 |
| [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md) | C4 A3 **FATTO** |
| `.cursor/rules/test-telefono-main.mdc` | Solo `main` |
| `.cursor/rules/processo-analisi.mdc` | 4 fasi |
| `.cursor/rules/annotazioni-renato.mdc` | Annotazioni fuori contesto |

---

## Incolla in chat (nuova sessione — post A3)

```
A3 Foto CHIUSO (SI 18/09, play 1.3.9). Ingresso: docs/famiglia/PROMPT_CONTINUITA_A3_FOTO.md
(solo storico) oppure PROMEMORIA.

Prossima fetta: solo con SI — default B–C Export/Import avanzati
oppure backlog (B-PREMIUM-UNIFY-PAGE / QR batch / UI bottoni).
Identità: una sola BoxManager.
```
