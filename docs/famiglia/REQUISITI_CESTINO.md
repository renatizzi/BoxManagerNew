# Requisiti — Cestino (soft-delete / ripristino)

**Stato:** documento scritto 07/09/2026 — in attesa **CONVALIDA aggiornamento documento** (passo 4).  
**CONVALIDA analisi/requisiti (passo 3):** SI Renato 07/09/2026 (C-Q1–C-Q9 + vincolo **premium** Progetto 2).  
**Uso:** riferimento funzionale e tecnico-architetturale fino all’implementazione Progetto 2 / A2.  
**Destinazione:** confluire nella Nota Integrata ufficiale (Allegato / rinvio Roadmap) senza riaprire il merito.  
**Assessment di lavoro:** [ASSESSMENT_CESTINO.md](ASSESSMENT_CESTINO.md) (storico; prevale **questo** file su conflitti).  
**Sequenza:** dopo A1 [QR avanzato](REQUISITI_QR_AVANZATO.md) → **A2** → A3 [Foto](REQUISITI_FOTO_OGGETTO.md). **STOP codice** finché non c’è SI di apertura fetta.

---

## 0. Premium (vincolo architetturale Progetto 2)

Le voci in analisi Progetto 2 (**A1 QR avanzato**, **A2 Cestino**, **A3 Foto oggetto**, e le successive della stessa linea finché non diverso SI) sono funzionalità **Archivio completo / premium**: soggette alle **stesse condizioni di accesso** già vigenti (prova a tempo, CONDIVIDI, codice locale; gate `ArchivioCompletoNav` / `PremiumFeature`).

- Accesso negato → stesso pattern UI di sblocco delle altre feature premium (nessun bypass).  
- Nuova feature enum (es. `TRASH` / cestino) in implementazione; pitch commerciale fuori catalogo 2.6.  
- Liste “vive”, Backup base e anagrafiche non premium restano utilizzabili; **entrare nel Cestino**, ripristinare, svuotare = gate premium.

---

## 1. Obiettivo prodotto

Consentire di **annullare eliminazioni** di contenitori e oggetti: l’Elimina sposta in **Cestino**; da lì si **ripristina** o si **elimina definitivamente** (anche per scadenza).  
Distinto dai **tombstone famiglia** (B5): quelli propagano il delete nel merge; non sono la UI Cestino.

---

## 2. Requisiti funzionali (congelati — CONVALIDA passo 3)

### R1 — Modello (C-Q1 = C1)
- Soft-delete + **schermata Cestino** (non solo hook file senza UI).

### R2 — Entità (C-Q2)
- In cestino: **oggetti** e **contenitori**.  
- **Fuori:** categorie e posizioni (restano regole “in uso” V1).

### R3 — Elimina → Cestino
- Stessi dialog catalogo **2.6** per la conferma eliminazione (testi esistenti invariati finché non si aggiungono frasi ufficiali — R10).  
- Dopo SI: l’entità **esce** da liste vive, conteggi, ricerca (Pipeline esclusa).  
- **Undo snackbar** immediato «Annulla» (C-Q5) oltre al Cestino.

### R4 — Schermata Cestino
- Ingresso: **Utility** (C-Q4).  
- Gate **premium** (§0).  
- Lista: nome, tipo (oggetto/contenitore), data eliminazione.  
- Azioni: **Ripristina** | **Elimina definitivamente** | **Svuota cestino** (con conferma).

### R5 — Retention (C-Q3)
- Scadenza **30 giorni** dal soft-delete → hard delete automatico (check all’avvio / ingresso Cestino).  
- Svuota manuale sempre disponibile.

### R6 — Ripristino e dipendenze (C-Q6)
- Ripristina oggetto: torna nel contenitore di origine se il box è **vivo**.  
- Se il box è **in cestino**: ripristina **anche** il contenitore.  
- Se il box è già **hard-deleted**: dialog per **scegliere** un contenitore vivo destinazione.

