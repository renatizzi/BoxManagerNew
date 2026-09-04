# Promemoria — interventi trasversali (BoxManager)

**Aggiornato:** 04/09/2026. Ingresso sessione → [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md).  
Regola: `.cursor/rules/annotazioni-renato.mdc`.

---

## Backlog aperto (in evidenza)

Indicazioni di Renato **fuori dalla fetta in corso**. Restano qui in cima **fino alla presa in carico**. Non implementare prima del SI. Scrivere nella stessa sessione, committare, confermare l’ID.

| ID | Data | Indicazione | Stato |
|----|------|-------------|-------|
| **B-SEL-CARTELLA** | M1 / CK1 | Selettore cartella + NAS via **CIFS Documents Provider** (gratuita). Guida operativa: [GUIDA_CARTELLA_RETE_CIFS.md](GUIDA_CARTELLA_RETE_CIFS.md). B5.16 codice; Nota ufficiale a fine ciclo | **In ritest** |

---

## Fix ritest B5.8 → B5.15 (`cursor/promemoria-fix-d69a`)

Regressione introdotta da **B-F7-FORMATO-LISTA**: l’apertura lista Contenitori
per layout F7 aveva dirottato anche inventari/report degli altri Core.

| Domanda | Output atteso (ripristinato) |
|---------|------------------------------|
| Categorie usate / elenco categorie | Lista Categorie con **solo usate** (`FILTER_USED`); contatore = trovate; report con domanda |
| Tutti gli oggetti in archivio | Report oggetti con domanda utente |
| **Quali oggetti ho in cantina?** | Report oggetti filtrati per luogo (non lista Contenitori) |
| Elenco posizioni / luoghi in uso | Lista Posizioni **usate** (`FILTER_USED`); report con domanda |
| F7 (domande 4/7 CK2) | Lista contenitori con layout card (unico caso Motore B → lista) |
| Altro Motore B | Messaggio + stampa ad hoc |

**B5.13:** audit 20 domande; fix KO «oggetti in cantina» (router).

**B5.14 (device IT):** sole «luoghi in uso» OK — due bug UI Intent/filtro.

**B5.15 (device EN):** IT OK, EN apriva liste Contenitori / report «No. containers»:
1. `InventoryListRouter` chiamato senza `SearchLocaleContext` EN → «objects» non riconosciuto
2. function words EN senza `in`/`what`/`which`/`where`/… → luoghi nominati (Cellar) non matchavano

---

## Presa in carico 04/09/2026 (`cursor/promemoria-backlog-d69a`)

| ID | Esito |
|----|-------|
| **B-NOME-APP-BAT** | **Fatto** — launcher e bat → BoxManager (flavor Gradle `famiglia` = solo build) |
| **B-NOME-AUTO-SAVE** | **Fatto** — nome utente salvato mentre digiti / al blur; tasto Salva nascosto; modificabile |
| **B-DEFAULT-IT-EN** | **Fatto** — primo switch IT→EN: seed 16 categorie + 3 posizioni (e `box.position` se ancora seed) → EN; rinominati intatti |
| **B-F7-FORMATO-LISTA** | **Fatto** — F7 apre lista contenitori; titolo con `(oggetto)`; card categoria • posizione • data |
| **B-RICERCA-SENZA-SPECIFICHE** | **Fatto** — stringa vuota su Contenitori/Posizione/Oggetti/Categorie in dashboard; oggetti filtro vuoto = tutti |
| **B-VOCE-OGGETTO** | **Fatto** — microfono su nome e descrizione in Aggiungi/Modifica oggetto |
| **M0** | **Fatto** — [store-listing-en.md](../play/store-listing-en.md) |

---

## Contesto post–Play 1.2 (transizione)

Dopo il rilascio Play **1.2**, due filoni in **parallelo al test** (decisione Renato, confermata 03/09/2026): la 1.2 su `main` resta identica; questo sviluppo **diventa** l’ufficiale a test chiuso e **sostituisce** la 1.2. Si tocca 1.2 solo per bug bloccanti. Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

| Filone | Documento | Stato |
|--------|-----------|-------|
| **Archivio condiviso** | [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) B0–B5 | **CONVALIDATO** — funzione di BoxManager, non un’altra app |
| **Inglese** (scelta lingua + ricerca EN) | [../multilingua/PROMPT_CONTINUITA_M.md](../multilingua/PROMPT_CONTINUITA_M.md) | **M1/CK1** e **M2/CK2 CONVALIDATI** (CK2 device 04/09/2026). Stessa BoxManager. M3 a test Play chiuso. |

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

**P2** — Sync bugfix Play 1.2 ↔ sviluppo: [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md).

---

## Come usare questo file

- Aprire una **nuova sessione** correttivi: leggere [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) + tabella P0 + **Backlog aperto**.
- Chiudere una voce solo dopo **SI Renato** (o criterio equivalente CONVALIDATO).
- Non duplicare qui il dettaglio prodotto delle fette B0–B5 → [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md).
- Indicazione fuori contesto in un altro filone: stessa tabella Backlog aperto, stessa sessione, commit. Regola `.cursor/rules/annotazioni-renato.mdc`.
