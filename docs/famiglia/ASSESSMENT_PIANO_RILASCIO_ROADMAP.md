# Assessment — Piano rilascio / Play Console / Roadmap / Premium / Nota (RP)

**Stato:** ANALISI (bozza) → **FEEDBACK** — 08/09/2026. **Non CONVALIDATO.** Zero codice.  
**Processo:** passo **2** di 4.  
**Richiesta Renato (fonte, non ricomposta):** prima di un nuovo filone, definire un piano strutturato su:  
1. Rilasciare le nuove funzionalità **già implementate** (unica soluzione o più versioni).  
2. Incombenze **Play Console** sui prossimi rilasci.  
3. Aggiornare la **Roadmap ufficiale** con funzioni già sviluppate e quelle analizzate di recente.  
4. **Priorità** e conferma se **tutte** le nuove funzioni debbono essere **premium**.  
5. Verificare se la **Nota Integrata ufficiale** sia aggiornata con tutte le novità (analisi inclusa).  
**Vincolo noto:** SI codice Progetto 2 **alla fine del test Play** (già detto più volte) — non anticipare implementazione.  
**ID:** **B-PIANO-RILASCIO-RP** (Promemoria).

---

## 0. Distinzione obbligatoria (AS-IS)

| Classe | Cosa include oggi | Dove sta |
|--------|-------------------|----------|
| **I — Già in codice sviluppo** (flavor `famiglia`, etichetta `1.3-famigliaB5.24` / vc 1343) | Archivio condiviso (B0–B5), EN (M1–M2), Disco di rete (4.22), correttivi P0/P1, B5.24 CSV, Motore B F7–F9, Dark, ecc. | Branch sviluppo; **non** su Play durante il test |
| **II — Solo analisi / requisiti congelati** (zero codice applicativo) | A1 QR avanzato, A2 Cestino, A3 Foto, B–C Export/Import avanzati, D Dashboard (U0 no-code), B-QTY | `docs/famiglia/REQUISITI_*.md` |
| **III — Play attuale** | BoxManager **1.2** su `main` | Test chiuso; freeze salvo bug bloccanti |

Il punto 1 della richiesta parla di «già implementate» → classe **I** (+ passaggio M3).  
Le voci classe **II** entrano in Roadmap/Nota/priorità/premium (punti 3–5) e in **rilasci successivi** dopo che il codice sarà aperto a test chiuso.

Piano già congelato in [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) **Fase C:** a fine test la build di sviluppo **sostituisce** la 1.2 su Play (non è un’altra app).

---

## 1. Rilascio funzioni già implementate — opzioni

| Opzione | Descrizione | Pro | Contro |
|---------|-------------|-----|--------|
| **R1 — Un solo passaggio (M3)** | A test chiuso: un AAB da sviluppo → produzione/closed successore; `versionName` tipo **1.3**; package `it.renatizzi.boxmanager` | Allineato a Strategia Fase C; una sola volta in Console per «sostituire 1.2» | Bundle più grosso di novità insieme (EN + famiglia + disco + …) |
| **R2 — Più versioni Play dopo M3** | M3 porta solo il delta «già in codice»; poi **1.3.1 / 1.4…** per ogni fetta P2 (A1, A2, …) quando pronta | Controllo regressioni; store listing aggiornabile a pezzi | Più cicli Console / tester |
| **R3 — M3 minimale + feature flag** | M3 senza esporre UI nuove non finite; flag per pezzi | Raro bisogno qui: le nuove P2 **non sono ancora in codice** | Complessità inutile se II resta zero-code fino a SI |

**Raccomandazione analisi:** **R1 per il primo rilascio post-test** (classe I → Play), poi **R2** per le fette classe II man mano che il codice è CONVALIDATO. Non spezzare M3 in due app. Non caricare `famiglia` debug su Play.

**Non confondere:** WIP A1 su `cursor/qr-avanzato-wip-parked-8374` **non** entra in M3 finché non ripristinato e chiuso dopo SI codice.

---

## 2. Play Console — incombenze prossimi rilasci

### 2.1 Subito dopo chiusura test (M3) — checklist proposta

