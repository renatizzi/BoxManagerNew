# Assessment — Foto miniatura (riconoscimento fisico)

**Stato:** analisi aggiornata con **SI Renato 07/09/2026** (STOP implementazione).  
**Posizione nella sequenza concordata:** A3 (dopo QR avanzato e Cestino).  
**Fonte Roadmap:** Nota 9.2 §4.1.6 / Progetto 2 (dopo Play verde).  
**AS-IS:** nessuna foto su `ObjectEntity`; in lista oggetti `iconArea` mostra icona **fissa** (`item_object_icon`); fotocamera oggi solo QR (immagini non salvate). Backup già **modulare** (Nota 3.4.1: future estensioni es. fotografie).

---

## 1. Decisioni SI (07/09/2026)

| # | Decisione | SI Renato |
|---|-----------|-----------|
| 1 | Ambito | **Solo oggetti** (i contenitori hanno già il QR; la foto supporta il *contenuto*) |
| 2 | Origine | **Entrambi:** galleria *oppure* scatto in inserimento/modifica oggetto |
| 3 | Persistenza / condivisione | **Entrambi:** Backup ZIP **e** Invia/Ricevi Archivio |
| 4 | Vision / ML | **Escluso** — solo supporto visivo umano |
| 5 | UI lista | Miniatura **al posto** dell’icona fissa attuale (`iconArea`) |
| — | Privacy | Foto di **oggetti** (inventario); **nessun dato personale** trattato (volto/documento). Da riflettere in privacy/Data safety in modo sobrio |
| — | Preoccupazione | **Peso memoria** Backup + storage (non tanto il DB: le immagini non vanno in blob Room) |
| 6 | Lato max | **Sotto 1024** (esclusi 1280+). Policy tecnica sotto |
| 7 | Thumb lista | **Decisione tecnica:** sì, thumb dedicata (vedi §5) |
| 8 | Invia Archivio | **ZIP** = CSV merge + `photos/objects/` (SI) |

---

## 2. Cosa intendiamo (congelato per questa voce)

| Lettura | Stato |
|---------|--------|
| **A — Miniatura per riconoscimento umano** dell’oggetto in lista/dettaglio | **In scope** |
| **B — Riconoscimento automatico** (vision / cerca per foto) | **Fuori scope** (SI) |

Una foto per oggetto (opzionale). Senza foto → resta il placeholder attuale (icona fissa).

---

## 3. Usability

### Flusso
- Aggiungi / Modifica oggetto → **Foto** → **Scegli dalla galleria** | **Scatta** | **Rimuovi**.
- Lista oggetti nel box: `iconArea` = miniatura se presente, altrimenti icona fissa.
- Non obbligatoria: Salva oggetto senza foto resta valido.

### Copy / privacy (basso profilo)
- Messaggio CAMERA distinto dal QR: «BoxManager può usare la fotocamera per una foto dell’oggetto. Resta sul telefono / nel backup che fai tu; non è inviata a servizi Google.»
- Galleria: preferire **Photo Picker** di sistema (meno permessi).
- Guida: «Una foto chiara dell’oggetto aiuta a riconoscerlo nella lista.»

### Rischi UX
| Rischio | Mitigazione |
|---------|-------------|
| Liste lente con molte foto | Solo file **miniatura** in UI; decode async + cache |
| Scatto di qualità piena | **Ridimensiona e comprimi al salvataggio** (vedi §5) |
| Merge famiglia più lento | Pacchetto archivio diventa multi-file o ZIP; anteprima “N foto, ~X MB” |
| Cestino / delete oggetto | Cancellare anche il file foto (legame a cestino) |

---

## 4. Impatto architetturale

### 4.1 Dati — **non** nel database come BLOB
Room tiene solo un riferimento leggero, es.:

- `object_photos`: `objectPermanentId`, `fileName`, `updatedAt`, `byteSize` (opzionale)
- oppure colonna `photoFileName` su `objects`

I byte dell’immagine stanno in **file** sotto storage app, es.  
`files/object_photos/{objectPermanentId}.jpg`  
(nome = permanentId, come per merge/QR).

**Il DB resta piccolo**; il peso è su filesystem + Backup + pacchetto Invia Archivio.

### 4.2 Backup / Ripristino
Modulo ZIP già previsto dalla Nota:

- Cartella `photos/objects/{objectPermanentId}.jpg`
- `manifest.json` elenca le foto (conteggio + checksum opzionale)
- Bump versione formato Backup; ZIP senza foto restano validi
- Ripristino REPLACE ripristina anche i file

### 4.3 Invia / Ricevi Archivio (SI: entrambi)
Il CSV attuale **non** basta. Opzioni:

| Opzione | Nota |
|---------|------|
| **A — ZIP unico** `Condivisione_Archivio_….zip` = CSV merge + cartella `photos/objects/` | Un file da condividere; allineato mentalmente al Backup |
| **B — CSV + cartella affiancata** | Fragile se l’utente sposta solo il CSV |

**Raccomandazione analisi:** opzione **A** (un ZIP di condivisione archivio). Tabelle condivise (solo categorie/posizioni) **senza** foto.

