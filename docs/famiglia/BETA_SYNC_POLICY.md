# Policy — allineare un fix 1.2 sulla BoxManager di sviluppo

## Obiettivo

Correggere un bug **bloccante** della **1.2** (Play) **senza** far divergere la BoxManager di sviluppo, e **senza** pubblicare quella build su Play **durante** il test.

Una sola app: **BoxManager**. Vedi [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

## Branch e flavor Gradle (non nomi di prodotto)

| Copia | Come si ottiene | Dove vive |
|-------|-----------------|-----------|
| BoxManager 1.2 | branch `main` → `assemblePlayRelease` | Play Store / test chiuso |
| Freeze 1.2 | branch `cursor/versione-test-5409` (snapshot di `main`) | Solo ripristino snapshot; **non** sostituisce `main` |
| BoxManager sviluppo (storico B2.2) | branch `cursor/family-b-beta-75ee` → `assembleFamigliaDebug` | Installazione locale |
| BoxManager sviluppo (attuale) | branch `cursor/family-unione-unificata-e5b5` → `assembleFamigliaDebug` | Installazione locale; topbar **1.3-famigliaB5.7** |

Due product flavor Gradle sulla stessa app:

- **`play`** — `applicationId` Store; versionCode 3 / versionName 1.2; `FAMILY_BETA=false` (la 1.2 dei tester).
- **`famiglia`** — altro `applicationId` così Android non sovrascrive la 1.2; versionName **`1.3-famigliaB5.7`** (versionCode 1326); `FAMILY_BETA=true` (funzione archivio condiviso accesa). **Non** è un altro nome di app.

## Regola d’oro

1. **Bug bloccante della 1.2** (unica ragione per toccare `main` durante il test) → fix su **`main`**, convalida, AAB Play solo per pubblicare quel fix.
2. Subito dopo: sul branch di sviluppo  
   `git fetch github main && git merge github/main`  
   (oppure `git pull` da remote dove sta `main`).
3. **Mai** sviluppare funzioni nuove solo su `main` Play.
4. **Mai** caricare la build di sviluppo su Play Console **durante** il test.
5. Se un fix urgente nasce sullo sviluppo e vale per la 1.2: cherry-pick su `main` **prima** o **insieme** al merge.

## Comandi utili (macchina Renato)

```bash
cd BoxManagerNew
git fetch origin
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
./gradlew :app:assembleFamigliaDebug
./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.family.*"
```

APK debug tipico:

`app/build/outputs/apk/famiglia/debug/app-famiglia-debug.apk`

## Cosa evita il disallineamento

- Stesso codice del fix in entrambe le installazioni dopo il merge.
- `applicationId` diverso → lo Store non sovrascrive la copia di sviluppo (e viceversa).
- Funzione archivio condiviso dietro `BuildConfig.FAMILY_BETA` → la 1.2 resta comportamentalmente quella dei tester.

## Periodo test chiuso Play

**Fonte congelata:** [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) (conferma Renato 03/09/2026).

Durante il test: su **`main`** la 1.2 resta **identica**. Si accetta **solo** un aggiornamento per **bug bloccanti**. Freeze: `cursor/versione-test-5409`. Lo sviluppo **non** entra in 1.2 a metà test. Alla fine del test quella BoxManager **diventa** l’ufficiale e sostituisce la 1.2. Dopo ogni fix Play (se c’è), merge su `cursor/family-unione-unificata-e5b5`.

## Cosa fare in caso di conflitto di merge

1. Preferire il comportamento **1.2** per file già in produzione (Import, Restore, Guida base, premium).
2. Re-applicare solo i delta dello sviluppo (archivio condiviso, card Utility, Nota).
3. Rieseguire test unitari catalogo + Guida.
