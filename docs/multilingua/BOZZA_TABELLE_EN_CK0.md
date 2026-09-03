# Bozza tabelle EN — Ricerca avanzata (M2a)

**Stato:** BOZZA in attesa di **CK0 SI Renato**.  
**Data:** 03/09/2026  
**Non è codice.** Finché CK0 non è SI, `domain/search` non si tocca.

Fonte IT (elenco intero, non riassunto):

| Artefatto | Dove |
|-----------|------|
| Alias Core | Nota Integrata **1.3.3** Matrice Indicatori Core V2 (copia del foglio Excel `query_operative_V4.xlsx`, non presente in repo) |
| Perifrasi | stessa tabella 1.3.3 |
| Indicatori confronto / aggregazione | Nota **3.3.5** (esempi elencati; la «ecc.» non è un secondo elenco) |
| Messaggi ricerca | Catalogo **2.6** sezione RICERCA AVANZATA + Allegato **4.5** |
| F7 / PATTERN_007 | Allegato **4.17** (cinque varianti ufficiali) + Allegato 1 §1.1 |
| F8 / PATTERN_008 | Allegato **4.17** (quattro varianti ufficiali) + Allegato 1 §1.1 |
| F1–F6 / F9 | Allegato 1 §1.1 (stesso metodo 1:1; serve a CK2, non è un elenco sostitutivo di F7/F8) |

Regole di questa bozza:

- Ogni riga IT ufficiale ha **una** proposta EN. Nessun termine IT omesso.
- Plurali e forme senza accento restano **solo matching** (come in IT): non sono un secondo elenco da approvare.
- I 3.3.5 «esempi» (articolo, scatola, dove, …) **non** sostituiscono le quattro righe 1.3.3.
- Nomi di archivio (Vite, Cantina, …) **non** si traducono.
- Pipeline 0–10 **invariata**.

Sidecar Nota: Allegato **4.21** in `docs/Nota_Integrata_9.1_B7.docx` (stesso contenuto; 4.20 è già usato in 9.2 per il merge famiglia).

---

## Cosa chiedere a Renato (CK0)

Leggere le tabelle. Per ogni punto: **SI** così com’è, oppure segnare la riga EN da cambiare.

Non servono git né Android Studio: è solo testo.

| # | Decisione | Proposta agente | Serve SI |
|---|-----------|-----------------|----------|
| D1 | Quattro righe alias 1.3.3 EN | Tabelle A1–A4 | **Sì** — blocca M2b |
| D2 | Perifrasi 1.3.3 EN | Tabella B | **Sì** |
| D3 | Indicatori confronto / aggregazione / `dove` | Tabella C | **Sì** |
| D4 | Sette messaggi ricerca 2.6 / 4.5 EN | Tabella D | **Sì** |
| D5 | Cinque varianti F7 + heading EN | Tabella E | **Sì** |
| D6 | Quattro varianti F8 + heading EN | Tabella F | **Sì** |
| D7 | Header CSV `Modello_Importazione.csv` | Restano **italiani** in V1 | **Sì** (consigliato: restano IT) |
| D8 | `locale` (Allegato 4.3 / codice; **non** in riga LOCATION 1.3.3) | Non importare in tabella ufficiale EN | **Sì** se si vuole aggiungerlo |
| D9 | F1–F6 / F9 EN (supporto CK2) | Tabelle G | Consigliato nello stesso SI |
| D10 | Frase R19 omonimi (non è una riga autonoma del catalogo 2.6) | Tabella H | Consigliato |

Fuori da questo SI: PATTERN_005 / F5 (BACKLOG V2) e PATTERN_010 (SOSPESO). Tradotti in coda solo per non accorciare Allegato 1; **non** entrano in M2b.

---

## Collisioni EN (da sapere, non da “sistemare” in silenzio)

L’inglese ha meno parole distinte dell’italiano. La bozza **non** inventa sinonimi extra per evitare doppioni. In matching EN il set collassa da solo.

