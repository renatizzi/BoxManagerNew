# Assessment — OCR locale su cover (proposta testo oggetto)

**Stato:** **ANALISI** (fase 1 di 4) — aperta con SI A3 17/09/2026.  
**Fonte idea:** [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) §6 · backlog **B-IDEA-OCR-COVER**.  
**Non è codice.** Niente OCR in A3 V1 finché non passano FEEDBACK → CONVALIDA → AGGIORNAMENTO DOCUMENTO.

---

## 1. Contesto

A3 Foto (supporto visivo) è in implementazione (R1–R6 / T1–T8).  
L’idea OCR: dalla stessa occasione dello scatto, proporre **testo** per nome/descrizione oggetto, poi tenere solo mini display/thumb (T2). L’HD è temporaneo.

Renato 17/09: in A3 **prendere in considerazione** questa nota (non implementare di nascosto).

---

## 2. Intento prodotto (da §6)

1. Scatto (o frame) HD **temporaneo**  
2. OCR **locale on-device** (niente internet obbligatorio)  
3. Utente **conferma/corregge** nome e/o descrizione  
4. Persistono solo display 800 + thumb 160  
5. HD **non** in archivio né Backup  

Barcode scartato (offline inutile senza catalogo) — SI 10/09.

---

## 3. Domande aperte (FEEDBACK Renato)

| # | Domanda | Opzioni bozza |
|---|---------|----------------|
| Q1 | Solo **nome** tipo, o anche **descrizione**? | A) solo nome · B) nome+descrizione se OCR ricco |
| Q2 | Solo da **scatto**, o anche **galleria**? | A) solo scatto · B) entrambi |
| Q3 | Gate **Archivio completo** (come R6 foto)? | A) sì premium · B) free (sconsigliato se usa ML kit) |
| Q4 | Libreria: ML Kit Text Recognition on-device vs alternativa? | Default proposta: ML Kit on-device |
| Q5 | Revisione esplicita del fuori-scope «niente ML» del §1 requisiti foto? | Serve SI se si conferma OCR |
| Q6 | Lingua / mixed IT-EN sul packaging? | Default: OCR senza vincolo lingua UI |
| Q7 | Stesso gesto del flusso foto A3 o passo separato «Suggerisci da etichetta»? | A) opzione nello scatto · B) bottone dedicato |

---

## 4. Impatto su A3 V1 (ora)

| Voce | Decisione bozza analisi |
|------|-------------------------|
| Codice OCR in 1.3.8 | **No** — solo foto R1–R6 |
| Hook UI | Lasciare spazio mentale: dopo scatto, in seguito «Proponi testo» |
| Storage HD | Non introdurre file HD permanenti in A3 |
| Privacy / Data safety | Se OCR SI: aggiornare testi (on-device, no cloud) |

---

## 5. Prossimo passo processo

1. Renato risponde Q1–Q7 (FEEDBACK)  
2. CONVALIDA analisi  
3. AGGIORNAMENTO: estendere REQUISITI_FOTO o Allegato OCR dedicato  
4. Solo dopo: codice OCR (dipendenza + UI conferma)

Finché manca CONVALIDA: **B-IDEA-OCR-COVER** resta Idea/Analisi, zero codice OCR.
