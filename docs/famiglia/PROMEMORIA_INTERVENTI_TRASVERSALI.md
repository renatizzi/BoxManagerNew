# Promemoria — interventi trasversali (famiglia + Play)

**Aggiornato:** 31/08/2026 (post **B4 CONVALIDATO**, SI Renato).

Elenco di fix/igiene **non legati a una sola fetta**, da affrontare quando si tocca l’area o in un giro dedicato (B7 igiene / sync Play).

---

## P0 — Segnalati in beta famiglia

| ID | Area | Problema | Evidenza | Azione prevista |
|----|------|----------|----------|-----------------|
| **T1** | Utility → **Backup Archivio** → card «Backup Directory» | Il campo cartella mostra testo **illeggibile** (URI/encoded, es. `acc=1;doc=encoded=…`) invece del **nome cartella** umano; feedback percepito come **toast** / non persistente in pagina | Screenshot Renato, build **1.3-famigliaB4.10**, 31/08/2026 | **Fix in B5.0** (`SafFolderLabel` gestisce `acc=…;doc=encoded=…` + test). Chiusura solo con **SI Renato** su device |

---

## P1 — Igiene salvataggio file (regola `salvataggio-file.mdc`, chiusura B7)

Verificare **ogni** punto che scrive un file e allineare dove ha senso al criterio Esporta già convalidato:

- Nome proposto datato (`prefisso_ddMMyy_HHmm`)
- Riuso cartella dopo primo CONSENTI Android
- Box unico nome + domanda + SI/NO (sovrascrittura catalogo 2.6)
- `Modello_Importazione.csv` resta nome fisso

**Già allineati in famiglia (B4):** Invia Condivisione Archivio (`KEY_FAMILY_SHARE`, box nome, OK post-salvataggio).

**Da rivedere:** Backup Directory (T1), PRE_RESTORE, Genera Modello, Esporta vista (Play), altri punti elencati in sidecar B7.

---

## P2 — Sync bugfix Play 1.2 ↔ beta famiglia

Vedi [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md): fix su `main` → merge nel branch famiglia; mai pubblicare flavor `famiglia` su Play.

---

## Come usare questo file

- Aprire una **nuova sessione** su B5/B7/backup: leggere la tabella P0.
- Chiudere una voce solo dopo **SI Renato** (o criterio equivalente CONVALIDATO).
- Non duplicare qui il dettaglio prodotto delle fette B0–B5 → [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md).
