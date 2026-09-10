# Prompt di continuità — M3 merge / distribuzione Play / Roadmap congelata

**Ingresso unico** per la sessione post-test (o preparazione immediata pre-cutover).  
**Data bozza:** 10/09/2026.  
**Fonti vincolanti (già CONVALIDATE — non rivotare):**

| Fonte | Ruolo |
|-------|--------|
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | Una BoxManager; Fase C = sviluppo **sostituisce** 1.2 |
| [REQUISITI_PIANO_RILASCIO_ROADMAP.md](REQUISITI_PIANO_RILASCIO_ROADMAP.md) | R1/R2, checklist Console C1–C12, priorità, premium |
| Nota Integrata **9.2** Allegato **4.23** + quadro **08/09/2026** | Roadmap ufficiale recepita |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Backlog vivo + checklist V1 |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Sync bug Play ↔ sviluppo |
| `docs/play/README.md` | AAB, asset Store, tester |

**Identità:** una sola **BoxManager** (`it.renatizzi.boxmanager` su Play). Flavor `famiglia` = build di sviluppo, **non** un secondo prodotto.

### Backlog in evidenza (non blocca M3, ma va in coda post-cutover)

| ID | Indicazione |
|----|-------------|
| **B-ROTATE-FORM-DRAFT** | Perdita testo form su rotazione — **da risolvere**, standby fino a ripresa codice post-test |
| **B-QTY / A1–A3 / B–C** | Solo dopo M3 e SI «apri fetta codice» (sequenza sotto) |

---

## 0. Risposte brevi alle domande di Renato (10/09)

| Domanda | Risposta congelata (piano RP) |
|---------|-------------------------------|
| Conviene rilasciare subito **tutto** ciò che è già realizzato? | **Sì, in un solo cutover M3 (R1)** — ma solo la **classe I** (già in codice). **Non** includere A1–A3/B–C/D/QTY (classe II = solo requisiti). |
| Altri test su Play Console? | Dopo M3: opzionale **nuovo closed / open** sulla 1.3 prima di produzione ampia; non obbligatorio se promuovi direttamente. Verifica sviluppatore/pacchetti **già ok** se stato «Registrata». |
| Come integrare Multilingua (assente in 1.2)? | **Entra automaticamente con M3**: è già in codice sviluppo (filone M CONVALIDATO). In Console: listing **EN** + screenshot che mostrano Scelta lingua / UI EN; **non** usare «Traduci con Play Console» al posto di `strings.xml` / ricerca EN. |

---

## 1. Attività per il merge delle due versioni (1.2 Play ↔ sviluppo → ufficiale)

Obiettivo: a **test chiuso**, una sola ufficiale Play = contenuto sviluppo (classe I), package `it.renatizzi.boxmanager`.

### 1.1 Prima del cutover (ancora in parallelo al test — zero merge su `main` per feature)

| # | Attività | Chi | Note |
|---|----------|-----|------|
| M0.1 | Congelare elenco **classe I** da portare | Agente + Renato | Vedi §3 roadmap sotto |
| M0.2 | Smoke su build sviluppo (`assembleFamigliaDebug`) | Renato device | Archivio condiviso, EN, Disco, Backup/CSV |
| M0.3 | Sync eventuali fix bloccanti da `main` → branch sviluppo | Agente | Solo se ci sono stati fix Play ([BETA_SYNC](BETA_SYNC_POLICY.md)) |
| M0.4 | Decidere traccia post-M3 | Renato | Produzione diretta **oppure** closed 1.3 breve |
| M0.5 | Preparare listing IT/EN + screenshot aggiornati | Renato (+ bozze agent) | Bozze: `docs/play/store-listing-it.md`, `store-listing-en.md`; screenshot EN ancora Renato |
| M0.6 | **Non** caricare flavor `famiglia` / `.famiglia` su Play | Tutti | C8 |

### 1.2 Giorno M3 — git / binario (dettaglio operativo)

| # | Attività | Dettaglio |
|---|----------|-----------|
| M1 | Chiudere / promuovere il test chiuso 1.2 | Console — C1; secondo esito Google e scelta Renato |
| M2 | Aggiornare branch integrazione sviluppo | Partire dalla catena sviluppo vigente (non da `main` “vuoto”); includere Disco, M, B5.24, correttivi |
| M3 | Portare su linea **play** / `main` il codice classe I | Merge controllato sviluppo → `main` (o release branch) **solo a test chiuso** + SI Console; flavor **play**, `applicationId` `it.renatizzi.boxmanager`, **senza** `FAMILY_BETA` se non previsto in release ufficiale |
| M4 | Alzare `versionCode` (> ultimo Play) e `versionName` (es. **1.3**) | C3 — topbar ufficiale, non etichetta `1.3-famigliaB5.x` |
| M5 | Generare AAB **release** flavor **play** | Keystore locale PC; `docs/play/README.md` — C2 |
| M6 | Upload AAB sulla traccia scelta | C4 |
| M7 | Aggiornare scheda Store | Icona, feature graphic, screenshot, testi IT/EN — C5/C6 |
| M8 | Comunicazione tester / utenti | Se resta closed: `BOXMANAGER-TESTER`; novità 1.3 (EN, archivio condiviso, disco, …) — C7 |
| M9 | Verifica post-install | Package giusto; lingua IT/EN; Backup; nessun applicationId `.famiglia` |
| M10 | Annotare freeze post-M3 | Aggiornare Promemoria + Nota quadro se SI |

