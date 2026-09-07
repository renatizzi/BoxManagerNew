# Assessment — Foto miniatura (riconoscimento fisico)

**Stato:** solo analisi (STOP implementazione).  
**Posizione nella sequenza concordata:** A3 (dopo QR avanzato e Cestino).  
**Data:** 07/09/2026.  
**Fonte Roadmap:** Nota 9.2 §4.1.6 / Progetto 2 (dopo Play verde).  
**AS-IS codice:** nessuna foto su `BoxEntity` / `ObjectEntity`; fotocamera solo per **lettura QR** (privacy: immagini non salvate). Backup già **modulare** (Nota 3.4.1: future estensioni es. fotografie).

---

## 1. Cosa intendiamo (due letture)

| Lettura | Significato utente | Impatto |
|---------|-------------------|---------|
| **A — Miniatura come aiuto umano** | Sulla card/dettaglio si vede una piccola foto del contenitore (o dell’oggetto) per riconoscerlo a colpo d’occhio | Medio |
| **B — Riconoscimento automatico** | La fotocamera “capisce” cosa c’è e cerca in archivio (vision / ML) | Alto — **fuori** da questa bozza; rischia di violare 3.3.9 (niente NLP/vision avanzata in V1; in V2 va deciso esplicitamente) |

**Ipotesi di lavoro per questa analisi:** lettura **A** (miniatura per riconoscimento fisico da parte dell’utente). La B resta idea separata, da SI dedicato.

Ambito consigliato in prima fetta: **una foto per Contenitore** (non per ogni oggetto). Motivo: il “riconoscimento fisico” tipico è del box in cantina/garage; gli oggetti restano testo/quantità; meno storage e UI più semplice.

---

## 2. Usability (clientela basso profilo)

### Cosa funziona bene
- Card Contenitore con **icona categoria oggi** → la miniatura può affiancare o sostituire solo dove c’è foto; altrimenti resta l’icona (niente buco UI).
- Flusso naturale: Dettaglio contenitore → **Aggiungi foto** / **Cambia** / **Rimuovi**.
- Origine foto: **Galleria** (più semplice) ± **Scatta** (riuso permesso CAMERA già noto dal QR, ma messaggio privacy diverso: qui l’immagine **si salva**).

### Rischi UX
| Rischio | Mitigazione |
|---------|-------------|
| Utente non sa cosa fotografare | Guida: «Una foto del contenitore chiuso o dell’etichetta» |
| Foto enorme / lentezza liste | Solo **miniatura** in UI; originale compresso o ridimensionato in salvataggio |
| Privacy / Play Data safety | Dichiarare foto **solo sul dispositivo**; non in cloud; aggiornare privacy policy |
| Condivisione famiglia | Decidere se la foto viaggia nel merge o resta locale al telefono |
| Disco di rete / Backup | ZIP più pesanti; tempo Backup/Ripristino |

### Principio UI (allineato al resto dell’app)
- Una azione chiara, zero gergo.
- Opzionale: contenitore senza foto = comportamento attuale.
- Non obbligatorio in censimento (non bloccare “Salva” senza foto).

---

## 3. Impatto architetturale

### 3.1 Dati (Room)
Oggi `box` non ha campi media. Opzioni:

| Opzione | Pro | Contro |
|---------|-----|--------|
| **Colonna** `thumbnailPath` / `photoUri` su `box` | Semplice | URI SAF fragili; file orfani |
| **Tabella** `box_photo` (boxPermanentId, fileName, updatedAt) | Pulita, estendibile a N foto dopo | Una migrazione in più |
| File in **directory app** (`files/box_photos/{permanentId}.jpg`) + riferimento in DB | Controllabile; no dipendenza SAF per le mini | Va incluso nel Backup |

**Raccomandazione analisi:** file locali sotto storage app + riferimento per `permanentId` (non id Room). Coerente con QR / merge famiglia.

