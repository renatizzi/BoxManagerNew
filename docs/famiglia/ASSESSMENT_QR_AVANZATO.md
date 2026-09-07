# Assessment — QR avanzato (Progetto 2 / A1)

**Stato:** ANALISI (bozza) — 07/09/2026. **Non CONVALIDATO.** Zero codice.  
**Processo:** passo 1 di 4 (ANALISI → FEEDBACK → CONVALIDA analisi/requisiti → AGGIORNAMENTO DOCUMENTO).  
**Posizione nella sequenza concordata:** **A1** → A2 Cestino → A3 Foto oggetto.  
**Fonte Roadmap:** Nota Integrata **9.2** §**4.1.6** — voce **QR AVANZATO** (Priorità: medio-alta).  
**Baseline V1 (non riaprire):** §**3.4.4** Codice QR + Allegato **4.8** (B4 CONVALIDATO 21/08/2026); ingresso etichetta da card Contenitore (B7).

---

## 0. Perché questa voce ora

- Continuity 07/09: prossima priorità Progetto 2 = **QR avanzato** o **Cestino**, prima di Foto in codice.
- Sequenza implementativa già concordata: **1 QR avanzato → 2 Cestino → 3 Foto**.
- Analisi **in anticipo** ammessa (come Foto); implementazione resta **dopo Play verde** / SI di apertura fetta (Nota: Progetto 2 dopo Play verde). Non mescolare con filone M né Motore B.

---

## 1. Fonte ufficiale — cosa dice la Nota (da recepire, non ricomporre)

### 1.1 §4.1.6 — elenco intero della voce QR AVANZATO

Testo vigente (Nota 9.2):

| Elemento | Valore |
|----------|--------|
| Titolo | **QR AVANZATO:** |
| Priorità | **medio-alta** |
| Bullet 1 | **condivisione** |
| Bullet 2 | **stampa multipla** |
| Bullet 3 | **gestione batch** |

Nient’altro sotto quella voce. Non ci sono layout multipli, formati etichetta aggiuntivi, encoding esteso, né QR su oggetti.

### 1.2 Baseline V1 (AS-IS funzionale — chiuso)

Da **3.4.4** / **4.8** (già CONVALIDATO):

- QR = collegamento permanente contenitore fisico ↔ archivio.
- Payload: **solo** identificativo permanente (`BoxQrPayload`: `src` / `ver` / `id`). Categoria, posizione, contenuto si leggono dall’archivio.
- Id Room **non** sull’etichetta; QR **rigenerato**, non salvato in DB.
- Funzioni V1: **Anteprima**, **Stampa**, **Esporta PDF** — **un** layout standard.
- Accessi: scan da Dashboard/Utility → Dettaglio Contenitore; etichetta da Dettaglio / menu card **⋮ → Visualizza etichetta QR**.
- Messaggi catalogo 2.6 (non inventarne di nuovi senza SI).

### 1.3 Codice oggi (verifica AS-IS, non requisito nuovo)

- `QrLabelActivity`: un `boxId` → una etichetta → Stampa (PrintManager A6) o Esporta PDF (`CreateDocument`).
- `QrLabelPdf`: **una pagina** per chiamata (`PAGE_WIDTH/HEIGHT` fissi).
- Lista Contenitori: già esiste **selectionMode** multiplo (oggi usato per eliminazione / azioni lista), **non** per QR batch.
- Nessuna `ACTION_SEND` / share dell’etichetta QR.

### 1.4 Fuori da questa voce (espliciti in Nota / altre voci 4.1.6)

- **Cestino** — non è bullet sotto QR AVANZATO in 4.1.6; è voce separata nella sequenza Progetto 2 (dipendenza Foto). Analisi dedicata **dopo** (o in parallelo solo se Renato SI).
- EXPORT / IMPORT AVANZATO, Dashboard avanzata, UI avanzata (header, micro-interazioni).
- Swipe tab: già V1; fuori roadmap V2.
- Encoding dati contenitore nel QR: **non si applica** (4.8 / 4.1.3).
- Foto oggetto, SMB nativo, Motore B oltre 4.17.

