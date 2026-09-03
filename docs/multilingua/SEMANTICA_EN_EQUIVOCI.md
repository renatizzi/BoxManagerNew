# Semantica EN — tipi di equivoco (M2b, prima del motore)

**Data:** 03/09/2026  
**SI Renato:** procedere con M2b; pipeline **invariata**; niente interprete del “senso” della frase.  
**Stato:** analisi; **`domain/search` non toccato**. Motore EN solo dopo le decisioni in fondo.

Renato non deve leggere l’inglese: ogni esempio EN è spiegato in italiano.

---

## 1. Cosa dice la Nota sulla «semantica» (non è un buco)

La preoccupazione è giusta: una traduzione letterale della *frase* può cambiare il senso. La Nota **non** risolve questo con un traduttore né con un interprete di significato. Lo risolve **non usando la frase come classificazione**.

| Punto Nota | Regola (testo vivo) |
|------------|---------------------|
| **3.3.2.3** Principio degli indizi | Gli indizi servono solo a interpretare; **non** decidono da soli trasformazione, tipologia o Output |
| **3.3.5** | Indicatori = evidenze interpretative; **non** decidono entità, fulcro, routing |
| **3.3.6** | Interpretazione **deterministica**; separata dall’esecuzione della ricerca; ogni fase usa solo l’uscita della precedente |
| **3.3.7** | Vietato classificare dalla **formulazione linguistica**, dagli indicatori, dalla sola presenza di Core |
| **3.3.8** | Navigazione **indipendente** dalla formulazione; query **indipendente** dalla formulazione |
| **3.3.9** Core V1 | ✘ NLP, ✘ **interpretazione semantica avanzata**, ✘ embedding, ✘ linguaggio naturale avanzato |
| **R8** (Allegato 1.2.1) | Percorso solo su entità **riconosciute in archivio**. **Il significato grammaticale o semantico di un termine non è evidenza sufficiente** |
| **R13** | Indicatori in forma **canonica**; varianti = normalizzazione (fase 1), non una seconda tabella |
| **R14** | Perifrasi isolate (`quale`, `in which`, …) **non** bastano per Core / fulcro / percorso |
| **R15 / R19** | Più letture plausibili senza trasformazione dominante → **chiarificazione**, vietato scegliere in automatico |
| **4.3** | I «profili» object/box/location/category sono **indizi di copertura**. *Dove + verbo + oggetto* può essere contenitore **oppure** luogo: prevalgono fulcro, chiavi, R19 |
| **4.7** | `Trova box` ≠ `Trova contenitore box`. Vietato riscrivere `box` → `contenitore` **prima** della fase 3 |
| **4.17 F7-04** | «dove» è **indicatore, non instrada** |
| **4.17 F6/F9** | Alias posizione + contenitori + oggetto; **non «tipo»** (in 1.3.3 è alias CATEGORY e apre F8) |

Conseguenza per l’inglese: **non** si traduce la domanda in italiano e poi si lancia il motore IT. Si fa la **stessa pipeline 0–10** con tabelle alias/indicatori **EN** (bozza CK0) e nomi d’archivio **non tradotti**.

---

## 2. Tipi di domanda che possono equivocare

Ogni tipo: cosa teme la traduzione letterale → cosa fa già la Nota → proposta.

I nomi in archivio degli esempi restano quelli italiani di test (*Vite*, *Cantina*, *box*, …): in inglese l’utente cerca *Vite*, non “screws”, salvo che abbia censito l’oggetto in inglese.

### Tipo A — «Dove» / *Where* (luogo o contenitore?)

| | |
|-|-|
| **IT già noto** | *Dov'è il trapano elettrico?* (4.3: contenitore **o** luogo). *Dove trovo lo stesso tipo di oggetti* (F7-04) |
| **EN** | *Where is the electric drill?* / *Where do I find the same type of objects* |
| **In italiano** | «Dove sta il trapano?» / «Dove trovo gli oggetti dello stesso tipo?» |
| **Rischio** | In inglese *where* sembra sempre «luogo». La traduzione letterale spingerebbe tutto su LOCATION |
| **Nota** | 4.3: profilo ≠ routing. 4.17: *dove* in F7-04 **non instrada**. Fase 3 = chiavi in **archivio**, non il senso di *where* |
| **Proposta** | **Nessuna regola nuova.** *where* resta indizio LOCATION (1.3.3 / 3.3.5). F7-04 EN è variante ufficiale, come in IT. Se c’è il nome *Trapano elettrico* in archivio → Motore A come oggi. Se è la variante F7 → Motore B, *where* non decide il fulcro |

