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

Processo di sempre (il più semplice):

1. Io (agente) preparo il codice, **mergio su `main`**, e ti dico il **`versionName` atteso** (es. `v. 1.3.4`)
2. Tu: `git checkout main` + `git pull origin main` (mai altri branch per il test telefono)
3. Build Variants → **`playDebug`** (versione **senza firma**, come il Run di sempre)
4. **Run** sul telefono → controlla topbar = versione comunicata (senza `famiglia`)
5. Solo dopo il tuo OK ai test: Generate Signed App Bundle → **`playRelease`** (con firma) → Play Console → Invio release

Nota: **`playRelease` al Run** dà errore di firma: non usarlo per le prove. Serve solo per il file da caricare su Play.
**SI 14/09:** prima di ogni test l’agente indica sempre il riferimento versione in topbar.
**SI 14/09 (permanente):** test telefono **solo su `main`** — l’agente fa sempre il merge su `main` prima di chiedere il test.

**SI 16/09:** quando ti chiedo un test, trovi sempre una **procedura numerata** (pull, variante, versione in topbar, tap da fare, cosa deve succedere). Se manca, chiedi «passi operativi».

## Stop — non fare

- Non caricare la build di sviluppo su Play Console **durante** il test  
- Non disinstallare 1.2 dal tablet  
- Non usare Ripristino ZIP per “unire” archivi  

## Quando hai finito

Scrivi solo: branch pushato sì/no + BoxManager di sviluppo avviata sul telefono sì/no.  
Al passo successivo ti guido sul **Catalogo** (Utility) per l’archivio condiviso.
