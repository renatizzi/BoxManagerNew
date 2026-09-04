# Installare BoxManager (sviluppo) senza il triangolo Run

Se Android Studio non mostra Run o il Terminale dà `JAVA_HOME is not set`, usa **Esplora file Windows** (non Android Studio).

È la **stessa** BoxManager dei tester, build di sviluppo (funzioni in più). Lo script si chiama ancora `INSTALLA_FAMIGLIA.bat` (nome file Gradle), non un’altra app.

## Opzione A — Telefono collegato USB

1. `git pull` del branch di lavoro indicato in chat
2. Apri la cartella `BoxManagerNew` in Esplora file
3. **Doppio clic** su `INSTALLA_FAMIGLIA.bat`
4. Attendi "FATTO"
5. Apri **BoxManager** sul telefono (copia di sviluppo; topbar `1.3-famigliaB5.7`)

## Opzione B — Solo APK (nessun USB obbligatorio)

1. `git pull` del branch di lavoro indicato in chat
2. **Doppio clic** su `CREA_APK_FAMIGLIA.bat`
3. Si apre Esplora file sull’APK
4. Copia `app-famiglia-debug.apk` sul telefono (USB, Drive, WhatsApp)
5. Sul telefono: apri il file → Installa

Versione attesa in topbar: **1.3-famigliaB5.8**

### Cartelle di rete / NAS (B-SEL-CARTELLA)

**Guida operativa (CIFS):** [GUIDA_CARTELLA_RETE_CIFS.md](GUIDA_CARTELLA_RETE_CIFS.md).

**Files di Google non basta** per il NAS. Serve l’app gratuita **CIFS Documents Provider** (Play / F-Droid): si registra lì la cartella di rete, poi in BoxManager → Sfoglia la si sceglie come le altre.

Da **B5.16**, Sfoglia elenca anche le radici già registrate sul telefono e apre il selettore di sistema. BoxManager non monta SMB da solo.