### Tipo B — «Tipo» / *type* (categoria o «stesso tipo»?)

| | |
|-|-|
| **IT già noto** | *tipo* è alias CATEGORY (1.3.3). F7-04 e F8 usano «stesso tipo di oggetto». 4.17: senza nome, *tipo* **apre F8**, non F6/F9 |
| **EN** | *same type of objects* / *same type of object* |
| **In italiano** | «oggetti dello stesso tipo» / «lo stesso tipo di oggetto» |
| **Rischio** | Sembrare una categoria nominata, o perdere F8 se si toglie *type* «perché è vago» |
| **Nota** | Stesso token ufficiale. Non è un calco sbagliato: è la riga 1.3.3 |
| **Proposta** | **Tenere `type` in CATEGORY.** Non cancellare *type of* in normalizzazione (romperebbe F7/F8). F6/F9 EN non devono introdurre *type* se l’IT ufficiale non ce l’ha |

### Tipo C — *Find box* vs *Find container box* (omonimia)

| | |
|-|-|
| **IT congelato 4.7** | *Trova box* → R19 se la chiave *box* sta su più Core. *Trova contenitore box* → fulcro BOX, lista dei contenitori di nome box, niente R19 |
| **EN** | *Find box* / *Find container box* |
| **In italiano** | identico: «Trova box» / «Trova contenitore box» |
| **Rischio** | «Aiutare» l’inglese riscrivendo *box* in *container* **prima** della fase 3 (cancella il nome in archivio). È l’errore già visto in IT |
| **Nota** | pipeline-ufficiale.mdc + 4.7 |
| **Proposta** | **Stessi due attraversamenti 0–10.** Vietato un piano-nomi o un replace *box*→*container* in fase 1 |

### Tipo D — *in order to* (rumore inglese, non esiste in IT)

