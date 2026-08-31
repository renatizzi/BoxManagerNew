# Promemoria — interventi trasversali (famiglia + Play)

**Aggiornato:** 31/08/2026 (post **B5 CONVALIDATO**; fix **T2** in corso su branch `cursor/fix-category-list-promemoria-7b83`).

Elenco di fix/igiene **non legati a una sola fetta**, da affrontare quando si tocca l’area o in un giro dedicato (B7 igiene / sync Play).

---

## Contesto post–Play 1.2 (transizione)

Dopo il rilascio Play **1.2**, due filoni paralleli (decisione Renato):

| Filone | Documento | Stato |
|--------|-----------|-------|
| **Famiglia** — condivisione archivio | [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) B0–B5 | **CONVALIDATO** |
| **Multilingua** — Scelta lingua UI | Nota Integrata 3.6.6 Impostazioni («Prossime implementazioni»); **non** ancora in NOTA famiglia | **Da pianificare** (filone M) |

---

## P0 — Segnalati in beta famiglia

| ID | Area | Problema | Evidenza | Stato |
|----|------|----------|----------|-------|
| **T1** | Utility → **Backup Archivio** → «Backup Directory» | Nome cartella illeggibile (id opaco base64) | Screenshot Renato, B5.1, 31/08/2026 | **CONVALIDATO** B5.2 (SI Renato): `SafFolderLabel` + cache `folder_label` |
| **T2** | **Lista Oggetti** (header contenitore) e **Lista Oggetti Trovati** (gruppo per box) | Categoria/icona assenti o incoerenti tra schermo e stampa/export (race osservatori LiveData; snapshot export senza risoluzione per `categoryId`) | Segnalazione Renato beta famiglia; assicurato in chat ma **non trascritto** al commit `32dbeaf` | **Fix in corso** — `BoxDetailActivity.refreshHeader`, `SearchResultActivity.resolveCategoryForGroup` |
| **T3** | *(da recuperare)* | Secondo bug segnalato insieme a T2 in sessione precedente | Da chat Renato | **Aperto** — descrizione mancante in repo |

---

## P1 — Igiene salvataggio file (regola `salvataggio-file.mdc`, chiusura B7)

Verificare **ogni** punto che scrive un file e allineare dove ha senso al criterio Esporta già convalidato:

- Nome proposto datato (`prefisso_ddMMyy_HHmm`)
- Riuso cartella dopo primo CONSENTI Android
- Box unico nome + domanda + SI/NO (sovrascrittura catalogo 2.6)
- `Modello_Importazione.csv` resta nome fisso

**Già allineati in famiglia (B4):** Invia Condivisione Archivio (`KEY_FAMILY_SHARE`, box nome, OK post-salvataggio).

**Da rivedere:** PRE_RESTORE, Genera Modello, Esporta vista (Play), altri punti elencati in sidecar B7.

---

## P2 — Sync bugfix Play 1.2 ↔ beta famiglia

Vedi [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md): fix su `main` → merge nel branch famiglia; mai pubblicare flavor `famiglia` su Play.

---

## Come usare questo file

- Aprire una **nuova sessione** su B5/B7/backup: leggere la tabella P0.
- Chiudere una voce solo dopo **SI Renato** (o criterio equivalente CONVALIDATO).
- Non duplicare qui il dettaglio prodotto delle fette B0–B5 → [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md).
