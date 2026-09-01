# Strategia unificazione — Play (test Console) + beta famiglia

**Data:** 31/08/2026  
**Contesto:** Play **1.2** in (o verso) **test chiuso** Google Console; beta **famiglia** in sideload familiare.

Obiettivo: **un solo codice condiviso** per i bugfix, **due binari distinti** (Play vs famiglia), senza mescolare feature né pubblicare famiglia su Play.

---

## Principio

```
                    ┌─────────────────┐
                    │   main (Play)   │
                    │  flavor play    │
                    │  1.2 → 1.2.x    │
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

- **Play:** unico canale Play Store; durante il test si toccano **solo bug** segnalati dai tester.
- **Famiglia:** laboratorio merge + correttivi P0/P1; **mai** upload su Console. Fonte prodotto: Nota **Allegato 4.20** su `main`.
- **Unificazione** = stesso motore bugfix su `main`, riportato su branch famiglia — **non** un solo APK unico in questa fase.

---

## Fase A — Test chiuso Play (in corso / imminente)

| Cosa | Regola |
|------|--------|
| Modifiche su `main` / flavor `play` | **Solo bugfix** da feedback tester (o bloccanti pre-release) |
| Nuove feature Play | **No** (privacy/Data safety/AAB già in roadmap Progetto 1; niente scope creep) |
| Feature famiglia (B4/B5, merge CSV) | **Solo** branch famiglia — non entrano in AAB Play |
| Versione Play | `versionCode` / `versionName` su `main` solo per release tester approvata |
| Dopo ogni fix Play | `git merge origin/main` su `cursor/family-unione-unificata-e5b5` |

### Flusso bug tester Play

1. Tester segnala su canale concordato.
2. Riproduzione su build **play** da `main`.
3. Fix su `main` → PR → AAB su track test chiuso (se SI).
4. **Stesso giorno o subito dopo:** merge `main` → branch famiglia + test unitari famiglia.
5. **Non** portare su `main` fix nati solo su famiglia senza cherry-pick valutato.

---

## Fase B — Beta famiglia (parallela)

| Cosa | Regola |
|------|--------|
| Branch sorgente | `cursor/family-unione-unificata-e5b5` |
| Build | `assembleFamigliaDebug` / `installFamigliaDebug` |
| Correttivi P0 | T1 **CONVALIDATO** B5.2; T2 **CONVALIDATO** B5.3 (SI Renato 01/09/2026) |
| P1 igiene file | Giro dedicato post-P0 |
| Versione | `1.3-famigliaB5.x` — incrementare a ogni consegna testabile |
| Telefoni famiglia | Solo APK famiglia; Play 1.2 resta installabile affiancata ([DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md)) |

---

## Fase C — Dopo test Play verde (orientamento, non impegnativo ora)

| Opzione | Quando | Nota |
|---------|--------|------|
| **C1** — Play resta 1.2.x, famiglia continua B5.x | Test ok, famiglia ancora in uso domestico | Stato attuale prolungato |
| **C2** — Portare famiglia in `main` dietro flag (futuro) | SI esplicito prodotto | Non in questo periodo |
| **C3** — Filone multilingua (M) | Dopo stabilizzazione bug | Nota 3.6.6 Scelta lingua; branch o fetta dedicata |

Nessuna unificazione «big bang» durante i 14 giorni di test: solo **sync bugfix**.

---

## Cosa NON fare

- Pubblicare APK `famiglia` su Play Console.
- Sviluppare feature famiglia solo su `main`.
- Fixare su famiglia senza riportare su `main` se lo stesso bug esiste in Play.
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

Topbar attesa dopo B5.3: **1.3-famigliaB5.3**.

---

## Riferimenti

- [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) — elenco interventi (copia; prevale Nota 4.20 su `main`)
- [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) — tracking P0–P2
- [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) — regola d’oro merge
- [docs/play/README.md](../play/README.md) — closed test Play