| IT 1.3.3 | EN proposta | Nota |
|----------|-------------|------|
| box / scatola | box / box | stesso token EN |
| contenitore / container | container / container | la riga IT ha già entrambi |
| posizione / ubicazione | location / location | stesso token EN |
| duplicato / doppione | duplicate / duplicate | `doppione` è in F7 ufficiale, non negli esempi 3.3.5 |
| diverso / differente | different / different | |

---

## A — Alias Core (Nota 1.3.3)

Ordine = ordine della riga ufficiale. Canonico IT invariato: `oggetto` / `contenitore` / `posizione` / `categoria`.

### A1 OBJECT (9)

| # | IT ufficiale | EN bozza |
|---|--------------|----------|
| 1 | oggetto | object |
| 2 | articolo | article |
| 3 | elemento | element |
| 4 | utensile | utensil |
| 5 | cosa | thing |
| 6 | affare | affair |
| 7 | roba | stuff |
| 8 | prodotto | product |
| 9 | arnese | tool |

### A2 BOX (34)

| # | IT ufficiale | EN bozza |
|---|--------------|----------|
| 1 | contenitore | container |
| 2 | box | box |
| 3 | boxes | boxes |
| 4 | scatola | box |
| 5 | scatolone | carton |
| 6 | cassetta | crate |
| 7 | confezione | pack |
| 8 | baule | trunk |
| 9 | busta | envelope |
| 10 | bustone | large envelope |
| 11 | cassetto | drawer |
| 12 | barattolo | jar |
| 13 | vaso | vase |
| 14 | bacinella | basin |
| 15 | recipiente | receptacle |
| 16 | cassa | chest |
| 17 | cassone | large chest |
| 18 | bidone | bin |
| 19 | cassonetto | dumpster |
| 20 | cassaforte | safe |
| 21 | portafoglio | wallet |
| 22 | portaoggetti | storage case |
| 23 | portagioie | jewelry box |
| 24 | portadocumenti | document holder |
| 25 | container | container |
| 26 | involucro | wrapping |
| 27 | custodia | case |
| 28 | cover | cover |
| 29 | imballaggio | packaging |
| 30 | armadio | cupboard |
| 31 | guardaroba | wardrobe |
| 32 | stipo | cabinet |
| 33 | libreria | bookcase |
| 34 | scaffale | shelf |

### A3 LOCATION (14)

`locale` **non** è in questa riga (vedi D8).

| # | IT ufficiale | EN bozza |
|---|--------------|----------|
| 1 | posizione | location |
| 2 | luogo | place |
| 3 | posto | spot |
| 4 | ubicazione | location |
| 5 | sito | site |
| 6 | area | area |
| 7 | zona | zone |
| 8 | perimetro | perimeter |
| 9 | spazio | space |
| 10 | ambiente | environment |
| 11 | città | city |
| 12 | paese | town |
| 13 | località | locality |
| 14 | punto | point |

### A4 CATEGORY (16)

| # | IT ufficiale | EN bozza |
|---|--------------|----------|
| 1 | categoria | category |
| 2 | classe | class |
| 3 | classificazione | classification |
| 4 | gruppo | group |
| 5 | aggregato | aggregate |
| 6 | raggruppamento | grouping |
| 7 | specie | species |
| 8 | famiglia | family |
| 9 | ordine | order |
| 10 | divisione | division |
| 11 | grado | grade |
| 12 | fascia | band |
| 13 | tipo | type |
| 14 | tipologia | typology |
| 15 | qualità | quality |
| 16 | genere | kind |

---

## B — Perifrasi qualificanti (Nota 1.3.3)

Non sono alias Core. Restano indizi (3.3.5 / fase 2).

| Core | IT ufficiale | EN bozza |
|------|--------------|----------|
| OBJECT | quale, quali | which |
| BOX | quale, quali, in quale, dove | which, in which, where |
| LOCATION | dove, in quale | where, in which |
| CATEGORY | quale, in quale, a quale | which, in which, to which |

Nota 1.3.3: «A quale» è qualificatore forte (CATEGORY). EN proposta: `to which`.

