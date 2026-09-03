# Strategia — test Play 1.2 e sviluppo BoxManager

**Data:** 01/09/2026  
**Conferma 03/09/2026** (Renato): piano **congelato**. Non rivotarlo. Non parafrasarlo con «merge su `main` solo con SI». Non parlare di «app famiglia» o «app multilingue».

---

## Identità — una sola app

L’unica app si chiama **BoxManager**.

| Copia | Cos’è |
|-------|--------|
| **1.2** (test Play, branch `main`) | La BoxManager dei tester. Resta **identica** fino alla fine del test. |
| **Sviluppo** (branch dedicati) | La **stessa** BoxManager, con funzioni in più: archivio condiviso, inglese, correttivi. |

Archivio condiviso e inglese **non** sono altre app. Sono funzioni della BoxManager di sviluppo. A test chiuso quella copia **diventa** l’ufficiale e **sostituisce** la 1.2.

Nomi Gradle (`play`, `famiglia`), cartelle `docs/famiglia`, filone M: **etichette di lavoro** (branch, flavor, documenti), non nomi di prodotto.

Durante il test Android può tenere **due installazioni** (package diversi) così lo Store non sovrascrive la 1.2. Restano la stessa app: una senza le funzioni nuove, una con.

---

## Congelato — 1.2, sviluppo, ufficiale

| Punto | Regola |
|-------|--------|
| **1.2 su `main`** | Resta **identica** fino alla **fine del test** Play (circa 10 giorni da questa conferma). I tester vedono solo quella. |
| **Sviluppo nel frattempo** | Continua in parallelo sulla stessa BoxManager (archivio condiviso **già fatto**; inglese **in corso**; correttivi). Branch dedicati. **Mai** usare `main` come base di nuove funzioni. |
| **Cosa diventa l’ufficiale** | Alla fine del test, la BoxManager di sviluppo **sostituisce** la 1.2 su Play. È il piano, non un optional. |
| **Unica eccezione durante il test** | Bug **bloccante** dei tester → si aggiorna la **1.2**. Altrimenti `main` non si tocca. |
| **Cosa non è** | Non è «chiedere SI per copiare l’inglese su Play a metà test». Non è «la 1.2 resta l’ufficiale e lo sviluppo è un’altra app». |

Il SI operativo resta solo per **pubblicare** un AAB (fix 1.2 in emergenza, oppure il passaggio all’ufficiale a test chiuso): è il via in Console, non il *se* le funzioni nuove debbano entrare.

---

## Principio (durante il test)

```
                    ┌─────────────────┐
                    │  BoxManager 1.2 │
                    │  main / Play    │
                    │  identica       │
                    │  1.2.x solo se  │
                    │  bug bloccante  │
                    └────────┬────────┘
                             │ solo quel fix
                             ▼
              ┌──────────────────────────────┐
              │  BoxManager sviluppo         │
              │  branch dedicati             │
              │  + archivio condiviso        │
              │  + inglese + correttivi      │
              └──────────────────────────────┘
```

- **1.2:** unico binario del test chiuso; si tocca **solo** per bug **bloccanti**.
- **Sviluppo:** avanza in parallelo. Il flavor Gradle `famiglia` **non** si carica su Play durante il test (sovrascriverebbe il patto con i tester).
- **Dopo il test** l’ufficiale Play **è** la BoxManager di sviluppo (sostituisce la 1.2).

---

## Fase A — Test chiuso Play (in corso)

| Cosa | Regola |
|------|--------|
| Modifiche su `main` / flavor `play` | **Solo** bug **bloccanti** da tester. La 1.2 resta identica. |
| Funzioni nuove (archivio condiviso, inglese, …) | **No** su `main` durante il test |
| Archivio condiviso (B4/B5) | Solo branch di sviluppo — non nell’AAB 1.2 |
| Inglese (filone M) | Branch M dedicati, base sviluppo — non nell’AAB 1.2 |
| Versione Play 1.2 | `versionCode` / `versionName` su `main` solo se si pubblica un fix bloccante |
| Freeze 1.2 | Branch `cursor/versione-test-5409` — snapshot; i tester restano su `main` |
| Sviluppo su `main` durante il test | **No** |
| Dopo ogni fix Play (se c’è) | `git merge origin/main` sui branch di sviluppo |

