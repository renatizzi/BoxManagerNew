# Runbook giorno M3 — aggregato A11

**Stato:** **A11 OK** SI Renato 10/09/2026.  
**Traccia decisa (A10):** breve closed **1.3** → poi produzione.  
**Non fare** finché A4 ≠ 14/14 giorni con ≥12 tester.

### Vincolo identità (chiarito SI Renato 10/09 sera)

**Una sola app:** BoxManager. La 1.3 è la **stessa** app aggiornata, non un secondo prodotto «family».

| Cosa | Regola |
|------|--------|
| Nome prodotto | Solo **BoxManager** (mai «app famiglia» / «app family») |
| `versionName` / topbar ufficiale | **1.3** (etichetta sviluppo `1.3-famigliaB5.x` = solo build locale parallela al test) |
| Package Store | `it.renatizzi.boxmanager` — **mai** caricare `.famiglia` su Play |
| Branch | Confluiscono in **`main`** al cutover M3 |
| Testi in-app (guida, messaggi, «famiglia/familiari/family») | **Restano** se utili al senso: non vanno ripuliti solo perché c’è la parola |

«Family» / flavor `famiglia` = **nome tecnico provvisorio** di lavoro (branch, flavor, package parallelo), non un mandato a censurare i testi utente.

---

## Prima di iniziare (tu, checklist rapida)

- [ ] Console: **14 giorni di fila** con almeno **12** tester  
- [ ] Screenshot Dashboard IT/EN in `docs/play/screenshots/` (a M3 meglio da AAB ufficiale **1.3**)  
- [ ] Keystore / `key.properties` sul PC (come per la 1.2)  
- [ ] Nessun AAB flavor sviluppo / `.famiglia` da caricare  

---

## Ordine del giorno (semplice)

| Passo | Chi | Cosa |
|-------|-----|------|
| 1 | Renato | In Console: se richiesto, **richiedi accesso produzione** (dopo 14/14) e rispondi alle domande sul test chiuso |
| 2 | Agente | Codice cutover: gate Disco + Condividi Archivio (**B-prep FATTO** 10/09 su branch); funzioni nel binario **play** |
| 3 | Agente | Merge sviluppo → **`main`**; `versionName` **1.3**; `versionCode` > 3 |
| 4 | Renato PC | Genera AAB **release** flavor **play** (Android Studio / Gradle + keystore) |
| 5 | Renato | Upload AAB su **test chiuso** (traccia closed 1.3 / successore), non ancora “tutti” |
| 6 | Renato | Scheda Store: testi IT/EN; icona/feature graphic; screenshot |
| 7 | Renato | Avviso tester: novità 1.3 + codice `BOXMANAGER-TESTER` se serve Archivio completo |
| 8 | Renato | Smoke post-install da Play (package `it.renatizzi.boxmanager`, topbar **1.3**, lingua, Backup) |
| 9 | Renato | Dopo pochi giorni closed ok → **promuovi / pubblica in produzione** |
| 10 | Agente | Con SI: annotare freeze post-M3 in Promemoria / Nota |

---

## Cosa non fare quel giorno

- Caricare build `.famiglia` o pubblicare come se fosse un’altra app  
- Lasciare topbar ufficiale con etichetta sviluppo `…-famiglia…`  
- Mettere in produzione la 1.3 **senza** il breve closed (salvo tuo ripensamento esplicito)  
- Aprire codice Progetto 2 (A1…) nello stesso giorno del cutover  
- «Pulire» guida/messaggi solo perché contengono «famiglia/familiari»  

---

## Dopo A11 OK / ripresa post 14/14

1. Smoke gate device (rinviato).  
2. Aggregato **B5–B9**: merge → `main`, versioni **1.3**, AAB play, closed 1.3, scheda.  
3. Solo **dopo** M3 + SI «apri fetta codice»: recuperare WIP A1 da `cursor/qr-avanzato-wip-parked-8374` @ `f523735`.
