# Solo ciò che puoi fare tu (Renato)

Il resto (codice, Nota B0, flavor Gradle, catalogo, patch/bundle) è già pronto sull’agent.

Una sola app: **BoxManager**. Qui installi la copia di **sviluppo** accanto alla 1.2, non un’altra app.

## Dispositivi (fatto tuo)

| Dispositivo | Azione |
|-------------|--------|
| **Tablet** | Lascia **solo BoxManager 1.2** da Play — test Google. Non installare lo sviluppo. |
| **Telefono** | Qui installerai **BoxManager** di sviluppo. Può restare anche la 1.2 affiancata. |

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

### A) Sviluppo (lavoro quotidiano)
1. Apri il progetto `BoxManagerNew`
2. Attendi Sync Gradle
3. Seleziona variante **`famigliaDebug`**
4. Run / installa sul **telefono**
5. Topbar tipo `v. 1.3-famiglia…` = normale per lo sviluppo

### B) Prima di caricare su Play (obbligatorio — SI Renato 13/09)
Stesso processo Run di sempre, ma variante Store:
1. `git pull origin main`
2. Build Variants → **`playRelease`**
3. **Run** sul telefono
4. Topbar deve essere tipo **`v. 1.3`** (senza `famiglia`)
5. Solo dopo: crea il file firmato **playRelease** e caricalo su Play Console
6. Completa l’invio della release (non lasciare in bozza)

## Stop — non fare

- Non caricare la build di sviluppo su Play Console **durante** il test  
- Non disinstallare 1.2 dal tablet  
- Non usare Ripristino ZIP per “unire” archivi  

## Quando hai finito

Scrivi solo: branch pushato sì/no + BoxManager di sviluppo avviata sul telefono sì/no.  
Al passo successivo ti guido sul **Catalogo** (Utility) per l’archivio condiviso.
