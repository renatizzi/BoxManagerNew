# Requisiti — Piano rilascio / Play Console / Roadmap / Premium / Nota (B-PIANO-RILASCIO-RP)

**Stato:** **CONGELATO** 08/09/2026 (SI Renato).  
**CONVALIDA analisi/requisiti (passo 3):** SI Renato 08/09/2026 — «Convalido RP-Q1–Q8» (tutte come raccomandato).  
**CONVALIDA aggiornamento documento (passo 4):** SI Renato 08/09/2026 («Convalido documento»).  
**Uso:** piano operativo fino a fine test Play e ai primi rilasci post-test; guida al recepimento Nota/Roadmap.  
**Assessment:** [ASSESSMENT_PIANO_RILASCIO_ROADMAP.md](ASSESSMENT_PIANO_RILASCIO_ROADMAP.md) (storico; prevale **questo** file).  
**STOP codice Progetto 2** fino a fine test Play (SI codice già previsto a quella data) / SI esplicito «apri fetta codice».  
**Prossimo lavoro (RP-Q8):** recepimento Nota / Roadmap — **eseguito** 08/09/2026 (patch `_patch_nota_9_2_recepimento_rp.py`: quadro 08/09, §4.1.6, Allegati **4.21** + **4.23**). **CONVALIDA aggiornamento Nota SI Renato 08/09/2026**.

---

## 0. Decisioni congelate (passo 3)

| Voce | Decisione |
|------|-----------|
| Primo rilascio post-test | **R1** — cutover unico **M3**: build di sviluppo (classe I) **sostituisce** Play 1.2 |
| Rilasci Progetto 2 | **R2** — dopo M3, fette P2 in **più versioni** Play |
| Console M3 | Sessione dedicata con checklist **C1–C8** |
| Prima di nuovo filone feature | **Recepimento Nota / Roadmap** (documentazione), non nuova feature |
| Priorità codice post-Play | **M3** → **A1 → A2 → A3 → B–C**; **D** no-code; **B-QTY** dopo |
| Premium linea P2 | **Sì** — A1, A2, A3, B–C, D (se codice), B-QTY = **Archivio completo** |
| Eccezioni premium | **Nessuna** in questa chiusura |
| Base free (4.19) | Invariata (CRUD, ricerca semplice, Backup, stampa, Disco, …) |

---

## 1. Classi di lavoro (obbligatorio)

| Classe | Contenuto | Rilascio |
|--------|-----------|----------|
| **I — Già in codice sviluppo** | Archivio condiviso, EN (M1–M2), Disco di rete, correttivi, B5.24 CSV, Motore B F7–F9, Dark, … | Entra in **M3** (R1) |
| **II — Solo analisi congelata** | A1 QR, A2 Cestino, A3 Foto, B–C Export/Import, D (U0), B-QTY | **Dopo** SI codice; Play a pezzi (**R2**) |
| **III — Play attuale** | BoxManager **1.2** su `main` | Freeze fino a M3 (salvo bug bloccanti) |

WIP A1 su `cursor/qr-avanzato-wip-parked-8374` **non** entra in M3 finché non ripristinato e chiuso dopo apertura codice.

---

## 2. R1 — Cutover M3 (post-test)

Allineato a [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) Fase C:

- Una sola app Play: package `it.renatizzi.boxmanager`.  
- AAB da flavor **play** (mai `famiglia` / `.famiglia` su Console).  
- `versionName` ufficiale (es. **1.3**); `versionCode` > ultimo su Play.  
- SI del momento = via Console (AAB, versionCode, scheda), non «scegliere se le funzioni I entrano».

---

## 3. Play Console — checklist

### 3.1 M3 (C1–C8) — obbligatoria a test chiuso