| | |
|-|-|
| **IT** | Non c’è una locuzione fissa equivalente d’uso comune |
| **EN** | *In order to find the drill* |
| **In italiano** | «Al fine di trovare il trapano» (l’utente vuole il trapano, non una categoria «ordine») |
| **Rischio** | `order` è alias CATEGORY (*ordine* in 1.3.3). Restano i token *order* + *drill* → si può leggere «trapano nella categoria ordine» |
| **Nota** | 3.3.9 vieta l’interprete semantico. **4.4 / R13**: il rumore si toglie in **normalizzazione** (come *Dov'è* → *dove è*), non con un significato |
| **Proposta** | **Sì, lista chiusa di locuzioni-rumore EN in fase 1** (solo forme fisse, elenco intero da SI). Candidata: `in order to`. Non è “capire la frase”: è lo stesso mestiere di 4.4 |

### Tipo E — *kind of* / *type of* (riempitivo vs alias)

| | |
|-|-|
| **EN** | *It's kind of in the garage* («è un po’ in garage») vs *same type of object* (F8 ufficiale) |
| **Rischio** | Togliere sempre *type of* / *kind of* rompe F7/F8. Lasciarli fa sì che *kind* (*genere*) resti CATEGORY, come *tipo* in IT |
| **Nota** | In IT «tipo» ha lo stesso doppio uso e **non** è stato tolto |
| **Proposta** | **Non strappare `type of` / `kind of`.** Accettare lo stesso residuo dell’italiano. *kind* resta in 1.3.3 |

### Tipo F — Parole che in inglese sono anche verbi/aggettivi

Esempi (alias già in tabella A, una parola): *cover* (custodia / «coprire»), *pack* (confezione / «impacchettare»), *safe* (cassaforte / «sicuro»), *case* (custodia / «nel caso»), *point* (punto / «indicare»), *room* (ambiente / «spazio per»).

| | |
|-|-|
| **Rischio** | Un inglese «pack the cables» potrebbe accendere BOX su *pack* |
| **Nota** | **R8**: il senso grammaticale **non** è evidenza. Conta se in archivio c’è una chiave. 4.18: solo alias Core senza nome → inventario type-only, non un indovino |
| **Proposta** | **Nessun disambiguatore verbo/nome.** Stesso rischio dell’IT (*cover* è già in 1.3.3 BOX) |

### Tipo G — «Tutti / elenco / quali» vs *all / list / which*

| | |
|-|-|
| **IT** | 3.3.5 e 4.18: non classificare da «elenco» o «tutti» |
| **EN** | *List of the containers that have identical objects* è **variante ufficiale F7**, non un indizio isolato |
| **Proposta** | Indicatori di aggregazione = indizi. F7/F8 restano **elenco ufficiale di varianti** (match dopo normalizzazione), non un rilevatore di “vuole una lista” |

### Tipo H — F1 / F4 «Dove ho messo X» (confine già in Allegato 1)

| | |
|-|-|
| **IT** | Allegato 1: confine F1/F4 «da verificare». 4.3: non riaprire con presunzioni |
| **EN** | *Where did I put the screws?* = *Dove ho messo le viti?* |
| **Proposta** | **Non riaprire il confine.** Stesso attraversamento 0–10 dell’IT. Nomi d’archivio non tradotti (*Vite* ≠ *screws* se l’oggetto si chiama Vite) |

### Tipo I — F6 / F9 *spots* vs *places*

| | |
|-|-|
| **IT** | *posti* vs *luoghi*: stesso Core LOCATION, stessa query 4.17 |
| **EN** | *spots* (*posto*) vs *places* (*luogo*) |
| **Proposta** | Nessun trattamento speciale. Due alias della stessa riga 1.3.3 |

### Tipo J — Tradurre la domanda e poi usare il motore IT

| | |
|-|-|
| **Rischio** | È esattamente il modo in cui la traduzione letterale **inficia** il senso (oggetto della tua osservazione) |
| **Nota** | 3.3.6: interpretazione deterministica sulle **tabelle + archivio**, non su una parafrasi |
| **Proposta** | **Vietato** uno strato “translate to Italian then dispatch”. Locale EN = stesse fasi, tabelle EN |

---

## 3. Decisioni (sì / no, senza inglese)

Tutto ciò che è già congelato in Nota **non si rivota**. Servono solo queste tre:

| ID | Domanda | Proposta agente | Effetto |
|----|---------|-----------------|---------|
| **S1** | Vietare il traduttore EN→IT davanti alla pipeline? | **Sì** (Tipo J) | Motore locale-aware, non un dizionario di frasi |
| **S2** | In fase 1, togliere solo locuzioni-rumore fisse (prima: `in order to`), elenco chiuso in Nota/bozza, niente “capire la frase”? | **Sì** (Tipo D, come 4.4) | Evita che *order* accenda CATEGORY a vuoto |
| **S3** | Lasciare `type` / `kind` / `type of` come in IT «tipo» (niente strip, F7/F8 restano)? | **Sì** (Tipo B, E) | F7-04 e F8 EN restano allineati a 4.17 |

A, C, F, G, H, I si applicano **da soli** con le regole già SI (R8, R19, 4.3, 4.7, 4.17, 3.3.9).

---

## 4. Come si verifica dopo il SI (senza device in questa fetta)

Per ogni domanda di Tipo A–C, H: scrivere uscite **0–10** come chiede `pipeline-ufficiale.mdc`, in **IT e EN affiancate**. Se una fase manca nel codice, **fermarsi** — non sostituire con un matching.

Campione minimo (stesso archivio di `SearchOfficialPipelineTest`):

| IT | EN (stesso senso) | Esito atteso (già IT) |
|----|-------------------|------------------------|
| Trova box | Find box | R19 omonimi |
| Trova contenitore box | Find container box | lista contenitori di nome box |
| Dov'è il trapano elettrico? | Where is the electric drill? | Motore A, niente R19 |
| Dove trovo lo stesso tipo di oggetti | Where do I find the same type of objects | F7, *where* non instrada |
| Cerca i contenitori con categoria diversa che contengono lo stesso tipo di oggetto | Search the containers with a different category that contain the same type of object | F8 |

Il motore (M2b codice) parte **dopo** S1–S3.

---

## 5. Fuori scope

- NLP / embedding / “capire” la frase (3.3.9)
- Tradurre i **nomi** in archivio
- Riaprire F1/F4, F6/F9, F7, Motore B in V1 oltre 4.17
- Header CSV bilingue (D7 CK0: restano IT)
