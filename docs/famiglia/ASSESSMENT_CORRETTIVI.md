# Assessment interventi correttivi — famiglia + Play

**Data:** 01/09/2026 (T2 SI device)  
**Branch riferimento famiglia:** `cursor/family-unione-unificata-e5b5` (dopo merge fix T2 → **1.3-famigliaB5.3**)  
**Play Store:** `main` → **1.2** (versionCode 3)  
**Fonte ufficiale:** `docs/Nota_Integrata_9.2.docx` su **`main`**, **Allegato 4.20**

Documento di assessment (punto 1 del piano Renato). Per la strategia operativa vedi [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

---

## Riepilogo esecutivo

| Priorità | Voci | Azione |
|----------|------|--------|
| **P0** | T1, T2, T3 chiusi | T2 **CONVALIDATO** 01/09/2026; prossimo = P1 |
| **P1** | Igiene salvataggio file (5 punti) | Giro dedicato **dopo** chiusura P0; non blocca test Play |
| **P2** | Sync bugfix Play ↔ famiglia | Processo continuo (vedi strategia) |
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
| PRE_RESTORE | `PRE_RESTORE_ddMMyy_HHmm.zip` | Stessa Backup | Parziale (nome non editabile) | **Da rivedere** |
| Esporta vista Play | `ESPORTA_…csv` | `KEY_IMPORT_EXPORT` | Sì | **OK** (criterio matrice) |
| Genera Modello | `Modello_Importazione.csv` | Usa import/export, non Backup | Scrittura diretta | **Da rivedere** (Nota: riuso cartella Backup) |
| Import auto-backup | `BCK_…` | Backup | Sì | **OK** |
| Invia/Ricevi famiglia B4 | datato | `KEY_FAMILY_SHARE` | Sì | **OK** |
| Etichetta QR → PDF | contesto QR | — | flusso dedicato | Fuori criterio Esporta |

**Raccomandazione:** giro P1 dedicato **dopo** SI esplicito a procedere (P0 T2 chiuso 01/09/2026); nessun blocco per closed test.

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
| Condivisione famiglia B0–B5 | **CONVALIDATO** | Nota **Allegato 4.20** su `main`; copia [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) |
| Multilingua (Scelta lingua) | Da pianificare | Nota 3.6.6; filone **M** — non in scope correttivi |

---

## Ordine di lavoro concordato

1. **Assessment** — fatto (SI Renato).
2. **Versione famiglia B5.3** — fatto; merge T2 su `cursor/family-unione-unificata-e5b5`.
3. **Strategia unificazione** — documentata.
4. **T2** — **CONVALIDATO** 01/09/2026 (SI Renato). Prossimo: P1 igiene salvataggio (solo dopo SI esplicito a procedere). Fonte ufficiale: Nota **Allegato 4.20** su `main`.
