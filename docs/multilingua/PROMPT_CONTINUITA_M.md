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

## Stato al 03/09/2026

| Voce | Valore |
|------|--------|
| **Assessment** | [ASSESSMENT_M.md](ASSESSMENT_M.md) — PR **#16** |
| **Piano** | **Approvato SI Renato** |
| **Lingue V1** | Italiano (default) + English |
| **Play Console traduzione app** | **Non usare** — lavoro in Cursor |
| **M1 / CK1** | **CONVALIDATO SI Renato device 03/09/2026** (M1a–M1d su PR **#18**) |
| **M2a** | **EN verificata** su delega Renato 03/09/2026 — [BOZZA_TABELLE_EN_CK0.md](BOZZA_TABELLE_EN_CK0.md) + Allegato **4.21** |
| **M2b** | **S1–S3 SI** 03/09/2026 — motore locale-aware in corso ([SEMANTICA_EN_EQUIVOCI.md](SEMANTICA_EN_EQUIVOCI.md)) |
| **Prossimo pacchetto** | Completare M2b (test EN campione) poi **M2c** UI ricerca |
| **Branch lavoro** | `cursor/multilingua-m2b-5409` |
| **Branch base** | `cursor/multilingua-m2a-5409` (EN verificata) |
| **Play** | `main` **1.2** — test chiuso ~2 sett.; **non** merge M su `main` senza SI |
| **Famiglia** | **1.3-famigliaB5.7** — P1 CONVALIDATO; eventuali bug Play → fix famiglia **a fine test** |
| **Ricerca avanzata EN** | Pipeline 0–10 invariata; **niente** traduttore EN→IT; **niente** interprete semantico (Nota 3.3.9) |
| **Checkpoint** | **CK0** tabelle EN verificate; **S1–S3 SI**; **CK1** ✅, **CK2** |

### Documenti vincolanti (leggere prima di codice)

| File | Ruolo |
|------|--------|
| [ASSESSMENT_M.md](ASSESSMENT_M.md) | Piano M0–M3, inventario, architettura |
| [BOZZA_TABELLE_EN_CK0.md](BOZZA_TABELLE_EN_CK0.md) | Bozza EN 1:1 da Nota (CK0) |
| [SEMANTICA_EN_EQUIVOCI.md](SEMANTICA_EN_EQUIVOCI.md) | Tipi di domanda EN equivoci; decisioni S1–S3 prima del motore |
| `.cursor/rules/fonti-ufficiali.mdc` | Elenchi alias/messaggi — importare, non riassumere |
| `.cursor/rules/pipeline-ufficiale.mdc` | Ricerca: pipeline 0–10 invariata |
| Nota Integrata **3.6.6** | Scelta lingua Impostazioni |
| Nota **1.3.3** / Allegato **4.21** | Alias Core EN **bozza** — import in codice solo dopo CK0 |

---

## Regole non negoziabili

1. **Fonti ufficiali:** alias, messaggi 2.6, matrice indicatori EN → importare da Nota/Excel, non tradurre a memoria.
2. **Pipeline 0–10:** invariata; solo input locale-aware (`pipeline-ufficiale.mdc`).
3. **Dati utente:** nomi archivio non tradotti.
4. **Test Play aperto:** branch M dedicato; merge `main` solo con SI / post-test.
5. **CK0 obbligatorio** prima del primo commit che tocca `domain/search` per EN.
6. **Guida rapida in-app** (`QuickStartGuideCopy`, topbar «Guida»): **M1 obbligatorio**, verificata in **CK1** (§8 famiglia incluso). **Ritocchi testuali** (semplificazione) possono essere integrati da Renato in M1d prima del CK1.
7. **Branch base:** tutto il nuovo sviluppo va su `famiglia` (e suoi feature branch `cursor/...`). `main` è riservato al test Google Play — **mai** usarlo come base di sviluppo.
8. **Allineamento obbligatorio prima di ogni sessione:** eseguire `git fetch origin` e verificare che il branch di lavoro contenga i commit più recenti del branch base famiglia. Se c'è uno scarto, allineare **prima** di scrivere codice. Documentare il commit HEAD di partenza nel primo messaggio di sessione.
9. **Scelta lingua:** la voce di selezione lingua (IT/EN) si trova in **Impostazioni** — non spostarla altrove.
10. **Topbar 1.2:** il titolo in-app è sempre **BoxManager** (`topbar_app_title`). `app_name` serve solo al launcher (flavor famiglia = «BoxManager Famiglia»). Non collegare i due.

## Istruzioni CK0 per Renato (nessun device)

M2a **non** cambia l’app. La verifica dell’inglese è **già fatta**. M2b è aperto sull’analisi degli equivoci: [SEMANTICA_EN_EQUIVOCI.md](SEMANTICA_EN_EQUIVOCI.md). Per il codice motore servono S1–S3 (sì/no, in italiano).

## Regole per le istruzioni di test a Renato

Renato non programma e non gestisce branch da riga di comando. Le istruzioni di test devono:

- **Non** richiedere operazioni git manuali.
- Indicare solo **cosa fa Android Studio in automatico** (pull al Run, o `INSTALLA_FAMIGLIA.bat`).
- Descrivere **solo le differenze rispetto al test precedente** (cosa è cambiato, dove guardare).
- Elencare schermata per schermata cosa si vede in EN e cosa ancora in IT (normale, senza allarme).
- Omettere commit SHA o nomi branch nelle istruzioni operative.

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
2. Confrontare `git log --oneline -5 origin/<branch-base>` con HEAD del branch di lavoro. Se il branch di lavoro è indietro, allineare con merge/rebase **prima** di toccare file.
3. Leggere [ASSESSMENT_M.md](ASSESSMENT_M.md) §4–§7 e tabella checkpoint §6.
4. Branch: `git checkout -b cursor/multilingua-<pacchetto>-5409` da base famiglia concordata (mai da `main`).

### Fase 2 — Esecuzione pacchetto

4. Implementare il pacchetto indicato nel messaggio sessione (M0 / M1a / …).
5. **Non** toccare `domain/search` per EN finché **S1–S3** in [SEMANTICA_EN_EQUIVOCI.md](SEMANTICA_EN_EQUIVOCI.md) non sono SI (tabelle CK0 già verificate). **S1–S3 sono SI** (03/09/2026).
6. Test: `assemblePlayDebug` + `assembleFamigliaDebug` + unit test verdi.
7. Commit, push, PR draft; aggiornare questo file se cambia «prossimo pacchetto».

### Fase 3 — Checkpoint

8. **CK1** solo a fine **M1d** (device: lingua + **Guida intera EN** + flussi core).
9. **CK0** tabelle EN: **verificate**. **S1–S3 SI** (03/09/2026). Motore EN in M2b.
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

**Ora:** M2b codice motore. **S1–S3 SI** 03/09/2026.

```
Continua filone M — Multilingua IT/EN da docs/multilingua/PROMPT_CONTINUITA_M.md
Pacchetto: M2b codice motore (S1–S3 già SI)
Branch: cursor/multilingua-m2b-5409
Vincoli: pipeline 0–10 invariata; niente traduttore EN→IT; niente interprete semantico (3.3.9);
importare elenchi interi bozza CK0; rumore fase 1 solo elenco chiuso.
Obiettivo: SearchLocale + alias/matrix EN; test IT invariati; campione EN 0–10.
```

---

## Fuori scope V1

- Lingue oltre IT/EN
- Motore B multilingua (V1 = Motore A)
- Traduzione automatica Play App strings
- Header CSV import bilingue (salvo SI CK0)

---

## Backlog annotato (non perdere)

| ID | Richiesta Renato | Note |
|----|------------------|------|
| **B-SEL-CARTELLA** | Selettore cartella (Backup, Esporta, Condivisione tabelle, …): estendere la scelta anche a drive non riconosciuti direttamente da Android (es. disco di rete locale / NAS SMB) | Fuori scope M1; valutare in filone igiene file o post-M1 |
| **B-PALETTE-ACCENT** | Colori card funzione devono seguire la palette Impostazioni **indipendentemente dalla lingua** | Risolto con `ThemeAccentTextViews` (ID view, non testo IT) |
| **B-FAMILY-DOMAIN-ERR** | Errori dominio merge famiglia (`SharedTablesMerger` / `FamilyInventoryMerger` / reader) ancora IT in path blocco rarissimi | Opzionale post-M1d; non bloccano CK1 UI |

---

## Riferimenti chiusi (non riaprire in sessione M)

- Filone correttivi P0/P1 — [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md)
- B0–B5 famiglia prodotto — CONVALIDATO
