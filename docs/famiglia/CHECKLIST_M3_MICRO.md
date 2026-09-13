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
| B7–B9 | AAB play, closed 1.3, scheda, smoke | **ATTESA Renato** | PC: `bundlePlayRelease` + upload closed |
| B10 | Freeze docs post-M3 | **DOPO** | Con SI |

**Traccia:** breve **closed 1.3** → poi produzione **solo dopo** email OK Google + closed ok (A10).

**Smoke gate device:** **FATTO** SI Renato 13/09.

---

## C — Dopo M3

| # | Micro-step | Stato |
|---|------------|-------|
| C1 | Opz. B-ROTATE-FORM-DRAFT | **DOPO** |
| C2 | A1 — WIP `cursor/qr-avanzato-wip-parked-8374` @ `f523735` | **DOPO** + SI codice |
| C3 | A2 → A3 (+ OCR idea §6 foto) → B–C → B-QTY | **DOPO** |
| C4 | D Dashboard | no-code |

---

## Prossimo (solo Renato PC — B7)

**Regola Renato 13/09 (permanente) — processo ovvio:**
1. Agente modifica codice su `main`
2. Renato: `git pull` + **Run `playDebug`** (senza firma) → test telefono → topbar `v. 1.3` senza famiglia
3. Solo dopo OK test: Renato crea AAB **`playRelease`** (con firma) → Play → Invio
4. Scheda IT/EN + `BOXMANAGER-TESTER` se closed
5. Scrivi: `SI AAB closed`
6. **Non** produzione ampia finché email Google OK + closed ok

Ora (cutover 1.3 già su `main` / AAB già caricato): completa Invio se in bozza; oppure sul PC `playDebug` Run per vedere `v. 1.3` mentre aspetti la revisione Play.
