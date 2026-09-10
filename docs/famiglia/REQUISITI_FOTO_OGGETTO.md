# Requisiti congelati — Foto oggetto (supporto visivo)

**Stato:** CONGELATO 07/09/2026 (SI Renato).  
**CONVALIDA aggiornamento documento:** SI Renato 07/09/2026 (T1 allineamento DB, T3–T5/T8 tracciati file, processo 4 fasi).  
**Integrazione 07/09/2026:** **R6 Premium** (Progetto 2) — SI Renato in sessione Cestino; non riapre il resto del merito foto.  
**Uso:** riferimento funzionale e tecnico-architetturale fino all’implementazione Progetto 2.  
**Destinazione:** confluire nella Nota Integrata ufficiale (Allegato dedicato) senza riaprire il merito.  
**Assessment di lavoro:** [ASSESSMENT_FOTO_MINIATURA.md](ASSESSMENT_FOTO_MINIATURA.md) (storico analisi; prevale **questo** file su conflitti).  
**Sequenza implementativa:** dopo **QR avanzato** e **Cestino**. STOP codice finché non si apre quella fetta.

---

## 1. Obiettivo prodotto

Fornire un **supporto visivo del contenuto**: una foto **opzionale** per ogni **oggetto**, così da riconoscerlo in lista e in dettaglio.  
I **contenitori** restano identificati dal **QR** (fuori da questa voce).  
Niente riconoscimento automatico / vision / ML / cerca-per-immagine **nel perimetro congelato 07/09** (vedi §6 idea OCR correlata — da approfondire in seguito, non attiva ora).

---

## 2. Requisiti funzionali (congelati)

### R1 — Ciclo di vita foto in inserimento/modifica oggetto
- In **Aggiungi oggetto** e **Modifica oggetto** l’utente può:
  - **aggiungere** una foto da **galleria**;
  - **scattare** una foto;
  - **riscattare** (sostituire con nuovo scatto o altra scelta da galleria);
  - **eliminare** la foto già associata.
- Al massimo **una** foto per oggetto (sostituzione = sovrascrittura).

### R2 — Lista oggetti
- A **sinistra** della riga oggetto (`iconArea`, oggi icona fissa):
  - se c’è foto → **thumb**;
  - se non c’è → placeholder = icona fissa attuale.
- **Tap** sulla thumb (quando presente) → **anteprima ingrandita** (foto display); chiusura semplice (Indietro / X / tap fuori).

### R3 — Persistenza e recupero
| Canale | Foto recuperabili? |
|--------|-------------------|
| **Backup** → **Ripristina** | **Sì** (modulo foto nel ZIP Backup) |
| **Invia Archivio** → **Ricevi Archivio** | **Sì** (pacchetto ZIP: CSV merge + foto) |
| **Esporta / Importa Dati** (CSV V1 attuale) | **Non con il solo CSV**; **sì** quando il canale sarà esteso a pacchetto ZIP (o equivalente) con foto + aggancio per id stabili — vedi §4. Non è un divieto di prodotto |

### R4 — Facoltatività
- La foto **non** è obbligatoria.
- Salvare un oggetto senza foto resta valido.
- Assenza foto = comportamento UI attuale (icona fissa).

### R5 — Privacy (perimetro dichiarato)
- Finalità: immagini di **oggetti di inventario**, non trattamento di dati personali (volti/documenti come scopo).
- Conservazione: dispositivo + Backup / pacchetti che l’utente genera; non invio a servizi di analisi immagini.
- Aggiornare privacy policy / Data safety in fase di implementazione (testo sobrio).

### R6 — Premium (Progetto 2)
- **SI Renato 07/09/2026:** Foto oggetto è funzionalità **Archivio completo / premium**, come A1 QR avanzato e A2 Cestino. Gate alle azioni foto (aggiungi/scatta/anteprima dedicata se gate UI) secondo le condizioni vigenti; dettaglio enum/pitch in implementazione.

---

## 3. Requisiti tecnico-architetturali (congelati)