### R7 — Family / tombstone (C-Q7 = F1)
- Soft-delete: **non** scrive tombstone merge.  
- Hard delete (definitivo, svuota, scadenza): come oggi → tombstone + `CANCELLAZIONI` in flavor famiglia.

### R8 — Backup (C-Q8)
- Backup ZIP **include** entità in cestino (metadato `deletedAt` / equivalente).  
- Ripristina REPLACE le riporta nello stesso stato.

### R9 — Invia / Ricevi Archivio
- Inventario inviato: solo entità **vive**.  
- Hard delete → sezione CANCELLAZIONI (invariato B5).  
- Soft-delete non propaga da solo sugli altri dispositivi.

### R10 — Catalogo 2.6 (C-Q9)
- Nuove frasi (es. esito spostamento / svuota) **solo** dopo elenco SI e inserimento in catalogo ufficiale **prima** del codice.  
- Finché assenti: riusare conferme eliminazione esistenti dove bastano; messaggi tecnici minimi fuori 2.6 solo se già pattern premium (non inventare catalogo).

### R11 — Ricerca
- Entità in cestino **escluse** da ricerca semplice e Pipeline 0–10. Non riaprire Motore B.

---

## 3. Requisiti tecnico-architetturali (congelati — CONVALIDA passo 3)

### T1 — Persistenza
- Soft-delete via `deletedAt` (nullable) su `objects` / `boxes` **oppure** equivalente documentato; liste/DAO filtrano `deletedAt IS NULL`.  
- Migrazione Room **non** distruttiva; `allowBackup=false` invariato; play + famiglia.

### T2 — Hard delete
- Rimuove riga; in famiglia registra tombstone.  
- Hook per A3 Foto: cancella file display+thumb + metadati foto.

### T3 — Soft-delete e Foto (A3)
- File foto **restano** finché l’oggetto è in cestino.  
- Ripristino → ricongiunzione per `objectPermanentId` (REQUISITI Foto T1).

### T4 — Premium
- Gate all’ingresso Cestino e azioni ripristina/svuota/elimina definitivo (coerente §0).  
- Soft-delete dalla lista: se l’Elimina V1 resta usabile senza premium, l’item va comunque in cestino; **aprire/gestire** il Cestino richiede premium. (*Se SI diverso: Elimina stessa gate — default: Elimina resta come oggi; gestione Cestino premium.*)

### T5 — Stack
- Activity + XML + MVVM + Room. Nessun Compose/Hilt obbligatorio.

---

## 4. Fuori scope

- Cestino categorie/posizioni.  
- UI sui tombstone famiglia come “cestino remoto”.  
- Implementazione Foto / QR (voci separate).  
- Personalizzazione retention utente (solo 30 gg fissi in A2).  
- Motore B oltre esclusione.

---

## 5. Criterio di accettazione (ritest futuro)

1. Elimina oggetto/box → assente dalle liste vive; presente in Cestino (se Archivio completo attivo).  
2. Snackbar Annulla ripristina subito.  
3. Ripristina da Cestino secondo R6.  
4. Svuota / Elimina definitivo / 30 gg → hard delete (+ tombstone famiglia).  
5. Senza Archivio completo: gate come altre premium; niente bypass gestione Cestino.  
6. Backup→Ripristina preserva cestino.  
7. Ricerca non elenca cestino.  
8. Migrazione senza wipe.

---

## 6. Recepimento ufficiale

In Nota: Allegato «Cestino» (o numerazione vigente) + rinvio Roadmap / Progetto 2; chiarire ≠ tombstone B5.  
Questo file è la **fonte** fino all’implementazione.

### Processo

1. ANALISI — [ASSESSMENT_CESTINO.md](ASSESSMENT_CESTINO.md)  
2. FEEDBACK — C-Q1–C-Q9 + premium Progetto 2  
3. CONVALIDA analisi/requisiti — **SI Renato 07/09/2026**  
4. AGGIORNAMENTO DOCUMENTO — **questo file**; in attesa **CONVALIDA aggiornamento documento**
