# Prompt di continuità — Chiusura allineamento Play + R8 (02/10/2026)

**Ingresso unico** per la chiusura V1 operativa dopo pubblicazione Store.  
**Identità:** una sola **BoxManager**. Test telefono solo su `main` — `.cursor/rules/test-telefono-main.mdc`.

---

## Decisioni Renato 02/10/2026 (congelate)

| # | Decisione |
|---|-----------|
| 1 | **Allineare produzione** alla linea codice completa (release **`1.3.25`** = contenuto 1.3.24 + **R8**) |
| 2 | **B-QTY** e **D Dashboard** = **congelati a tempo indeterminato** — zero codice senza nuovo SI |
| 3 | **Strategia** (post-test / unificazione / Roadmap viva) = **riprendere solo dopo** `produzione 1.3.25 ok` |
| 4 | Documentazione allineata a **questa** attività di chiusura (non riaprire Progetto 2) |
| 5 | **B-PLAY-DEX-R8** = **in codice** in questa fetta (`isMinifyEnabled` + shrink) per soglia Play DEX ≥25% (oggi ~17%) |

**Photo&VideoManager:** in **pausa** finché Renato non riceve OK agenti su punti residui BoxManager (questo prompt + SI AAB produzione).

---

## Build ufficiale di allineamento

| Voce | Valore |
|------|--------|
| Flavor | **`play`** (mai `.famiglia`) |
| `versionName` | **`1.3.25`** |
| `versionCode` | **`29`** |
| R8 | `isMinifyEnabled = true`, `isShrinkResources = true` |
| Mapping | upload `mapping.txt` in Play Console (App bundle explorer / deobfuscazione) |

---

## Sequenza operativa (Renato PC)

### A — Riaprire BoxManager in Android Studio

Chiudere Photo&VideoManager e riaprire BoxManager **di solito basta**. Procedura sicura:

1. **File → Close Project** (chiude Photo&VideoManager).
2. **File → Open…** → cartella repo **BoxManager** (quella con `settings.gradle.kts` / `app/`).
3. Attendi **Gradle Sync**.
4. In basso a sinistra **Build Variants** → **`playDebug`** (non `famiglia*`, non `playRelease` per Run).
5. Verifica topbar attesa dopo Run: **`v. 1.3.25`**.

Se Studio apre ancora l’altro progetto: **File → Open Recent** → scegli esplicitamente BoxManager.  
Due finestre Studio contemporanee vanno bene; evita di Run sulla finestra sbagliata.

```text
git checkout main
git pull origin main
```

Poi Sync + Run **`playDebug`**.

### B — Smoke device (obbligatorio prima di AAB)

Dopo merge su `main` dell’agente:

1. `git checkout main` + `git pull origin main`
2. Build Variants → **`playDebug`**
3. Run sul telefono → topbar **`v. 1.3.25`**
4. Smoke minimo: Dashboard, Contenitori, Backup, una ricerca semplice, Guida, (se premium) Foto / QR / Utility
5. Risposta: `1.3.25 ok` oppure elenco KO

### C — AAB produzione

Solo dopo `1.3.25 ok`:

1. **Build → Generate Signed App Bundle** → flavor/build **`playRelease`**
2. Stesso keystore storico BoxManager
3. Play Console → **Produzione** → nuova release → carica AAB **`1.3.25` (vc 29)**
4. Carica anche **`mapping.txt`** (deobfuscazione crash) se Console lo chiede
5. In **App bundle explorer** verifica % **obfuscation / shrinking / optimization** (target ≥ **25%** ciascuna se DEX > 10 MB)
6. Quando live: messaggio `produzione 1.3.25 ok`

### D — Dopo `produzione 1.3.25 ok` (sessione successiva)

1. Freeze docs **B10** + eventuale nota strategia (punto 3).
2. Photo&VideoManager: ripresa solo con SI Renato.
3. **Non** aprire B-QTY / D.

---

## Fuori scope (questa fetta)

- Codice B-QTY, D Dashboard, Motore B avanzato
- Nuove feature Progetto 2
- Flavor `famiglia` su Play
- Ripresa Photo&VideoManager

---

## File correlati

| File | Ruolo |
|------|--------|
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Backlog + freeze B/D |
| [CHECKLIST_M3_MICRO.md](CHECKLIST_M3_MICRO.md) | Stato M3 / allineamento |
| [../play/README.md](../play/README.md) | AAB / keystore |
| `app/build.gradle.kts` + `app/proguard-rules.pro` | R8 release |
