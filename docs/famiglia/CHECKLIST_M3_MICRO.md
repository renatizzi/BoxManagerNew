# Checklist M3 — micro-step (stato vivo)

**Aggiornato:** 02/10/2026.  
**Ingresso chiusura allineamento:** [PROMPT_CONTINUITA_CHIUSURA_ALLINEAMENTO.md](PROMPT_CONTINUITA_CHIUSURA_ALLINEAMENTO.md).  
**Storico cutover:** [PROMPT_CONTINUITA_M3_CUTOVER.md](PROMPT_CONTINUITA_M3_CUTOVER.md).  
**Contesto prep:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md).  
**Runbook:** [RUNBOOK_M3_GIORNO.md](RUNBOOK_M3_GIORNO.md).

**Delega (SI Renato 10/09):** priorità/impatto/traccia → agente; Renato OK unico su aggregati.  
**Freeze 02/10:** B-QTY e D = tempo indeterminato; strategia dopo `produzione 1.3.25 ok`.

Legenda: **FATTO** · **IN CORSO** · **ATTESA Renato** · **ATTESA Google** · **DOPO** · **CONGELATO**

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
| B1–B2 | Accesso produzione / domande Google | **FATTO** | SI Renato 16/09: **email Google OK** |
| B3–B4 | Gate Disco/Condividi | **FATTO in B-prep** | |
| B5–B6 | Merge → `main` + **1.3** / vc > Play | **FATTO** | `main` @ `271808c` — play **1.3** / vc **4** (FF merge 13/09) |
| B7–B9 | AAB play, closed 1.3, scheda, smoke | **FATTO** (closed) | SI Renato 14/09: 1.3 su Play closed, scaricata e testata |
| B10 | Freeze docs post-M3 | **DOPO** | Dopo `produzione 1.3.25 ok` + eventuale strategia |

**Traccia:** breve **closed 1.3** → poi produzione **solo dopo** email OK Google + closed ok (A10).

**Smoke gate device:** **FATTO** SI Renato 13/09.

---

## C — Dopo M3

| # | Micro-step | Stato | Note |
|---|------------|-------|------|
| C1 | B-ROTATE-FORM-DRAFT | **FATTO** | SI Renato 14/09; play **1.3.1** |
| — | B-AUTO-FILES-LIST | **FATTO** | SI 14/09; play **1.3.2** — PRE_* fuori lista Ripristino |
| C2 | A1 QR avanzato | **FATTO** | SI device Renato 14/09 — play **1.3.3** ok; polish UI batch **1.3.4 OK** |
| C3 | A2 Cestino | **FATTO** | SI device Renato 16/09 — play **1.3.7 ok** |
| C4 | A3 → B–C → lote UI → Guida | **FATTO** | A3 1.3.9; B–C 1.3.15; lote **1.3.22**; Guida **1.3.24** |
| C4bis | Allineamento produzione + R8 | **IN CORSO** | play **1.3.25** / vc **29** — [PROMPT_CONTINUITA_CHIUSURA_ALLINEAMENTO.md](PROMPT_CONTINUITA_CHIUSURA_ALLINEAMENTO.md) |
| C5 | D Dashboard | **CONGELATO** | SI 02/10 tempo indeterminato |
| C6 | B-QTY | **CONGELATO** | SI 02/10 tempo indeterminato |

---

## Smoke telefono playDebug

**Prossimo (allineamento):** dopo merge su `main` → Run **`playDebug`** → topbar **`v. 1.3.25`**.

## Prossimo ora (02/10) — allineamento Store

1. Agente: merge branch R8/docs su **`main`**.
2. Renato: chiudi Photo&VideoManager → apri BoxManager (vedi prompt chiusura) → `git pull` → **`playDebug`** → `1.3.25 ok`.
3. AAB **`playRelease`** → Produzione → verifica % DEX in App bundle explorer.
4. Messaggio: `produzione 1.3.25 ok` → poi B10 / strategia (sessione dedicata).
5. **Non** aprire B-QTY / D.

**Regola test:** l’agente indica sempre il `versionName` atteso in topbar; merge su **`main`** obbligatorio prima del test telefono (SI 14/09).
