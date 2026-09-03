# Promemoria — interventi trasversali (BoxManager)

**Aggiornato:** 03/09/2026 — identità: una sola BoxManager. T2 **CONVALIDATO**; P1 **CONVALIDATO** B5.7. Ingresso sessione → [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md).

Elenco di fix/igiene **non legati a una sola fetta**, da affrontare quando si tocca l’area o in un giro dedicato (B7 igiene / sync Play).

---

## Contesto post–Play 1.2 (transizione)

Dopo il rilascio Play **1.2**, due filoni in **parallelo al test** (decisione Renato, confermata 03/09/2026): la 1.2 su `main` resta identica; questo sviluppo **diventa** l’ufficiale a test chiuso e **sostituisce** la 1.2. Si tocca 1.2 solo per bug bloccanti. Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

| Filone | Documento | Stato |
|--------|-----------|-------|
| **Archivio condiviso** | [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) B0–B5 | **CONVALIDATO** — funzione di BoxManager, non un’altra app |
| **Inglese** (scelta lingua + ricerca EN) | [../multilingua/PROMPT_CONTINUITA_M.md](../multilingua/PROMPT_CONTINUITA_M.md) | **M1/CK1 CONVALIDATO**; M2b motore (S1–S3 SI). Stessa BoxManager. |

---

## P0 — Segnalati sulla BoxManager di sviluppo

| ID | Area | Problema | Evidenza | Stato |
|----|------|----------|----------|-------|
| **T1** | Utility → **Backup Archivio** → «Backup Directory» | Nome cartella illeggibile (id opaco base64) | Screenshot Renato, B5.1, 31/08/2026 | **CONVALIDATO** B5.2 (SI Renato) |
| **T2** | **Lista Oggetti** / **Lista Oggetti Trovati** | Categoria/icona assenti o incoerenti (header / stampa-export) | Segnalazione su sviluppo; ritest 01/09/2026 tre prove OK | **CONVALIDATO** B5.3 (SI Renato) |
| **T3** | — | Secondo bug non recuperato dalle chat | — | **Chiuso** — riaprire solo con nuova evidenza |

---

## P1 — Igiene salvataggio file (regola `salvataggio-file.mdc`, chiusura B7)

Verificare **ogni** punto che scrive un file e allineare dove ha senso al criterio Esporta già convalidato:

- Nome proposto datato (`prefisso_ddMMyy_HHmm`)
- Riuso cartella dopo primo CONSENTI Android
- Box unico nome + domanda + SI/NO (sovrascrittura catalogo 2.6)
- `Modello_Importazione.csv` resta nome fisso

**Già allineati in famiglia (B4):** Invia Condivisione Archivio (`KEY_FAMILY_SHARE`, box nome, OK post-salvataggio).

**B5.7 (P1 CONVALIDATO, SI Renato device 01/09/2026):**

- **PRE_RESTORE** — SFOGLIA sceglie il ZIP; box unico nome editabile + SI/NO (titolo «Copia di sicurezza»), poi conferma ripristino.
- **Genera Modello** — `Modello_Importazione.csv` fisso; riuso cartella Backup; box con cartella visibile + pulsante **Cartella**.
- **Importa** — picker CSV (non ZIP); parte dalla cartella Backup.

**Già OK:** Backup, Esporta vista, Import auto-backup, Invia famiglia B4.

---

## P2 — Sync bugfix Play 1.2 ↔ BoxManager di sviluppo

Vedi [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) e [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md): fix sulla 1.2 → riportarlo sullo sviluppo; non caricare lo sviluppo su Play durante il test.

---

## Come usare questo file

- Aprire una **nuova sessione** correttivi: leggere [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) + tabella P0.
- Chiudere una voce solo dopo **SI Renato** (o criterio equivalente CONVALIDATO).
- Non duplicare qui il dettaglio prodotto delle fette B0–B5 → [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md).
