# Assessment interventi correttivi — famiglia + Play

**Data:** 01/09/2026 (P1 CONVALIDATO)  
**Branch riferimento famiglia:** `cursor/p1-igiene-file-5409` → merge in `cursor/family-unione-unificata-e5b5`  
**Play Store:** `main` → **1.2** (versionCode 3); freeze `cursor/versione-test-5409`  
**Fonte ufficiale:** `docs/Nota_Integrata_9.2.docx` su **`main`**, **Allegato 4.20**

Documento di assessment (punto 1 del piano Renato). Per la strategia operativa vedi [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

---

## Riepilogo esecutivo

| Priorità | Voci | Azione |
|----------|------|--------|
| **P0** | T1, T2, T3 chiusi | T2 **CONVALIDATO** 01/09/2026 |
| **P1** | Igiene salvataggio file | **CONVALIDATO** B5.7 (SI Renato device 01/09/2026) |
| **P2** | Sync bugfix Play ↔ famiglia | Processo continuo; **non** invertire famiglia su `main` durante il test |
| **Filone M** | Multilingua / Scelta lingua | **Fuori** da questo assessment — pianificazione separata |

---

## P0 — Bug segnalati (beta famiglia)

| ID | Area | Problema | Root cause (codice) | Intervento | Stato |
|----|------|----------|---------------------|------------|-------|
| **T1** | Backup → Directory | Nome cartella illeggibile | `SafFolderLabel` + id opaco SAF | B5.2 | **CONVALIDATO** |
| **T2** | Lista Oggetti / Lista Oggetti Trovati | Categoria o icona assenti o diversi in stampa/export | Race `BoxDetailActivity` (box prima delle categorie); `SearchResultActivity` export senza risoluzione `categoryId` | `refreshHeader`, `resolveCategoryForGroup` | **CONVALIDATO** B5.3 (SI Renato, 01/09/2026, tre prove OK) |
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
| PRE_RESTORE | `PRE_RESTORE_ddMMyy_HHmm.zip` | Stessa Backup | Box unico nome + SI/NO + Cartella/SFOGLIA ZIP | **CONVALIDATO** B5.7 |
| Esporta vista Play | `ESPORTA_…csv` | `KEY_IMPORT_EXPORT` | Sì | **OK** (criterio matrice) |
| Genera Modello | `Modello_Importazione.csv` | Cartella Backup (`KEY_BACKUP`) | Box unico SI/NO + pulsante Cartella | **CONVALIDATO** B5.7 |
| Import auto-backup | `BCK_…` | Backup | Sì | **OK** |
| Invia/Ricevi famiglia B4 | datato | `KEY_FAMILY_SHARE` | Sì | **OK** |
| Etichetta QR → PDF | contesto QR | — | flusso dedicato | Fuori criterio Esporta |

**Raccomandazione:** P1 **chiuso**. Prossimo filone correttivi = P2 (sync bug Play → famiglia). Nessun blocco per closed test Play.

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

1. **Assessment** — fatto (SI Renato).
2. **Versione famiglia B5.3** — fatto; T2 **CONVALIDATO** 01/09/2026.
3. **Strategia unificazione** — documentata; freeze `cursor/versione-test-5409`; **non** invertire su `main`.
4. **P1** — **CONVALIDATO** 01/09/2026 (SI Renato, build **1.3-famigliaB5.7**). Prossimo: P2.
