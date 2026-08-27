# Play — keystore, AAB, icona 512

## Icona Store (già pronta)
File: `docs/play/icon_play_512.png` (512×512, RGB, da `boxmanager_launcher`).

In Play Console → scheda dello Store principale → **Icona dell'app** / alta risoluzione → carica questo PNG.

## Immagine in primo piano (banner)
File: `docs/play/feature_graphic_1024x500.png` (1024×500).

In Play Console → scheda dello Store → **Immagine in primo piano** → carica questo PNG.

Il **video** promo sulla scheda Store è **facoltativo**: puoi saltarlo.

## Keystore + AAB (solo sul tuo PC — non in git)

### A) Con Android Studio (consigliato)
1. Menu **Build** → **Generate Signed App Bundle / APK…**
2. Scegli **Android App Bundle** → **Next**
3. **Create new…** (keystore):
   - Path: fuori dal progetto, es. `C:\Users\<tu>\keystores\boxmanager-upload.jks`
   - Password keystore: sceglila e salvala offline
   - Alias: `boxmanager`
   - Password chiave: (può coincidere) salvala offline
   - Validity: 25+ anni
   - Certificate: nome/organizzazione (tuoi dati)
4. **Next** → build type **release** → **Create**
5. Annota il path del file `.aab` generato (di solito sotto `app/release/`).

### B) Opzionale da riga di comando
1. Copia `key.properties.example` → `key.properties` nella **root** del progetto.
2. Compila i path/password (il file è gitignored).
3. `./gradlew :app:bundleRelease`
4. AAB in `app/build/outputs/bundle/release/app-release.aab`

### Upload Console
1. Play Console → BoxManager → **Testa e rilascia** → test **interno** (o closed, fetta successiva).
2. Crea release → carica l’`.aab`.
3. Al primo upload: accetta **Play App Signing** (consigliato).
4. Il package deve risultare `it.renatizzi.boxmanager` (già in Gradle).

## Non mettere in git
- `*.jks` / `*.keystore`
- `key.properties`
- password / alias in chat o nei commit
