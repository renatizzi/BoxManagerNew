# Prompt di continuità — A3 Foto oggetto (17/09/2026)

**Ingresso unico** per riprendere **questa** linea (A3 Foto + OCR + T4/T5). Non mescolare con filone M, Motore B, o Export/Import avanzati completi (B–C oltre il minimo T5).

**Identità:** una sola **BoxManager**. Non esiste un’«app famiglia». Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md), `.cursor/rules/identita-app.mdc`.

**Test telefono (SI 14/09 permanente):** solo su `main` — merge + push di `main` **prima** di chiedere `git pull` + Run `playDebug`. Indicare sempre il `versionName` atteso in topbar. Regola: `.cursor/rules/test-telefono-main.mdc`.

### Backlog aperto (in evidenza — non è questa fetta)

Fonte viva: [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md).

| ID | Indicazione |
|----|-------------|
| **B-PLAY-DEX-R8** | Play DEX obfuscation 1%; enforcement feb 2027 — non bloccante oggi |
| **B-CURSOR-CREDITS** | Ottimizzare sessioni Cursor (crediti) — **Aperto** 16/09 |
| **B-UI-BUTTONS-ACCESSO-RAPIDO** | Uniformare bottoni stile Accesso rapido Dashboard — **Aperto** |
| **B-QR-BATCH-CODE-FONT** | Stampa multietichette: carattere più piccolo per il codice — **Aperto** |
| **B-QR-BATCH-BOX-NAME** | Stampa multietichette: nome contenitore in grassetto sopra al codice — **Aperto** |
| **B-QTY-KPI-SEARCH** | **CONGELATO** — zero codice finché SI apertura |
| *(altre)* | P2 sync continuo; B–C Export/Import avanzati (selezione/report) dopo chiusura device A3 |

---

## Stato al 17/09/2026 (sera — post merge)

| Voce | Valore |
|------|--------|
| **Branch lavoro** | `cursor/a3-foto-oggetto-d5b2` (chiuso) |
| **PR** | [#35](https://github.com/renatizzi/BoxManagerNew/pull/35) — **MERGIATO** su `main` (FF `b04570d`) |
| **Head `main`** | `b04570d` — tip A3 completo (foto + OCR + T4 + T5) |
| **Build play** | Topbar **`v. 1.3.8`** (versionCode **12**) — **su `main`**, pronto per test telefono |
| **Play Console** | 1.3.7 in revisione / closed; DEX non bloccante (B-PLAY-DEX-R8) |
| **A3 codice** | **COMPLETO su `main`** — **manca solo test device + SI Renato «A3 OK»** |
| **Documento requisiti** | [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) — CONVALIDATO (T1–T8 + §6 OCR) |

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

## Prossimi passi (ordine obbligatorio)

| # | Azione | Chi | Note |
|---|--------|-----|------|
| **1** | Merge PR **#35** su `main` + push `main` | Agente | **FATTO** 17/09 — FF `b04570d` |
| **2** | Istruzioni operative **numerate** a Renato | Agente | **FATTO** — topbar **`v. 1.3.8`**. Vedi sotto. |
| **3** | Renato: `checkout main` + `pull` + Run **`playDebug`** | Renato | Mai branch `cursor/…` sul telefono — **ATTESA** |
| **4** | Ritest accettazione A3 (criteri §4 REQUISITI) | Renato + agente annota | Checklist sotto — **ATTESA** |
| **5** | SI Renato «A3 OK» / chiudere fetta | Renato | Solo allora aggiornare PROMEMORIA / CHECKLIST C4 → **FATTO** |
| **6** | (Dopo A3 chiuso) prossima fetta codice | — | Default: **B–C Export/Import avanzati** (selezione, report errori) **oppure** backlog QR batch / UI bottoni — **solo con SI** Renato |

### Non fare nella prossima sessione (salvo SI)
- Riaprire merito R1–R6 / T1–T8 / R-OCR senza SI revisione
- Test telefono sul branch PR
- Anticipare Dashboard (D) o B-QTY
- Espandere Export avanzato (profili selezione, report riga-per-riga) oltre T5 già shippato

---

## Istruzioni operative test device (attive — passo 3)

Topbar attesa: **`v. 1.3.8`**. Solo su `main`.

1. `git checkout main`
2. `git pull origin main`
3. Android Studio → Build Variants → **`playDebug`** (flavor play, non release / non famiglia)
4. Run sul telefono
5. Verificare topbar **`v. 1.3.8`** (senza `famiglia`)
6. **Foto:** oggetto → galleria + scatto; thumb in lista; tap anteprima; rimuovi foto; Salva
7. **OCR:** nuovo scatto/galleria → proposta Descrizione → conferma; Nome non modificato da OCR
8. **Premium:** senza Archivio completo, gate sulle azioni foto
9. **Backup → Ripristina:** foto presenti dopo REPLACE
10. **Invia Archivio → Ricevi Archivio** (altro device o stesso dopo wipe dati se serve): ZIP; foto sugli oggetti per id; riepilogo foto in Invia
11. **Esporta dati ZIP → Importa** (su archivio dove ha senso): foto riagganciate; **Esporta CSV** resta senza foto
12. **Cestino:** elimina oggetto con foto → file foto spariti
13. Rispondere in chat con OK/KO per ogni punto 6–12 (es. `1.3.8 ok` o elenco KO)

---

## Criterio accettazione (da REQUISITI §4 — ritest)

1. Foto galleria + scatto; modifica; elimina foto; Salva senza foto OK  
2. Lista: thumb; tap → ingrandimento; senza foto → icona fissa  
3. Backup → Ripristina: foto presenti  
4. Invia → Ricevi: foto per `objectPermanentId`  
5. Esporta ZIP → Importa ZIP: foto; CSV legacy senza foto  
6. Gate premium foto  
7. OCR: proposta Descrizione; Nome invariato; solo mini in archivio  

---

## Sequenza agente (allineamento nuova sessione)

1. Leggere **questo** prompt + [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) + [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md).
2. `git fetch origin` · `main` @ tip A3 (`b04570d` o successivo docs).
3. Merge #35: **già fatto**. Non riaprire codice A3 senza SI.
4. Se manca SI device: ripetere istruzioni operative + attendere OK/KO Renato.
5. Dopo SI «A3 OK»: aggiornare PROMEMORIA / CHECKLIST C4 → **FATTO**; commit docs; **non** aprire B–C senza SI.

---

## Documenti vincolanti

| File | Ruolo |
|------|--------|
| [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) | Freeze A3 + OCR §6 |
| [ASSESSMENT_OCR_COVER.md](ASSESSMENT_OCR_COVER.md) | Storico OCR CONVALIDATO |
| [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) | B–C completo (dopo A3 device) |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Backlog + stato Progetto 2 |
| [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md) | C4 A3 |
| [SOLO_TU.md](SOLO_TU.md) | Come Renato prova sul telefono |
| `.cursor/rules/test-telefono-main.mdc` | Solo `main` |
| `.cursor/rules/processo-analisi.mdc` | 4 fasi |
| `.cursor/rules/annotazioni-renato.mdc` | Annotazioni fuori contesto |

---

## Incolla in chat (nuova sessione)

```
Continua A3 Foto da docs/famiglia/PROMPT_CONTINUITA_A3_FOTO.md.

Priorità: (1) PR #35 già su main — non ri-mergiare; (2) se manca SI device,
ripetere istruzioni playDebug topbar v. 1.3.8; (3) dopo SI «A3 OK» chiudere
PROMEMORIA/CHECKLIST C4; (4) non aprire B–C senza SI.
Identità: una sola BoxManager.
```
