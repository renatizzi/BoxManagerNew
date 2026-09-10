# Play — keystore, AAB, icona 512

**Ingresso cutover M3 (post-test):** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](../famiglia/PROMPT_CONTINUITA_M3_MERGE_PLAY.md) (CONVALIDATO 10/09/2026).  
Finché il test chiuso **1.2** è aperto: **niente** AAB flavor `famiglia` / `.famiglia` su Console (C8); niente merge feature su `main`.

### Checklist Console M3 (C1–C8) — solo a test chiuso + SI

1. **C1** — Chiudere / promuovere il test 1.2  
2. **C2** — AAB **release** flavor **play** (keystore locale; sotto)  
3. **C3** — `versionCode` > ultimo Play; `versionName` ufficiale (es. **1.3**)  
4. **C4** — Release sulla traccia scelta (produzione o breve closed 1.3)  
5. **C5** — Icona 512, feature graphic, screenshot (aggiungere shot EN / novità se serve)  
6. **C6** — Listing IT/EN — bozze: [store-listing-it.md](store-listing-it.md), [store-listing-en.md](store-listing-en.md)  
7. **C7** — Messaggio tester / `BOXMANAGER-TESTER` se resta closed  
8. **C8** — Mai `.famiglia`

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

## Screenshot telefono (Play)
Cartella: `docs/play/screenshots/` (già ritagliati a 1080×2160, rapporto 2:1).

Caricare in Console, in quest'ordine:
1. `01_contenitori.png`
2. `02_categorie.png`
3. `03_utility.png`
4. `04_dashboard_it.png` (Dashboard IT — prep M3)
5. `05_dashboard_en.png` (Dashboard EN — prep M3; mostra Scelta lingua / UI inglese)

Non usare lo screenshot Impostazioni con nome reale.
I shot 04/05 oggi mostrano topbar `1.3-famigliaB5.24` (build sviluppo): a M3, se possibile, sostituire con shot dalla AAB **play 1.3** ufficiale.

## Test chiuso — Archivio completo per i tester (release)

In release **non** c'è il pannello debug. I tester sbloccano Archivio completo con codice in-app:

`BOXMANAGER-TESTER`

(Accetta anche spazi/minuscole: es. `boxmanager tester`.)

Messaggio da inviare con il link opt-in Play:

```text
Ciao! Test chiuso BoxManager (gratis su Play).

1) Apri il link e tocca "Diventa tester"
2) Installa BoxManager dal Play Store (stesso account Google)
3) In app: tocca Guida per la guida rapida
4) Per Archivio completo: apri una funzione avanzata → campo codice → BOXMANAGER-TESTER
5) Resta iscritto al test almeno 14 giorni

Grazie!
```

Il codice amico `BOXMANAGER-AMICO` resta valido per il rinnovo via condivisione.

## Test chiuso — checklist (solo sul tuo PC / Console)

**Stato Play 1.2 (durante test):** flavor **play** su `main` — `versionCode = 3`, `versionName = 1.2`. Topbar da Gradle. A M3: alzare oltre questo (es. **1.3** / vc > 3) sulla linea ufficiale.

### Tu — passo 1: aggiorna e genera AAB

```bash
git pull origin main
```

Android Studio → **Sync** → **Build → Generate Signed App Bundle** (release, stesso keystore di prima).

File da caricare: AAB **play release** (package `it.renatizzi.boxmanager`). Per M3: versione ufficiale nuova (es. **1.3**), non la build `famiglia`.

### Tu — passo 2: Play Console

1. **Testa e rilascia** → **Test** → **Test chiusi** → **Test chiusi - Alpha** → **Gestisci canale**
2. **Crea nuova versione** → carica l’AAB → **Pubblica** sul track

### Tu — passo 3: tester

1. Stesso canale Alpha → **Tester** → elenco email (14–20 Gmail)
2. Copia **link opt-in** e invia il messaggio sopra (codice `BOXMANAGER-TESTER`)
3. Attendi **14 giorni** con almeno **12** tester iscritti

Niente altro obbligatorio da codice finché non parte il test chiuso.
