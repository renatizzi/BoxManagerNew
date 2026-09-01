# Solo ciò che puoi fare tu (Renato)

Il resto (codice, Nota B0, flavor, catalogo B1, patch/bundle) è già pronto sull’agent.

## Dispositivi (fatto tuo)

| Dispositivo | Azione |
|-------------|--------|
| **Tablet** | Lascia **solo BoxManager 1.2** da Play — test Google. Non installare Famiglia. |
| **Telefono** | Qui installerai **BoxManager Famiglia** (beta). Può restare anche la 1.2 affiancata. |

## PC — una volta sola (git + GitHub)

1. Scarica dagli **Artifacts** di questa sessione il file  
   `family-b-beta.bundle`
2. Nella cartella del clone `BoxManagerNew`:

```bash
chmod +x scripts/renato-bootstrap-family-beta.sh
# Se lo script non c’è ancora sul tuo main, scaricalo pure dagli artifact
# oppure esegui a mano i comandi sotto.

./scripts/renato-bootstrap-family-beta.sh ~/Downloads/family-b-beta.bundle
```

Equivalente manuale se preferisci:

```bash
cd BoxManagerNew
git checkout main
git pull origin main
git fetch ~/Downloads/family-b-beta.bundle HEAD:cursor/family-b-beta-75ee
git checkout cursor/family-b-beta-75ee
git push -u origin cursor/family-b-beta-75ee
```

## Android Studio — build sul telefono

1. Apri il progetto `BoxManagerNew` (branch `cursor/family-b-beta-75ee`)
2. Attendi Sync Gradle
3. Seleziona variante **`famigliaDebug`** (non `play`)
4. Run / installa sul **telefono**
5. Apri l’icona **BoxManager Famiglia** → Impostazioni → imposta il **nome utente** (es. Renato)

## Stop — non fare

- Non caricare AAB/APK Famiglia su Play Console  
- Non disinstallare 1.2 dal tablet  
- Non usare Ripristino ZIP per “unire” archivi  

## Quando hai finito

Scrivi solo: branch pushato sì/no + app Famiglia avviata sul telefono sì/no.  
Al passo successivo ti guido sul **Catalogo famiglia** (Utility).