### Flusso bug tester Play

1. Tester segnala su canale concordato.
2. Riproduzione sulla 1.2 da `main`.
3. Fix su `main` → PR → AAB sul test chiuso **solo** se il bug è bloccante e si pubblica l’aggiornamento 1.2.
4. **Stesso giorno o subito dopo:** riportare il fix sui branch di sviluppo + test.
5. **Non** mettere su `main` un fix nato solo sullo sviluppo se non è lo stesso bug Play.

---

## Fase B — BoxManager di sviluppo (parallela al test)

| Cosa | Regola |
|------|--------|
| Branch | `cursor/family-unione-unificata-e5b5` e feature `cursor/…` |
| Build | `assembleFamigliaDebug` / `installFamigliaDebug` (flavor Gradle `famiglia` = build di sviluppo, **non** un altro nome di app) |
| Correttivi P0 | T1–T2 **CONVALIDATO**; T3 chiuso |
| P1 igiene file | **CONVALIDATO** B5.7 (SI Renato 01/09/2026) |
| Versione in topbar | `1.3-famigliaB5.x` — etichetta di build, non nome prodotto |
| Telefoni | La 1.2 Store può restare affiancata alla build di sviluppo ([DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md)) |

---

## Fase C — Dopo il test Play (piano congelato)

Non è un menu di opzioni. Alla fine del test l’ufficiale su Play **è** la BoxManager di sviluppo (archivio condiviso + inglese + correttivi) e **sostituisce** la 1.2.

Il SI di quel momento è solo il via in Console (AAB, versionCode, scheda store), non «decidi se le funzioni nuove devono entrare».

| Cosa | Nota |
|------|------|
| **Durante il test** | Nessun «big bang» su `main`. Solo 1.2 identica, salvo bug bloccanti. |
| **Inglese (M)** | Già in parallelo al test (SI 01/09/2026), non «dopo» il test. Funzione della stessa app. |
| «La 1.2 resta per sempre, lo sviluppo è un’altra app» | **Scartata.** |

Dettaglio operativo del passaggio in Console: quando il test è chiuso, sessione dedicata — non durante M2.

---

## Cosa NON fare

- Parlare di «app famiglia» o «app multilingue».
- Caricare la build di sviluppo su Play Console **durante** il test.
- Toccare `main` / 1.2 per funzioni nuove mentre il test è aperto.
- Usare «merge su `main` solo con SI» per le funzioni: durante il test `main` **non si tocca** (salvo bloccanti); dopo il test lo sviluppo **è** l’ufficiale.
- Sviluppare funzioni nuove partendo da `main`.
- Fixare sulla copia di sviluppo un bug **Play bloccante** senza riportarlo su `main` (la 1.2 è quella da aggiornare).
- Riaprire Motore B / pipeline ricerca per correttivi trasversali.

---

## Comandi rapidi (Renato)

### Dopo fix su main

```bash
git fetch origin
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
git merge origin/main
./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.family.*"
./gradlew :app:assembleFamigliaDebug
```

### Installare la BoxManager di sviluppo

```bash
# Windows
INSTALLA_FAMIGLIA.bat
# oppure
./gradlew :app:installFamigliaDebug
```

Topbar attesa dopo P1: **1.3-famigliaB5.7** (etichetta di build).

---

## Riferimenti

- [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) — elenco interventi
- [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) — tracking P0–P2
- [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) — allineare un fix 1.2 sulla copia di sviluppo
- [docs/play/README.md](../play/README.md) — closed test Play
- `.cursor/rules/identita-app.mdc` — stessa identità per ogni sessione agente
