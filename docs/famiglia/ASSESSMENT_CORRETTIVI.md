# Assessment interventi correttivi — BoxManager

**Data:** 01/09/2026 (P1 CONVALIDATO)  
**Branch riferimento sviluppo:** `cursor/p1-igiene-file-5409` → merge in `cursor/family-unione-unificata-e5b5`  
**Play Store:** `main` → BoxManager **1.2** (versionCode 3); freeze `cursor/versione-test-5409`  
**Fonte ufficiale:** `docs/Nota_Integrata_9.2.docx` su **`main`**, **Allegato 4.20**

Una sola app: **BoxManager**. Documento di assessment (punto 1 del piano Renato). Strategia: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

---

## Riepilogo esecutivo

| Priorità | Voci | Azione |
|----------|------|--------|
| **P0** | T1, T2, T3 chiusi | T2 **CONVALIDATO** 01/09/2026 |
| **P1** | Igiene salvataggio file | **CONVALIDATO** B5.7 (SI Renato device 01/09/2026) |
| **P2** | Sync bugfix Play 1.2 ↔ sviluppo | Processo continuo; **non** mettere lo sviluppo su `main` durante il test |
| **Filone M** | Inglese / Scelta lingua | **Fuori** da questo assessment — stessa BoxManager, sessione separata |

---

## P0 — Bug segnalati (BoxManager di sviluppo)

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

**Raccomandazione:** P1 **chiuso**. Prossimo filone correttivi = P2 (sync bug 1.2 → sviluppo). Nessun blocco per closed test Play.

---

## P2 — Allineamento codice 1.2 ↔ sviluppo

| Aspetto | Stato attuale | Nota |
|---------|---------------|------|
| Due flavor Gradle (`play` / `famiglia`) | Operativo | `applicationId` distinti → due *installazioni*, stessa BoxManager |
| Bugfix 1.2 su `main` | Da riportare sul branch di sviluppo | [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) |
| Funzione archivio condiviso | `FAMILY_BETA` sulla build di sviluppo | Non nella 1.2 dei tester |
| Pubblicazione sviluppo su Play durante il test | **Vietata** | A test chiuso lo sviluppo **è** l’ufficiale |

---

## Filoni post–Play 1.2 (contesto, non correttivi)

| Filone | Stato | Documento |
|--------|-------|-----------|
| Archivio condiviso B0–B5 | **CONVALIDATO** | [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) |
| Inglese (scelta lingua) | In corso (M) | Nota 3.6.6; stessa BoxManager — non in scope correttivi |

---

## Ordine di lavoro concordato

1. **Assessment** — fatto (SI Renato).
2. **Versione sviluppo B5.3** — fatto; T2 **CONVALIDATO** 01/09/2026.
3. **Strategia** — documentata; freeze `cursor/versione-test-5409`; **non** mettere lo sviluppo su `main` durante il test.
4. **P1** — **CONVALIDATO** 01/09/2026 (SI Renato, build **1.3-famigliaB5.7**). Prossimo: P2.
