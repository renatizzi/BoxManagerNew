# Promemoria — interventi trasversali (BoxManager)

**Aggiornato:** 16/09/2026.  
Ingresso sessione 07/09 → [PROMPT_CONTINUITA_07_09_DISCO_FOTO.md](PROMPT_CONTINUITA_07_09_DISCO_FOTO.md).  
**Ingresso M3 / merge Play / roadmap** → [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md).  
**Ingresso cutover 1.3 (nuova sessione)** → [PROMPT_CONTINUITA_M3_CUTOVER.md](PROMPT_CONTINUITA_M3_CUTOVER.md).  
**Checklist micro-step M3** → [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md).  
Correttivi → [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md).  
Regola: `.cursor/rules/annotazioni-renato.mdc`.  
Processo analisi: `.cursor/rules/processo-analisi.mdc`.  
**Test telefono (SI 14/09 permanente):** solo su `main` — merge prima di chiedere `playDebug` (`.cursor/rules/test-telefono-main.mdc`).

---

### Processo analisi (obbligatorio per nuove voci)

Per requisiti / Progetto 2 / documenti destinati alla Nota — **4 fasi**:

1. **ANALISI** — bozza scritta (assessment), **senza codice** salvo SI esplicito  
2. **FEEDBACK** — chiarimenti e SI Renato in chat  
3. **CONVALIDA** — dell’**analisi** e dei **requisiti condivisi** (merito); nessun aggiornamento ufficiale prima  
4. **AGGIORNAMENTO DOCUMENTO** — scrittura/congelamento; chiusura con **CONVALIDA dell’aggiornamento del documento**  

Esempio già seguito: [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md).  
Ingresso sessione 07/09: [PROMPT_CONTINUITA_07_09_DISCO_FOTO.md](PROMPT_CONTINUITA_07_09_DISCO_FOTO.md).

---

### Analisi anticipate (zero codice) — Progetto 2

**Premium trasversale (SI Renato 07/09/2026):** A1 QR avanzato, A2 Cestino, A3 Foto (e la linea Progetto 2 in analisi) = funzionalità **Archivio completo**, soggette alle condizioni di accesso vigenti.

**Policy codice Progetto 2 (chiarito 07/09 sera):** durante il test Play 1.2 resta solo **analisi/documenti**. Niente implementazione A1/A2/A3 finché test chiuso **oppure** SI esplicito del tipo «apri fetta codice». Un «Sì» di conferma documenti **non** apre il codice. (Commit prematuro A1 revertato sul branch lavoro; **WIP conservato** su `cursor/qr-avanzato-wip-parked-8374` @ `f523735` per ripresa dopo Play.)

Sequenza concordata: **A1 QR avanzato → A2 Cestino → A3 Foto** → poi B/C/D. Assessment:

