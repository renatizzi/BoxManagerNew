# Promemoria — interventi trasversali (BoxManager)

**Aggiornato:** 10/09/2026.  
Ingresso sessione 07/09 → [PROMPT_CONTINUITA_07_09_DISCO_FOTO.md](PROMPT_CONTINUITA_07_09_DISCO_FOTO.md).  
Correttivi → [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md).  
Regola: `.cursor/rules/annotazioni-renato.mdc`.  
Processo analisi: `.cursor/rules/processo-analisi.mdc`.

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
| **A1 QR avanzato** | [REQUISITI_QR_AVANZATO.md](REQUISITI_QR_AVANZATO.md) (**congelato** + CONVALIDA documento 07/09; R10 premium Progetto 2) · [ASSESSMENT_QR_AVANZATO.md](ASSESSMENT_QR_AVANZATO.md) (storico) | Requisiti chiusi; zero codice finché SI apertura fetta |
| **A2 Cestino** | [REQUISITI_CESTINO.md](REQUISITI_CESTINO.md) (**congelato** + CONVALIDA documento 07/09) · [ASSESSMENT_CESTINO.md](ASSESSMENT_CESTINO.md) (storico) | Requisiti chiusi; zero codice finché SI apertura fetta |
| **A3 Foto oggetto** | [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) (**congelato** + CONVALIDA documento 07/09; **R6 + T6bis Premium**) · [ASSESSMENT_FOTO_MINIATURA.md](ASSESSMENT_FOTO_MINIATURA.md) (storico) | Requisiti + tecnica chiusi; codice dopo QR + Cestino |
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
| **M3** | Sviluppo → ufficiale al posto della 1.2; screenshot; versionCode; Console |
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
| **B-ROTATE-FORM-DRAFT** | 10/09/2026 | In inserimento dati (qualsiasi elemento) la stringa digitata **sparisce** se, prima della conferma, si ruota lo schermo. | **Da risolvere** — SI Renato 10/09 (necessario fix). **Standby** fino a ripresa attività implementative **dopo fine test Play** (con M3 / apertura codice). Non implementare ora. Verificato: `AlertDialog` effimeri + recreate Activity; colpisce add/edit box, oggetto, categoria, luogo (+ dialog testo affini). Eccezione: username Impostazioni |
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
| **M0** | **Fatto** — [store-listing-en.md](../play/store-listing-en.md) |

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
