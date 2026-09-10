# Checklist M3 — micro-step (stato vivo)

**Aggiornato:** 10/09/2026.  
**Ingresso:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md).  
**Modo:** micro-step (o aggregato stessa natura); stato sempre aggiornato qui.

**Delega (SI Renato 10/09/2026):** su priorità, impatto, traccia release e scelte “di piano” decide l’agente; Renato dà **un solo OK** sull’aggregato. Renato resta su: smoke device, screenshot, azioni Console/AAB sul suo PC, SI su merito requisiti/codice quando previsto.

Legenda: **FATTO** · **IN CORSO** · **ATTESA Renato** · **ATTESA Google** · **DOPO**

---

## A — Preparazione (test Play ancora aperto)

| # | Micro-step | Stato | Note |
|---|------------|-------|------|
| A1–A3 | Classe I, premium, dichiarazioni Console | **FATTO** | |
| A4 | Giorni test chiuso | **IN CORSO** | 12 tester, **11/14** |
| A5–A11 | Listing, sync, smoke, shot, traccia, runbook | **FATTO** | A7 CONVALIDATO; A10/A11 OK |
| **B-prep** | Codice gate + play features (questo branch) | **FATTO** | SI OK Renato «prossimo step»; **no merge `main`** |

### B-prep — dettagli (chiuso 10/09)

- `PremiumFeature.NETWORK_DRIVE` + `ARCHIVE_SHARE` con pitch IT/EN  
- Gate: Impostazioni → Disco; Utility → Condividi Archivio; `FamilyCatalogActivity`  
- Flavor **play**: `FAMILY_BETA=true`, **senza** `applicationIdSuffix` (package Store)  
- `versionName` play ancora **1.2** finché cutover (poi **1.3**, vc > 3)  
- Test unitari `premium.*` OK  

---

## B — Giorno cutover (solo dopo 14/14)

| # | Micro-step | Stato | Note |
|---|------------|-------|------|
| B1–B2 | Accesso produzione + domande Google | **ATTESA Google** | Dopo A4 |
| B3–B4 | Gate Disco/Condividi | **FATTO in B-prep** | Già su questo branch |
| B5–B6 | Merge → `main` + versioni **1.3** | **DOPO** | Solo a test chiuso |
| B7–B9 | AAB play, upload closed 1.3, scheda, smoke | **DOPO** | Renato PC/Console |
| B10 | Freeze docs post-M3 | **DOPO** | Con SI |

---

## C — Dopo M3

| # | Micro-step | Stato |
|---|------------|-------|
| C1 | Opz. B-ROTATE-FORM-DRAFT | **DOPO** |
| C2–C3 | A1 → A2 → A3 → B–C → B-QTY | **DOPO** (SI codice) |
| C4 | D Dashboard | no-code (U0) |

---

## Prossimo

**Fatto ora:** B-prep codice.  
**In attesa:** **A4** → 14/14 giorni Google.  
**Poi:** B5–B9 (merge/`main`/AAB/closed 1.3) con un solo OK quando i giorni sono completi.
