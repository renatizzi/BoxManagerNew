# Prompt di continuità — B5 (famiglia) — **CHIUSO / CONVALIDATO**

**SI Renato, 31/08/2026** — build verificata **1.3-famigliaB5.2** (1321). Branch: `cursor/family-b5-createdby-delete-7b83` (PR #9).

---

## Stato al 31/08/2026 (chiusura B5)

| Voce | Valore |
|------|--------|
| **B5** | **CONVALIDATO** (SI Renato). Build: **1.3-famigliaB5.2** (1321) |
| **B4** | **CONVALIDATO** (1.3-famigliaB4.10) |
| **Branch** | `cursor/family-b5-createdby-delete-7b83` → PR #9 |
| **Flavor** | solo `famiglia` |
| **Play** | resta **1.2** su `main` |

### B5 chiuso — deliverable

- `createdBy` su contenitori/oggetti (CSV retrocompatibile)
- Delete familiare automatica (tombstone + `CANCELLAZIONI`)
- **T1** Backup Directory leggibile (B5.2)
- Test: `./gradlew :app:testFamigliaDebugUnitTest --tests "com.example.boxmanagernew.family.*"`

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
