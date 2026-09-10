# Requisiti — QR avanzato (batch / stampa multipla / condivisione)

**Stato:** CONGELATO 07/09/2026 (SI Renato).  
**CONVALIDA analisi/requisiti (passo 3):** SI Renato 07/09/2026 (Q1–Q9; Q7 nome file `QR_…`; Q2 = **Q2-Y** L0+L1).  
**CONVALIDA aggiornamento documento (passo 4):** SI Renato 07/09/2026.  
**Uso:** riferimento funzionale e tecnico-architetturale fino all’implementazione Progetto 2 / A1.  
**Destinazione:** confluire nella Nota Integrata ufficiale (rinvio da §4.1.6 QR AVANZATO) senza riaprire il merito.  
**Assessment di lavoro:** [ASSESSMENT_QR_AVANZATO.md](ASSESSMENT_QR_AVANZATO.md) (storico analisi; prevale **questo** file su conflitti).  
**Baseline V1 (non riaprire):** Nota §3.4.4 + Allegato 4.8; layout etichetta singola invariato.  
**Sequenza:** A1 (questa voce) → A2 Cestino → A3 Foto. **STOP codice** finché non c’è SI di apertura fetta (Progetto 2 dopo Play verde, salvo diverso SI).

---

## 1. Obiettivo prodotto

Estendere il QR V1 da «una etichetta per volta» a **gestione batch**: selezionare più contenitori, produrre un unico PDF (stampa multipla), **condividere** quel PDF — senza cambiare il contratto del codice QR (solo permanentId).

Perimetro = i tre bullet Nota **9.2 §4.1.6**: condivisione · stampa multipla · gestione batch.

---

## 2. Requisiti funzionali (congelati — CONVALIDA passo 3)

### R1 — Perimetro
- Solo i tre bullet Nota. Niente encoding anagrafica nel QR, niente QR su oggetti, niente branding/layout grafico diverso da V1.

### R2 — Ingresso batch
- Solo dalla **lista Contenitori** in **selectionMode** (già presente).
- Nuova azione (barra/menu selezione): etichette QR (copy da catalogare in implementazione, allineata al 2.6 senza frasi inventate se già coperto).
- Utility / Dettaglio: restano il flusso V1 singola etichetta.

### R3 — Flusso batch
1. Selezione N contenitori.  
2. Schermata riepilogo: conteggio + elenco **nomi** (ordine **A→Z** per nome).  
3. Scelta **formato foglio** (vedi R5).  
4. Azioni: **Stampa** | **Esporta PDF** | **Condividi**.  
5. Contenitori senza `permanentId`: **esclusi** con riepilogo «K esclusi» (non blocco totale).

### R4 — Singola etichetta V1
- Anteprima / Stampa / Esporta PDF da card o Dettaglio: **invariati** (layout standard 3.4.4).

### R5 — Formato foglio (Q2-Y)
Policy di imposizione sul foglio (`LabelSheetSpec`), separata dal layout etichetta V1.

| Livello | In scope A1 | Descrizione |
|---------|-------------|-------------|
| **L0** | **Sì** | 1 etichetta / pagina (multi-page PDF; allineato mentalmente a V1/A6) |
| **L1** | **Sì** | Preset A4 fissi: almeno **2×2** (4/foglio); **2×3** (6/foglio) se poco costo aggiuntivo. Margini fissi in codice |
| **L2** | **No** | Personalizzabile pieno (righe×colonne/margini liberi) — estensione futura documentata, non in questa voce |

### R6 — Condivisione
- Share sheet Android (`ACTION_SEND`) del **PDF** batch. Niente share di PNG multiple in questa voce.
- Copy distinto da Invia Archivio (etichette ≠ archivio).

### R7 — Nome file PDF batch
- Proposto: `QR_ddMMyy_HHmm.pdf` (criterio salvataggio-file: prefisso + data/ora).
- Riuso cartella dopo primo CONSENTI (stesse preferenze Backup/export dove applicabile).

### R8 — Anteprima batch
- Lista testuale + genera documento (niente carosello anteprime bitmap in prima fetta).

### R9 — Testo sotto QR
- Come oggi in V1 (`permanentId` sull’etichetta). Non riaprire 3.4.4.

### R10 — Premium
- Stessa gate `PremiumFeature.QR_LABEL` / Archivio completo del flusso etichetta V1.
- **Vincolo Progetto 2 (SI Renato 07/09/2026):** A1 QR avanzato, A2 Cestino e A3 Foto oggetto sono funzionalità **premium** (Archivio completo), soggette alle condizioni di accesso vigenti.

---

## 3. Requisiti tecnico-architetturali (congelati — CONVALIDA passo 3)

### T1 — Payload e domain
- `BoxQrPayload` invariato (`src` / `ver` / `id` = permanentId).
- QR rigenerato; non salvato in Room.
- Nessuna migrazione DB per questa voce.

### T2 — Architettura PDF
- Separare:
  1. **modello etichetta singola** (view/layout V1);
  2. **`LabelSheetSpec`** (pageSize, rows, cols, margins) per L0/L1.
- Stampa: PrintManager; L0 può restare media A6; L1 usa **ISO_A4**.
- Esporta / Condividi: stesso PDF generato.

### T3 — Stack
- Activity + XML + MVVM + Room. No Compose / Navigation Component / Hilt obbligatori.

### T4 — Non toccare
- Pipeline ricerca / Motore B; Foto oggetto; Cestino; merge famiglia; Disco di rete (salvo riuso helper cartella/file).

---

## 4. Fuori scope

- L2 personalizzabile pieno.
- Cambio payload / QR oggetti / encoding anagrafica.
- Layout grafico etichetta diverso da V1 (logo, colori, campi extra).
- Stampa Bluetooth vendor-specific.
- Motore B / Foto / Cestino (voci separate).

---

## 5. Criterio di accettazione (ritest futuro)

1. V1 singola: Anteprima / Stampa / PDF invariati.  
2. Selezione N → PDF batch L0 (N pagine) e L1 (A4 2×2; 2×3 se presente).  
3. Stampa batch apre PrintManager sul documento.  
4. Condividi apre share sheet con il PDF.  
5. Esporta propone `QR_ddMMyy_HHmm.pdf`.  
6. Box senza permanentId esclusi con riepilogo.  
7. Senza Archivio completo: stessa gate del QR V1.  
8. Nessuna migrazione Room; ricerca intatta.

---

## 6. Recepimento ufficiale

In Nota Integrata: dettaglio sotto §4.1.6 QR AVANZATO (o Allegato dedicato), con rinvio a questo file come fonte congelata fino all’implementazione.  
Non riaprire R1–R10 / T1–T4 in chat di implementazione salvo SI esplicito di revisione.

### Processo

1. ANALISI — [ASSESSMENT_QR_AVANZATO.md](ASSESSMENT_QR_AVANZATO.md)  
2. FEEDBACK — SI Q1/Q3/Q4; approfondimento Q2; Q7 nome `QR_…`  
3. CONVALIDA analisi/requisiti — **SI Renato 07/09/2026** (fino a Q9; Q2-Y)  
4. AGGIORNAMENTO DOCUMENTO — **questo file**; **CONVALIDA aggiornamento documento SI Renato 07/09/2026**
