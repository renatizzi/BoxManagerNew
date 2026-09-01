# Prompt di continuità — Filone M (Multilingua IT/EN)

**SI Renato, 01/09/2026** — mini-assessment e piano **approvati**; **Guida rapida in-app** obbligatoria in M1/CK1.  
**Ingresso unico** per sessioni agente sul multilingua. Non confondere con [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md).

---

## Nuova sessione — sì o no?

| Situazione | Nuova sessione? |
|------------|-----------------|
| Inizi **M0**, **M1** o **M2** (codice o doc filone M) | **SÌ** — contesto pulito, solo filone M |
| Solo correttivi P2 / bug Play / famiglia B5.7 | **No** — usa [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md) |
| Attesa test Play chiuso, nessun lavoro M | **No** — non aprire sessione M ancora |
| **SI Renato 01/09/2026** — M in parallelo al test (~2 sett.) | **SÌ** — nuova sessione filone M (M1a…) |

**Regola:** una sessione = un filone (M **oppure** correttivi), per non mescolare regole pipeline / checkpoint.

**Bug Play durante il test:** Renato segnala quando arrivano; fix su branch **famiglia** **poco prima** della fine del test (non bloccano M1). Vedi [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md).

---

## Stato al 01/09/2026

| Voce | Valore |
|------|--------|
| **Assessment** | [ASSESSMENT_M.md](ASSESSMENT_M.md) — PR **#16** |
| **Piano** | **Approvato SI Renato** |
| **Lingue V1** | Italiano (default) + English |
| **Play Console traduzione app** | **Non usare** — lavoro in Cursor |
| **Prossimo pacchetto** | **M1c** (Copy/Configuration + DialogUtils → risorse) — M1b chiuso in questa sessione |
| **Branch lavoro** | `cursor/multilingua-m1a-5409` (include merge `cursor/guida-ritocchi-5409` PR #17) |
| **Branch base** | `cursor/p1-igiene-file-5409` (B5.7) + **obbligatorio** `cursor/guida-ritocchi-5409` per Guida |
| **Play** | `main` **1.2** — test chiuso ~2 sett.; **non** merge M su `main` senza SI |
| **Famiglia** | **1.3-famigliaB5.7** — P1 CONVALIDATO; eventuali bug Play → fix famiglia **a fine test** |
| **Ricerca avanzata EN** | **M2** — **CK0** prima di `domain/search` EN |
| **Checkpoint** | **CK0**, **CK1**, **CK2** (assessment §6) |

### Documenti vincolanti (leggere prima di codice)

| File | Ruolo |
|------|--------|
| [ASSESSMENT_M.md](ASSESSMENT_M.md) | Piano M0–M3, inventario, architettura |
| `.cursor/rules/fonti-ufficiali.mdc` | Elenchi alias/messaggi — importare, non riassumere |
| `.cursor/rules/pipeline-ufficiale.mdc` | Ricerca: pipeline 0–10 invariata |
| Nota Integrata **3.6.6** | Scelta lingua Impostazioni |
| Nota **1.3.3** / Excel | Alias Core (**M2**, dopo CK0) |

---

## Regole non negoziabili

1. **Fonti ufficiali:** alias, messaggi 2.6, matrice indicatori EN → importare da Nota/Excel, non tradurre a memoria.
2. **Pipeline 0–10:** invariata; solo input locale-aware (`pipeline-ufficiale.mdc`).
3. **Dati utente:** nomi archivio non tradotti.
4. **Test Play aperto:** branch M dedicato; merge `main` solo con SI / post-test.
5. **CK0 obbligatorio** prima del primo commit che tocca `domain/search` per EN.
6. **Guida rapida in-app** (`QuickStartGuideCopy`, topbar «Guida»): **M1 obbligatorio**, verificata in **CK1** (§8 famiglia incluso). **Ritocchi testuali** (semplificazione) possono essere integrati da Renato in M1d prima del CK1.

---

## Pacchetti autonomi (ordine)

| ID | Contenuto | Checkpoint |
|----|-----------|------------|
| **M0** | `docs/play/store-listing-en.md` | — |
| **M1a** | Infrastruttura Scelta lingua (3.6.6) | — |
| **M1b** | Layout → `strings.xml` + `values-en` | — |
| **M1c** | Copy/Configuration + DialogUtils → risorse | — |
| **M1d** | **Guida rapida in-app** + Premium + Famiglia + sweep Kotlin | **CK1** |
| **M2a** | Bozza tabelle EN in Nota | **CK0** (prima di M2b) |
| **M2b** | Motore ricerca locale-aware EN | — |
| **M2c** | UI ricerca + test suite EN | **CK2** |
| **M3** | Screenshot Play, merge main, release | SI prodotto |

L’agente esegue **M1a→M1d** in sequenza senza fermarsi, salvo KO test/build.

---

## Sequenza consigliata per l’agente

### Fase 1 — Allineamento (obbligatoria)

1. `git fetch origin`
2. Leggere [ASSESSMENT_M.md](ASSESSMENT_M.md) §4–§7 e tabella checkpoint §6.
3. Branch: `git checkout -b cursor/multilingua-m1a-5409` da base famiglia concordata.

### Fase 2 — Esecuzione pacchetto

4. Implementare il pacchetto indicato nel messaggio sessione (M0 / M1a / …).
5. **Non** toccare `domain/search` per EN finché **CK0** non è chiuso.
6. Test: `assemblePlayDebug` + `assembleFamigliaDebug` + unit test verdi.
7. Commit, push, PR draft; aggiornare questo file se cambia «prossimo pacchetto».

### Fase 3 — Checkpoint

8. **CK1** solo a fine **M1d** (device: lingua + **Guida intera EN** + flussi core).
9. **CK0** solo prima di **M2b** (SI tabelle EN in Nota).
10. **CK2** a fine **M2c** (device: campione domande EN Matrice Test).

---

## Comandi utili

```bash
cd BoxManagerNew
git fetch origin
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
git checkout -b cursor/multilingua-m1a-5409

./gradlew :app:assemblePlayDebug
./gradlew :app:assembleFamigliaDebug
./gradlew :app:testPlayDebugUnitTest
./gradlew :app:testFamigliaDebugUnitTest
```

---

## Branch naming

`cursor/multilingua-<pacchetto>-5409` (es. `cursor/multilingua-m1a-5409`).

---

## Messaggio tipo per **nuova sessione** (copia-incolla)

```
Continua filone M — Multilingua IT/EN da docs/multilingua/PROMPT_CONTINUITA_M.md

Piano: APPROVATO SI Renato (01/09/2026)
Assessment: docs/multilingua/ASSESSMENT_M.md (PR #16)

Pacchetto da eseguire: M1a
Branch nuovo: cursor/multilingua-m1a-5409
Base: cursor/p1-igiene-file-5409 (1.3-famigliaB5.7) — oppure family-unione se merge P1 fatto

Vincoli:
- Solo IT + EN in V1
- NON toccare domain/search EN (CK0 non ancora)
- Guida rapida in-app: obbligatoria in M1d / CK1
- Play main 1.2: non merge senza SI
- Leggere ASSESSMENT_M §4–§6 e fonti-ufficiali.mdc prima del codice

Obiettivo sessione: chiudere M1a (Scelta lingua 3.6.6) + test; poi proseguire M1b se tempo.
```

*(Sostituire `M1a` con `M0`, `M1b`, … quando si apre la sessione successiva.)*

---

## Fuori scope V1

- Lingue oltre IT/EN
- Motore B multilingua (V1 = Motore A)
- Traduzione automatica Play App strings
- Header CSV import bilingue (salvo SI CK0)

---

## Riferimenti chiusi (non riaprire in sessione M)

- Filone correttivi P0/P1 — [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md)
- B0–B5 famiglia prodotto — CONVALIDATO