### T1 — Storage e allineamento database
- **Nessun BLOB** immagine in Room (né in colonne testo “base64”).
- File sotto storage app, **sempre** nominati / cartelle per `objectPermanentId` (**mai** per id Room autoincrement).
- DB: solo metadati leggeri, es. tabella `object_photos` (`objectPermanentId`, nomi file display/thumb, `updatedAt`, `byteSize` opz.) **oppure** colonne equivalenti su `objects`.
- **Migrazione Room** non distruttiva (no wipe; `allowBackup=false` resta). Flavor play + famiglia.
- **Allineamento agli oggetti:**
  - create/update oggetto con foto → scrive file + aggiorna riga metadati per lo stesso `objectPermanentId`;
  - elimina foto in modifica → cancella file display+thumb + metadati;
  - elimina / cestino oggetto → cancella anche file e metadati foto (dopo Cestino);
  - Ripristina / Ricevi: dopo (o insieme a) restore/merge oggetti, **ricongiungere** foto per `objectPermanentId` (file presenti senza oggetto → ignorare o scartare; oggetto senza file → UI senza foto).
- Import CSV V1 (solo testo): oggetti nuovi con **nuovi** permanentId → **nessun** riaggancio foto automatico finché non c’è pacchetto ZIP (§T5 / §T8).

### T2 — Dimensioni e peso (policy compressione)
Al salvataggio (galleria o scatto), pipeline obbligatoria:

| Livello | Specifica | Uso |
|---------|-----------|-----|
| **Display** | lato lungo max **800 px**, JPEG **75** | Dettaglio / anteprima ingrandita |
| **Thumb** | lato lungo max **160 px**, JPEG **70** | Solo lista (`iconArea` 32dp) |

- Esclusi 1280 px e superiori.
- Una foto oggetto → scrive/aggiorna **entrambi** i file.
- Ordine di grandezza di riferimento: ~500 oggetti con foto ≈ **70–110 MB** totali (non grezze multi-MB).

### T3 — Backup / Ripristina (tracciato file)
- Formato: ZIP Backup esistente + **modulo foto** (bump versione formato; ZIP senza foto restano validi).
- Layout indicativo nel ZIP:
  - `photos/objects/{objectPermanentId}.jpg` — display
  - `photos/objects/{objectPermanentId}_thumb.jpg` — thumb  
    (o sottocartelle `display/` / `thumb/` equivalenti, documentate in manifest)
- `manifest.json` (o equivalente): elenco permanentId con foto + conteggio; checksum opzionale.
- **Ripristina REPLACE:** ripristina DB **e** file foto; poi allineamento §T1 (foto orfane scartate).

### T4 — Invia / Ricevi Archivio (tracciato file)
- Pacchetto = **ZIP unico** (non CSV isolato):
  - CSV FamilyMerge (come oggi) **più**
  - `photos/objects/` (display + thumb, stessi nomi di §T3)
- Tabelle condivise (solo categorie/posizioni): **senza** foto.
- Semantica merge: foto segue `objectPermanentId` come l’inventario (insert/update; in conflitto allineata alla regola dell’oggetto vincente).
- Prima di Invia: riepilogo “Foto: N oggetti, circa X MB”.

### T5 — Esporta / Importa Dati (tracciato esterno)
- **Oggi (CSV V1):** tracciato ufficiale solo testo (`Modello_Importazione` / `ESPORTA_…`); Import assegna nuovi permanentId agli oggetti nuovi → **nessun** recupero foto.
- **Estensione prevista** (stessa fetta o subito dopo, senza reinventare il merito):
  - Esporta → **ZIP**: CSV (con **objectPermanentId** / id stabili dove serve al match) + `photos/objects/` come §T3;
  - Importa → consuma quel ZIP; dopo il MERGE oggetti, **riaggancia** le foto per `objectPermanentId`.
- Vietato: BLOB/base64 dentro celle CSV.

### T8 — Sintesi tracciati (congelata)

| Canale | Tracciato | Chiave aggancio foto |
|--------|-----------|----------------------|
| Backup / Ripristina | ZIP Backup + `photos/objects/` | `objectPermanentId` |
| Invia / Ricevi Archivio | ZIP (CSV merge + `photos/objects/`) | `objectPermanentId` |
| Esporta / Importa (esteso) | ZIP (CSV + `photos/objects/`) | `objectPermanentId` |
| Esporta / Importa CSV V1 | solo CSV | — (niente foto) |

