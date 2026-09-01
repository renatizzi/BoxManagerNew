# Prompt di continuità — Correttivi post-B5 (famiglia + sync Play)

**SI Renato, 31/08/2026** — piano a 3 passi. **01/09/2026:** T2 CONVALIDATO; freeze Play 1.2; **non** invertire famiglia su `main`; P1 in B5.5.

Usare questo file come **unico ingresso** per la prossima sessione agente sul filone **correzioni** (non riaprire B5 prodotto né filone multilingua M).

---

## Stato al 01/09/2026

| Voce | Valore |
|------|--------|
| **Branch famiglia** | `cursor/family-unione-unificata-e5b5` (integrazione) |
| **Build famiglia** | **1.3-famigliaB5.5** (versionCode **1324**) — P1 su `cursor/p1-igiene-file-5409` |
| **Play** | `main` → **1.2** (vc 3) — test chiuso Console |
| **Freeze Play 1.2** | `cursor/versione-test-5409` (snapshot `main`; PR #14) |
| **Inversione famiglia → main** | **No** durante il test chiuso (idea accantonata; C2 solo dopo SI) |
| **B0–B5 merge** | **CONVALIDATO** — Nota Integrata **9.2** Allegato **4.20** |
| **Filone correttivi** | P0 chiuso; **P1** in ritest device; P2 continuo |

### Documenti vincolanti (leggere prima di codice)

| File | Ruolo |
|------|--------|
| [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) | Elenco P0–P2, audit salvataggio file |
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | Play = solo bug tester; merge `main` → famiglia |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Tracking T1–T3, P1, P2 |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Regola d’oro merge |
| `.cursor/rules/salvataggio-file.mdc` | Criterio P1 |

---

## P0 — Bug beta (stato)

| ID | Stato | Azione prossima sessione |
|----|-------|--------------------------|
| **T1** Backup Directory | **CONVALIDATO** B5.2 | Nessuna |
| **T2** Categoria lista oggetti | **CONVALIDATO** B5.3 (SI device 01/09/2026) | Nessuna |
| **T3** Secondo bug | **Chiuso** (non recuperato) | Ignorare salvo nuova segnalazione con evidenza |

### Checklist ritest T2 (device, famiglia B5.3)

1. Contenitore → **Lista Oggetti**: header **categoria + icona** al primo caricamento.
2. Dashboard → ambito **Oggetti** → **Lista Oggetti Trovati**: categoria corretta per gruppo contenitore.
3. Stampa / Esporta da (2): stessa categoria a schermo.

**Chiusura T2:** solo dopo **SI Renato** esplicito su device → aggiornare PROMEMORIA + ASSESSMENT.

### File toccati da T2 (B5.3)

- `app/.../ui/BoxDetailActivity.kt` — `refreshHeader()`
- `app/.../ui/search/SearchResultActivity.kt` — `resolveCategoryForGroup()`
- `app/.../viewoutput/model/ContainerViewSnapshotFactory.kt` — `searchResultGroupBlock()`

---

## Sequenza consigliata per l’agente

### Fase 1 — Allineamento (obbligatoria)

1. `git checkout cursor/family-unione-unificata-e5b5 && git pull`
2. Leggere tabella P0 in [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md) e [ASSESSMENT](ASSESSMENT_CORRETTIVI.md).
3. Verificare topbar attesa **1.3-famigliaB5.5** in `app/build.gradle.kts` (flavor `famiglia`).

### Fase 2 — P0 residuo

4. **T2 CONVALIDATO** (01/09/2026). Non riaprire.
5. **Non** riaprire T3 senza evidenza nuova.

### Fase 3 — P1 igiene salvataggio file (**B5.5**, ritest device)

| Punto | Stato codice |
|-------|----------------|
| **PRE_RESTORE** | Box unico nome editabile + `Confermi?` / `File già esistente. Sostituirlo?`; poi conferma ripristino |
| **Genera Modello** | Nome proposto `Modello_Importazione.csv`; riuso cartella **Backup**; stesso box SI/NO |
| **Esporta vista** | Già OK (criterio matrice) |

Chiusura P1: solo dopo **SI Renato** su device (Ripristino copia di sicurezza + Genera Modello).

### Fase 4 — P2 sync Play (continuo)

6. Se arriva bugfix su `main` (tester Play): merge su branch famiglia, test famiglia, **non** invertire l’ordine.
7. Se fix nato su famiglia e vale anche Play: cherry-pick su `main` **prima** o insieme al merge.
8. **Mai** `assembleFamigliaRelease` / APK famiglia su Play Console.

### Fuori scope (non iniziare senza SI)

- Filone **multilingua** (M) — Nota 3.6.6 Scelta lingua
- Nuove fette B6 famiglia prodotto
- Motore B / pipeline ricerca (B7 chiuso)
- Feature Play oltre bugfix tester

---

## Vincoli non negoziabili

- Flavor `famiglia`: `applicationId` `…boxmanager.famiglia` — **non** su Play.
- `BuildConfig.FAMILY_BETA` / UI famiglia solo su flavor `famiglia`.
- Room: **no** `fallbackToDestructiveMigration()`; `allowBackup=false`.
- Chiusura voci PROMEMORIA solo con **SI Renato** (o CONVALIDATO equivalente).
- Play test period: **solo bugfix** su `main` ([STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md)).

---

## Comandi utili

```bash
cd BoxManagerNew
git fetch origin
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5

# Dopo merge da main (bug Play)
git merge origin/main

./gradlew :app:assembleFamigliaDebug
./gradlew :app:installFamigliaDebug
./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.family.*"
./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.viewoutput.ContainerViewSnapshotFactoryTest"
```

**Windows:** `INSTALLA_FAMIGLIA.bat` — topbar attesa **1.3-famigliaB5.5**.

**Play locale (regressione):** `./gradlew :app:assemblePlayDebug`

---

## Versioning correttivi

| Tipo | versionName famiglia | Quando |
|------|----------------------|--------|
| Fix P0/P1 testabile | `1.3-famigliaB5.5`, `B5.6`, … | Incrementare `versionCode` flavor famiglia |
| Solo doc | Invariato | OK senza bump |
| Play release tester | `1.2.x` su `main` | Solo flavor `play`, SI Renato |

Aggiornare sempre: `app/build.gradle.kts`, `INSTALLA_FAMIGLIA.bat`, `docs/famiglia/README.md`, riga famiglia in `BETA_SYNC_POLICY.md`.

---

## PR / branch

- Branch lavoro: `cursor/family-unione-unificata-e5b5` (o `cursor/correctivi-*-7b83` se policy cloud agent richiede branch dedicato → merge in unione).
- PR #10 (`cursor/fix-category-list-promemoria-7b83`): fix T2 già **mergiato** in unione; riferimento storico.

---

## Messaggio tipo per nuova sessione Cursor

```
Continua filone CORRETTIVI post-B5 da docs/famiglia/PROMPT_CONTINUITA_CORRETTIVI.md.

Branch: cursor/family-unione-unificata-e5b5
Build: 1.3-famigliaB5.5

Priorità:
1) Ritest P1 (PRE_RESTORE + Genera Modello) → SI/KO
2) Non invertire famiglia su main durante il test Play
3) Merge main se bug Play tester

Leggi ASSESSMENT + STRATEGIA + PROMEMORIA prima del codice.
```

---

## Riferimenti chiusi (non riaprire)

- [PROMPT_CONTINUITA_B5.md](PROMPT_CONTINUITA_B5.md) — B5 CONVALIDATO B5.2
- NOTA sidecar B0–B7 Play — chiusi
