# Assessment — Cestino (Progetto 2 / A2)

**Stato:** storico ANALISI/FEEDBACK; **prevale** [REQUISITI_CESTINO.md](REQUISITI_CESTINO.md) (**CONGELATO** + CONVALIDA documento SI Renato 07/09/2026). Zero codice.  
**Processo:** **chiuso** (4/4).  
**Posizione nella sequenza concordata:** A1 QR avanzato (**congelato**) → **A2** → A3 Foto oggetto.  
**Dipendenza a valle:** [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) richiede delete/ripristino che gestisca i file foto.  
**Nota su fonte:** in Nota **9.2 §4.1.6** non c’è oggi un blocco intitolato «CESTINO»; merito in [REQUISITI_CESTINO.md](REQUISITI_CESTINO.md).  
**Premium:** A1–A3 (e linea Progetto 2 in analisi) = **Archivio completo** — SI Renato 07/09/2026 (anche Foto: R6 in REQUISITI_FOTO).

---

## 0. Perché questa voce ora

- A1 QR avanzato: **CONVALIDA documento** chiusa 07/09/2026.  
- Sequenza: Cestino **prima** di Foto in codice (Foto già requisiti congelati).  
- Oggi l’eliminazione è **definitiva** in Room; con le foto in file serve un modello di delete/ripristino esplicito.

---

## 1. AS-IS (verifica codice / Note — non requisiti nuovi)

### 1.1 Eliminazione utente (V1)

- Conferme catalogo **2.6**: `Conferma eliminazione?`; contenitore con oggetti; categorie/posizioni in uso bloccate.
- **Oggetti / contenitori:** delete **hard** — riga rimossa da Room (`ObjectRepository.delete`, `BoxRepository.deleteBox`, multi-select).
- Nessuna UI «Cestino», nessuna retention, nessun ripristino post-delete.

### 1.2 Tombstone famiglia (B5) — **non è** il Cestino

- Flavor sviluppo: `FamilyPropagatingDelete` + tabella `family_deletion_tombstone` + sezione `CANCELLAZIONI` in Invia Archivio.
- Scopo: **propagare** la cancellazione agli altri dispositivi nel merge (archivio unico).
- L’entità locale viene comunque **rimossa**; il tombstone non tiene una copia ripristinabile in UI.
- **Vietato** confondere tombstone merge con recycle bin utente senza SI esplicito.

### 1.3 Cosa chiede Foto (già congelato)

Da REQUISITI Foto T1 / T7:

- elimina / cestino oggetto → cancella anche file e metadati foto;  
- ripristino (se previsto dal Cestino) deve poter **ricongiungere** foto per `objectPermanentId`;  
- implementare Foto **dopo** Cestino.

---

## 2. Cosa potrebbe significare «Cestino» (opzioni prodotto)

Tre letture distinte — **una sola** va scelta in FEEDBACK:

| Lettura | Comportamento | Impatto | Legame Foto |
|---------|---------------|---------|-------------|
| **C1 — Soft-delete + UI Cestino** | Elimina → esce dalle liste vive; resta in «Cestino» ripristinabile; svuota / scadenza → hard delete | **Alto** (stato, query, UI, retention) | Foto: file restano finché in cestino; hard delete li toglie |
| **C2 — Solo contratto delete↔file** (senza UI cestino) | Hard delete come oggi, ma API unica che cancella anche allegati (foto future); niente ripristino | **Basso-medio** | Soddisfa “cancella file”; **non** soddisfa “ripristino oggetto+foto” |
| **C3 — Soft-delete senza schermata dedicata** | Flag `deletedAt` / nascosto dalle liste; ripristino solo da dettaglio storico o undo breve | **Medio** | Intermedio; UX più debole |

**Ipotesi da FEEDBACK:** la sequenza «Cestino → Foto» e il testo Foto su ripristino puntano a **C1**. C2 da solo è insufficiente se si vuole undo reale. C3 è compromesso.

---

## 3. Ambito entità (da SI)

| Ambito | Nota |
|--------|------|
| **Solo oggetti** | Allineato a Foto (solo oggetti hanno foto); contenitori restano hard delete V1 |
| **Oggetti + contenitori** | Coerente con “cestino archivio”; eliminazione box con oggetti: regole 2.6 da ripensare (cestino cascata?) |
| **Anche categorie/posizioni** | **Sconsigliato** in A2 — già blocco “in uso”; poco valore recycle |

**Raccomandazione analisi:** **oggetti + contenitori** (C1), categorie/posizioni fuori. Se SI solo oggetti, documentarlo (contenitori hard).

---

## 4. Usability (bozza se C1)

### 4.1 Elimina (liste / dettaglio)

1. Stessi dialog 2.6 (testi invariati finché non si aggiunge frase ufficiale).  
2. SI → sposta in Cestino (non più in liste vive / conteggi / ricerca).  
3. Opzionale: snackbar breve «Annulla» (undo immediato) — **da SI**.

### 4.2 Schermata Cestino

