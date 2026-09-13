# Assessment — QR avanzato (Progetto 2 / A1)

**Stato:** storico ANALISI/FEEDBACK; **prevale** [REQUISITI_QR_AVANZATO.md](REQUISITI_QR_AVANZATO.md) (**CONGELATO** + CONVALIDA documento SI Renato 07/09/2026). Zero codice.  
**Processo:** **chiuso** (4/4).  
**Posizione nella sequenza concordata:** **A1** → A2 Cestino → A3 Foto oggetto.  
**Fonte Roadmap:** Nota Integrata **9.2** §**4.1.6** — voce **QR AVANZATO** (Priorità: medio-alta).  
**Baseline V1 (non riaprire):** §**3.4.4** Codice QR + Allegato **4.8** (B4 CONVALIDATO 21/08/2026); ingresso etichetta da card Contenitore (B7).  
**Su conflitti prevale** [REQUISITI_QR_AVANZATO.md](REQUISITI_QR_AVANZATO.md).

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

## 2. Cosa intendiamo (dopo FEEDBACK 07/09)

Lettura dei tre bullet **come un unico perimetro** — **SI Renato** (allineato alle raccomandazioni Q1/Q3/Q4; Q2 in approfondimento §5.1):

| Bullet Nota | Lettura concordata | Chiusura |
|-------------|-------------------|----------|
| **gestione batch** | Selezionare **N contenitori** (riuso selectionMode lista) e produrre etichette **in un colpo solo** | **Q4 = A** — solo lista Contenitori |
| **stampa multipla** | Stampa / PDF di **N etichette** senza ripetere Anteprima→Stampa N volte; formato pagina = **Q2** (§5.1) | Vedi impatto A4 multi-etichetta |
| **condivisione** | Share sheet Android del **PDF** prodotto (`ACTION_SEND`) | **Q3 = A** — solo PDF (niente PNG multiple in prima fetta) |

**SI:** i tre bullet non sono tre prodotti distinti; sono **tre facce** dello stesso flusso batch. **Q1 = solo i 3 bullet Nota** (niente encoding, niente QR oggetti, niente branding custom oltre al foglio).

---

## 3. Usability (bozza)

### 3.1 Flusso proposto (singolo → invariato)

V1 resta: card / dettaglio → Visualizza etichetta → Anteprima → Stampa | Esporta PDF.  
**Non** togliere né cambiare il layout V1 senza SI.

### 3.2 Flusso proposto (batch) — SI ingresso lista

1. Lista Contenitori → entra in **selezione** (già presente) → seleziona N box.  
2. Azione nuova (menu / barra selezione): **Etichette QR** (nome da catalogare).  
3. Schermata riepilogo: «N etichette» + elenco nomi; se Q2 prevede fogli A4 → scelta **formato foglio** (§5.1).  
4. Azioni: **Stampa** | **Esporta PDF** | **Condividi** (PDF).  
5. Premium / Archivio completo: stessa gate `PremiumFeature.QR_LABEL` (**Q6 = Sì**).

### 3.3 Ordine etichette

**Q5 = B (proposta, in attesa conferma esplicita):** ordine alfabetico nome A→Z (riproducibile).

### 3.4 Box senza permanentId

Preferenza analisi: **salta + riepilogo** «K esclusi». Conferma in CONVALIDA.

### 3.5 Rischi UX

| Rischio | Mitigazione |
|---------|-------------|
| PDF enorme (centinaia di box) | Limite soft + progresso; conferma se N > soglia (es. 50) |
| Condivisione confusa con Invia Archivio | Copy distinto: «Condividi etichette PDF», non archivio |
| Griglia A4 illeggibile / QR troppo piccolo | Preset con QR min. utile; vietare densità oltre soglia |
| Taglio stampante / margini | Margini minimi fissi nei preset; avviso se stampante “fit to page” |
| Selezione usata solo per Elimina oggi | Stessa selectionMode; nuova azione accanto, senza cambiare Elimina |

---

## 4. Impatto architetturale

