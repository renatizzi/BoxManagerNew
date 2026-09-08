# Assessment — Export / Import avanzati (Progetto 2 / B–C)

**Stato:** CONVALIDA analisi/requisiti (passo 3) **SI Renato 08/09/2026** (EI-Q1–EI-Q10 + non core + trascodifica fogli/DB esterni). Documento: [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) — in attesa CONVALIDA aggiornamento documento (passo 4). Zero codice.  
**Processo:** passo **4** in corso.  
**Posizione:** dopo A1 QR · A2 Cestino · A3 Foto (requisiti congelati; codice in STOP fino a fine test Play).  
**Fonte Roadmap:** Nota Integrata **9.2 §4.1.6** — voci **EXPORT AVANZATO** e **IMPORT AVANZATO** (Priorità: medio-bassa).  
**Policy:** solo analisi durante test Play; SI «apri fetta codice» / Play chiuso per implementare.  
**Premium:** come A1–A3 — **Archivio completo** (SI trasversale 07/09).  
**Su conflitti prevale** [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md).

---

## 0. Perché insieme B e C

In Nota 4.1.6 le due voci sono affiancate e simmetriche (selezione/gestione file vs validazione/errori). L’AS-IS è **asimmetrico** (Import Utility sì; Export Utility no). Ha senso un’unica ANALISI con domande separate dove diverge.

---

## 1. Fonte ufficiale — elenco intero (da recepire)

### EXPORT AVANZATO (4.1.6)
| Elemento | Valore |
|----------|--------|
| Priorità | Medio-bassa |
| Bullet 1 | **selezione contenuti** |
| Bullet 2 | **gestione file avanzata** |

### IMPORT AVANZATO (4.1.6)
| Elemento | Valore |
|----------|--------|
| Priorità | Medio-bassa |
| Bullet 1 | **validazione dati estesa** |
| Bullet 2 | **gestione errori dettagliata** |

Nient’altro sotto queste voci. Non dicono ZIP, foto, id stabili, UI Utility «Esporta» — quelle sono ipotesi da FEEDBACK (anche alla luce di Foto T5).

### Fuori da questa ANALISI
- A1–A3 (già congelati).  
- Dashboard avanzata / UI avanzata (D — dopo).  
- SMB nativo (fuori V1).  
- Famiglia Invia/Ricevi (canale distinto).  
- Backup/Ripristino ZIP (canale distinto).  
- Motore B / pipeline.

---

## 2. AS-IS V1 (verifica — non requisiti nuovi)

| Canale | Cosa fa oggi |
|--------|----------------|
| **Importa Dati** (Utility) | CSV `BoxManager_Import`; sezioni CONTENITORI / OGGETTI; Genera `Modello_Importazione.csv`; MERGE; PRE_IMPORT ZIP; gate `IMPORT` |
| **Esporta vista** (header liste) | Stesso schema CSV; solo **snapshot della vista** corrente; nome `ESPORTA_ddMMyy_HHmm.csv`; gate `EXPORT` |
| **Utility «Esporta Dati»** | **Assente** |
| **Selezione entità** | **Assente** (né export né import a sottoinsieme esplicito) |
| **Foto / permanentId in CSV** | CSV V1 senza foto; Import assegna **nuovi** permanentId agli insert |

Validazione Import attuale: file/formato/struttura/obbligatori + dipendenze cat/pos/box; report letti/importati/ignorati/scartati; se scarti → non applica.

---

## 3. Lettura bozza dei bullet Nota

### Export — «selezione contenuti»
Possibili letture (da SI):

| Opzione | Descrizione |
|---------|-------------|
| **E1** | Export **archivio intero** (tutti i contenitori/oggetti) oltre alla sola vista |
| **E2** | Picker esplicito: contenitori / oggetti / (opz.) categorie-posizioni da includere |
| **E3** | Entrambi: profilo «Tutto» + profilo «Selezione» |

### Export — «gestione file avanzata»
| Opzione | Descrizione |
|---------|-------------|
| **F1** | Solo CSV migliorato (nome, cartella, Utility Esporta) |
| **F2** | Pacchetto **ZIP** (CSV + eventuale `photos/objects/` come da Foto T5) |
| **F3** | F1 ora; F2 allineato all’implementazione Foto |

### Import — «validazione dati estesa»
| Opzione | Descrizione |
|---------|-------------|
| **V1** | Più controlli sullo stesso CSV (tipi, range quantità, caratteri, duplicati soft) |
| **V2** | Supporto **ZIP** + riaggancio per `objectPermanentId` / `permanentId` (Foto T5) |
| **V3** | V1 + V2 |