---

## C — Altri indicatori (Nota 3.3.5)

Elenco = gli esempi nominati in 3.3.5, senza aggiungere la «ecc.».  
`doppione` è extra rispetto a 3.3.5 ed è nella **variante ufficiale F7**: va in D3/D5, non è un alias Core.

### C1 Confronto

| # | IT (3.3.5) | EN bozza |
|---|------------|----------|
| 1 | uguale | equal |
| 2 | stesso | same |
| 3 | duplicato | duplicate |
| 4 | diverso | different |
| 5 | differente | different |
| 6 | confronto | comparison |

Parola F7 (non in 3.3.5 esempi):

| # | IT ufficiale F7 | EN bozza |
|---|-----------------|----------|
| 7 | doppione | duplicate |

### C2 Aggregazione

| # | IT (3.3.5) | EN bozza |
|---|------------|----------|
| 1 | tutti | all |
| 2 | elenco | list |
| 3 | quali | which |

### C3 Indizio LOCATION (condiviso con BOX in 1.3.3)

| # | IT | EN bozza |
|---|----|----------|
| 1 | dove | where |

---

## D — Messaggi ricerca (catalogo 2.6 / Allegato 4.5)

Elenco intero della sezione RICERCA AVANZATA. I tre testi già in `values-en` (M1) si **riusano**, non si riscrivono.

| # | Ruolo 4.5 | IT ufficiale | EN bozza |
|---|-----------|--------------|----------|
| 1 | Elaborazione | Sto analizzando la richiesta... | Analyzing the request… |
| 2 | Fallback | Non riesco a capire esattamente quello che stai cercando. Prova a formulare la richiesta con parole più chiare. | I cannot understand exactly what you are looking for. Try rephrasing the request with clearer words. |
| 3 | Commiato | Tocca qui per tornare alla Dashboard | Tap here to return to the Dashboard |
| 4 | Non compreso | Non ho compreso la richiesta. | I did not understand the request. |
| 5 | Chiarificazione | Puoi formulare la richiesta in modo più preciso? | Can you phrase the request more precisely? |
| 6 | Nessun risultato | Nessun risultato trovato. | No results found. |
| 7 | Motore B / non in V1 | Questo tipo di richiesta non è ancora disponibile. | This type of request is not yet available. |

I messaggi 2.6 di Backup / Import / QR / Categorie **non** sono tabelle motore: già in M1 UI. Non si riaprono qui.

---

## E — F7 / PATTERN_007 (Allegato 4.17, elenco intero)

Entità attese: OBJECT, BOX. Fulcro: OBJECT.  
`dove` in F7-04 è indicatore, non instrada.

| # | IT ufficiale | EN bozza |
|---|--------------|----------|
| 1 | Cerca tutti i contenitori che contengono doppioni | Find all the containers that contain duplicates |
| 2 | In quali contenitori ci sono oggetti uguali | In which containers are there equal objects |
| 3 | Elenco dei contenitori che hanno oggetti uguali | List of the containers that have equal objects |
| 4 | Dove trovo lo stesso tipo di oggetti | Where do I find the same type of objects |
| 5 | Trova i contenitori che hanno almeno un oggetto uguale | Find the containers that have at least one equal object |

Heading ufficiale F7:

| IT | EN bozza |
|----|----------|
| Elenco dei contenitori che hanno oggetti uguali | List of the containers that have equal objects |

---

## F — F8 / PATTERN_008 (Allegato 4.17, elenco intero)

Entità attese: OBJECT, BOX, CATEGORY. Fulcro: OBJECT.  
«Elenco dei contenitori con categoria diversa» **non** è variante ufficiale F8.

| # | IT ufficiale | EN bozza |
|---|--------------|----------|
| 1 | Cerca i contenitori con categoria diversa che contengono lo stesso tipo di oggetto | Find the containers with a different category that contain the same type of object |
| 2 | Quali contenitori hanno categoria diversa e contengono oggetti uguali | Which containers have a different category and contain equal objects |
| 3 | Trova contenitori con categoria diversa e oggetti uguali | Find containers with a different category and equal objects |
| 4 | Elenco contenitori con categoria diversa e oggetti uguali | List of containers with a different category and equal objects |