### T6 — Fuori scope di questa voce
- Foto sui **contenitori**
- Vision / ML / cerca per immagine *(nel perimetro congelato 07/09; idea OCR cover → §6, da approfondire)*
- Foto in Esporta vista / stampa A4 (salvo SI successivo)
- Obbligatorietà foto in censimento
- BLOB immagini in SQLite / CSV

### T6bis — Premium (allineamento Progetto 2)
- Gate **Archivio completo** sulle azioni foto (R6). Stesso vincolo trasversale di A1 QR e A2 Cestino (SI Renato 07/09/2026).

### T7 — Dipendenze di sequenza
- Implementare **dopo** QR avanzato e **Cestino** (delete/ripristino oggetto deve gestire i file foto).

---

## 4. Criterio di accettazione (per il ritest futuro)

1. Aggiungo oggetto con foto da galleria e da scatto; modifico (riscatto); elimino foto; Salva senza foto OK.  
2. In lista oggetti: thumb a sinistra; tap → ingrandimento; senza foto → icona fissa.  
3. Backup → Ripristina: foto presenti.  
4. Invia Archivio → Ricevi Archivio: foto presenti sugli oggetti corrispondenti per id.  
5. (Se SI estensione Import/Export ZIP) ciclo Esporta ZIP → Importa ZIP ripristina foto; il solo CSV legacy resta senza foto.  
6. Senza Archivio completo: gate premium sulle azioni foto (R6); nessun bypass.

---

## 5. Nota per il recepimento ufficiale

In Nota Integrata: Allegato “Foto oggetto — supporto visivo” (o numerazione vigente), con rinvio da Roadmap 4.1.6 / Progetto 2.  
Questo file è la **fonte congelata**; non riaprire R1–R5 / T1–T8 in chat di implementazione salvo SI esplicito di revisione.

### Processo analisi (da usare in futuro)

Per nuove funzionalità / requisiti (come questa voce) — **4 fasi**:

1. **ANALISI** — bozza (assessment o sezione dedicata)  
2. **FEEDBACK** — chiarimenti e SI Renato in chat  
3. **CONVALIDA** — dell’**analisi** e dei **requisiti condivisi** (merito); nessun aggiornamento ufficiale prima  
4. **AGGIORNAMENTO DOCUMENTO** — scrittura/congelamento; chiusura con **CONVALIDA dell’aggiornamento del documento**  

Vedi anche [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md) § Processo analisi · [PROMPT continuità 07/09](PROMPT_CONTINUITA_07_09_DISCO_FOTO.md).

---

## 6. Idea correlata — OCR locale sulla cover (annotata 10/09/2026, da approfondire)

**Stato:** solo **idea** (SI Renato 10/09: annotare qui; barcode scartato perché offline inutile senza catalogo).  
**Non** fa parte dei requisiti congelati R1–R6 / T1–T8. **Non** apre codice. Quando si aprirà la fetta Foto (dopo QR avanzato + Cestino), approfondire con processo 4 fasi e SI esplicito (toccherebbe anche il “niente ML” del §1).

### Intento
In fase di **censimento**, ridurre il lavoro di digitazione: dalla stessa occasione dello scatto foto oggetto, ricavare una **proposta di testo** (nome/descrizione) da confermare.

### Schema concordato in chat (10/09)
1. Scatto (o frame) **ad alta definizione** solo temporaneo  
2. **OCR locale on-device** (niente internet obbligatorio) sulla cover/etichetta  
3. Utente **conferma/corregge** il testo nei campi oggetto  
4. Si conserva solo la **mini** (policy T2 / thumb-display) per lista e scheda  
5. L’immagine HD **non** resta in archivio né in Backup  

«Due piccioni con una fava»: un gesto → supporto visivo (mini) + aiuto compilazione testo (OCR).

### Da chiarire al momento dell’analisi
- Qualità OCR su packaging reali; solo nome vs nome+descrizione  
- Premium (allineamento R6) sì/no  
- Galleria vs solo scatto; lingua/mixed text  
- Revisione esplicita del fuori-scope «niente ML» se si conferma l’OCR
