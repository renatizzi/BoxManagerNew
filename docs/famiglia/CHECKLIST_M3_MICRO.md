# Checklist M3 — micro-step (stato vivo)

**Aggiornato:** 13/09/2026.  
**Ingresso cutover (nuova sessione):** [PROMPT_CONTINUITA_M3_CUTOVER.md](PROMPT_CONTINUITA_M3_CUTOVER.md).  
**Contesto prep:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md).  
**Runbook:** [RUNBOOK_M3_GIORNO.md](RUNBOOK_M3_GIORNO.md).

**Delega (SI Renato 10/09):** priorità/impatto/traccia → agente; Renato OK unico su aggregati.  
Renato: smoke device, screenshot, Console/AAB sul PC, SI su merito quando previsto.

Legenda: **FATTO** · **IN CORSO** · **ATTESA Renato** · **ATTESA Google** · **DOPO**

---

## A — Preparazione

| # | Micro-step | Stato | Note |
|---|------------|-------|------|
| A1–A3 | Classe I, premium, dichiarazioni | **FATTO** | 10/09 |
| A4 | Giorni / chiusura test chiuso | **IN CORSO** | Renato 13/09: «test chiusi, pronto produzione» — **confermare in Console** (incolla stato) |
| A5–A11 | Listing, sync, smoke A7, shot, traccia, runbook | **FATTO** | |
| **B-prep** | Gate Disco/Condividi + play features | **FATTO** | Branch `cursor/qr-avanzato-analisi-8374`; **no merge `main` finché cutover** |

### Smoke gate device (prima del cutover codice)

**RINVIATO** → da fare **ora** prima di B5: Disco + Condividi senza Archivio completo = paywall.  
(Prova scaduta o debug unlock OFF; username admin da solo non apre.)

---

## B — Cutover

| # | Micro-step | Stato | Note |
|---|------------|-------|------|
| B0 | Check Console aggregato (C-A…C-E) | **ATTESA Renato** | Vedi prompt cutover |
| B1–B2 | Accesso produzione / domande Google | **ATTESA** | Dopo B0 OK |
| B3–B4 | Gate Disco/Condividi | **FATTO in B-prep** | |
| B5–B6 | Merge → `main` + **1.3** / vc > Play | **DOPO** | Nuova sessione dopo B0 |
| B7–B9 | AAB play, closed 1.3, scheda, smoke | **DOPO** | Renato PC |
| B10 | Freeze docs post-M3 | **DOPO** | Con SI |

**Traccia:** breve **closed 1.3** → poi produzione (A10 OK).

---

## C — Dopo M3

| # | Micro-step | Stato |
|---|------------|-------|
| C1 | Opz. B-ROTATE-FORM-DRAFT | **DOPO** |
| C2 | A1 — WIP `cursor/qr-avanzato-wip-parked-8374` @ `f523735` | **DOPO** + SI codice |
| C3 | A2 → A3 (+ OCR idea §6 foto) → B–C → B-QTY | **DOPO** |
| C4 | D Dashboard | no-code |

---

## Prossimo (questa conversazione → nuova sessione)

1. Renato: incolla stato **Play Console** (test / produzione / avvisi).  
2. Agente: marca B0; se PRONTO, Renato fa smoke gate device.  
3. Renato: apre **nuova** sessione Agents con ingresso `PROMPT_CONTINUITA_M3_CUTOVER.md`.  
4. Lì: cutover B5–B9.