Heading ufficiale F8:

| IT | EN bozza |
|----|----------|
| Elenco dei contenitori che hanno categoria diversa e contengono oggetti uguali | List of the containers that have a different category and contain equal objects |

Non importare in EN (non sono varianti 4.17 / Allegato 1; oggi il codice IT le accetta come “famiglia” F8):

- Quali contenitori hanno una categoria diversa e contengono oggetti uguali
- Trova i contenitori con categoria diversa e con oggetti uguali

---

## G — Pattern Allegato 1 §1.1 (supporto CK2)

Stesso metodo 1:1. Placeholder: `<oggetto x>` → `<object x>`; `<contenitore x>` → `<container x>`; `<posizione x>` → `<location x>`; `<oggetto1..n>` → `<object1..n>`.

### G1 PATTERN_001 / F1

| IT ufficiale | EN bozza |
|--------------|----------|
| Cerca \<oggetto x\> | Search \<object x\> |
| Dove ho messo \<oggetto x\> | Where did I put \<object x\> |
| Dov'è \<oggetto x\> | Where is \<object x\> |
| Dove si trova \<oggetto x\> | Where is \<object x\> located |
| In quale contenitore trovo \<oggetto x\> | In which container do I find \<object x\> |
| Trova \<oggetto x\> | Find \<object x\> |
| Dove ho conservato \<oggetto x\> | Where did I store \<object x\> |

### G2 PATTERN_002 / F2

| IT ufficiale | EN bozza |
|--------------|----------|
| Cerca gli oggetti nel \<contenitore x\> | Search the objects in \<container x\> |
| Cosa c'è nel \<contenitore x\> | What is in \<container x\> |
| Cosa contiene il \<contenitore x\> | What does \<container x\> contain |
| Quali oggetti ci sono nel \<contenitore x\> | Which objects are in \<container x\> |
| Elenco degli oggetti conservati nel \<contenitore x\> | List of the objects stored in \<container x\> |

### G3 PATTERN_003 / F3

| IT ufficiale | EN bozza |
|--------------|----------|
| Cerca i contenitori in \<posizione x\> | Search the containers in \<location x\> |
| Quali contenitori ci sono in \<posizione x\> | Which containers are there in \<location x\> |
| Quali contenitori sono presenti in \<posizione x\> | Which containers are present in \<location x\> |
| Quali sono i contenitori conservati in \<posizione x\> | Which are the containers stored in \<location x\> |
| Elenco dei contenitori in \<posizione x\> | List of the containers in \<location x\> |

### G4 PATTERN_004 / F4

| IT ufficiale | EN bozza |
|--------------|----------|
| Cerca i contenitori che contengono \<oggetto1..n\> | Search the containers that contain \<object1..n\> |
| Quali sono i contenitori dove ho conservato \<oggetto1..n\> | Which are the containers where I stored \<object1..n\> |
| Dove ho messo \<oggetto1..n\> | Where did I put \<object1..n\> |
| In quali contenitori ho conservato \<oggetto1..n\> | In which containers did I store \<object1..n\> |
| Elenco dei contenitori dove ho conservato \<oggetto1..n\> | List of the containers where I stored \<object1..n\> |

### G5 PATTERN_006 / F6 (Allegato 4.17, elenco intero)

| IT ufficiale | EN bozza |
|--------------|----------|
| Cerca in quali posti sono conservati \<oggetto1..n\> | Search in which spots \<object1..n\> are stored |
| Dove ho conservato \<oggetto1..n\> | Where did I store \<object1..n\> |
| In quali luoghi ho conservato \<oggetto1..n\> | In which places did I store \<object1..n\> |
| Elenco dei posti dove sono conservati \<oggetto1..n\> | List of the spots where \<object1..n\> are stored |

### G6 PATTERN_009 / F9 (Allegato 4.17, elenco intero)

