# Runbook giorno M3 — aggregato A11

**Stato:** **A11 OK** SI Renato 10/09/2026.  
**Traccia decisa (A10):** breve closed **1.3** → poi produzione.  
**Non fare** finché A4 ≠ 14/14 giorni con ≥12 tester.

### Vincolo ufficiale (SI Renato 10/09) — niente «family» in UI

Nell’app **Play ufficiale** non deve comparire la parola **family / famiglia** (né etichette di build `…-famiglia…`).

| Cosa | Regola |
|------|--------|
| `versionName` / topbar | Solo **1.3** (mai `1.3-famigliaB5.x`) |
| Package | `it.renatizzi.boxmanager` — **mai** `.famiglia` |
| Flavor Gradle `famiglia` | Solo etichetta interna di build sviluppo; **non** sullo Store |
| Testi utente IT/EN | Niente «famiglia / familiari / family»; usare **archivio condiviso / chi condivide / household** |
| Nomi codice (`FamilyCatalog…`, id `family_*`) | Possono restare interni; non sono visibili all’utente |

Questo entra obbligatoriamente nell’aggregato codice **B** (giorno M3).

---

## Prima di iniziare (tu, checklist rapida)

- [ ] Console: **14 giorni di fila** con almeno **12** tester  
- [ ] Screenshot Dashboard IT/EN in `docs/play/screenshots/` (rifare da AAB 1.3 se topbar ancora `famiglia`)  
- [ ] Keystore / `key.properties` sul PC (come per la 1.2)  
- [ ] Nessun AAB `famiglia` / `.famiglia` da caricare  

---

## Ordine del giorno (semplice)

| Passo | Chi | Cosa |
|-------|-----|------|
| 1 | Renato | In Console: se richiesto, **richiedi accesso produzione** (dopo 14/14) e rispondi alle domande sul test chiuso |
| 2 | Agente | Codice cutover: gate Disco + Condividi Archivio; **play** con funzioni; **testi senza family/famiglia**; topbar **1.3** |
| 3 | Agente | Merge sviluppo → play/`main`; `versionName` **1.3**; `versionCode` > 3 |
| 4 | Renato PC | Genera AAB **release** flavor **play** (Android Studio / Gradle + keystore) |
| 5 | Renato | Upload AAB su **test chiuso** (traccia closed 1.3 / successore), non ancora “tutti” |
| 6 | Renato | Scheda Store: testi IT/EN; icona/feature graphic; screenshot **senza** topbar famiglia |
| 7 | Renato | Avviso tester: novità 1.3 + codice `BOXMANAGER-TESTER` se serve Archivio completo |
| 8 | Renato | Smoke post-install da Play (package giusto, **nessuna** scritta family/famiglia, lingua, Backup) |
| 9 | Renato | Dopo pochi giorni closed ok → **promuovi / pubblica in produzione** |
| 10 | Agente | Con SI: annotare freeze post-M3 in Promemoria / Nota |

---

## Cosa non fare quel giorno

- Caricare build `.famiglia` o lasciare `versionName` con «famiglia»  
- Mettere in produzione la 1.3 **senza** il breve closed (salvo tuo ripensamento esplicito)  
- Aprire codice Progetto 2 (A1…) nello stesso giorno del cutover  

---

## Dopo A11 OK

Resta in attesa **A4** (14/14). Al via: aggregato **B** (codice gate + copy anti-family + merge + versioni) con un solo OK.
