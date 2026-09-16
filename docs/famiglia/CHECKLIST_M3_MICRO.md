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
| A4 | Giorni / chiusura test chiuso | **FATTO** | Screenshot Console 13/09: 12 tester × 14 giorni **ok**; bottone **Richiedi per la produzione** attivo |
| A5–A11 | Listing, sync, smoke A7, shot, traccia, runbook | **FATTO** | |
| **B-prep** | Gate Disco/Condividi + play features | **FATTO** | Era su `cursor/qr-avanzato-analisi-8374`; cutover su `cursor/m3-cutover-13-d5b2` |

### Smoke gate device (prima del cutover codice)

**FATTO** SI Renato 13/09 — Disco + Condividi senza Archivio completo = paywall.

---

## B — Cutover

| # | Micro-step | Stato | Note |
|---|------------|-------|------|
| B0 | Check Console aggregato (C-A…C-E) | **PRONTO enough** | C-A OK; C-B richiesta inviata. Scheda/dichiarazioni ok al 10/09; avvisi residui solo se rossi |
| B1–B2 | Accesso produzione / domande Google | **ATTESA Google** | Richiesta inviata **13/09 ~12:59**; esame tipico ≤7 gg. **Non blocca** closed 1.3 |
| B3–B4 | Gate Disco/Condividi | **FATTO in B-prep** | |
| B5–B6 | Merge → `main` + **1.3** / vc > Play | **FATTO** | `main` @ `271808c` — play **1.3** / vc **4** (FF merge 13/09) |
| B7–B9 | AAB play, closed 1.3, scheda, smoke | **FATTO** (closed) | SI Renato 14/09: 1.3 su Play closed, scaricata e testata |
| B10 | Freeze docs post-M3 | **DOPO** | Con SI |

**Traccia:** breve **closed 1.3** → poi produzione **solo dopo** email OK Google + closed ok (A10).

**Smoke gate device:** **FATTO** SI Renato 13/09.

---

## C — Dopo M3

| # | Micro-step | Stato |
|---|------------|-------|
| C1 | B-ROTATE-FORM-DRAFT | **FATTO** | SI Renato 14/09; play **1.3.1** |
| — | B-AUTO-FILES-LIST | **FATTO** | SI 14/09; play **1.3.2** — PRE_* fuori lista Ripristino |
| C2 | A1 QR avanzato | **FATTO** | SI device Renato 14/09 — play **1.3.3** ok; polish UI batch **1.3.4 OK** |
| C3 | A2 → A3 (+ OCR idea §6 foto) → B–C → B-QTY | **DOPO** |
| C4 | D Dashboard | no-code |

---

## Smoke telefono playDebug

**FATTO** SI Renato 13/09 — topbar `v. 1.3` + smoke ok (Dashboard, Contenitori, Backup, lingua, Dark, paywall Disco/Condividi, ricerca).

## Prossimo (solo Renato PC — B7)

**Regola Renato 13/09 + 14/09 (permanente) — processo ovvio:**
1. Agente porta il codice su **`main`** (merge del branch di lavoro **prima** di chiedere il test)
2. Renato: `git checkout main` + `git pull origin main` + **Run `playDebug`** → topbar = versione detta dall’agente (ora **`v. 1.3.4`**, senza famiglia). **Mai** altri branch per il test telefono.
3. Solo dopo OK test: Renato crea AAB **`playRelease`** (con firma) → Play → Invio
4. Scheda IT/EN + `BOXMANAGER-TESTER` se closed
5. Scrivi: `SI AAB closed`
6. **Non** produzione ampia finché email Google OK + closed ok

## Prossimo ora (14/09) — due binari in parallelo

**Binario Play (non blocca il codice):**
1. **ATTESA Google** accesso produzione (B1–B2). Quando arriva: `email Google OK` → promuovi 1.3 in produzione.
2. Opz. scheda Store IT/EN / screenshot da 1.3 ufficiale se ancora da rifinire.
3. Dopo promozione: freeze docs **B10** con SI.

**Binario piano:**
1. **C1** — **FATTO** (1.3.1).
2. **B-AUTO-FILES-LIST** — **FATTO** (1.3.3 ok).
3. **C2 / A1** — **FATTO** SI device 14/09 (1.3.3 + polish **1.3.4 OK**).
4. **C3 / A2 Cestino** — **IN CORSO** SI Renato 16/09 («Riprendi da A2»).
5. Backlog QR batch (non A2): **B-QR-BATCH-CODE-FONT**, **B-QR-BATCH-BOX-NAME** — Aperto 16/09.
6. Poi A3 Foto → B–C → B-QTY; **C4** Dashboard no-code.

**Regola test:** l’agente indica sempre il `versionName` atteso in topbar; merge su **`main`** obbligatorio prima del test telefono (SI 14/09).

