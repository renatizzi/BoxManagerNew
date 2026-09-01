# Promemoria — interventi trasversali (famiglia + Play)

**Aggiornato:** 01/09/2026 — T2 **CONVALIDATO**; freeze Play `cursor/versione-test-5409`; P1 in **1.3-famigliaB5.6**. Ingresso sessione → [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md).

Elenco di fix/igiene **non legati a una sola fetta**, da affrontare quando si tocca l’area o in un giro dedicato (B7 igiene / sync Play).

---

## Contesto post–Play 1.2 (transizione)

Dopo il rilascio Play **1.2**, due filoni paralleli (decisione Renato):

| Filone | Documento | Stato |
|--------|-----------|-------|
| **Famiglia** — condivisione archivio | [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) B0–B5 | **CONVALIDATO** |
| **Multilingua** — Scelta lingua UI | Nota Integrata 3.6.6 Impostazioni («Prossime implementazioni») | **Da pianificare** (filone M) |

---

## P0 — Segnalati in beta famiglia

| ID | Area | Problema | Evidenza | Stato |
|----|------|----------|----------|-------|
| **T1** | Utility → **Backup Archivio** → «Backup Directory» | Nome cartella illeggibile (id opaco base64) | Screenshot Renato, B5.1, 31/08/2026 | **CONVALIDATO** B5.2 (SI Renato) |
| **T2** | **Lista Oggetti** / **Lista Oggetti Trovati** | Categoria/icona assenti o incoerenti (header / stampa-export) | Segnalazione beta famiglia; ritest 01/09/2026 tre prove OK | **CONVALIDATO** B5.3 (SI Renato) |
| **T3** | — | Secondo bug non recuperato dalle chat | — | **Chiuso** — riaprire solo con nuova evidenza |

---

## P1 — Igiene salvataggio file (regola `salvataggio-file.mdc`, chiusura B7)

Verificare **ogni** punto che scrive un file e allineare dove ha senso al criterio Esporta già convalidato:

- Nome proposto datato (`prefisso_ddMMyy_HHmm`)
- Riuso cartella dopo primo CONSENTI Android
- Box unico nome + domanda + SI/NO (sovrascrittura catalogo 2.6)
- `Modello_Importazione.csv` resta nome fisso

**Già allineati in famiglia (B4):** Invia Condivisione Archivio (`KEY_FAMILY_SHARE`, box nome, OK post-salvataggio).

**B5.5 (P1, attende SI device):**

- **PRE_RESTORE** — box unico nome editabile + SI/NO (stesso criterio Esporta), poi conferma ripristino.
- **Genera Modello** — `Modello_Importazione.csv` fisso come proposto; riuso cartella Backup; box SI/NO.

**Già OK:** Backup, Esporta vista, Import auto-backup.

---

## P2 — Sync bugfix Play 1.2 ↔ beta famiglia

Vedi [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) e [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md): fix su `main` → merge nel branch famiglia; mai pubblicare flavor `famiglia` su Play.

---

## Come usare questo file

- Aprire una **nuova sessione** correttivi: leggere [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) + tabella P0.
- Chiudere una voce solo dopo **SI Renato** (o criterio equivalente CONVALIDATO).
- Non duplicare qui il dettaglio prodotto delle fette B0–B5 → [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md).