| IT ufficiale | EN bozza |
|--------------|----------|
| Cerca i luoghi dove ho conservato \<oggetto1..n\> | Search the places where I stored \<object1..n\> |
| Dove ho conservato \<oggetto1..n\> | Where did I store \<object1..n\> |
| Elenco dei posti dove ho conservato \<oggetto1..n\> | List of the spots where I stored \<object1..n\> |
| Trova i posti dove sono \<oggetto1..n\> | Find the spots where \<object1..n\> are |

---

## H — Chiarificazione R19 (non riga autonoma 2.6)

Catalogo 2.6 per la chiarificazione è solo: *Puoi formulare la richiesta in modo più preciso?*  
Il codice, se R19 ha ≥2 Core omonimi, usa un template extra (non in 2.6 come frase fissa).

| Pezzo IT oggi in codice | EN bozza |
|-------------------------|----------|
| Riformula la domanda in modo che sia chiaro se ti riferisci | Rephrase the question so it is clear whether you mean |
| a un oggetto | an object |
| a un contenitore | a container |
| a una posizione | a location |
| a una categoria | a category |

Se CK0 non vuole una seconda frase: in EN si usa solo D5 (chiarificazione 2.6).

---

## I — Normalizzazione 4.4 (EN, se SI)

IT già in specifica: Dov'è / Dove è / Qual è / Quale è / Com'è / Cos'è.  
Proposta EN (stesso ruolo: espandere, poi togliere la forma verbale dalla chiave):

| IT (4.4) | EN bozza |
|----------|----------|
| Dov'è / Dove è | Where's / Where is |
| Qual è / Quale è | Which is |
| Com'è | How's / How is |
| Cos'è | What's / What is |
| c'è (normalizer IT) | there's / there is |

Non è un elenco Core. M2b lo applica solo dopo CK0.

---

## J — Fuori perimetro M2b (Allegato 1, non accorciare)

### J1 PATTERN_005 / F5 — BACKLOG V2

| IT ufficiale | EN bozza (non in V1) |
|--------------|----------------------|
| Cerca \<oggetto x\> conservato dopo \<data\> | Search \<object x\> stored after \<date\> |
| Dove ho messo \<oggetto x\> conservato dopo \<data\> | Where did I put \<object x\> stored after \<date\> |
| Trova \<oggetto x\> conservato dopo \<data\> | Find \<object x\> stored after \<date\> |
| In quale contenitore trovo \<oggetto x\> conservato dopo \<data\> | In which container do I find \<object x\> stored after \<date\> |

### J2 PATTERN_010 — SOSPESO

| IT ufficiale | EN bozza (non in V1) |
|--------------|----------------------|
| Cerca le categorie più utilizzate | Search the most used categories |
| Quali sono le categorie più usate | Which are the most used categories |
| Elenco delle categorie più usate | List of the most used categories |
| Trova quali categorie sono più usate | Find which categories are most used |

---

## K — Non importare senza SI (non sono la tabella 1.3.3)

| Voce | Perché è fuori |
|------|----------------|
| `locale` | Allegato 4.3 («posto, locale, sito, luogo, posizione»); assente dalla riga LOCATION 1.3.3. Codice IT: commento «in più». EN se SI: `room` |
| `pacco`, `valigia` | Solo esempi 3.3.5 BOX; non in riga 1.3.3 |
| Header CSV | Modello ufficiale `Modello_Importazione.csv` resta nome fisso; colonne IT in V1 (D7) |
| Stopword / imperativi del matcher (`trova`, `cerca`, …) | Non sono tabella 1.3.3; M2b potrà avere il set EN `find`, `search`, … solo come matching, dopo CK0 |

---

## Dopo il SI

- **SI** → sessione **M2b** (motore locale-aware). Pipeline 0–10 invariata; importare **questi** elenchi, non una copia accorciata.
- **NO / correzioni** → aggiornare questa bozza e l’Allegato 4.21; niente `domain/search` finché il SI non è sulle tabelle corrette.