| # | Incombenza | Note |
|---|------------|------|
| C1 | Chiudere / promuovere il test chiuso secondo esito Google | Solo Renato in Console |
| C2 | Generare AAB **release** flavor **play** (package `it.renatizzi.boxmanager`), non `famiglia` | Keystore locale; `docs/play/README.md` |
| C3 | `versionCode` > ultimo su Play; `versionName` ufficiale (es. **1.3**) | Oggi play flavor in repo ancora allineato a era 1.2 — va alzato in sessione M3 |
| C4 | Release su traccia (produzione o closed successivo) | SI Renato al momento |
| C5 | Scheda Store: icona 512, feature graphic, screenshot ordine già in `docs/play/` | Verificare se servono shot nuovi (EN, Disco, famiglia) |
| C6 | Testo listing IT/EN | Bozza EN: `docs/play/store-listing-en.md` (M0); aggiornare note novità 1.3 |
| C7 | Comunicazione tester: codice `BOXMANAGER-TESTER` resta se si tiene closed; altrimenti trial/CONDIVIDI | Allegato 4.19 |
| C8 | Non mischiare AAB di sviluppo / applicationId `.famiglia` | Strategia + BETA_SYNC |

### 2.2 Rilasci successivi (post-M3, con codice P2)

| # | Incombenza | Note |
|---|------------|------|
| C9 | Nuovo AAB per ogni fetta (o batch di fette) rilasciata | R2 |
| C10 | «Novità della release» in Console | Elenco corto da Roadmap aggiornata |
| C11 | Eventuali screenshot / privacy se Foto (A3) tocca permessi | Da fare **prima** dell’upload A3 |
| C12 | Nessuna traduzione automatica Console al posto di `strings.xml` | Filone M già chiaro |

**Raccomandazione:** checklist **C1–C8** = sessione dedicata «M3 Console» a test chiuso; **C9–C12** = template ripetibile per ogni release P2.

---

## 3. Roadmap ufficiale — cosa aggiornare

**Fonte unica:** Nota Integrata **9.2** §**4.1** quadro + §**4.1.6** (conflitto: quadro prevale).

### 3.1 Già sviluppate (classe I) — da portare in quadro / stato «fatto»

| Voce | In Nota 9.2 oggi? |
|------|-------------------|
| Archivio condiviso / merge | Sì — Allegato **4.20** |
| Play paywall / Archivio completo | Sì — Allegato **4.19** |
| Disco di rete | Sì — Allegato **4.22** + quadro 07/09 |
| EN ricerca / M1–M2 | **Parziale:** Allegato **4.21** risulta in **9.1_B7**, **assente in 9.2** (gap da sanare) |
| B5.24 estensione `.csv` | Operativo in codice; **non** evidenziato in Roadmap quadro |
| Dark / AppShell / Motore B F7–F9 | Allegati già in Nota (storico B7) |

### 3.2 Analizzate di recente (classe II) — da allineare a §4.1.6 / allegati

| Voce | In §4.1.6 9.2 | Documento congelato | Gap |
|------|---------------|---------------------|-----|
| QR avanzato | Sì (titoli) | `REQUISITI_QR_AVANZATO.md` | Dettaglio/allegato non recepito |
| Export/Import avanzati | Sì (titoli) | `REQUISITI_EXPORT_IMPORT_AVANZATO.md` | Idem |
| Dashboard / UI avanzata | Sì (titoli) | `REQUISITI_DASHBOARD_UI_AVANZATA.md` | Decisione U0 / no-code da annotare |
| **Cestino** | **No** titolo dedicato | `REQUISITI_CESTINO.md` | **Assente** da 4.1.6 |
| **Foto oggetto** | **No** (solo rinvii md) | `REQUISITI_FOTO_OGGETTO.md` | **Assente** da 4.1.6 + allegato |
| **B-QTY** KPI/query quantità | **No** | `REQUISITI_QTY_KPI_SEARCH.md` | **Assente** |

**Raccomandazione:** un **unico aggiornamento Nota** (es. 9.3 o patch 9.2+) che:  
(a) marca classe I come **fatto / in V1.3 post-Play**;  
(b) inserisce Cestino + Foto + B-QTY in Roadmap con priorità;  
(c) rinvia ai REQUISITI o crea allegati numerati;  
(d) **riporta Allegato 4.21** in 9.2/9.3 se mancante.

**Non fare** prima della CONVALIDA di questo piano (passo 3–4). Zero patch Nota in questa ANALISI.

---

## 4. Priorità e premium

### 4.1 Priorità codice post-Play (già concordata, da confermare)

1. **M3** — portare classe I su Play (non è «feature P2», è cutover).  
2. Poi fette P2: **A1 → A2 → A3 → B–C**; **D** resta no-code; **B-QTY** dopo (KPI poi pattern B), con frasi 2.6 SI pre-codice.

### 4.2 Premium — stato decisioni già SI