Semantica merge foto: come inventario per `objectPermanentId` — arriva foto se oggetto inserito/aggiornato; se conflitto lastModified, stessa regola del payload oggetto (o “foto segue l’oggetto vincente”).

### 4.4 Liste
Sostituire il contenuto di `iconArea` con `ImageView` miniatura (stesso `@dimen/icon_size`). Placeholder = icona fissa attuale.

### 4.5 Permessi / Play
- CAMERA: già presente (QR); aggiornare rationale (ora si **salva** foto oggetto).
- Galleria: Photo Picker → spesso senza `READ_MEDIA_IMAGES` ampio.
- Privacy / Data safety: foto inventario oggetti, on-device / backup utente; **non** dati biometrici / volti come finalità.

### 4.6 Ricerca
Pipeline 0–10 invariata. Nessuna cerca-per-immagine.

### 4.7 Sequenza
Dopo **QR avanzato** e **Cestino** (delete oggetto + file foto + eventuale ripristino).

---

## 5. Peso memoria — il punto critico (SI Renato)

### 5.1 Ordini di grandezza (indicativi)

| Cosa | Peso tipico |
|------|-------------|
| Foto telefono “grezza” | 2–8 **MB** |
| Display lato **800 px**, JPEG ~75 | ≈ **100–200 KB** |
| Thumb lista **160 px**, JPEG ~70 | ≈ **10–25 KB** |
| Solo path in Room | ≈ **decine di byte** |

Esempio archivio “pesante”: **500 oggetti con foto** — vedi numeri aggiornati in §5.2.

Senza compressione al salvataggio il Backup/Invia diventa **impraticabile** su disco di rete e Wi‑Fi famiglia.

### 5.2 Policy tecnica **congelata** (SI Renato 07/09 + scelta tecnica)

**Perché sotto 1024 vale la pena:** in lista l’area è già `32dp` (~128 px su schermi densi); in dettaglio oggetto basta riconoscere l’oggetto a schermo, non una stampa ad alta risoluzione. 1280+ gonfia Backup/Invia senza guadagno per questa finalità.

| Livello | Specifica | Ruolo |
|---------|-----------|--------|
| **Display** | lato lungo max **800 px**, JPEG qualità **75** | Dettaglio / modifica |
| **Thumb lista** | lato lungo max **160 px**, JPEG **70** | Solo `iconArea` (32dp): scroll fluido |
| **DB** | solo path / permanentId | Nessun BLOB |
| **Invia Archivio** | **ZIP** unico: CSV FamilyMerge + `photos/objects/` (display + thumb) | SI |

**Perché due file (display + thumb):** un solo 800 px obbligerebbe a ridimensionare in memoria a ogni riga di lista → rischio jank. Thumb 160 ≈ **10–25 KB**/oggetto; su 500 oggetti ≈ **5–12 MB** in più, liste più fluide. Vale la pena.

**Ordine di grandezza (500 oggetti con foto):**

| | Solo display 800 | Display 800 + thumb 160 | Grezze ~4 MB |
|--|------------------|-------------------------|--------------|
| Storage / Backup / ZIP Invia | ≈ **60–100 MB** | ≈ **70–110 MB** | ≈ **2 GB** |

Al salvataggio (galleria o scatto): pipeline unica → scrive/aggiorna **entrambi** i file. Una foto per oggetto.

In **Invia Archivio**: riepilogo “Foto: N oggetti, circa X MB” prima del salvataggio.

**Non** in prima fetta: foto in Esporta vista CSV / stampa A4 (salvo SI successivo).

### 5.3 Cosa non preoccupa
- **Room / DB:** resta leggero (solo metadati).
- **Privacy “dati personali”:** finalità = inventario oggetti; non profilazione. Dichiarare “foto / immagini” in Data safety se richiesto dalla Console (app functionality / on-device).

### 5.4 Cosa resta impegnativo (onesto)
- Invia Archivio non è più solo CSV → **ZIP** + UI di progresso copia.
- Su disco di rete un Backup ~100 MB è lento ma accettabile se le foto sono già compresse; grezze no.

---

## 6. Invasività (aggiornata)

| Area | Invasività |
|------|------------|
| UI oggetto (form + `iconArea`) | Media |
| File system + riferimento DB | Media |
| Backup modulo `photos/objects` | Media-alta |
| Invia/Ricevi Archivio → ZIP con foto | **Alta** (cambio contratto pacchetto) |
| Privacy / Data safety | Bassa-media (testi) |
| Pipeline ricerca | Nessuna |

---

## 7. Esito — assessment **chiuso** lato prodotto/tecnica

**Congelato:**
- Solo **oggetti**; galleria + scatto; no vision; UI al posto icona fissa
- Display **800** + thumb lista **160**; nessun BLOB in Room
- Backup ZIP + **Invia Archivio = ZIP** (CSV + foto)

**Implementazione:** solo dopo QR avanzato + Cestino. Nessun codice finché non si apre quella fetta Progetto 2.
