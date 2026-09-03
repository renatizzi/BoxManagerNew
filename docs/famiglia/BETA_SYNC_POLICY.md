# Policy anti-disallineamento — beta famiglia vs Play 1.2

## Obiettivo

Correggere bug della **1.2** (Play / Alpha) **senza** far divergere la betatest famiglia, e **senza** pubblicare la B su Play.

## Branch e flavor

| Binario | Come si ottiene | Dove vive |
|---------|-----------------|-----------|
| Play 1.2 | branch `main` → `assemblePlayRelease` | Play Store / test chiuso |
| Freeze Play 1.2 | branch `cursor/versione-test-5409` (snapshot di `main`) | Solo ripristino snapshot; **non** sostituisce `main` |
| Beta famiglia (base B2.2) | branch `cursor/family-b-beta-75ee` → `assembleFamigliaRelease` (o `famigliaDebug`) | Solo sideload familiare |
| Beta famiglia (B5 + correttivi) | branch `cursor/family-unione-unificata-e5b5` → `assembleFamigliaDebug` | Solo sideload; v. **1.3-famigliaB5.7** |

Su questi branch esistono due product flavor:

- **`play`** — stessi `applicationId` / versionCode 3 / versionName 1.2; `FAMILY_BETA=false` (verifica regressione locale).
- **`famiglia`** — `applicationId` `…boxmanager.famiglia`; versionName **`1.3-famigliaB5.7`** (versionCode 1326); `FAMILY_BETA=true`.

## Regola d’oro

1. **Bug bloccante della 1.2** (unica ragione per toccare `main` durante il test) → fix su **`main`**, convalida, AAB Play solo per pubblicare quel fix.
2. Subito dopo: sul branch famiglia  
   `git fetch github main && git merge github/main`  
   (oppure `git pull` da remote dove sta `main`).
3. **Mai** sviluppare feature B solo su `main` Play.
4. **Mai** caricare artifact `famiglia` su Play Console.
5. Se un fix urgente nasce mentre si lavora in famiglia: cherry-pick / riportare il commit su `main` **prima** o **insieme** al merge, così Play e beta restano allineati sul bugfix.

## Comandi utili (macchina Renato)

```bash
cd BoxManagerNew
git fetch origin
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
# base integrazione (dopo merge PR):
# git checkout cursor/family-b-beta-75ee
# git merge origin/main
./gradlew :app:assembleFamigliaDebug
./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.family.*"
```

APK debug tipico:

`app/build/outputs/apk/famiglia/debug/app-famiglia-debug.apk`

## Cosa evita il disallineamento

- Stesso codice bugfix in entrambi i binari dopo il merge.
- `applicationId` diverso → Play non sovrascrive la beta in famiglia (e viceversa).
- Feature B dietro `BuildConfig.FAMILY_BETA` / UI solo flavor famiglia → il path `play` resta comportamentalmente 1.2.

## Periodo test chiuso Play

**Fonte congelata:** [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) riquadro «1.2, sviluppo, ufficiale» (conferma Renato 03/09/2026).

Durante il test: su **`main`** la 1.2 resta **identica**. Si accetta **solo** un aggiornamento per **bug bloccanti** dei tester. Freeze nominato: `cursor/versione-test-5409`. Lo sviluppo (famiglia, inglese) **non** entra in 1.2 a metà test. Alla fine del test quello sviluppo **diventa** l’ufficiale e sostituisce la 1.2. Dopo ogni fix Play (se c’è), merge su `cursor/family-unione-unificata-e5b5`.

## Cosa fare in caso di conflitto di merge

1. Preferire il comportamento **1.2** per file già in produzione (Import, Restore, Guida base, premium).
2. Re-applicare solo i delta famiglia (catalogo, card Utility, Nota).
3. Rieseguire test unitari catalogo + Guida.