| Area | Solo 1 etichetta/pagina (A) | + Foglio A4 multi-etichetta (preset / personalizzabile) |
|------|-----------------------------|--------------------------------------------------------|
| Domain QR (`BoxQrPayload`, matrix, bitmap) | **Bassa** — riuso N encode/render | **Bassa** — stesso; eventuale size matrix per cella più piccola |
| Generatore PDF | **Media** — loop pagine A6 (`QrLabelPdf` multi-page) | **Media-alta** — motore **layout foglio**: griglia, margini, cella, overflow pagine |
| UI lista Contenitori | **Media** — azione batch | Invariata |
| Schermata batch | **Media** — riepilogo + azioni | **+** selettore formato (e, se full custom, controlli densità) |
| Share / SAF | **Bassa-media** | Invariata (sempre un PDF) |
| Room / migrazioni | **Nessuna** | **Nessuna** se i preset sono codice/costanti; **bassa** solo se si persistono preferenze utente (SharedPreferences, non Room) |
| Pipeline / Motore B / Foto | **Nessuna** | **Nessuna** |

**Stack:** Activity + XML + MVVM + Room — invariato (no Compose/Hilt).

**Gancio architetturale (indipendente dalla scelta di prima fetta):** separare (1) *modello etichetta singola* (view V1) da (2) *policy di imposizione sul foglio* (`LabelSheetSpec`: pageSize, rows, cols, margins). Così A4 multi-etichetta non forza un riscrittura del flusso batch se arriva dopo.

---

## 5. Checklist SI — CONVALIDATA (passo 3, 07/09/2026)

| # | Decisione | Esito |
|---|-----------|--------|
| **Q1** | Solo i 3 bullet Nota | **SI** |
| **Q2** | Formato stampa/PDF | **Q2-Y** — L0 + L1 (preset A4); L2 fuori. SI implicito in «convalida fino a Q9» sulla raccomandazione §5.1 |
| **Q3** | Condivisione = share PDF | **SI** (A) |
| **Q4** | Ingresso = selectionMode lista Contenitori | **SI** (A) |
| **Q5** | Ordine nome A→Z | **SI** (B) |
| **Q6** | Premium = stessa gate QR_LABEL | **SI** |
| **Q7** | Nome file PDF batch | **SI** — `QR_ddMMyy_HHmm.pdf` (non `ETICHETTE_…`) |
| **Q8** | Anteprima = lista testuale + genera | **SI** (A) |
| **Q9** | Testo sotto QR invariato | **SI** |

### 5.1 Q2 — Impatto formato personalizzabile (più etichette su A4)

Richiesta Renato (FEEDBACK): valutare in termini di impatto la possibilità di **prevedere** un formato personalizzabile = **più etichette in un foglio A4**.

#### Ordini di grandezza (foglio A4 ≈ 595×842 pt)

| Densità | Celle / foglio | Cella ≈ | QR max utile (indicativo) | Uso tipico |
|---------|----------------|---------|---------------------------|------------|
| 1×1 | 1 | foglio intero | grande | etichetta “poster” |
| 2×2 | 4 | ~298×421 | simile all’A6 V1 | buon compromesso |
| 2×3 | 6 | ~298×281 | ancora leggibile | foglio economico |
| 3×3 | 9 | ~198×281 | stretto | solo se testo minimo |
| 3×4 | 12 | ~198×210 | rischio scan difficile | sconsigliato senza prova device |

Oggi V1 stampa in **A6** una etichetta; su A4 “affiancando” celle tipo A6 entrano circa **2** per foglio in altezza, non una griglia densa senza ridimensionare.

#### Tre livelli di “personalizzabile” (impatto crescente)

| Livello | Cosa offre all’utente | Invasività | Rischio prodotto | Quando |
|---------|----------------------|------------|------------------|--------|
| **L0 — Solo 1 etichetta / pagina** (A6 o A4 1×1) | Come V1, ma N pagine in un PDF | **Media** (multi-page) | Basso | Minimo per chiudere “stampa multipla” |
| **L1 — Preset A4 fissi** (es. 2×2, 2×3) | Scelta tra 2–3 densità; margini fissi in codice | **Media-alta** | Medio (QR piccoli su 2×3/3×3) | Copre “più etichette su A4” **senza** UI da tipografo |
| **L2 — Personalizzabile pieno** (righe×colonne, margini, gap, forse formato carta) | Controlli numerici / slider; salvataggio preferenza | **Alta** | Alto (etichette illegibili, support stampante, copy, test matrice) | Non necessario per i 3 bullet Nota; feature “da tipografia” |

**Cosa costa in più L1 rispetto a L0**

