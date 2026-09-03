# Strategia unificazione — Play (test Console) + sviluppo in parallelo

**Data:** 01/09/2026  
**Conferma testo 03/09/2026** (Renato): il piano sotto è **congelato**. Non rivotarlo in sessione. Non parafrasarlo con «merge su `main` solo con SI».

**Contesto:** Play **1.2** in test chiuso Google Console; freeze nominato `cursor/versione-test-5409`; sviluppo (archivio condiviso, inglese, …) su branch dedicati, **non** su `main`.

Obiettivo durante il test: **un solo codice condiviso** per i bugfix, **due binari distinti** (Play 1.2 vs laboratorio famiglia), senza pubblicare il flavor `famiglia` su Play.

---

## Congelato — 1.2, sviluppo, ufficiale

| Punto | Regola |
|-------|--------|
| **1.2 su `main`** | Resta **identica** fino alla **fine del test** Play (circa 10 giorni da questa conferma). I tester vedono solo quella. |
| **Sviluppo nel frattempo** | Continua in parallelo (archivio condiviso **già fatto**; filone inglese **in corso**; correttivi famiglia). Branch dedicati. **Mai** usare `main` come base di nuove feature. |
| **Cosa diventa l’app ufficiale** | Alla fine del test, **quella** versione di sviluppo (famiglia + inglese + correttivi) **sostituisce** la 1.2 su Play. È il piano, non un optional da rivotare. |
| **Unica eccezione durante il test** | I tester segnalano un **bug bloccante** → allora sì, si aggiorna la **1.2** (`main` / Play). Altrimenti `main` non si tocca. |
| **Cosa non è** | Non è «chiedere SI per copiare l’inglese su Play a metà test». Non è «la 1.2 resta l’ufficiale e lo sviluppo resta laboratorio per sempre». |

Il SI operativo resta solo per **pubblicare** un AAB (bugfix 1.2 in emergenza, oppure il passaggio all’ufficiale a test chiuso): è il via in Console, non il *se* famiglia/inglese debbano entrare.

---

## Principio

```
                    ┌─────────────────┐
                    │   main (Play)   │
                    │  flavor play    │
                    │  1.2 (identica) │
                    │  1.2.x solo se  │
                    │  bug bloccante  │
                    └────────┬────────┘
                             │ merge bugfix (solo)
                             ▼
              ┌──────────────────────────────┐
              │ cursor/family-unione-…     │
              │  flavor famiglia           │
              │  1.3-famigliaB5.x          │
              │  + B0–B5 + correttivi P0   │
              └──────────────────────────────┘
```

- **Play 1.2:** unico binario del test chiuso; si tocca **solo** per bug **bloccanti** dei tester.
- **Sviluppo (famiglia + inglese):** avanza in parallelo; flavor `famiglia` **mai** in upload Console durante il test.
- **Durante il test** unificazione = stesso motore **bugfix** da `main` verso i branch di sviluppo — **non** un solo APK unico in questa fase.
- **Dopo il test** l’ufficiale Play **è** lo sviluppo (sostituisce la 1.2). Vedi riquadro congelato sopra.

---

## Fase A — Test chiuso Play (in corso / imminente)

| Cosa | Regola |
|------|--------|
| Modifiche su `main` / flavor `play` | **Solo** bug **bloccanti** da tester. Nessun’altra modifica. La 1.2 resta identica. |
| Nuove feature (famiglia, inglese, …) | **No** su `main` durante il test |
| Feature famiglia (B4/B5, merge CSV) | **Solo** branch famiglia — non entrano nell’AAB 1.2 |
| Filone inglese (M) | Branch M dedicati, base famiglia — non entrano nell’AAB 1.2 |
| Versione Play 1.2 | `versionCode` / `versionName` su `main` solo se si pubblica un fix bloccante |
| Freeze 1.2 | Branch `cursor/versione-test-5409` — snapshot; i tester restano su `main` |
| Sviluppo su `main` durante il test | **No** |
| Dopo ogni fix Play (se c’è) | `git merge origin/main` sui branch di sviluppo (famiglia / M) |

### Flusso bug tester Play

1. Tester segnala su canale concordato.
2. Riproduzione su build **play** da `main`.
3. Fix su `main` → PR → AAB sul test chiuso **solo** se il bug è bloccante e si pubblica l’aggiornamento 1.2.
4. **Stesso giorno o subito dopo:** merge `main` → branch famiglia + test unitari famiglia.
5. **Non** portare su `main` fix nati solo su famiglia senza cherry-pick valutato.

---

## Fase B — Beta famiglia (parallela)

| Cosa | Regola |
|------|--------|
| Branch sorgente | `cursor/family-unione-unificata-e5b5` |
| Build | `assembleFamigliaDebug` / `installFamigliaDebug` |
| Correttivi P0 | T1–T2 **CONVALIDATO**; T3 chiuso |
| P1 igiene file | **CONVALIDATO** B5.7 (SI Renato 01/09/2026) |
| Versione | `1.3-famigliaB5.x` — incrementare a ogni consegna testabile |
| Telefoni famiglia | Solo APK famiglia; Play 1.2 resta installabile affiancata ([DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md)) |

---

## Fase C — Dopo il test Play (piano congelato)

Non è un menu di opzioni. Alla fine del test l’app ufficiale su Play **è** lo sviluppo fatto in parallelo (archivio condiviso + inglese + correttivi) e **sostituisce** la 1.2.

Il SI di quel momento è solo il via in Console (AAB, versionCode, scheda store), non «decidi se famiglia/inglese devono entrare».

| Cosa | Nota |
|------|------|
| **Durante il test** | Nessun «big bang» su `main`. Solo 1.2 identica, salvo bug bloccanti. |
| **Inglese (M)** | Già in parallelo al test (SI 01/09/2026), non «dopo» il test. |
| **C1** «Play resta 1.2 per sempre, sviluppo laboratorio» | **Scartata.** Contraddice il congelato. |

Dettaglio operativo del passaggio in Console: quando il test è chiuso, sessione dedicata — non durante M2.

---

## Cosa NON fare

- Pubblicare APK `famiglia` su Play Console durante il test.
- Toccare `main` / 1.2 per feature (famiglia, inglese, …) mentre il test è aperto.
- Usare la formula «merge su `main` solo con SI» per le feature: durante il test `main` **non si tocca** (salvo bloccanti); dopo il test lo sviluppo **diventa** l’ufficiale.
- Sviluppare feature nuove partendo da `main`.
- Fixare su famiglia un bug **Play bloccante** senza riportarlo su `main` (la 1.2 è quella da aggiornare).
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

### Install famiglia

```bash
# Windows
INSTALLA_FAMIGLIA.bat
# oppure
./gradlew :app:installFamigliaDebug
```

Topbar attesa dopo P1: **1.3-famigliaB5.7**.

---

## Riferimenti

- [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) — elenco interventi
- [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) — tracking P0–P2
- [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) — regola d’oro merge
- [docs/play/README.md](../play/README.md) — closed test Play