| Voce | Documento | Stato |
|------|-----------|--------|
| **A1 QR avanzato** | [REQUISITI_QR_AVANZATO.md](REQUISITI_QR_AVANZATO.md) (**congelato** + CONVALIDA documento 07/09; R10 premium) · [ASSESSMENT_QR_AVANZATO.md](ASSESSMENT_QR_AVANZATO.md) | **FATTO** SI device Renato 14/09 — batch + UI standard su play **1.3.4 OK** |
| **A2 Cestino** | [REQUISITI_CESTINO.md](REQUISITI_CESTINO.md) (**congelato** + CONVALIDA documento 07/09) · [ASSESSMENT_CESTINO.md](ASSESSMENT_CESTINO.md) (storico) | **FATTO** SI device Renato 16/09 — play **1.3.7 ok** |
| **A3 Foto oggetto** | [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) (**congelato** + CONVALIDA documento 07/09; **R6 + T6bis Premium**) · [ASSESSMENT_FOTO_MINIATURA.md](ASSESSMENT_FOTO_MINIATURA.md) (storico) · OCR: [ASSESSMENT_OCR_COVER.md](ASSESSMENT_OCR_COVER.md) | **IN CORSO** codice SI Renato 17/09 — play target **1.3.8**; OCR solo analisi |
| **B–C Export/Import avanzati** | [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) (**congelato** + CONVALIDA documento 08/09) · [ASSESSMENT_EXPORT_IMPORT_AVANZATO.md](ASSESSMENT_EXPORT_IMPORT_AVANZATO.md) | Requisiti chiusi; zero codice finché SI apertura fetta (dopo A1–A3) |
| **D Dashboard / UI avanzata** | [REQUISITI_DASHBOARD_UI_AVANZATA.md](REQUISITI_DASHBOARD_UI_AVANZATA.md) (**congelato** + CONVALIDA documento 08/09; U0; **no codice**) · [ASSESSMENT_DASHBOARD_UI_AVANZATA.md](ASSESSMENT_DASHBOARD_UI_AVANZATA.md) | Requisiti chiusi; zero codice finché SI apertura |
| **B-QTY-KPI-SEARCH** | [REQUISITI_QTY_KPI_SEARCH.md](REQUISITI_QTY_KPI_SEARCH.md) (**congelato** + CONVALIDA documento 08/09) · [ASSESSMENT_QTY_KPI_SEARCH.md](ASSESSMENT_QTY_KPI_SEARCH.md) | Requisiti chiusi (K1+K2); zero codice finché SI apertura |
| **B-PIANO-RILASCIO-RP** | [REQUISITI_PIANO_RILASCIO_ROADMAP.md](REQUISITI_PIANO_RILASCIO_ROADMAP.md) (**congelato**) · Nota 9.2 Allegati **4.21**+**4.23** | **CONVALIDATO** recepimento Nota 08/09; zero codice P2 fino a fine Play |

---

## Checklist V1 in attesa fine test Google Play

Lavoro ammesso **in parallelo** al test chiuso 1.2 (su branch di sviluppo, **non** su `main`).  
A test chiuso: M3 — questa BoxManager **sostituisce** la 1.2.

### Fatto (durante l’attesa)

| Voce | Stato |
|------|--------|
| Archivio condiviso B0–B5 | **CONVALIDATO** — Nota 9.2 Allegato **4.20** |
| P0 / P1 correttivi (T1–T2, igiene file) | **CONVALIDATO** |
| Filone M — Scelta lingua + ricerca EN (M1–M2, CK1–CK2) | **CONVALIDATO** |
| Promemoria batch 04/09 (nome app, auto-save nome, default IT→EN, F7 lista, ricerca vuota, voce oggetto, M0) | **Fatto** |
| Fix ricerca B5.13–B5.15 (inventari / EN) | **Fatto** |
| **B-SEL-CARTELLA** Disco di rete | **CONVALIDATO** — Nota 9.2 Allegato **4.22** + Roadmap 07/09/2026 |
| **B5.24** CSV estensione forzata | **Fatto** — `CsvFileNames` (come `.zip` Backup) |

### Aperto / continuo (ancora in V1, non V2)

| Voce | Note |
|------|------|
| **P2** sync bugfix Play 1.2 → sviluppo | Processo continuo — [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) |
| Bug **bloccanti** segnalati dai tester Play | Solo su `main` / 1.2; non mescolare con sviluppo |
| **B-FAMILY-DOMAIN-ERR** | Opzionale: messaggi dominio merge ancora IT in path rari |
| Idee non pianificate (es. suggerimento Host = gateway Wi‑Fi) | Solo con SI esplicito |

### Solo a test Play **chiuso** (non prima)

| Voce | Note |
|------|------|
| **M3** | Sviluppo → ufficiale al posto della 1.2; screenshot; versionCode; Console — **ingresso:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md) |
| SMB nativo in BoxManager | **Fuori V1** (resta CIFS esterno) |
| Voci Roadmap **4.1.6** / Progetto 2 | Dopo Play verde |

### Fuori scope finché il test è aperto

- Merge dello sviluppo su `main`
- Pubblicare flavor `famiglia` sulla Console
- Anticipare blocchi V2 (4.1.6) prima della convalida

---

## Backlog aperto (in evidenza)

Indicazioni di Renato **fuori dalla fetta in corso**. Restano qui in cima **fino alla presa in carico**. Non implementare prima del SI.