| # | Incombenza |
|---|------------|
| **C1** | Chiudere / promuovere il test chiuso secondo esito Google |
| **C2** | Generare AAB **release** flavor **play** (keystore locale; `docs/play/README.md`) |
| **C3** | Alzare `versionCode` / `versionName` ufficiali |
| **C4** | Creare release sulla traccia concordata (produzione o closed successore) |
| **C5** | Verificare icona 512, feature graphic, screenshot (`docs/play/`); aggiornare shot se servono EN/Disco/famiglia |
| **C6** | Aggiornare listing IT/EN (novità 1.3); bozza EN: `docs/play/store-listing-en.md` |
| **C7** | Comunicazione tester / codice `BOXMANAGER-TESTER` se resta closed; altrimenti trial/CONDIVIDI (Allegato 4.19) |
| **C8** | Vietato mischiare AAB sviluppo / applicationId `.famiglia` |

### 3.2 Rilasci successivi P2 (C9–C12)

| # | Incombenza |
|---|------------|
| **C9** | Nuovo AAB per fetta (o batch) rilasciata |
| **C10** | Testo «Novità della release» da Roadmap aggiornata |
| **C11** | Screenshot / privacy / permessi se A3 Foto li richiede — **prima** dell’upload |
| **C12** | Nessuna traduzione automatica Console al posto di `strings.xml` |

---

## 4. Roadmap e Nota — recepimento (prossimo lavoro documentale)

**Fonte ufficiale:** Nota Integrata **9.2** (o successore) §**4.1** quadro + §**4.1.6**.

### 4.1 Recepimento Nota 9.2 — eseguito 08/09/2026 (SI esecuzione)

Patch: `docs/_patch_nota_9_2_recepimento_rp.py` su `docs/Nota_Integrata_9.2.docx`.

1. Quadro §4.1 → **08/09/2026**; classe I pronta M3; P2 requisiti congelati.  
2. §4.1.6: stato U0/D; rinvii REQUISITI su QR/Export/Import/UI; voci **CESTINO**, **FOTO OGGETTO**, **QUANTITÀ (B-QTY)**.  
3. Allegato **4.21** (EN / filone M) riportato da 9.1_B7 e allineato a CK1–CK2.  
4. Allegato **4.23** (Progetto 2 + piano rilascio RP).  

**CONVALIDA aggiornamento Nota SI Renato 08/09/2026** sul testo in `Nota_Integrata_9.2.docx` (quadro 08/09, §4.1.6, Allegati 4.21+4.23).

### 4.2 Audit gap (storico pre-patch)

| Novità | Prima della patch 08/09 |
|--------|-------------------------|
| Disco 4.22, Paywall 4.19, Merge 4.20 | Sì |
| EN / 4.21 | No in 9.2 |
| Dettaglio A1–A3, B–C, D, B-QTY, piano RP | No / solo titoli parziali |

---

## 5. Priorità codice (quando SI a fine test)

1. **M3** (cutover classe I).  
2. **A1 QR avanzato** → **A2 Cestino** → **A3 Foto** → **B–C** Export/Import.  
3. **D** Dashboard/UI: resta **no-code** (REQUISITI D).  
4. **B-QTY**: dopo; ordine interno KPI (K1) → frasi 2.6 SI → pattern Motore B (K2).

---

## 6. Premium

- Linea **Progetto 2** (A1, A2, A3, B–C, D se un giorno codice, B-QTY) = **Archivio completo**, stesse condizioni di accesso vigenti (Allegato 4.19).  
- **Nessuna eccezione** in questa chiusura.  
- Base free invariata (4.19): anagrafiche, ricerca semplice, Backup, stampa, Disco di rete, ecc.

---

## 7. Fuori scope

- Codice A1–A3 / P2 durante il test Play.  
- Caricare flavor `famiglia` su Play.  
- Nuovo filone feature **prima** del recepimento Nota/Roadmap (RP-Q8).  
- Numerazione allegati Nota senza SI in sessione patch.

---

## 8. Processo

1. ANALISI — [ASSESSMENT_PIANO_RILASCIO_ROADMAP.md](ASSESSMENT_PIANO_RILASCIO_ROADMAP.md)  
2. FEEDBACK — RP-Q1–Q8  
3. CONVALIDA merito — **SI 08/09/2026**  
4. AGGIORNAMENTO DOCUMENTO — **questo file**; **CONVALIDA aggiornamento documento SI 08/09/2026**