---

## 2. Cosa intendiamo (bozza da FEEDBACK)

Lettura proposta dei tre bullet **come un unico perimetro**:

| Bullet Nota | Lettura bozza | Domanda aperta |
|-------------|---------------|----------------|
| **gestione batch** | Selezionare **N contenitori** (riuso selectionMode lista / azione dedicata) e produrre etichette **in un colpo solo** | Ingresso: solo lista Contenitori? anche da Utility? |
| **stampa multipla** | Stampa / PDF di **N etichette** (N pagine o foglio multi-etichetta) senza ripetere Anteprima→Stampa N volte | Formato: **1 etichetta = 1 pagina** (estensione naturale di `QrLabelPdf`) **oppure** griglia su A4? |
| **condivisione** | Condividere il risultato (PDF o immagini) via share sheet Android (`ACTION_SEND` / `SEND_MULTIPLE`), oltre a salvare in cartella | Condividere **solo PDF multi-pagina**, o anche PNG singole? Cartella Backup / SAF come Esporta? |

**Ipotesi di lavoro (da SI):** i tre bullet non sono tre prodotti distinti; sono **tre facce** dello stesso flusso batch.

---

## 3. Usability (bozza)

### 3.1 Flusso proposto (singolo → invariato)

V1 resta: card / dettaglio → Visualizza etichetta → Anteprima → Stampa | Esporta PDF.  
**Non** togliere né cambiare il layout V1 senza SI.

### 3.2 Flusso proposto (batch)

1. Lista Contenitori → entra in **selezione** (già presente) → seleziona N box.  
2. Azione nuova (menu / barra selezione): **Etichette QR** (nome da catalogare).  
3. Schermata riepilogo: «N etichette» + elenco nomi (non permanentId in UI principale, salvo riga tecnica se già oggi sull’anteprima singola).  
4. Azioni: **Stampa** | **Esporta PDF** | **Condividi** (se SI su condivisione).  
5. Premium / Archivio completo: stessa gate di `QrLabelActivity` (`PremiumFeature.QR_LABEL`) — bozza: sì, stesso perimetro.

### 3.3 Ordine etichette

Bozza: ordine = ordine di selezione **oppure** ordine alfabetico nome. **Da SI.**

### 3.4 Box senza permanentId

V1 già esclude (activity finish). In batch: saltare e segnalare conteggio «K esclusi» — o bloccare tutto. **Da SI** (preferenza analisi: salta + riepilogo).

### 3.5 Rischi UX

| Rischio | Mitigazione bozza |
|---------|-------------------|
| PDF enorme (centinaia di box) | Limite soft + progresso; conferma se N > soglia (es. 50) |
| Condivisione confusa con Invia Archivio | Copy distinto: «Condividi etichette PDF», non archivio |
| Griglia A4 vs 1/pagina | 1 pagina/etichetta = meno decisioni layout; griglia = più prodotto stampabile ma più design |
| Selezione usata solo per Elimina oggi | Stessa selectionMode; nuova azione accanto, senza cambiare Elimina |

---

## 4. Impatto architetturale (bozza)

| Area | Invasività | Nota |
|------|------------|------|
| Domain QR (`BoxQrPayload`, matrix, bitmap) | **Bassa** | Riuso; N encode/render |
| `QrLabelPdf` | **Media** | Multi-page document (loop pagine) |
| UI lista Contenitori | **Media** | Azione batch in selectionMode |
| Nuova Activity / estensione `QrLabelActivity` | **Media** | Preferibile Activity batch dedicata o stesso schermo con lista id |
| Share (`ACTION_SEND`) | **Bassa-media** | File cache + grant URI |
| SAF / salvataggio file | **Bassa** | Allineare a `salvataggio-file.mdc` (nome datato `ETICHETTE_…pdf`, riuso cartella) |
| Room / migrazioni | **Nessuna** | Nessun nuovo campo |
| Pipeline ricerca / Motore B | **Nessuna** | Vietato toccare |
| Family merge / tombstone | **Nessuna** in questa voce | Cestino = voce successiva |

