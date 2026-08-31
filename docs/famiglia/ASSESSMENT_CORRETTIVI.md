# Assessment interventi correttivi — famiglia + Play

**Data:** 31/08/2026  
**Branch riferimento famiglia:** `cursor/family-unione-unificata-e5b5` (dopo merge fix T2 → **1.3-famigliaB5.3**)  
**Play Store:** `main` → **1.2** (versionCode 3)

Documento di assessment (punto 1 del piano Renato). Per la strategia operativa vedi [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

---

## Riepilogo esecutivo

| Priorità | Voci | Azione |
|----------|------|--------|
| **P0** | T1 chiuso; T2 fix pronto; T3 non recuperato | Ritest T2 su device → SI; T3 chiuso fino a nuova segnalazione |
| **P1** | Igiene salvataggio file (5 punti) | Giro dedicato **dopo** chiusura P0; non blocca test Play |
| **P2** | Sync bugfix Play ↔ famiglia | Processo continuo (vedi strategia) |
| **Filone M** | Multilingua / Scelta lingua | **Fuori** da questo assessment — pianificazione separata |

---

## P0 — Bug segnalati (beta famiglia)

| ID | Area | Problema | Root cause (codice) | Intervento | Stato |
|----|------|----------|---------------------|------------|-------|
| **T1** | Backup → Directory | Nome cartella illeggibile | `SafFolderLabel` + id opaco SAF | B5.2 | **CONVALIDATO** |
| **T2** | Lista Oggetti / Lista Oggetti Trovati | Categoria o icona assenti o diversi in stampa/export | Race `BoxDetailActivity` (box prima delle categorie); `SearchResultActivity` export senza risoluzione `categoryId` | `refreshHeader`, `resolveCategoryForGroup` | **Fix in B5.3** — attende SI device |
| **T3** | *(non identificato)* | Secondo bug citato in chat, non trascritto | — | — | **Chiuso (non recuperabile)** — riaprire solo con nuova evidenza |

### Ritest T2 (checklist Renato)

1. Contenitore → **Lista Oggetti**: header con **categoria + icona** al primo caricamento.
2. Dashboard → ambito **Oggetti** → **Lista Oggetti Trovati**: categoria corretta per ogni gruppo contenitore.
3. Stampa / Esporta da (2): stessa categoria mostrata a schermo.

---

## P1 — Igiene salvataggio file (`salvataggio-file.mdc`)

Criterio di riferimento: nome datato, riuso cartella SAF, box nome + SI/NO; `Modello_Importazione.csv` fisso.

| Punto | Prefisso / nome | Cartella | Box SI/NO | Valutazione |
|-------|-----------------|----------|-----------|-------------|
| Backup | `BCK_ddMMyy_HHmm.zip` | `KEY_BACKUP` | Sì | **OK** |
| PRE_RESTORE | `PRE_RESTORE_ddMMyy_HHmm.zip` | Stessa Backup | Parziale (nome non editabile) | **Da rivedere** |
| Esporta vista Play | `ESPORTA_…csv` | `KEY_IMPORT_EXPORT` | Sì | **OK** (criterio matrice) |
| Genera Modello | `Modello_Importazione.csv` | Usa import/export, non Backup | Scrittura diretta | **Da rivedere** (Nota: riuso cartella Backup) |
| Import auto-backup | `BCK_…` | Backup | Sì | **OK** |
| Invia/Ricevi famiglia B4 | datato | `KEY_FAMILY_SHARE` | Sì | **OK** |
| Etichetta QR → PDF | contesto QR | — | flusso dedicato | Fuori criterio Esporta |

**Raccomandazione:** giro P1 dedicato **dopo** SI su T2 e avvio stabile test Play; nessun blocco per closed test.

---

## P2 — Allineamento codice Play ↔ famiglia

| Aspetto | Stato attuale | Nota |
|---------|---------------|------|
| Due flavor (`play` / `famiglia`) | Operativo | `applicationId` distinti → convivenza su stesso telefono |
| Bugfix Play su `main` | Da mergiare su branch famiglia | Regola in [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) |
| Feature famiglia | Solo `FAMILY_BETA` / flavor famiglia | Non su Play |
| Pubblicazione famiglia su Play | **Vietata** | Invariato |

---

## Filoni post–Play 1.2 (contesto, non correttivi)

| Filone | Stato | Documento |
|--------|-------|-----------|
| Condivisione famiglia B0–B5 | **CONVALIDATO** | [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) |
| Multilingua (Scelta lingua) | Da pianificare | Nota 3.6.6; filone **M** — non in scope correttivi |

---

## Ordine di lavoro concordato

1. **Questo assessment** (fatto).
2. **Sistemare versione famiglia** — merge T2, bump **1.3-famigliaB5.3**, ritest.
3. **Strategia unificazione** — test Play = solo bug tester; famiglia = correttivi P0 + merge da `main`.