| ID | Data | Indicazione | Stato |
|----|------|-------------|-------|
| **B-CURSOR-CREDITS** | 16/09/2026 | Rinnovato abbonamento Cursor: ottimizzare l’interazione per non sprecare crediti (mese scorso esauriti in ~1 settimana). | **Aperto** — regole operative sotto; SI Renato su eventuale regola agente permanente |
| **B-UI-BUTTONS-ACCESSO-RAPIDO** | 16/09/2026 | Uniformare il formato dei bottoni come da screenshot allegato (Accesso rapido Dashboard: card `MaterialCardView`, testo centrato grassetto ~18sp, `elevated_surface`; eventualmente senza icona). | **Aperto** |
| **B-TEST-ISTRUZIONI-OPERATIVE** | 16/09/2026 | Ogni richiesta di test telefono deve essere accompagnata da istruzioni operative sulle attività di Renato (passi concreti); escludere indicazioni generiche e vaghe. | **SI Renato 16/09** — regola agente: `.cursor/rules/test-telefono-main.mdc`, [SOLO_TU.md](SOLO_TU.md) |
| **B-QR-BATCH-CODE-FONT** | 16/09/2026 | Usare un carattere più piccolo per il codice riportato nella stampa multietichette (al momento i numeri si sovrappongono e nel caso si dovessero tagliare, non è chiaro quale sia la linea di demarcazione) | **Aperto** |
| **B-QR-BATCH-BOX-NAME** | 16/09/2026 | Sopra al codice inserire SEMPRE il nome del contenitore (IN GRASSETTO) per non fare confusione quando le etichette devono essere incollate | **Aperto** |
| **B-ROTATE-FORM-DRAFT** | 10/09/2026 | In inserimento dati (qualsiasi elemento) la stringa digitata **sparisce** se, prima della conferma, si ruota lo schermo. | **FATTO C1** SI Renato 14/09 — `configChanges` su Main/BoxDetail/Categorie/Luoghi; merge `main`; build **1.3.1** |
| **B-AUTO-FILES-LIST** | 14/09/2026 | Regressione: file **automatici** (`PRE_RESTORE_`, `PRE_IMPORT_`) non in lista Ripristino dell’app; restano con **Sfoglia**. | **FATTO** 14/09 — `BackupConfiguration.isAutomaticBackupFileName` + filtro sempre attivo; play **1.3.2** |
| **B-PREMIUM-POST-12** | 10/09/2026 | Principio: post-1.2 = premium salvo eccezioni. **Free:** IT/EN, Dark, fix, Guida. **Premium:** archivio condiviso + **Disco di rete** (correzione: non free). | **SI Renato 10/09** — docs RP/ingresso M3 aggiornati; gate codice Disco a M3; patch Nota 4.23 |
| **B-IDEA-OCR-COVER** | 10/09/2026 | Idea: OCR locale → Descrizione oggetto. | **CONVALIDA merito** 17/09; bozza in [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) §6 — attesa **CONVALIDA documento**; zero codice OCR |
| **B-DELEGA-PIANO** | 10/09/2026 | Su priorità / impatto / traccia release e scelte di piano: decide l’agente; Renato OK unico su aggregati. | **SI Renato 10/09** — in [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md) |
| **B-PRE-PLAY-PHONE-TEST** | 13/09/2026 | Processo ovvio: agente modifica → Renato pull + **Run `playDebug`** (senza firma) → OK test → solo allora AAB **`playRelease`** firmato su Play. Mai Run su `playRelease` per le prove. **SI 14/09:** l’agente comunica **sempre** il `versionName` atteso in topbar (es. `v. 1.3.1`) prima del test. | **SI Renato 13/09** + precisazione versione 14/09 — SOLO_TU / checklist |
| **B-NO-FAMILY-UI** | 10/09/2026 | Chiarito: «family» = nome tecnico provvisorio (flavor/branch), non seconda app. Ufficiale = **BoxManager 1.3** su `main`, package senza `.famiglia`, topbar **1.3**. **Non** obbligatorio rimuovere «famiglia/familiari/family» da guida e messaggi in-app. | **SI Renato 10/09 sera** — correzione malinteso; runbook aggiornato |
| **B-PIANO-RILASCIO-RP** | 08/09/2026 | Piano strutturato: rilascio funzioni già implementate; Play Console; Roadmap ufficiale; priorità/premium; verifica Nota vs analisi. | **CONVALIDATO** — Nota 9.2 quadro 08/09 + Allegati **4.21**+**4.23** (SI aggiornamento Nota 08/09) |
| **B-QTY-KPI-SEARCH** | 08/09/2026 | Quantità oggetti (facoltativa) come **chiave** per nuovi KPI e nuove query Ricerca avanzata. | **CONGELATO** — [REQUISITI_QTY_KPI_SEARCH.md](REQUISITI_QTY_KPI_SEARCH.md); zero codice finché SI apertura |