**Stack:** Activity + XML + MVVM + Room — invariato (no Compose/Hilt).

---

## 5. Opzioni da decidere in FEEDBACK (checklist SI)

| # | Decisione | Opzioni | Raccomandazione analisi |
|---|-----------|---------|-------------------------|
| **Q1** | Perimetro | Solo i 3 bullet Nota **vs** aggiungere layout/formati | **Solo** i 3 bullet |
| **Q2** | Formato stampa/PDF batch | A) 1 etichetta = 1 pagina (A6 come oggi) · B) griglia multipla su A4 | **A** in prima fetta (meno design; estende V1) |
| **Q3** | Condivisione | A) Share PDF multi-pagina · B) anche immagini · C) solo Esporta file (niente share) | **A** se «condivisione» resta in Nota |
| **Q4** | Ingresso batch | A) solo selectionMode lista Contenitori · B) anche Utility | **A** |
| **Q5** | Ordine | A) selezione · B) nome A→Z | **B** (riproducibile) |
| **Q6** | Premium | Stessa gate QR_LABEL | **Sì** |
| **Q7** | Nome file PDF batch | Prefisso datato tipo `ETICHETTE_ddMMyy_HHmm.pdf` | **Sì** (allinea salvataggio-file) |
| **Q8** | Anteprima batch | A) lista testuale + genera · B) carosello anteprime | **A** in prima fetta |
| **Q9** | Testo sotto QR sull’etichetta | Oggi codice mostra `permanentId`. Lasciare invariato in batch? | **Invariato** (non riaprire 3.4.4) |

---

## 6. Dipendenze e sequenza

```
A1 QR avanzato  →  A2 Cestino  →  A3 Foto (REQUISITI già congelati)
```

- **A1 non dipende** da Cestino né Foto.
- **Foto dipende** da Cestino (delete/ripristino + file foto).
- Analisi **A2 Cestino** resta da aprire (Nota 4.1.6 non ha oggi un blocco «CESTINO» dedicato; compare nella sequenza Progetto 2 / requisiti Foto e in un quadro Roadmap storico). Va chiarito in ANALISI Cestino cosa è in scope (soft-delete UI vs solo allineamento delete↔file).

---

## 7. Fuori scope (bozza — da CONVALIDA)

- Cambiare payload QR / versioning breaking.
- QR su **oggetti**.
- Più layout etichetta / branding custom.
- Stampa Bluetooth vendor-specific.
- Encoding anagrafica nel QR.
- Qualsiasi lavoro Motore B / pipeline.
- Codice Foto.

---

## 8. Criterio di accettazione (bozza, post-implementazione futura)

1. V1 singola etichetta: Anteprima / Stampa / PDF **invariati**.  
2. Seleziono N contenitori → un PDF con N pagine (se Q2=A) oppure griglia (se Q2=B).  
3. Stampa batch avvia PrintManager sul documento multiplo.  
4. Se Q3=A: Condividi apre share sheet con il PDF.  
5. Box senza permanentId gestiti come da Q (salta o blocca).  
6. Nessuna migrazione Room; nessun tocco ricerca.

---

## 9. Esito ANALISI (in attesa FEEDBACK)

**Prodotto:** estendere il QR V1 da «una etichetta per volta» a **batch** (selezione N) con **stampa/PDF multipla** e, se confermato, **condivisione** del PDF — senza cambiare il contratto del codice QR.

**Prossimo passo obbligatorio:** **FEEDBACK Renato** sulle Q1–Q9 (almeno Q1–Q4 e Q3).  
Poi **CONVALIDA** del merito → solo allora **AGGIORNAMENTO DOCUMENTO** (`REQUISITI_QR_AVANZATO.md` o equivalente) + **CONVALIDA aggiornamento documento**.

**Zero codice** finché non chiudono i passi 2–4 e non c’è SI di apertura fetta implementativa.
