# Assessment — OCR locale su cover (proposta testo oggetto)

**Stato:** passo **3 CONVALIDATO** SI Renato 17/09 (`CONVALIDA OCR` sul §3).  
Passo **4** in corso: bozza in [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) §6 — attesa **CONVALIDA aggiornamento documento**.  
**Fonte:** backlog **B-IDEA-OCR-COVER**.  
**Codice OCR:** solo dopo CONVALIDA passo 4.

---

## 1. Contesto

A3 Foto in implementazione. OCR: HD temporaneo → OCR → mini T2 → elimina HD (no cestino); propone solo **Descrizione**.

---

## 2. FEEDBACK Renato 17/09

| # | Risposta |
|---|----------|
| Q1 | Solo **Descrizione**. Nome manuale/vocale. |
| Q2 | Scatto **e** galleria |
| Q3 | **Premium** |
| Q4 | ML Kit on-device |
| Q5 | **SI** revisione §1 ML |
| Q6 | Senza vincolo lingua UI |
| Q7 | HD→OCR→mini→elimina HD; UI **A** nel flusso foto A3 |

---

## 3. Requisiti condivisi — **CONVALIDATI** (passo 3)

Vedi testo ufficiale proposto in `REQUISITI_FOTO_OGGETTO.md` §6 (**R-OCR-1…8**). Sintesi:

1. Solo Descrizione  
2. Scatto + galleria  
3. Pipeline HD → OCR → mini → elimina HD (no cestino; no Backup HD)  
4. Premium  
5. ML Kit on-device  
6. Lingua libera  
7. UI nel flusso foto A3  
8. Eccezione §1 limitata all’OCR testo Descrizione  

---

## 4. Fuori scope

- Cerca-per-immagine / riconoscimento oggetti  
- Barcode/catalogo  
- HD in Backup / Invia  
- Auto-Nome  

---

## 5. Prossimo passo

1. ~~FEEDBACK~~ FATTO  
2. ~~CONVALIDA merito §3~~ FATTO 17/09  
3. **CONVALIDA documento** — Renato conferma il testo in `REQUISITI_FOTO_OGGETTO.md` §1 + §6 (`CONVALIDA documento OCR`)  
4. Solo dopo: codice OCR  

Finché manca CONVALIDA documento: **zero codice OCR**.