### 1.3 Cosa **non** entra nel merge M3

- A1 QR avanzato (WIP parked `cursor/qr-avanzato-wip-parked-8374` — ripresa **dopo**)  
- A2 Cestino, A3 Foto, B–C Export/Import avanzati, B-QTY  
- D Dashboard codice (U0)  
- Fix **B-ROTATE-FORM-DRAFT** (standby: può entrare subito dopo M3 come correttivo V1.3.x se SI)  
- SMB nativo  

### 1.4 Dopo M3 — coda implementativa

1. (Opzionale) **B-ROTATE-FORM-DRAFT** — correttivo UX  
2. **A1 → A2 → A3 → B–C** (R2: una o più versioni Play)  
3. **B-QTY** (KPI poi pattern Motore B; frasi 2.6 SI pre-codice)  
4. D resta no-code  

---

## 2. Strategia distribuzione (ottimizzazione)

### 2.1 Un solo big-bang delle funzioni **già fatte** — sì (R1)

| Pro | Contro mitigato |
|-----|-----------------|
| Allineato a Strategia Fase C e RP-Q1 | Bundle novità più ampio → listing e Guida devono spiegarle |
| Un solo passaggio Console per uscire dal dualismo 1.2 / sviluppo | Tester vanno avvisati (EN, condivisione archivio, disco) |
| Evita “mezza 1.2” senza multilingua | Non include P2 non ancora codificato (corretto) |

**Non** spezzare EN / Disco / Famiglia in release Play separate **prima** di M3: sono già un unico binario di sviluppo.

### 2.2 Altri test su Play Console?

| Opzione | Quando ha senso |
|---------|-----------------|
| **Promuovere** closed 1.2 → produzione con AAB 1.3 | Se il closed ha già giorni/feedback sufficienti e vuoi velocità |
| **Nuovo closed (o open) sulla 1.3** | Se vuoi far provare EN + archivio condiviso + disco a tester **prima** del pubblico |
| Internal testing | Solo smoke tuo account |
| Verifica sviluppatore / pacchetti | **Già Registrata** (screenshot 10/09) — non dipende dalla fine del test |

Raccomandazione operativa (coerente RP): **M3 cutover**; se Renato vuole rete di sicurezza, un **breve closed 1.3** (giorni, non settimane) prima di produzione ampia — SI al momento.

### 2.3 Integrare su Play ciò che la 1.2 non ha (es. Multilingua)

| Canale | Cosa fare |
|--------|-----------|
| **Binario** | L’AAB M3 **già contiene** IT+EN (M1–M2), Scelta lingua, ricerca EN (Allegato 4.21) |
| **Scheda Store** | Aggiungere/aggiornare **listing in inglese**; titoli/descrizioni novità 1.3 |
| **Screenshot** | Almeno uno con UI EN o Impostazioni → lingua (senza dati personali) |
| **«Novità della release»** | Elenco corto: multilingua IT/EN, archivio condiviso, disco di rete, correttivi |
| **Play Console Translate** | **Non** sostituisce `values-en` né le tabelle ricerca EN — C12 |
| **Guida in-app** | Già localizzata in sviluppo; verifica smoke post-M3 |
| **Premium / trial** | Invariato Allegato 4.19; tester: `BOXMANAGER-TESTER` se closed |

Stesso schema per **archivio condiviso** e **disco di rete**: entrano col binario; in Console solo testi/screenshot/privacy se servono (Disco = CIFS esterno, non permesso SMB in-app).

### 2.4 Checklist Console M3 (ripasso C1–C8)

1. C1 — Chiudere/promuovere test 1.2  
2. C2 — AAB play release  
3. C3 — versionCode / versionName  
4. C4 — Release su traccia  
5. C5 — Asset grafici + screenshot  
6. C6 — Listing IT/EN  
7. C7 — Messaggio tester / codici  
8. C8 — Mai `.famiglia`  

Dettaglio AAB: `docs/play/README.md`.

---

## 3. Roadmap congelata (definito + realizzato fino al 10/09/2026)

Fonte ufficiale: Nota **9.2** quadro **08/09** + Allegati **4.19–4.23** + file `REQUISITI_*`.  
Questo paragrafo è lo **specchio operativo** per l’agente; in conflitto prevale la Nota / i REQUISITI CONVALIDATI.

### 3.1 Realizzato in codice (classe I) — entra in M3