- Ingresso: Utility o Impostazioni (da SI).  
- Lista elementi cestino: nome, tipo (oggetto/contenitore), data eliminazione.  
- Azioni: **Ripristina** | **Elimina definitivamente** | **Svuota cestino** (con conferma).  
- Ripristina oggetto: torna nel contenitore di origine se esiste; se il box è stato eliminato → regola da SI (ripristina anche box? oggetto orfano? scegli box?).

### 4.3 Retention

| Opzione | Pro | Contro |
|---------|-----|--------|
| **R-A** Solo manuale (nessuna scadenza) | Semplice | Cestino infinito |
| **R-B** Scadenza fissa (es. 30 gg) poi hard | Prevedibile | Serve job/check all’avvio |
| **R-C** Limite conteggio | Controlla storage | UX meno chiara |

**Raccomandazione:** **R-B 30 gg** + svuota manuale (allineabile a Foto/storage).

### 4.4 Ricerca / Motore A-B

- Entità in cestino: **escluse** da Pipeline e liste.  
- Non riaprire Motore B.

### 4.5 Family merge

- Soft-delete locale **vs** tombstone: da definire.  
  - Opzione F1: soft-delete **non** propaga finché hard delete dal cestino → poi tombstone come oggi.  
  - Opzione F2: già al soft-delete si scrive tombstone (gli altri perdono subito l’entità) — ripristino locale **non** rianima sugli altri senza nuovo Invia.  
**Raccomandazione:** **F1** (tombstone solo al hard delete / svuota) — evita “fantasmi” e resta coerente con merge attuale.

---

## 5. Impatto architetturale (bozza C1)

| Area | Invasività | Nota |
|------|------------|------|
| Room `objects` / `boxes` | **Media-alta** | `deletedAt` nullable **oppure** tabelle `*_trash` / copia riga. Preferenza analisi: colonna `deletedAt` + indici; query liste filtrano `deletedAt IS NULL`. Migrazione **non** distruttiva |
| DAO / repository | **Alta** | Tutte le query “vive” devono escludere cestino |
| UI Cestino | **Media** | Nuova Activity/lista |
| Family tombstone | **Media** | Spostare record tombstone al hard delete (F1) |
| Backup / Ripristina | **Media** | Inclusione entità cestino? Bozza: **sì** nel ZIP (con flag) così Ripristina non perde il cestino; **da SI** |
| Invia/Ricevi Archivio | **Media** | Solo entità vive in inventario; hard delete → CANCELLAZIONI |
| Foto (dopo A3) | Hook | Hard delete / svuota → cancella file; ripristino → file ancora presenti se non svuotato |
| Pipeline ricerca | Solo filtro esclusione | Nessuna nuova famiglia Motore B |

`allowBackup=false` e no `fallbackToDestructiveMigration()` restano.

---

## 6. Checklist FEEDBACK — CONVALIDATA (passo 3, 07/09/2026)

| # | Domanda | Esito |
|---|---------|--------|
| **C-Q1** | Lettura prodotto | **SI — C1** soft-delete + UI Cestino |
| **C-Q2** | Entità | **SI — oggetti + contenitori** |
| **C-Q3** | Retention | **SI — R-B 30 giorni** + svuota manuale |
| **C-Q4** | Ingresso UI | **SI — Utility** |
| **C-Q5** | Undo snackbar | **SI** |
| **C-Q6** | Box mancante al ripristino oggetto | **SI** — ripristina box se in cestino; se hard-deleted → scegli contenitore |
| **C-Q7** | Family / tombstone | **SI — F1** (tombstone solo a hard delete) |
| **C-Q8** | Backup include cestino | **SI** |
| **C-Q9** | Nuove frasi 2.6 | **SI** — solo dopo elenco ufficiale pre-codice |
| **Premium** | Gate Archivio completo | **SI** — A1/A2/A3 (linea Progetto 2 in analisi) |

---

## 7. Fuori scope (bozza)

- Cestino per categorie/posizioni.  
- Confondere UI Cestino con schermata tombstone famiglia.  
- Foto (A3) / QR avanzato (A1 già congelato).  
- Motore B / pipeline oltre esclusione cestino.  
- C2 da solo se si sceglie C1.

---

## 8. Criterio di accettazione (bozza, post-implementazione)

1. Elimina oggetto/box → sparisce dalle liste vive; compare in Cestino.  
2. Ripristina → torna in archivio vivo (regole C-Q6).  
3. Elimina definitivamente / Svuota / scadenza → hard delete (+ tombstone famiglia se flavor).  
4. Ricerca e liste non mostrano cestino.  
5. Backup→Ripristina preserva cestino se C-Q8=Sì.  
6. Migrazione Room non wipe; play+famiglia.  
7. (A3) Hard delete rimuove file foto; soft-delete li conserva.

---

## 9. Esito — A2 chiuso (documento congelato)

**Prodotto congelato:** C1 · oggetti+contenitori · 30 gg · Utility · undo · F1 · Backup con cestino · **premium**.

**Documento:** [REQUISITI_CESTINO.md](REQUISITI_CESTINO.md) — **CONGELATO** (CONVALIDA aggiornamento documento SI Renato 07/09/2026).

**Premium trasversale verificato:** A1 R10 · A2 §0/T4 · A3 R6 (Foto).

**Zero codice** finché SI apertura fetta.
