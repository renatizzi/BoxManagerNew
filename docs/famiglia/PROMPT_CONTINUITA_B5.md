# Prompt di continuità — B5 (famiglia)

Copia-incolla per avviare la prossima sessione agente / sviluppo dopo **B4 CONVALIDATO**.

---

## Stato al 31/08/2026

| Voce | Valore |
|------|--------|
| **B4** | **CONVALIDATO** (SI Renato). Ultima build verificata: **1.3-famigliaB4.10** (1318) |
| **Branch** | `cursor/family-unione-unificata-e5b5` → PR #8 → base `cursor/family-b-beta-75ee` |
| **Flavor** | solo `famiglia` (`it.renatizzi.boxmanager.famiglia`) |
| **Play** | resta **1.2** (versionCode 3) su `main` — non pubblicare famiglia su Play |

### B4 chiuso — cosa funziona

- Pagina **Condivisione Archivio**: UI card = Utility; testi B4.5; layout telefono B4.4.
- **Invia** (tabelle + archivio): riuso cartella SAF dopo primo CONSENTI; pulsante **Cartella** nel box nome; box **Salvataggio completato.** + OK post-export.
- **Ricevi**: anteprima SI/NO; merge per ID stabili.
- Test: `./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.family.*"`

### Interventi trasversali aperti

Leggere [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md):

- **T1 (P0):** Backup Archivio — campo «Backup Directory» con URI illeggibile / feedback toast → sistemare in B5 o giro igiene dedicato.

---

## Obiettivo B5 (fonte: NOTA_B0 §7–§8)

**Deliverable:** origine = **nome utente** Impostazioni su contenitori/oggetti; **delete esplicito** propagabile in merge.

### 1. Attribuzione `createdBy`

Fonte nome: `SharedPreferences` chiave `username` (Impostazioni) — **non** introdurre un secondo «membro famiglia».

| Punto | Comportamento |
|-------|----------------|
| Topbar famiglia | Nome salvato (già parzialmente presente) |
| Nuovo contenitore / oggetto | `createdBy` = username trim alla **creazione**; immutabile dopo create salvo SI esplicito |
| Nome vuoto | Fallback UI `Utente` |
| Pacchetto unione | Campo viaggia nel CSV merge; informativo («chi ha censito»), **non** ACL |

**Lavoro tecnico probabile:**

- Room: colonna `createdBy` su `box` e `object` (+ migration non distruttiva, no `fallbackToDestructiveMigration`).
- Writer/Reader merge famiglia: colonna in sezioni CONTENITORI / OGGETTI (versione formato o campo opzionale retrocompatibile).
- Merger/Applier: merge del campo; regole update vs conflitto coerenti con `lastModified`.
- UI creazione/modifica: valorizzazione automatica; eventuale visualizzazione origine (solo famiglia).

### 2. Delete esplicito propagabile

- Oggi: delete **non** propagato in automatico (NOTA_B0 §3.2).
- B5: meccanismo **esplicito** (es. flag/tombstone nel pacchetto o azione UI «rimuovi anche in famiglia») — **aprire NOTA_B0** e allineare con Renato prima di codice se il dettaglio non è in Nota.

### 3. Versione

- Prossima: `1.3-famigliaB5.0` (incrementare `versionCode` flavor famiglia).
- Aggiornare `INSTALLA_FAMIGLIA.bat` e README famiglia.

---

## Vincoli non negoziabili

- `BuildConfig.FAMILY_BETA` / flavor `famiglia` only per feature B.
- Room: **no** `fallbackToDestructiveMigration()`; `allowBackup=false`.
- Pipeline ricerca / Motore B: **non toccare** (B7 chiuso).
- B5 **non CONVALIDATO** in doc finché Renato non dice **SI** esplicito.
- Bugfix Play 1.2: `main` first, poi merge su branch famiglia ([BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md)).

---

## Sequenza consigliata per l’agente

1. Leggere `NOTA_B0_MERGE_FAMIGLIA.md` §7–§8 e `PROMEMORIA_INTERVENTI_TRASVERSALI.md`.
2. Proporre analisi B5 (schema CSV + migration + UI) → attendere **SI** Renato se ambiguo.
3. Implementare `createdBy` end-to-end con test unitari famiglia.
4. Implementare delete esplicito (scope confermato).
5. Valutare **T1** Backup Directory nello stesso giro se tocca `SafFolderLabel`/backup.
6. Bump B5.x, test, push PR #8 (o branch dedicato `cursor/family-b5-*-d28f` se policy cloud agent).

---

## Comandi utili

```bash
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
./gradlew :app:assembleFamigliaDebug
./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.family.*"
```

Install sideload: `INSTALLA_FAMIGLIA.bat` — topbar attesa dopo B4: **1.3-famigliaB4.10**.