| Area | Stato | Riferimento |
|------|-------|-------------|
| Core V1 D0–B7, Motore A/B F7–F9, AppShell, Dark | Fatto | Allegati 4.7–4.18 |
| Play paywall / Archivio completo | Fatto | Allegato **4.19** |
| Archivio condiviso B0–B5 | Fatto | Allegato **4.20** |
| Filone M — lingua + ricerca EN | Fatto | Allegato **4.21** |
| Disco di rete (CIFS) | Fatto | Allegato **4.22** |
| B5.24 CSV estensione forzata | Fatto | Promemoria / codice |
| P0/P1 correttivi, fix ricerca B5.13–15 | Fatto | Promemoria |

Build tipica sviluppo al 10/09: **1.3-famigliaB5.24** / vc **1343** (etichetta, non nome Store).

### 3.2 Analisi / requisiti congelati (classe II) — **non** in M3; codice dopo

| Voce | Doc | Priorità codice post-M3 |
|------|-----|-------------------------|
| A1 QR avanzato | `REQUISITI_QR_AVANZATO.md` | 1° |
| A2 Cestino | `REQUISITI_CESTINO.md` | 2° |
| A3 Foto oggetto | `REQUISITI_FOTO_OGGETTO.md` | 3° |
| B–C Export/Import avanzati | `REQUISITI_EXPORT_IMPORT_AVANZATO.md` | 4° |
| D Dashboard/UI | `REQUISITI_DASHBOARD_UI_AVANZATA.md` | **No codice** (U0) |
| B-QTY KPI/query | `REQUISITI_QTY_KPI_SEARCH.md` | Dopo B–C |
| Piano rilascio RP | `REQUISITI_PIANO_RILASCIO_ROADMAP.md` | Guida M3/R2 |

Premium linea P2 = **Archivio completo** (nessuna eccezione in chiusura RP).

### 3.3 Standby correttivo (post-test)

| ID | Cosa |
|----|------|
| **B-ROTATE-FORM-DRAFT** | Draft form perso su rotazione — fix dopo ripresa codice |

### 3.4 Fuori / sospeso

| Voce | Nota |
|------|------|
| PATTERN_010 | SOSPESO |
| SMB nativo in BoxManager | Fuori V1 |
| Motore B oltre F7–F9 senza SI | Non riaprire |
| Billing IAP | Congelato finché non c’è quadro fiscale |

### 3.5 Sequenza ufficiale post-Play (non invertire)

```
Fine test Play
    → M3 cutover (classe I) + Console C1–C8
    → [opz.] B-ROTATE-FORM-DRAFT
    → A1 → A2 → A3 → B–C   (R2, versioni Play successive)
    → B-QTY
    → D solo se SI futuro (oggi U0)
```

---

## 4. Sequenza agente (allineamento)

1. Leggere **questo** prompt + [REQUISITI_PIANO_RILASCIO_ROADMAP.md](REQUISITI_PIANO_RILASCIO_ROADMAP.md) + [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).  
2. Se test **ancora aperto**: niente merge feature su `main`; niente AAB sviluppo su Play; ammessi docs / preparazione listing.  
3. Se test **chiuso** + SI M3: eseguire §1.2; poi aggiornare Promemoria/Nota solo con SI.  
4. Codice P2 solo con SI «apri fetta codice» e sequenza §3.5.  
5. Processo analisi 4 fasi resta obbligatorio per nuove voci.

---

## 5. Documenti da tenere aperti

| File | Ruolo |
|------|--------|
| Questo file | Ingresso M3 / distribuzione / roadmap operativa |
| [REQUISITI_PIANO_RILASCIO_ROADMAP.md](REQUISITI_PIANO_RILASCIO_ROADMAP.md) | Decisioni R1/R2/C1–C12 |
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | Identità + Fase C |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Checklist + backlog |
| `docs/Nota_Integrata_9.2.docx` | Roadmap ufficiale + 4.19–4.23 |
| `docs/play/README.md` | AAB e asset |
| `docs/play/store-listing-it.md` | Bozza listing IT (M3 / 1.3) |
| `docs/play/store-listing-en.md` | Bozza listing EN (M3 / 1.3) |
| [PROMPT_CONTINUITA_07_09_DISCO_FOTO.md](PROMPT_CONTINUITA_07_09_DISCO_FOTO.md) | Storico analisi P2 / Disco |
| [PROMPT_CONTINUITA_M.md](../multilingua/PROMPT_CONTINUITA_M.md) | Filone EN (chiuso; utile smoke M3) |

---

## 6. Stato processo di questo documento

Scritto su richiesta Renato 10/09/2026 (prompt continuità M3 / distribuzione / roadmap).  
Allineato a decisioni **già CONVALIDATE** (RP + Strategia + Nota 9.2).  
Se Renato chiede CONVALIDA formale di **questo** testo come ingresso sessione: annotare SI qui sotto.

**CONVALIDA ingresso (passo documento):** SI Renato 10/09/2026 («testo convalidato»).