### 3.2 Backup / Ripristino
Nota già prevede estensioni modulari (es. fotografie) nel ZIP senza rompere il formato.

- Nuova entry o cartella nello ZIP: es. `photos/box/{permanentId}.jpg` + riga in `manifest.json`.
- Versione formato Backup: bump controllato; vecchi ZIP senza foto restano validi.
- Ripristino REPLACE: ripristina anche le foto; assenza foto = ok.

### 3.3 Archivio condiviso (merge famiglia)
Punto delicato:

| Scelta | Effetto |
|--------|---------|
| **Foto fuori dal merge CSV** (solo Backup ZIP) | Merge leggero; ogni telefono ha le proprie foto | Più semplice in V2-prima-fetta |
| **Foto nel pacchetto Invia Archivio** | Famiglia vede le stesse immagini | File grandi; nuovo formato oltre CSV; molto più lavoro |

**Raccomandazione analisi (prima fetta):** foto **locali + Backup**; **non** nel Ricevi/Invia tabelle/archivio CSV. Eventuale condivisione foto = fetta successiva.

### 3.4 Liste / performance
- Adapter Contenitori: caricare miniature **asincrone**, cache memoria, placeholder = icona categoria.
- Non decodificare full-size in lista.

### 3.5 Permessi e Play
- Oggi CAMERA = solo QR, testo privacy esplicito «non salvate».
- Con foto salvate: aggiornare **privacy**, **Data safety**, eventualmente `READ_MEDIA_IMAGES` / Photo Picker (Android 13+) senza permesso ampio se si usa il picker di sistema.
- Preferire **Photo Picker** / SAF get content per galleria → meno permessi, più adatto al basso profilo.

### 3.6 Ricerca
- Miniatura **non** entra nella pipeline 0–10 (nessun “cerca per immagine” in questa lettura A).
- Eventuale ricerca “contenitori con/senza foto” = Motore B / report: **fuori** dalla prima fetta.

### 3.7 Migrazione Room
- Serve migrazione non distruttiva (`allowBackup=false` già; no wipe).
- Flavor play + famiglia entrambi.

---

## 4. Dipendenze di sequenza

Concordato: **dopo QR avanzato e Cestino**.

| Prima | Perché aiuta le foto |
|-------|----------------------|
| **QR avanzato** | Stessa area “riconoscimento fisico del box”; etichetta + foto si rafforzano; riuso pattern stampa/batch |
| **Cestino** | Eliminazione contenitore con foto → serve policy (cancella file / ripristina dal cestino) |

Se si anticipasse solo l’analisi foto (questa) va bene; **non** anticipare il codice foto prima di cestino senza policy delete.

---

## 5. Stima di invasività (ordine di grandezza, non calendario)

| Area | Invasività |
|------|------------|
| UI Dettaglio + card lista | Media |
| Storage file + DB | Media |
| Backup/Ripristino modulo photos | Media-alta (contratto ZIP) |
| Merge famiglia | Bassa se escluso in v1-foto |
| Privacy / Play | Media (dichiarazioni) |
| Pipeline ricerca | Nessuna (lettura A) |

---

## 6. Decisioni da SI Renato (prima di qualsiasi codice)

1. **Solo Contenitore** o anche Oggetto in prima fetta?  
2. **Galleria only**, o anche Scatta?  
3. Foto nel **solo Backup**, o anche in **Invia Archivio**?  
4. Confermi esclusione **riconoscimento automatico** (vision) da questa voce?  
5. In lista: miniatura **al posto** dell’icona categoria o **accanto**?

---

## 7. Esito proposto (bozza, non congelato in Nota)

- Voce Roadmap = **miniatura Contenitore per riconoscimento umano**.  
- Storage locale + modulo Backup.  
- Fuori merge famiglia in prima fetta.  
- Nessuna vision/ML.  
- Implementazione solo **dopo** QR avanzato e Cestino, con SI su questo assessment.