- Motore PDF: non solo `startPage` in loop, ma **tile** della stessa view etichetta (o bitmap) in celle; gestione ultima pagina parziale.
- PrintAttributes: media size **ISO_A4** (oggi V1 usa A6).
- UI: un selettore «Formato: 1/pagina | A4 4 etichette | A4 6 etichette».
- Test: scan di QR ridotti da stampa reale (non solo PDF a schermo).
- Copy / guida: una riga su come tagliare il foglio.

**Cosa costa in più L2 rispetto a L1**

- UI impostazioni formato + validazione (righe/colonne min–max, margine minimo, QR sotto soglia → blocco).
- Persistenza preferenze; casi bordo (0 margini, 1×12, ecc.).
- Matrice test molto più ampia; rischio di riaprire “layout etichetta” oltre 3.4.4.
- **Non** richiesto dal testo Nota 4.1.6; è un’estensione di prodotto.

#### Raccomandazione analisi (aggiornata su richiesta Renato)

1. **Prevedere nel requisito** (documento ufficiale, quando si arriva al passo 4) che il PDF batch supporta **policy di foglio** (`LabelSheetSpec`), non solo “N×A6”.  
2. **Prima fetta implementativa:** **L0 + L1** — almeno  
   - L0: 1 etichetta / pagina (compatibile mentalmente con V1);  
   - L1: **un** preset A4 utile (consigliato **2×2** = 4/foglio) e, se poco costo aggiuntivo, **2×3**.  
3. **L2 (personalizzabile pieno):** **fuori dalla prima fetta**; resta estensione documentata (“previsto, non in V2.0 di questa voce”) — evita di gonfiare A1 oltre stampa multipla/batch/condivisione.

Così si **prevede** il formato multi-etichetta A4 (impatto controllato = L1) senza pagare subito il costo tipografico di L2.

#### Domanda Q2 — **chiusa: Q2-Y** (CONVALIDA 07/09/2026)

| Opzione | Descrizione | Esito |
|---------|-------------|-------|
| **Q2-X** | Solo L0 in A1 | — |
| **Q2-Y** | **L0 + L1 in requisiti A1** (preset A4); L2 fuori scope | **SI** |
| **Q2-Z** | L0 + L1 + **L2** in A1 | — |

---

## 6. Dipendenze e sequenza

```
A1 QR avanzato  →  A2 Cestino  →  A3 Foto (REQUISITI già congelati)
```

- **A1 non dipende** da Cestino né Foto.
- **Foto dipende** da Cestino (delete/ripristino + file foto).
- Analisi **A2 Cestino** resta da aprire (Nota 4.1.6 non ha oggi un blocco «CESTINO» dedicato; compare nella sequenza Progetto 2 / requisiti Foto e in un quadro Roadmap storico). Va chiarito in ANALISI Cestino cosa è in scope (soft-delete UI vs solo allineamento delete↔file).

---

## 7. Fuori scope (Q2-Y)

- Cambiare payload QR / versioning breaking.
- QR su **oggetti**.
- Branding / layout grafico etichetta diverso da V1 (logo, colori, campi anagrafici stampati oltre al codice già oggi).
- **L2** personalizzabile pieno.
- Stampa Bluetooth vendor-specific.
- Encoding anagrafica nel QR.
- Qualsiasi lavoro Motore B / pipeline.
- Codice Foto / Cestino.

---

## 8. Criterio di accettazione (post-implementazione futura)

1. V1 singola etichetta: Anteprima / Stampa / PDF **invariati**.  
2. Seleziono N contenitori → PDF batch L0 e L1 (preset A4).  
3. Stampa batch avvia PrintManager sul documento.  
4. Condividi apre share sheet con il PDF.  
5. Esporta propone `QR_ddMMyy_HHmm.pdf`.  
6. Box senza permanentId: salta + riepilogo.  
7. Nessuna migrazione Room; nessun tocco ricerca.

---

## 9. Esito — A1 chiuso (documento congelato)

**Prodotto congelato:** batch da lista Contenitori + L0/L1 + share PDF; nome file `QR_ddMMyy_HHmm.pdf`; payload invariato.

**Documento:** [REQUISITI_QR_AVANZATO.md](REQUISITI_QR_AVANZATO.md) — **CONGELATO** (CONVALIDA aggiornamento documento SI Renato 07/09/2026).

**Prossimo:** ANALISI **A2 Cestino**. **Zero codice** A1 finché non c’è SI di apertura fetta implementativa.
