# Prompt di continuità — M3 cutover Play (ingresso nuova sessione)

**Data:** 13/09/2026.  
**Uso:** **unico ingresso** della sessione che esegue il cutover 1.3 (dopo verifica Console).  
**Sessione precedente:** Cloud Agent «Continuità m3 merge play» (`bc-21f165cb-afdc-4e67-bd0a-e434d649d5b2`) — prep A/B-prep chiusa; PC di lavoro di nuovo allineato agli Agents.

---

## Ordine di lavoro di questa sessione (fisso)

1. **Play Console** — **C-A OK**; **B1–B2 inviata** 13/09 ~12:59 (**ATTESA Google**, tipico ≤7 gg). Non blocca il cutover closed.  
2. **Smoke gate device**: Disco + Condividi Archivio **senza** Archivio completo → paywall (admin da solo non sblocca; se serve: Scade prova / debug OFF).  
3. Eseguire cutover **B5–B9** (merge → `main`, 1.3, AAB play, upload **closed 1.3**) secondo [RUNBOOK_M3_GIORNO.md](RUNBOOK_M3_GIORNO.md).  
4. Promozione produzione = **solo dopo** email OK Google + closed 1.3 ok. A fine: docs + SI; **non** aprire Progetto 2 senza SI «apri fetta codice».

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
| C-A | Test chiuso 1.2 | **OK 13/09** — tre spunte verdi; bottone Richiedi attivo |
| C-B | Produzione | Richiesta **inviata** 13/09 ~12:59 — in esame Google; promozione ampia solo dopo OK email |
| C-C | Dichiarazioni app | Sicurezza dati, classificazione, privacy, ecc. complete (già ok al 10/09) |
| C-D | Scheda Store | Icona, feature graphic, screenshot, testi IT (EN listing da aggiornare in B8) |
| C-E | Pacchetto | `it.renatizzi.boxmanager` — nessun `.famiglia` |

Se manca qualcosa: elencare solo i buchi e fermarsi. Se tutto ok: **SI cutover** e partire da B5.

---

## Sequenza cutover (riassunto operativo)

| Step | Chi | Azione |
|------|-----|--------|
| 0 | Renato | Smoke gate device (se non ancora fatto) |
| 1 | Renato | **FATTO** 13/09: richiesta accesso produzione inviata (ATTESA Google) |
| 2–3 | Agente | Merge branch sviluppo → `main`; `versionName` **1.3**; `versionCode` > ultimo Play |
| 4a | Renato PC | **Obbligatorio (SI 13/09):** prima di Play, test telefono = **Run** con variante **`playDebug`** (non famiglia). Topbar ufficiale senza «famiglia»; AAB con **playRelease** |
| 4b | Renato PC | Solo dopo: AAB **release** flavor **play** + keystore |
| 5 | Renato | Upload su **closed 1.3** e invio release (non produzione ampia; non aspetta email Google) |
| 6–7 | Renato | Scheda Store IT/EN + messaggio tester (`BOXMANAGER-TESTER` se closed) |
| 8 | Renato | Smoke install da Play: package, topbar **1.3**, lingua, Backup |
| 9 | Renato | Closed ok **e** email Google OK → promuovi produzione |
| 10 | Agente | Freeze docs post-M3 con SI |

Dettaglio: [RUNBOOK_M3_GIORNO.md](RUNBOOK_M3_GIORNO.md). Stato vivo: [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md).

---

## Stato Play **1.3.7** (vc **11**) — lettura Console 19/09

Screenshot Renato (Produzione + scheda App bundle). **Priorità assoluta** chiarita così:

| Cosa vedi | Significato |
|-----------|-------------|
| Produzione → **11 (1.3.7) «In revisione»** | Release **inviata** a Google, **non ancora** pubblica per tutti |
| Produzione → **11 (1.3.7) «Bozza»** | Seconda release **incompleta** dello stesso AAB — **non** è lo stato ufficiale; non reinviare |
| Bundle → «2 attive» (Produzione + Test chiusi) | L’AAB è **collegato** a quelle tracce; **non** = già live in Store |
| Test chiusi attivo | I **tester closed** possono avere la 1.3.7 |

**Verdetto:** **non** è in produzione pubblica. È **in revisione** Google sulla traccia Produzione. Su closed può già essere attiva per i tester.

**Cosa fare (Renato):**
1. Aprire **Panoramica della pubblicazione** (link sulla release «In revisione») e attendere esito Google / email.
2. **Non** modificare né inviare la release in **Bozza** finché quella in revisione non chiude.
3. Quando lo stato diventa tipo «Disponibile su Google Play» → comunicare `produzione 1.3.7 ok` (poi B10 docs).
4. **A1** (SI 19/09): in coda — codice **dopo** ok/chiarimento Play.

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