| Funzione | Premium? | Fonte |
|----------|----------|--------|
| Ricerca avanzata, QR, Etichetta, Import, Export (V1) | Sì — Archivio completo | Allegato **4.19** |
| A1 / A2 / A3 | Sì | SI 07/09 + REQUISITI |
| B–C avanzati | Sì (`EXPORT`/`IMPORT`) | REQUISITI B–C |
| D S1 (se un giorno) | Sì | REQUISITI D |
| B-QTY | Sì | REQUISITI QTY |
| Disco di rete, Backup, CRUD, ricerca semplice, stampa | **No** (base) | 4.19 + pratica |
| Archivio condiviso (merge) | Base / famiglia — **non** gate Archivio completo sulle anagrafiche | 4.20 |
| EN (M) | Stesse gate delle funzioni già premium (ricerca avanzata resta gated) | Filone M |

**Domanda aperta esplicita di Renato:** «tutte le nuove funzioni debbono essere premium?»  
**Lettura analisi:** «tutte» = **linea Progetto 2 analizzata (A1–A3, B–C, D se codice, B-QTY)** = **sì**, già SI.  
**Non** significa premium su Disco, Backup, CRUD, EN di stringhe UI base.

**Raccomandazione:** confermare **P2-line = Archivio completo**; base free invariata (4.19). Eventuale eccezione solo con SI voce-per-voce.

---

## 5. Verifica Nota ufficiale vs novità (audit)

| Novità | Recepita in Nota **9.2**? | Dove invece |
|--------|---------------------------|-------------|
| Disco di rete | **Sì** (4.22) | — |
| Paywall / trial | **Sì** (4.19) | — |
| Merge famiglia | **Sì** (4.20) | — |
| EN / 4.21 | **No in 9.2** (gap) | 9.1_B7 + `docs/multilingua/` |
| Processo 4 fasi analisi | **No** allegato Nota | `.cursor/rules/processo-analisi.mdc` + Promemoria |
| A1 dettaglio | **No** (solo titoli 4.1.6) | `REQUISITI_QR_AVANZATO.md` |
| A2 Cestino | **No** | `REQUISITI_CESTINO.md` |
| A3 Foto | **No** | `REQUISITI_FOTO_OGGETTO.md` |
| B–C dettaglio | **No** (solo titoli) | `REQUISITI_EXPORT_IMPORT_AVANZATO.md` |
| D U0 / no-code | **No** decisione esplicita | `REQUISITI_DASHBOARD_UI_AVANZATA.md` |
| B-QTY | **No** | `REQUISITI_QTY_KPI_SEARCH.md` |
| B5.24 CSV ext | **No** esplicito | Codice + Promemoria |

**Esito audit:** la Nota **non** è allineata a tutte le novità di analisi recenti; è allineata su Disco/paywall/merge. Serve piano di **recepimento** (punto 3) dopo CONVALIDA di questo assessment.

---

## 6. Checklist FEEDBACK (RP-Q)

| # | Domanda | Raccomandazione analisi |
|---|---------|-------------------------|
| **RP-Q1** | Primo rilascio post-test: **R1** (M3 unico cutover classe I)? | **Sì — R1** |
| **RP-Q2** | Dopo M3: P2 in **più versioni Play (R2)**? | **Sì** |
| **RP-Q3** | Sessione dedicata «M3 Console» con checklist C1–C8 a test chiuso? | **Sì** |
| **RP-Q4** | Un aggiornamento Nota (Roadmap + gap 4.21 + Cestino/Foto/QTY + rinvii REQUISITI) **prima** di aprire nuovo filone funzionale? | **Sì** — recepimento come fetta documentazione, non codice |
| **RP-Q5** | Priorità codice post-Play: M3 poi A1→A2→A3→B–C; D no; B-QTY dopo? | **Sì** (già concordata) |
| **RP-Q6** | Tutte le funzioni **Progetto 2** (A1–A3, B–C, D se codice, B-QTY) = **premium** Archivio completo? | **Sì**; base free (4.19) invariata |
| **RP-Q7** | Eccezioni premium da elencare ora (es. pezzo di Cestino free)? | **Nessuna** in prima chiusura |
| **RP-Q8** | Prossimo lavoro dopo CONVALIDA di questo piano: **patch Nota / Roadmap** (no nuovo filone feature) finché gap §5 aperti? | **Sì** |

---

## 7. Fuori scope (bozza)

- Aprire codice A1–A3 ora.  
- Caricare AAB sviluppo su Play durante il test.  
- Decidere numerazione allegati Nota senza SI.  
- Riaprire Motore B V1 / D0–B7.

---

## 8. Esito ANALISI — in attesa FEEDBACK

**Prossimo:** risposte Renato su **RP-Q1–RP-Q8** (minimo Q1, Q4, Q6).  
Poi CONVALIDA merito → `REQUISITI_PIANO_RILASCIO_ROADMAP.md` (o titolo SI) → CONVALIDA documento → esecuzione patch Nota solo con SI.

**Zero codice applicativo.**
