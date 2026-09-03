# Installare BoxManager (sviluppo) senza il triangolo Run

Se Android Studio non mostra Run o il Terminale dà `JAVA_HOME is not set`, usa **Esplora file Windows** (non Android Studio).

È la **stessa** BoxManager dei tester, build di sviluppo (funzioni in più). Lo script si chiama ancora `INSTALLA_FAMIGLIA.bat` (nome file Gradle), non un’altra app.

## Opzione A — Telefono collegato USB

1. `git pull origin cursor/family-unione-unificata-e5b5`
2. Apri la cartella `BoxManagerNew` in Esplora file
3. **Doppio clic** su `INSTALLA_FAMIGLIA.bat`
4. Attendi "FATTO"
5. Apri **BoxManager** sul telefono (copia di sviluppo; topbar `1.3-famigliaB5.7`)

## Opzione B — Solo APK (nessun USB obbligatorio)

1. `git pull origin cursor/family-unione-unificata-e5b5`
2. **Doppio clic** su `CREA_APK_FAMIGLIA.bat`
3. Si apre Esplora file sull’APK
4. Copia `app-famiglia-debug.apk` sul telefono (USB, Drive, WhatsApp)
5. Sul telefono: apri il file → Installa

Versione attesa in topbar: **1.3-famigliaB5.7**
