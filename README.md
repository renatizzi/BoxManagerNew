# BoxManagerNew

App Android per inventariare contenitori e oggetti (`applicationId` Play: `it.renatizzi.boxmanager`).

## Track Play (Alpha / produzione futura)

- Branch: `main`
- Release corrente: **1.2** (`versionCode` **3**)
- Privacy: https://renatizzi.github.io/BoxManagerNew/privacy/
- **Non** caricare su Play artifact del flavor `famiglia`

## Betatest famiglia (Merge B — fuori Play)

Documentazione: [`docs/famiglia/`](docs/famiglia/).

- Branch base: `cursor/family-b-beta-75ee`
- Branch B4 in corso: `cursor/family-unione-unificata-e5b5` (`1.3-famigliaB4.3`)
- Flavor: **`famiglia`** → id `it.renatizzi.boxmanager.famiglia` (affiancabile a Play)
- Policy sync bugfix 1.2: [`docs/famiglia/BETA_SYNC_POLICY.md`](docs/famiglia/BETA_SYNC_POLICY.md)
- Nota prodotto: [`docs/famiglia/NOTA_B0_MERGE_FAMIGLIA.md`](docs/famiglia/NOTA_B0_MERGE_FAMIGLIA.md)

```bash
./gradlew :app:assembleFamigliaDebug
# oppure
./gradlew :app:assembleFamigliaRelease
```

Build di controllo allineata a 1.2 (stesso branch, senza UI famiglia):

```bash
./gradlew :app:assemblePlayDebug
```

## Requisiti locali

- Android Studio / JDK 11+
- Keystore solo in locale (`key.properties` — non in git) per release firmate
