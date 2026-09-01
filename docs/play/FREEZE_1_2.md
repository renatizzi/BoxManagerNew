# Freeze Play 1.2 — test chiuso Console

**Data freeze:** 01/09/2026  
**Branch:** `cursor/versione-test-5409`  
**Commit codice:** `f47c970` (`main` al momento del freeze)

## Cosa è

Copia nominata di **`main`** così com’era per il test chiuso Play:

- `versionName` **1.2**
- `versionCode` **3**
- `applicationId` `it.renatizzi.boxmanager`

Non è una nuova release. Non cambia il flusso tester: l’AAB da Console si continua a costruire da **`main`**.

## Cosa non è

- Non sostituisce `main`.
- Non porta la beta famiglia su Play.
- Non va caricato un APK/AAB `famiglia` su Console.

## Come usarlo

```bash
git fetch origin
git checkout cursor/versione-test-5409
```

Per tornare al lavoro Play corrente: `git checkout main`.

Ripristinare questo snapshot su `main` solo con **SI** esplicito (non durante il test, salvo emergenza).