### Import — «gestione errori dettagliata»
| Opzione | Descrizione |
|---------|-------------|
| **G1** | Report riga-per-riga (file scaricabile / schermata) oltre al riepilogo attuale |
| **G2** | Apply parziale (importa le righe OK, elenca le KO) — oggi se scarti → blocco totale |
| **G3** | G1 + scelta blocco vs parziale |

**Ipotesi di lavoro (da FEEDBACK):** Export = **E3 + F3**; Import = **V3 + G1** (G2 solo con SI esplicito — cambia semantica merge).

---

## 4. Usability (bozza se ipotesi)

### Export avanzato
1. Utility (o Impostazioni): card **Esporta dati** (oltre Esporta vista).  
2. Scelta: **Vista corrente** (come oggi) | **Archivio intero** | **Selezione…** (lista check).  
3. Formato: CSV (V1) e, quando Foto in codice, opzione ZIP.  
4. Nome `ESPORTA_…` / eventuale `ESPORTA_….zip`; riuso cartella `KEY_IMPORT_EXPORT`.  
5. Gate premium `EXPORT`.

### Import avanzato
1. Stesso ingresso Importa Dati.  
2. Accetta CSV e (fase successiva) ZIP.  
3. Anteprima + **elenco errori dettagliato** (sezione/riga/motivo).  
4. Default: blocco se errori bloccanti (come oggi); apply parziale solo se SI G2.  
5. Gate premium `IMPORT`.

### Distinzioni da non confondere
- Esporta **vista** resta strumento contestuale.  
- Invia/Ricevi Archivio = famiglia, non questo.  
- Backup ZIP = replace archivio, non merge CSV.

---

## 5. Impatto architetturale (bozza)

| Area | Invasività | Nota |
|------|------------|------|
| Nuova Utility Esporta | Media | Activity o estensione Import card |
| Selezione contenuti | Media-alta | UI multi-select + query |
| CSV + permanentId | Media | Cambio tracciato → versione formato / compatibilità Modello |
| ZIP + foto | Alta | Accoppiata a A3 Foto; stesso layout `photos/objects/` |
| Report errori | Media | Modello risultato riga |
| Room | Bassa–media | Solo se nuovi campi; no wipe |
| Pipeline ricerca | Nessuna | |

---

## 6. Checklist FEEDBACK — CONVALIDATA (passo 3, 08/09/2026)

| # | Domanda | Esito |
|---|---------|--------|
| **EI-Q1** | Perimetro = solo i 4 bullet Nota B+C? | **SI** |
| **EI-Q2** | Export selezione | **SI — E3** |
| **EI-Q3** | File | **SI — F3** (CSV prima; ZIP con Foto) |
| **EI-Q4** | Import validazione | **SI — V3** |
| **EI-Q5** | Errori | **SI — G1** (no apply parziale) |
| **EI-Q6** | Utility «Esporta dati» | **SI** |
| **EI-Q7** | Id stabili / formato | **SI — v2** + Modello; v1 ancora ok |
| **EI-Q8** | Premium | **SI** |
| **EI-Q9** | Ordine vs A1–A3 | **SI — dopo** QR+Cestino+Foto in codice |
| **EI-Q10** | Cat/pos in selezione export | **SI — fuori prima fetta** |
| **P1** | Importanti ma **non core** | **SI** Renato 08/09 |
| **P2** | Preferenza trascodifica Sheets/Excel/DB esterni | **SI** Renato 08/09 (CSV primario) |

---

## 7. Criterio di accettazione (bozza, post-codice futuro)

1. Esporta dati: intero / selezione / (vista resta).  
2. File datato + riuso cartella.  
3. Import: report errori dettagliato; CSV v1 ancora ok.  
4. Se F3/V3: ciclo ZIP con foto dopo A3.  
5. Famiglia e Backup non regressi.  
6. Senza Archivio completo: gate.

---

## 8. Esito — CONVALIDA merito; documento requisiti scritto

**Prodotto:** E3+F3+V3+G1; Utility Esporta dati; CSV v2; **non core**; priorità **trascodifica** verso Google Sheets / Microsoft Excel.

**Documento:** [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) — in attesa **CONVALIDA aggiornamento documento**.

**Poi:** ANALISI **D** Dashboard/UI (salvo SI). **Zero codice.**
