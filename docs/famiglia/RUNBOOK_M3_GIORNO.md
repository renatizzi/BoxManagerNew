# Runbook giorno M3 — aggregato A11

**Stato:** bozza per OK unico Renato (**A11**).  
**Traccia decisa (A10):** breve closed **1.3** → poi produzione.  
**Non fare** finché A4 ≠ 14/14 giorni con ≥12 tester.

---

## Prima di iniziare (tu, checklist rapida)

- [ ] Console: **14 giorni di fila** con almeno **12** tester  
- [ ] Screenshot EN (e opz. Utility) pronti, senza dati personali  
- [ ] Keystore / `key.properties` sul PC (come per la 1.2)  
- [ ] Nessun AAB `famiglia` / `.famiglia` da caricare  

---

## Ordine del giorno (semplice)

| Passo | Chi | Cosa |
|-------|-----|------|
| 1 | Renato | In Console: se richiesto, **richiedi accesso produzione** (dopo 14/14) e rispondi alle domande sul test chiuso |
| 2 | Agente | Codice prep cutover: gate **Disco** + **Condividi Archivio** su Archivio completo; binario **play** con quelle funzioni (non solo flag sviluppo) |
| 3 | Agente | Merge controllato sviluppo → linea play/`main`; `versionName` **1.3**; `versionCode` > 3 |
| 4 | Renato PC | Genera AAB **release** flavor **play** (Android Studio / Gradle + keystore) |
| 5 | Renato | Upload AAB su **test chiuso** (traccia closed 1.3 / successore), non ancora “tutti” |
| 6 | Renato | Scheda Store: testi IT/EN dalle bozze `docs/play/store-listing-*.md`; icona/feature graphic; screenshot |
| 7 | Renato | Avviso tester: novità 1.3 + codice `BOXMANAGER-TESTER` se serve Archivio completo |
| 8 | Renato | Smoke post-install da Play (package `it.renatizzi.boxmanager`, lingua, Backup) |
| 9 | Renato | Dopo pochi giorni closed ok → **promuovi / pubblica in produzione** |
| 10 | Agente | Con SI: annotare freeze post-M3 in Promemoria / Nota |

---

## Cosa non fare quel giorno

- Caricare build `.famiglia`  
- Mettere in produzione la 1.3 **senza** il breve closed (salvo tuo ripensamento esplicito)  
- Aprire codice Progetto 2 (A1…) nello stesso giorno del cutover  

---

## Dopo A11 OK

Resta in attesa **A4** (14/14). Al via: apriamo aggregato **B** (codice gate + merge + versioni) con un solo OK.
