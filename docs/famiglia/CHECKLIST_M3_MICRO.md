# Checklist M3 — micro-step (stato vivo)

**Aggiornato:** 10/09/2026.  
**Ingresso:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md).  
**Modo:** un micro-step (o piccolo aggregato) alla volta; stato sempre aggiornato qui.

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
| A8 | Screenshot Store (almeno 1 UI EN / lingua) | Renato | **ATTESA Renato** | Senza dati personali |
| A9 | Decidere traccia post-M3 | Renato | **PROSSIMO** | Produzione diretta **oppure** breve closed 1.3 |

**Ultimo chiuso:** A7. **Proposto ora:** **A9** (una sola scelta, senza device). In alternativa A8 se preferisci fare prima gli screenshot.

### A7 — cosa fare sul telefono (build sviluppo) — CHIUSO

Installazione tipica: `INSTALLA_FAMIGLIA.bat` oppure `./gradlew :app:installFamigliaDebug`.  
Topbar attesa: tipo **1.3-famigliaB5.24** (etichetta di build).

| # | Prova | OK se… |
|---|--------|--------|
| 1 | Impostazioni → lingua **English** | UI in inglese (menu/etichette) |
| 2 | Utility → **Condividi Archivio** | Si apre la pagina Invia/Ricevi (niente crash) |
| 3 | Impostazioni → **Disco di rete** | Si apre il dialogo (Installa/Apri CIFS) |
| 4 | Utility → **Backup Archivio** | Parte il flusso / Sfoglia cartella senza crash |

**Esito:** **A7 CONVALIDATO** SI Renato 10/09/2026.

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

**Aperto:** **A7** smoke — Renato sul device (tabella § A7).  
In parallelo resta **A4** (14 giorni Google).  
Dopo A7: A8 o A9.