**Chiuso di recente:** **B-SEL-CARTELLA** — CONVALIDATO (Nota 9.2 Allegato **4.22**). **Foto oggetto** — CONVALIDA aggiornamento documento 07/09/2026 ([REQUISITI](REQUISITI_FOTO_OGGETTO.md)).

---

## Fix ritest B5.8 → B5.15 (`cursor/promemoria-fix-d69a`)

Regressione introdotta da **B-F7-FORMATO-LISTA**: l’apertura lista Contenitori
per layout F7 aveva dirottato anche inventari/report degli altri Core.

| Domanda | Output atteso (ripristinato) |
|---------|------------------------------|
| Categorie usate / elenco categorie | Lista Categorie con **solo usate** (`FILTER_USED`); contatore = trovate; report con domanda |
| Tutti gli oggetti in archivio | Report oggetti con domanda utente |
| **Quali oggetti ho in cantina?** | Report oggetti filtrati per luogo (non lista Contenitori) |
| Elenco posizioni / luoghi in uso | Lista Posizioni **usate** (`FILTER_USED`); report con domanda |
| F7 (domande 4/7 CK2) | Lista contenitori con layout card (unico caso Motore B → lista) |
| Altro Motore B | Messaggio + stampa ad hoc |

**B5.13:** audit 20 domande; fix KO «oggetti in cantina» (router).

**B5.14 (device IT):** sole «luoghi in uso» OK — due bug UI Intent/filtro.

**B5.15 (device EN):** IT OK, EN apriva liste Contenitori / report «No. containers»:
1. `InventoryListRouter` chiamato senza `SearchLocaleContext` EN → «objects» non riconosciuto
2. function words EN senza `in`/`what`/`which`/`where`/… → luoghi nominati (Cellar) non matchavano

---

## Presa in carico 04/09/2026 (`cursor/promemoria-backlog-d69a`)

| ID | Esito |
|----|-------|
| **B-NOME-APP-BAT** | **Fatto** — launcher e bat → BoxManager (flavor Gradle `famiglia` = solo build) |
| **B-NOME-AUTO-SAVE** | **Fatto** — nome utente salvato mentre digiti / al blur; tasto Salva nascosto; modificabile |
| **B-DEFAULT-IT-EN** | **Fatto** — primo switch IT→EN: seed 16 categorie + 3 posizioni (e `box.position` se ancora seed) → EN; rinominati intatti |
| **B-F7-FORMATO-LISTA** | **Fatto** — F7 apre lista contenitori; titolo con `(oggetto)`; card categoria • posizione • data |
| **B-RICERCA-SENZA-SPECIFICHE** | **Fatto** — stringa vuota su Contenitori/Posizione/Oggetti/Categorie in dashboard; oggetti filtro vuoto = tutti |
| **B-VOCE-OGGETTO** | **Fatto** — microfono su nome e descrizione in Aggiungi/Modifica oggetto |
| **M0** | **Fatto** — listing EN base (filone M) |
| **M0.5** | **Bozze** — listing IT/EN M3: [store-listing-it.md](../play/store-listing-it.md), [store-listing-en.md](../play/store-listing-en.md); checklist C1–C8 in [docs/play/README.md](../play/README.md). Screenshot EN / novità: Renato su device |

---

## Contesto post–Play 1.2 (transizione)

