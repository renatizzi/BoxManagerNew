# Prompt di continuità — M3 cutover Play (ingresso nuova sessione)

**Data:** 13/09/2026.  
**Uso:** **unico ingresso** della sessione che esegue il cutover 1.3 (dopo verifica Console).  
**Sessione precedente:** Cloud Agent «Continuità m3 merge play» (`bc-21f165cb-afdc-4e67-bd0a-e434d649d5b2`) — prep A/B-prep chiusa; PC di lavoro di nuovo allineato agli Agents.

---

## Ordine di lavoro di questa sessione (fisso)

1. **Verifica Play Console** con Renato (testo Console basta) → aggiornare A4/B1.  
2. **Smoke gate device** (rinviato): Disco + Condividi Archivio **senza** Archivio completo → deve uscire paywall (admin da solo non sblocca; se serve: Scade prova / debug OFF).  
3. Eseguire cutover secondo [RUNBOOK_M3_GIORNO.md](RUNBOOK_M3_GIORNO.md) e [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md) §B.  
4. A fine cutover: aggiornare checklist + Promemoria; **non** aprire Progetto 2 senza SI «apri fetta codice».

---

## Stato già congelato (non rivotare)

| Voce | Decisione |
|------|-----------|
| Identità | Una sola **BoxManager** → ufficiale **1.3** su `main` |
| Traccia | **Breve closed 1.3** → poi produzione (A10 OK) |
| Classe I in 1.3 | Condiviso, EN, Disco, B5.24, Motore B F7–F9, Dark, correttivi, gate premium Disco/Condividi |
| Fuori M3 | A1–A3, B–C, B-QTY, D; B-ROTATE-FORM-DRAFT standby |
| Free | IT/EN, Dark, fix, Guida |
| Premium | Archivio completo (condiviso, Disco, ricerca avanzata, QR, import/export, P2) |
| «Family» | Solo tecnico (flavor/branch); **non** censurare testi guida/messaggi |
| Package Play | `it.renatizzi.boxmanager` — **mai** AAB `.famiglia` |
| WIP A1 | `cursor/qr-avanzato-wip-parked-8374` @ `f523735` — recuperare **dopo** M3 + SI codice |
| Idea OCR+foto | [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) §6 — solo idea |
| Delega piano | Agente decide priorità/impatto/traccia; Renato OK unico su aggregati |

---

## Check Console (aggregato — un solo esito)

Renato incolla testo (o elenco) da Play Console. L’agente marca **PRONTO** solo se:

| # | Controllo | OK se |
|---|-----------|--------|
| C-A | Test chiuso 1.2 | Chiuso / requisiti 12 tester × 14 giorni soddisfatti (o accesso produzione già ottenibile) |
| C-B | Produzione | Accesso richiedibile o già concesso; niente blocco rosso |
| C-C | Dichiarazioni app | Sicurezza dati, classificazione, privacy, ecc. complete (già ok al 10/09) |
| C-D | Scheda Store | Icona, feature graphic, screenshot, testi IT (EN listing da aggiornare in B8) |
| C-E | Pacchetto | `it.renatizzi.boxmanager` — nessun `.famiglia` |

Se manca qualcosa: elencare solo i buchi e fermarsi. Se tutto ok: **SI cutover** e partire da B5.

---

## Sequenza cutover (riassunto operativo)

| Step | Chi | Azione |
|------|-----|--------|
| 0 | Renato | Smoke gate device (se non ancora fatto) |
| 1 | Renato | Console: accesso produzione / domande test se richieste |
| 2–3 | Agente | Merge branch sviluppo → `main`; `versionName` **1.3**; `versionCode` > ultimo Play |
| 4 | Renato PC | AAB **release** flavor **play** + keystore |
| 5 | Renato | Upload su **closed 1.3** (non produzione ampia subito) |
| 6–7 | Renato | Scheda Store IT/EN + messaggio tester (`BOXMANAGER-TESTER` se closed) |
| 8 | Renato | Smoke install da Play: package, topbar **1.3**, lingua, Backup |
| 9 | Renato | Dopo pochi giorni closed ok → promuovi produzione |
| 10 | Agente | Freeze docs post-M3 con SI |

Dettaglio: [RUNBOOK_M3_GIORNO.md](RUNBOOK_M3_GIORNO.md). Stato vivo: [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md).

---

## File da tenere aperti

| File | Ruolo |
|------|--------|
| **Questo prompt** | Ingresso sessione cutover |
| [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md) | Stato micro-step |
| [RUNBOOK_M3_GIORNO.md](RUNBOOK_M3_GIORNO.md) | Ordine del giorno |
| [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](PROMPT_CONTINUITA_M3_MERGE_PLAY.md) | Contesto M3 / roadmap (storico prep) |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Backlog |
| `docs/play/README.md` + listing IT/EN | Store / AAB |

---

## Come avviare la nuova sessione (Renato)

In Cursor Agents (PC), nuova sessione Cloud sullo stesso repo, messaggio iniziale:

```text
Ingresso: docs/famiglia/PROMPT_CONTINUITA_M3_CUTOVER.md
Branch lavoro: cursor/qr-avanzato-analisi-8374
Segui il prompt: prima check Console (incollerò lo stato), poi smoke gate se manca, poi cutover B5–B9.
STOP Progetto 2 finché non SI «apri fetta codice».
```

---

## CONVALIDA ingresso

Bozza 13/09/2026 per sessione cutover.  
**CONVALIDA documento:** in attesa SI Renato (ok all’uso come ingresso nuova sessione).
