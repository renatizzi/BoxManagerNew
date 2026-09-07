# Requisiti congelati — Foto oggetto (supporto visivo)

**Stato:** CONGELATO 07/09/2026 (SI Renato).  
**Uso:** riferimento funzionale e tecnico-architetturale fino all’implementazione Progetto 2.  
**Destinazione:** confluire nella Nota Integrata ufficiale (Allegato dedicato) senza riaprire il merito.  
**Assessment di lavoro:** [ASSESSMENT_FOTO_MINIATURA.md](ASSESSMENT_FOTO_MINIATURA.md) (storico analisi; prevale **questo** file su conflitti).  
**Sequenza implementativa:** dopo **QR avanzato** e **Cestino**. STOP codice finché non si apre quella fetta.

---

## 1. Obiettivo prodotto

Fornire un **supporto visivo del contenuto**: una foto **opzionale** per ogni **oggetto**, così da riconoscerlo in lista e in dettaglio.  
I **contenitori** restano identificati dal **QR** (fuori da questa voce).  
Niente riconoscimento automatico / vision / ML / cerca-per-immagine.

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

---

## 3. Requisiti tecnico-architetturali (congelati)

### T1 — Storage
- **Nessun BLOB** immagine in Room.
- File sotto storage app, legati a `objectPermanentId` (non id Room), es.  
  `files/object_photos/…`
- DB: solo riferimento leggero (path / nome file / metadati minimi).

### T2 — Dimensioni e peso (policy compressione)
Al salvataggio (galleria o scatto), pipeline obbligatoria:

| Livello | Specifica | Uso |
|---------|-----------|-----|
| **Display** | lato lungo max **800 px**, JPEG **75** | Dettaglio / anteprima ingrandita |
| **Thumb** | lato lungo max **160 px**, JPEG **70** | Solo lista (`iconArea` 32dp) |

- Esclusi 1280 px e superiori.
- Una foto oggetto → scrive/aggiorna **entrambi** i file.
- Ordine di grandezza di riferimento: ~500 oggetti con foto ≈ **70–110 MB** totali (non grezze multi-MB).

### T3 — Backup / Ripristina
- Modulo nel ZIP Backup: es. `photos/objects/` (+ thumb) e voce in `manifest.json`.
- Bump versione formato Backup; ZIP senza foto restano validi.
- Ripristina REPLACE ripristina anche i file foto.

### T4 — Invia / Ricevi Archivio
- Pacchetto = **ZIP unico**: CSV FamilyMerge + `photos/objects/` (display + thumb).
- Tabelle condivise (solo categorie/posizioni): **senza** foto.
- Semantica: foto segue `objectPermanentId` come l’inventario (insert/update; in conflitto allineata alla regola dell’oggetto vincente).
- Prima di Invia: riepilogo “Foto: N oggetti, circa X MB”.

### T5 — Esporta / Importa Dati (estensione prevista, non CSV-only)
- **Oggi (CSV V1):** nessun canale foto; Import assegna nuovi permanentId agli oggetti nuovi.
- **Quando si estende** (stessa fetta o subito dopo, SI formato):
  - Esporta produce **ZIP** (CSV con id stabili ove necessario + cartella foto);
  - Importa consuma quel ZIP e riattacca le foto per **permanentId** (preferibile) o regola di match documentata.
- Non reinventare BLOB dentro celle CSV.

### T6 — Fuori scope di questa voce
- Foto sui **contenitori**
- Vision / ML / cerca per immagine
- Foto in Esporta vista / stampa A4 (salvo SI successivo)
- Obbligatorietà foto in censimento

### T7 — Dipendenze di sequenza
- Implementare **dopo** QR avanzato e **Cestino** (delete/ripristino oggetto deve gestire i file foto).

---

## 4. Criterio di accettazione (per il ritest futuro)

1. Aggiungo oggetto con foto da galleria e da scatto; modifico (riscatto); elimino foto; Salva senza foto OK.  
2. In lista oggetti: thumb a sinistra; tap → ingrandimento; senza foto → icona fissa.  
3. Backup → Ripristina: foto presenti.  
4. Invia Archivio → Ricevi Archivio: foto presenti sugli oggetti corrispondenti per id.  
5. (Se SI estensione Import/Export ZIP) ciclo Esporta ZIP → Importa ZIP ripristina foto; il solo CSV legacy resta senza foto.

---

## 5. Nota per il recepimento ufficiale

In Nota Integrata: Allegato “Foto oggetto — supporto visivo” (o numerazione vigente), con rinvio da Roadmap 4.1.6 / Progetto 2.  
Questo file è la **fonte congelata**; non riaprire R1–R5 / T1–T7 in chat di implementazione salvo SI esplicito di revisione.
