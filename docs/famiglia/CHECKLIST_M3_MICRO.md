# Checklist M3 — micro-step (stato vivo)

**Aggiornato:** 10/09/2026.  
**Ingresso:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md).  
**Modo:** micro-step (o aggregato stessa natura); stato sempre aggiornato qui.

**Delega (SI Renato 10/09/2026):** su priorità, impatto, traccia release e scelte “di piano” decide l’agente; Renato dà **un solo OK** sull’aggregato. Non chiedere OK su singole opzioni che richiedono giudizio tecnico/di prodotto che preferisce delegare. Renato resta su: smoke device, screenshot, azioni Console/AAB sul suo PC, SI su merito requisiti/codice quando previsto.

Legenda: **FATTO** · **IN CORSO** · **ATTESA Renato** · **ATTESA Google** · **PROSSIMO** · **BLOCCATO** · **DOPO**

---

## A — Preparazione (test Play ancora aperto)

| # | Micro-step | Chi | Stato | Note |
|---|------------|-----|-------|------|
| A1 | Congelare classe I (cosa entra in 1.3) | — | **FATTO** | Condiviso, EN, Disco, B5.24, Motore B F7–F9, Dark, correttivi |
| A2 | Principio premium + eccezioni free | — | **FATTO** | Free: IT/EN, Dark, fix, Guida. Premium: condiviso, Disco, P2 |
| A3 | Check Console: dichiarazioni / Data safety | Renato | **FATTO** | 10/10 complete |
| A4 | Check Console: giorni test chiuso | Renato+Google | **IN CORSO** | 12 tester, **11/14** giorni — mancano ~3 |
| A5 | Bozze listing IT/EN | Agente | **FATTO** | `docs/play/store-listing-*.md` |
| A6 | Sync fix Play `main` → sviluppo | Agente | **FATTO** | Nessun fix pendente |
| A7 | Smoke build sviluppo (EN, condiviso, Disco, Backup) | Renato | **FATTO** | **CONVALIDATO** SI Renato 10/09/2026 |
| A8 | Screenshot Store (almeno 1 UI EN / lingua) | Renato | **IN AGGREGATO A10** | Spec sotto |
| A9 | Traccia post-M3 | Agente (delega) | **DECISO** | **Breve closed 1.3** → poi produzione (vedi A10) |

### Aggregato **A10** — piano prep restante (un solo OK Renato)

Decisione agente (delega 10/09):

1. **Traccia:** dopo cutover, **breve test chiuso sulla 1.3** (stessi tester / pochi giorni), poi promozione in produzione. Motivo: novità ampie (EN, condiviso, Disco); i 14 giorni della 1.2 servono all’accesso produzione, non sostituiscono una prova corta della 1.3.  
2. **Screenshot da preparare (Renato, quando comodo):**  
   - uno con UI **inglese** (o Impostazioni → lingua)  
   - uno Utility / novità se utile (senza dati personali)  
   - restano validi icona + feature graphic + shot già in `docs/play/`  
3. **In parallelo:** lasciare correre A4 fino a **14/14** (non chiudere il closed 1.2 sotto i 12 tester).

**Esito atteso da Renato:** `A10 OK` (accetta aggregato) oppure nota solo se un punto non è fattibile.

### A7 — CHIUSO (CONVALIDATO 10/09)

| # | Prova | Esito |
|---|--------|--------|
| 1–4 | EN, Condividi Archivio, Disco, Backup | **OK** SI Renato |

---

## B — Giorno cutover M3 (solo dopo 14/14 + SI)

| # | Micro-step | Chi | Stato | Note |
|---|------------|-----|-------|------|
| B1 | Giorni test = 14/14; richiedere accesso produzione (se serve) | Renato | **ATTESA Google** | Dopo A4 |
| B2 | Rispondere alle domande Google sul test chiuso | Renato | **DOPO** | Anteprima già in Console |
| B3 | Gate codice: Disco → Archivio completo | Agente | **DOPO** | SI già: premium |
| B4 | Archivio condiviso nel binario **play** (non solo flag famiglia) | Agente | **DOPO** | + gate premium |
| B5 | Merge sviluppo → linea play/`main` | Agente+Renato | **DOPO** | Solo a test chiuso + SI |
| B6 | `versionCode` / `versionName` **1.3** | Agente | **DOPO** | > ultimo Play (oggi vc 3) |
| B7 | AAB release flavor **play** | Renato PC | **DOPO** | Keystore locale |
| B8 | Upload AAB + scheda Store (testi/shot) | Renato | **DOPO** | C4–C6 |
| B9 | Messaggio tester / verifica install | Renato | **DOPO** | Package giusto, no `.famiglia` |
| B10 | Annotare freeze post-M3 in Promemoria/Nota | Agente | **DOPO** | Con SI |

**Aggregati naturali:** (B3+B4) codice gate · (B5+B6) git/versioni · (B7+B8) Console binario/scheda.

---

## C — Dopo M3 (coda)

| # | Micro-step | Stato |
|---|------------|-------|
| C1 | Opz. B-ROTATE-FORM-DRAFT | **DOPO** |
| C2 | A1 QR avanzato | **DOPO** (SI «apri fetta codice») |
| C3 | A2 Cestino → A3 Foto → B–C → B-QTY | **DOPO** |
| C4 | D Dashboard | no-code (U0) |

---

## Prossimo passo proposto

**Chiuso:** A7.  
**Aperto per un solo OK:** aggregato **A10** (traccia = breve closed 1.3 + cosa serve di screenshot + non toccare i 14 giorni).  
In parallelo: **A4** Google.
