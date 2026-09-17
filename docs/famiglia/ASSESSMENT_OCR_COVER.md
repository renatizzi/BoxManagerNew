# Assessment — OCR locale su cover (proposta testo oggetto)

**Stato:** **FEEDBACK ricevuto** 17/09/2026 (SI Renato Q1–Q7) — in attesa **CONVALIDA** analisi/requisiti condivisi.  
**Fonte idea:** [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) §6 · backlog **B-IDEA-OCR-COVER**.  
**Non è codice.** Niente OCR finché CONVALIDA → AGGIORNAMENTO DOCUMENTO.

---

## 1. Contesto

A3 Foto (supporto visivo) in implementazione (R1–R6 / T1–T8).  
OCR: dalla stessa occasione della foto, proporre testo per la **descrizione** oggetto; HD temporaneo → OCR → mini (T2) → elimina HD (no cestino).

---

## 2. FEEDBACK Renato 17/09 (testuale)

| # | Risposta |
|---|----------|
| **Q1** | Solo **Descrizione** oggetto. Il **Nome** resta inserimento manuale o vocale. |
| **Q2** | **B)** scatto **e** galleria |
| **Q3** | **A)** premium (Archivio completo). Eccezioni free solo se Renato le comunica. |
| **Q4** | Default: **ML Kit Text Recognition on-device** |
| **Q5** | **SI** — revisione esplicita del fuori-scope «niente ML» del §1 foto |
| **Q6** | Default: OCR **senza** vincolo lingua UI |
| **Q7** | Sequenza: foto HD → OCR → Minifoto → elimina originale (**no cestino**). UI: **A)** nello stesso flusso foto A3 (se A non coerente → B). **Decisione analisi:** **A è coerente** con la sequenza. |

---

## 3. Requisiti condivisi proposti (per CONVALIDA)

### R-OCR-1 — Campo target
- L’OCR propone testo **solo** nel campo **Descrizione**.
- Il **Nome** (tipo oggetto) non viene riempito dall’OCR.

### R-OCR-2 — Origine immagine
- Attivabile da **scatto** e da **galleria** (stesso perimetro foto A3).

### R-OCR-3 — Pipeline file
1. Immagine HD (temporanea, solo sessione)  
2. OCR on-device  
3. Utente conferma/corregge la Descrizione  
4. Scrittura **solo** display 800 + thumb 160 (T2 A3)  
5. **Eliminazione immediata** del file HD (non passa dal Cestino)

### R-OCR-4 — Premium
- Gate **Archivio completo** (allineato R6 Foto / Progetto 2).

### R-OCR-5 — Motore
- **ML Kit Text Recognition** on-device (niente cloud obbligatorio).

### R-OCR-6 — Lingua
- Nessun vincolo alla lingua UI; packaging IT/EN/mixed accettati in lettura.

### R-OCR-7 — UI
- Integrato nel **flusso foto A3** (non bottone separato «Suggerisci da etichetta»), salvo ripensamento post-CONVALIDA.

### R-OCR-8 — Scope Nota
- Con CONVALIDA si **revisione** il fuori-scope «niente ML / vision» del §1 di `REQUISITI_FOTO_OGGETTO.md` limitatamente a OCR testo on-device per Descrizione (non cerca-per-immagine).

---

## 4. Fuori scope (restano fuori)

- Riconoscimento oggetti / cerca-per-immagine  
- Barcode/catalogo remoto  
- Conservazione HD in Backup / Invia Archivio  
- Auto-compilazione del Nome  

---

## 5. Impatto su A3 codice foto (V1)

| Voce | Stato |
|------|--------|
| Foto R1–R6 / Backup / UI | In corso (1.3.8) — indipendente |
| Codice OCR | **Dopo** CONVALIDA + AGGIORNAMENTO documento |
| Hook | Stesso binder foto: dopo scelta immagine → OCR → propone Descrizione → poi compressione T2 |

---

## 6. Prossimo passo processo

1. ~~FEEDBACK Q1–Q7~~ **FATTO** 17/09  
2. **CONVALIDA** Renato sul §3 (requisiti condivisi) — scrivi es. `CONVALIDA OCR`  
3. AGGIORNAMENTO: estendere `REQUISITI_FOTO_OGGETTO.md` (o Allegato OCR) + chiusura §1 ML  
4. Solo dopo: codice OCR (dipendenza ML Kit + UI)

Finché manca CONVALIDA: **zero codice OCR**.