Dopo il rilascio Play **1.2**, due filoni in **parallelo al test** (decisione Renato, confermata 03/09/2026): la 1.2 su `main` resta identica; questo sviluppo **diventa** l’ufficiale a test chiuso e **sostituisce** la 1.2. Si tocca 1.2 solo per bug bloccanti. Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

| Filone | Documento | Stato |
|--------|-----------|-------|
| **Archivio condiviso** | [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) B0–B5 | **CONVALIDATO** — funzione di BoxManager, non un’altra app |
| **Inglese** (scelta lingua + ricerca EN) | [../multilingua/PROMPT_CONTINUITA_M.md](../multilingua/PROMPT_CONTINUITA_M.md) | **M1/CK1** e **M2/CK2 CONVALIDATI**. M3 a test Play chiuso. |
| **Disco di rete** | Nota 9.2 Allegato **4.22** + [GUIDA_DISCO_RETE.md](GUIDA_DISCO_RETE.md) | **CONVALIDATO** |

---

## P0 — Segnalati sulla BoxManager di sviluppo

| ID | Area | Problema | Evidenza | Stato |
|----|------|----------|----------|-------|
| **T1** | Utility → **Backup Archivio** → «Backup Directory» | Nome cartella illeggibile (id opaco base64) | Screenshot Renato, B5.1, 31/08/2026 | **CONVALIDATO** B5.2 (SI Renato) |
| **T2** | **Lista Oggetti** / **Lista Oggetti Trovati** | Categoria/icona assenti o incoerenti (header / stampa-export) | Segnalazione su sviluppo; ritest 01/09/2026 tre prove OK | **CONVALIDATO** B5.3 (SI Renato) |
| **T3** | — | Secondo bug non recuperato dalle chat | — | **Chiuso** — riaprire solo con nuova evidenza |

---

## P1 — Igiene salvataggio file (regola `salvataggio-file.mdc`, chiusura B7)

Verificare **ogni** punto che scrive un file e allineare dove ha senso al criterio Esporta già convalidato:

- Nome proposto datato (`prefisso_ddMMyy_HHmm`)
- Riuso cartella dopo primo CONSENTI Android
- Box unico nome + domanda + SI/NO (sovrascrittura catalogo 2.6)
- `Modello_Importazione.csv` resta nome fisso

**Già allineati in famiglia (B4):** Invia Condivisione Archivio (`KEY_FAMILY_SHARE`, box nome, OK post-salvataggio).

**B5.7 (P1 CONVALIDATO, SI Renato device 01/09/2026):**

- **PRE_RESTORE** — SFOGLIA sceglie il ZIP; box unico nome editabile + SI/NO (titolo «Copia di sicurezza»), poi conferma ripristino.
- **Genera Modello** — `Modello_Importazione.csv` fisso; riuso cartella Backup; box con cartella visibile + pulsante **Cartella**.
- **Importa** — picker CSV (non ZIP); parte dalla cartella Backup.

**Già OK:** Backup, Esporta vista, Import auto-backup, Invia famiglia B4.

---

**P2** — Sync bugfix Play 1.2 ↔ sviluppo: [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md).

---

## Come usare questo file

- Aprire una **nuova sessione** correttivi: leggere [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) + tabella P0 + **Backlog aperto** + **Checklist V1**.
- Nuove funzionalità / requisiti: **ANALISI → FEEDBACK → CONVALIDA (analisi/requisiti) → AGGIORNAMENTO DOCUMENTO (+ CONVALIDA aggiornamento)** (regola `processo-analisi.mdc`).
- Sessione 07/09 (Disco + Foto + processo): [PROMPT_CONTINUITA_07_09_DISCO_FOTO.md](PROMPT_CONTINUITA_07_09_DISCO_FOTO.md).
- Chiudere una voce solo dopo **SI Renato** (o criterio equivalente CONVALIDATO).
- Non duplicare qui il dettaglio prodotto delle fette B0–B5 → [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md).
- Indicazione fuori contesto in un altro filone: stessa tabella Backlog aperto, stessa sessione, commit. Regola `.cursor/rules/annotazioni-renato.mdc`.
