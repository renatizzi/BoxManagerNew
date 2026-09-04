# Prompt di continuità — Correttivi (BoxManager sviluppo + sync 1.2)

**SI Renato, 31/08/2026** — piano a 3 passi. **01/09/2026:** T2 CONVALIDATO; freeze Play 1.2; **P1 CONVALIDATO** B5.7.

Usare questo file come **unico ingresso** per la sessione agente sui **correttivi**. Non riaprire B5 prodotto né il filone inglese (M).

**Identità:** una sola **BoxManager**. Non esiste un’«app famiglia». La 1.2 è la copia dei tester; qui si corregge la **stessa** app di sviluppo. Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md), `.cursor/rules/identita-app.mdc`.

### Backlog aperto (in evidenza — non è questa fetta)

Fonte viva: [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md). Restano visibili fino alla presa in carico.

| ID | Indicazione |
|----|-------------|
| **B-SEL-CARTELLA** | Selettore cartella anche su drive non visti da Android (NAS); SAF non basta da solo — valutazione aperta |

---

## Stato al 01/09/2026

| Voce | Valore |
|------|--------|
| **Branch sviluppo** | `cursor/family-unione-unificata-e5b5` (integrazione) |
| **Build sviluppo** | **1.3-famigliaB5.13** (versionCode **1332**) — etichetta di build, non nome app |
| **Play** | BoxManager **1.2** su `main` (vc 3) — test chiuso; **identica** salvo bug bloccanti |
| **Freeze Play 1.2** | `cursor/versione-test-5409` (snapshot `main`; PR #14) |
| **Dopo il test** | La BoxManager di sviluppo **sostituisce** la 1.2 come ufficiale. Non è un optional. [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) |
| **Archivio condiviso B0–B5** | **CONVALIDATO** — Nota Integrata **9.2** Allegato **4.20** |
| **Filone correttivi** | P0 chiuso; **P1 CONVALIDATO** B5.7; P2 continuo |
| **Backlog aperto** | **B-SEL-CARTELLA** — NAS/SAF; valutazione aperta. Altre voci Promemoria chiuse 04/09 |

### Documenti vincolanti (leggere prima di codice)

| File | Ruolo |
|------|--------|
| [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) | Elenco P0–P2, audit salvataggio file |
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | Una sola BoxManager; 1.2 identica in test; sviluppo = ufficiale a fine test |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Tracking T1–T3, P1, P2, **Backlog aperto** (indicazioni Renato fuori contesto) |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Regola d’oro merge |
| `.cursor/rules/salvataggio-file.mdc` | Criterio P1 |

---

## P0 — Bug beta (stato)

| ID | Stato | Azione prossima sessione |
|----|-------|--------------------------|
| **T1** Backup Directory | **CONVALIDATO** B5.2 | Nessuna |
| **T2** Categoria lista oggetti | **CONVALIDATO** B5.3 (SI device 01/09/2026) | Nessuna |
| **T3** Secondo bug | **Chiuso** (non recuperato) | Ignorare salvo nuova segnalazione con evidenza |

### T2 (storico B5.3)

CONVALIDATO 01/09/2026. File: `BoxDetailActivity.refreshHeader()`, `SearchResultActivity.resolveCategoryForGroup()`, `ContainerViewSnapshotFactory.searchResultGroupBlock()`.

---

## Sequenza consigliata per l’agente

### Fase 1 — Allineamento (obbligatoria)

1. `git checkout cursor/family-unione-unificata-e5b5 && git pull`
2. Leggere tabella P0 in [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md) e [ASSESSMENT](ASSESSMENT_CORRETTIVI.md).
3. Verificare topbar attesa **1.3-famigliaB5.12** in `app/build.gradle.kts` (flavor Gradle `famiglia` = build di sviluppo).

### Fase 2 — P0 residuo

4. **T2 CONVALIDATO** (01/09/2026). Non riaprire.
5. **Non** riaprire T3 senza evidenza nuova.

### Fase 3 — P1 igiene salvataggio file (**CONVALIDATO** B5.7)

| Punto | Stato |
|-------|--------|
| **PRE_RESTORE** | SFOGLIA ZIP; box «Copia di sicurezza» nome + SI/NO; poi conferma ripristino |
| **Genera Modello** | `Modello_Importazione.csv`; cartella Backup visibile; pulsante **Cartella** |
| **Importa** | Picker CSV (non ZIP); cartella Backup |
| **Esporta vista** | Già OK |

Non riaprire P1 senza nuova evidenza.

### Fase 4 — P2 sync Play (continuo)

6. Se arriva bugfix su `main` (tester Play): merge sul branch di sviluppo, test, **non** invertire l’ordine (prima 1.2, poi sviluppo).
7. Se fix nato sullo sviluppo e vale anche per la 1.2: cherry-pick su `main` **prima** o insieme al merge.
8. **Mai** caricare la build di sviluppo su Play Console **durante** il test.

### Fuori scope (non iniziare senza SI)

- Filone **inglese** (M) — Nota 3.6.6 Scelta lingua; stessa BoxManager, altra sessione
- Nuove fette B6 famiglia prodotto
- Motore B / pipeline ricerca (B7 chiuso)
- Feature Play oltre bugfix tester

---

## Vincoli non negoziabili

- Flavor Gradle `famiglia`: `applicationId` `…boxmanager.famiglia` — seconda *installazione* durante il test, **non** un’altra app; **non** su Play finché il test 1.2 è aperto.
- `BuildConfig.FAMILY_BETA` / UI archivio condiviso solo su quella build di sviluppo.
- Room: **no** `fallbackToDestructiveMigration()`; `allowBackup=false`.
- Chiusura voci PROMEMORIA solo con **SI Renato** (o CONVALIDATO equivalente).
- Play test period: 1.2 su `main` **identica**, salvo bug **bloccanti** ([STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md)).

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

**Windows:** `INSTALLA_FAMIGLIA.bat` — topbar attesa **1.3-famigliaB5.12**.

**Play locale (regressione):** `./gradlew :app:assemblePlayDebug`

---

## Versioning correttivi

| Tipo | versionName (etichetta build) | Quando |
|------|------------------------------|--------|
| Fix P0/P1 testabile | `1.3-famigliaB5.7`, `B5.8`, … | Incrementare `versionCode` flavor Gradle `famiglia` |
| Solo doc | Invariato | OK senza bump |
| Play release tester | `1.2.x` su `main` | **Solo** se bug **bloccante**; flavor `play` |

Aggiornare sempre: `app/build.gradle.kts`, `INSTALLA_FAMIGLIA.bat`, `docs/famiglia/README.md`, riga famiglia in `BETA_SYNC_POLICY.md`.

---

## PR / branch

- Branch lavoro: `cursor/family-unione-unificata-e5b5` (o `cursor/correctivi-*-7b83` se policy cloud agent richiede branch dedicato → merge in unione).
- PR #10 (`cursor/fix-category-list-promemoria-7b83`): fix T2 già **mergiato** in unione; riferimento storico.

---

## Messaggio tipo per nuova sessione Cursor

```
Continua filone CORRETTIVI post-B5 da docs/famiglia/PROMPT_CONTINUITA_CORRETTIVI.md.

Branch: cursor/promemoria-fix-d69a
Build: 1.3-famigliaB5.12

Priorità:
1) P2: se bug **bloccante** Play, fix 1.2 su main poi riportarlo sullo sviluppo
2) Durante il test non mettere lo sviluppo su main
3) Non riaprire P0/P1 senza evidenza nuova
4) B-SEL-CARTELLA solo con SI prodotto
5) Una sola app: BoxManager — niente «app famiglia»

Leggi ASSESSMENT + STRATEGIA + PROMEMORIA prima del codice.
```

---

## Riferimenti chiusi (non riaprire)

- [PROMPT_CONTINUITA_B5.md](PROMPT_CONTINUITA_B5.md) — B5 CONVALIDATO B5.2
- NOTA sidecar B0–B7 Play — chiusi
