RIEPILOGO REQUISITI RICERCA AVANZATA

ESTRATTO DA NOTA INTEGRATA V. 7.0

3.3 STRUMENTI DI RICERCA ARCHIVIO

3.3.1 OBIETTIVO

La Ricerca Interrogativa V1 costituisce il motore centrale di
interrogazione dell'archivio BoxManager.

Obiettivo è individuare la risposta archivistica corretta utilizzando
esclusivamente le informazioni presenti nell'archivio.

La pipeline deve:

• riconoscere le entità core;

• costruire automaticamente il percorso archivistico;

• individuare la trasformazione archivistica;

• determinare la tipologia della richiesta (Navigazione Archivistica
oppure Interrogazione Archivistica);

• produrre l'Output della Pipeline destinato al componente di
elaborazione.

3.3.2 PRINCIPI ARCHITETTURALI

Il modello ufficiale è costituito esclusivamente dalle relazioni:

OBJECT ⇄ BOX

BOX ⇄ LOCATION

BOX ⇄ CATEGORY

La pipeline costruisce automaticamente qualsiasi trasformazione
concatenando tali relazioni.

La tipologia della richiesta viene determinata sulla base della
Trasformazione Archivistica individuata dalla Pipeline.

La Navigazione Archivistica e l'Interrogazione Archivistica condividono
la medesima Pipeline Ufficiale. L'Interrogazione Archivistica introduce
esclusivamente elaborazioni aggiuntive mediante Query Archivistiche.

3.3.2.1 Obiettivo della Ricerca Interrogativa

La Ricerca Interrogativa individua la risposta archivistica riconoscendo
le entità core, costruendo il percorso archivistico, determinando la
trasformazione archivistica e determinando la tipologia della richiesta
e producendo l'Output della Pipeline.

3.3.2.2 Strategia dominante

Privilegiare sempre la risposta archivisticamente corretta utilizzando
il modello relazionale ufficiale.

3.3.2.3 Principio degli indizi

Gli indizi contribuiscono esclusivamente all'interpretazione della
richiesta e non determinano autonomamente trasformazione archivistica,
tipologia della richiesta o Output della Pipeline.

3.3.2.4 Entità core

Le sole entità core sono OBJECT, BOX, LOCATION e CATEGORY; ogni
trasformazione è costruita esclusivamente mediante le relazioni del
modello dati ufficiale.

3.3.2.5 Modello relazionale

OBJECT⇄BOX, BOX⇄LOCATION, BOX⇄CATEGORY. La tipologia della richiesta
deriva dalla Trasformazione Archivistica individuata dalla Pipeline.

3.3.2.6 Fulcro della richiesta

Il Fulcro è utilizzato esclusivamente per interpretare la richiesta,
costruire il percorso archivistico e gestire le disambiguazioni.

3.3.2.7 Chiarificazione

La chiarificazione è ammessa solo quando non è possibile determinare una
trasformazione archivistica dominante. R19: se la stessa chiave
identifica più entità core senza evidenze prevalenti, la pipeline
richiede chiarificazione.

3.3.2.8 Attributi non censiti

Gli attributi non censiti come il colore, il materiale, la marca, la
dimensione, ecc. costituiscono esclusivamente indizi non verificabili.

3.3.2.9 Principio di soddisfacibilità

La soddisfacibilità viene valutata dopo entità, percorso e
trasformazione archivistica; solo successivamente viene effettuato il
routing.

3.3.3 ENTITÀ CORE

Le uniche entità core sono:

• OBJECT

• BOX

• LOCATION

• CATEGORY

Ogni richiesta deve essere ricondotta esclusivamente a tali entità.
Elementi esterni costituiscono esclusivamente indizi e non modificano il
modello archivistico.

3.3.4 GERARCHIA DELLE ENTITÀ CORE

La gerarchia non rappresenta una classificazione funzionale ma un
supporto alla costruzione del percorso archivistico.

Le trasformazioni sono ottenute concatenando esclusivamente le relazioni
del modello dati ufficiale.

Non devono essere definite famiglie autonome derivate da percorsi
completi (es. OBJECT→CATEGORY).

3.3.5 INDICATORI LESSICALI

Gli indicatori lessicali costituiscono esclusivamente evidenze
interpretative.

Non determinano autonomamente entità, fulcro, trasformazione
archivistica, famiglia funzionale\* (V0 - modello precedente) o routing.
La classificazione finale deriva dalla valutazione congiunta di entità
riconosciute, fulcro, trasformazione archivistica e soddisfacibilità.
Una possibile classificazione è quella che si riferisce ad elementi
usati dall'utente nella formulazione della richiesta che orientano,
senza determinarne il ruolo funzionale, il riconoscimento dell'entità:

OBJECT (Esempi: articolo, elemento, prodotto, cosa, utensile, ecc.)

BOX (Esempi: box, scatola, cassetta, confezione, pacco, scatolone,
valigia, baule, ecc.)

LOCATION (Esempi: dove, luogo, posto, ubicazione, ecc.)

CATEGORY (Esempi: classificazione, classe, gruppo, ecc.)

Altri indicatori:

Indicatori di confronto: si riferiscono ad elementi che costituiscono
evidenze interpretative e possono richiedere elaborazioni aggiuntive
(Esempi: uguale, stesso, duplicato, diverso, differente, confronto,
ecc.). Tali indicatori possono suggerire richieste di tipo relazionale e
orientare quindi verso la costruzione di specifiche query e l'uso
dell'ENGINE_B)

Indicatori di aggregazione: si riferiscono ad elementi usati dall'utente
nella formulazione della richiesta che indicano possibili aggregazioni o
richieste multi-risultato senza influenzare autonomamente la
classificazione (Esempi: tutti, elenco, quali, ecc.).

3.3.6 PIPELINE UFFICIALE

La Ricerca Interrogativa V1 utilizza un'unica Pipeline Ufficiale, comune
a tutte le richieste archivistiche.

La pipeline costituisce il solo processo autorizzato per
l'interpretazione di una richiesta e deve essere eseguita integralmente,
senza omissioni né inversioni delle fasi.

Ogni fase utilizza esclusivamente gli output prodotti dalla fase
precedente.

Obiettivi

La Pipeline Ufficiale garantisce:

coerenza architetturale;

uniformità del comportamento;

interpretazione deterministica della richiesta;

riduzione delle ambiguità;

prevenzione delle regressioni;

completa separazione tra interpretazione della richiesta ed esecuzione
della ricerca.

Sequenza ufficiale della Pipeline

La Pipeline è composta dalle seguenti fasi obbligatorie.

1.  Normalizzazione

Input

richiesta testuale dell'utente.

Obiettivo

Produrre una rappresentazione normalizzata della richiesta.

Attività

normalizzazione maiuscole/minuscole;

normalizzazione degli spazi;

rimozione del rumore linguistico;

preparazione della richiesta alle fasi successive.

OUTPUT DELLA PIPELINE

L'Output della Pipeline costituisce il contratto logico tra la Pipeline
Ufficiale e il componente incaricato dell'elaborazione della richiesta.

L'Output viene prodotto esclusivamente al completamento di tutte le fasi
previste dalla Pipeline e rappresenta l'unica sorgente informativa
autorizzata per le fasi successive.

Contenuto dell'Output

L'Output della Pipeline deve contenere almeno le seguenti informazioni:

Entità Core riconosciute;

Fulcro della richiesta;

Percorso Archivistico;

Trasformazione Archivistica;

Tipologia della richiesta:

Navigazione Archivistica;

Interrogazione Archivistica;

eventuale Query Archivistica.

Regole

L'Output della Pipeline:

deve essere completo prima dell'avvio dell'elaborazione della richiesta;

deve essere coerente con tutte le regole della Pipeline Ufficiale;

non può essere modificato dai componenti destinatari;

costituisce l'unico riferimento autorizzato per l'elaborazione
successiva della richiesta.

Vincoli

I componenti destinatari:

non devono ricostruire informazioni già prodotte dalla Pipeline;

non devono reinterpretare la richiesta dell'utente;

devono utilizzare esclusivamente le informazioni contenute nell'Output
della Pipeline.

Qualsiasi elaborazione successiva deve essere effettuata preservando
integralmente il contenuto dell'Output prodotto dalla Pipeline.

Analisi degli indicatori

Input

richiesta normalizzata.

Obiettivo

Individuare gli indicatori lessicali utili all'interpretazione.

Attività

riconoscimento degli indicatori relativi alle entità core;

riconoscimento degli indicatori di confronto;

riconoscimento degli indicatori di aggregazione;

individuazione delle ulteriori evidenze linguistiche.

Regole

Gli indicatori:

costituiscono esclusivamente indizi;

non determinano autonomamente entità, fulcro, trasformazione
archivistica, famiglia funzionale\* (V0 - modello precedente) o routing.

Output

insieme degli indicatori riconosciuti.

Riconoscimento delle entità core

Input

richiesta normalizzata;

indicatori riconosciuti.

Obiettivo

Individuare le entità archivistiche effettivamente presenti nella
richiesta.

Le sole entità core ammesse sono:

OBJECT

BOX

LOCATION

CATEGORY

Elementi non riconducibili alle entità core possono contribuire
esclusivamente come evidenze interpretative.

Output

elenco delle entità core riconosciute.

Nota: Quando più interpretazioni risultano equivalenti e non esiste una
trasformazione dominante, la pipeline applica la Regola R19, attivando
la chiarificazione.

Individuazione del Fulcro

Input

richiesta;

entità core riconosciute;

indicatori;

evidenze disponibili.

Obiettivo

Determinare il reale obiettivo informativo della richiesta.

Il Fulcro viene utilizzato esclusivamente per:

interpretare la richiesta;

costruire il percorso archivistico;

gestire le eventuali disambiguazioni.

Il Fulcro:

non determina direttamente la famiglia funzionale\* (V0 - modello
precedente);

non determina direttamente il routing;

non coincide necessariamente con la prima entità riconosciuta;

prevale sugli indicatori lessicali.

Output

Fulcro candidato.

Costruzione del percorso archivistico

Input

entità core riconosciute;

Fulcro candidato.

Obiettivo

Costruire automaticamente il percorso archivistico necessario per
soddisfare la richiesta.

La costruzione del percorso utilizza esclusivamente il modello
relazionale ufficiale:

OBJECT ⇄ BOX

BOX ⇄ LOCATION

BOX ⇄ CATEGORY

Le relazioni sono bidirezionali.

Il percorso archivistico rappresenta esclusivamente il processo di
navigazione tra le entità e non costituisce un criterio di
classificazione.

Output

percorso archivistico candidato.

Individuazione della Trasformazione Archivistica

Input

percorso archivistico;

entità riconosciute;

Fulcro candidato.

Obiettivo

Individuare la trasformazione archivistica richiesta.

La trasformazione archivistica rappresenta la relazione archivistica
finale sulla quale converge il risultato della ricerca.

La trasformazione:

deriva dal percorso archivistico costruito dalla pipeline;

può essere diretta oppure composta;

non viene individuata mediante un elenco statico di casi.

Esempi:

OBJECT → BOX

BOX → OBJECT

LOCATION → BOX

BOX → LOCATION

OBJECT → LOCATION

LOCATION → OBJECT

OBJECT → CATEGORY

CATEGORY → OBJECT

Output

trasformazione archivistica candidata.

Routing

Input

famiglia funzionale\* (V0 - modello precedente) candidata.

Obiettivo

Individuare il motore incaricato dell'elaborazione.

ENGINE_A ed ENGINE_B condividono il medesimo modello di navigazione
archivistica.

ENGINE_B introduce esclusivamente elaborazioni aggiuntive non
soddisfacibili mediante ENGINE_A.

Output

motore selezionato.

Produzione della risposta

Input

motore selezionato.

Obiettivo

Produrre la risposta archivistica più corretta.

La risposta deve rispettare:

il Fulcro individuato;

il percorso archivistico costruito;

la Trasformazione Archivistica individuata;

la famiglia funzionale\* (V0 - modello precedente) classificata.

La chiarificazione deve essere utilizzata esclusivamente nei casi
previsti dalla Pipeline.

Output

risposta finale.

Divieti architetturali

Sono espressamente vietati:

classificare direttamente una famiglia funzionale\* (V0 - modello
precedente) senza avere prima individuato la Trasformazione
Archivistica;

utilizzare il percorso archivistico come criterio di classificazione;

determinare automaticamente una famiglia funzionale\* (V0 - modello
precedente) in presenza di ambiguità tra entità core;

effettuare routing basato esclusivamente sugli indicatori lessicali;

effettuare routing basato esclusivamente sulle entità riconosciute;

identificare il Fulcro sulla sola posizione gerarchica delle entità;

richiedere chiarificazioni quando esiste una trasformazione archivistica
dominante;

produrre classificazioni non riconducibili alle famiglie funzionali
validate (V0 -- Modello precedente).

3.3.7 DETERMINAZIONE DELLA TIPOLOGIA DELLA RICHIESTA

La classificazione delle richieste archivistiche costituisce l'ultima
fase logica della Pipeline Ufficiale prima del routing.

La classificazione viene effettuata esclusivamente dopo il completamento
delle seguenti fasi:

riconoscimento delle entità core;

individuazione del Fulcro;

costruzione del percorso archivistico;

individuazione della Trasformazione Archivistica;

valutazione della soddisfacibilità.

La classificazione non può essere effettuata sulla base:

della formulazione linguistica della richiesta;

degli indicatori lessicali;

della sola presenza di una o più entità core;

del percorso archivistico completo.

La classificazione utilizza esclusivamente le informazioni prodotte
dalla Pipeline.

Tipologie di richiesta

Le richieste archivistiche sono classificate in due sole tipologie.

A. Richieste di Navigazione Archivistica

Appartengono a questa tipologia tutte le richieste che possono essere
soddisfatte mediante la navigazione del modello archivistico.

La Pipeline:

individua le entità core;

costruisce il percorso archivistico;

individua la Trasformazione Archivistica;

restituisce direttamente il risultato richiesto.

La risposta è ottenuta esclusivamente attraversando una o più relazioni
del modello archivistico ufficiale.

B. Richieste di Interrogazione Archivistica

Appartengono a questa tipologia le richieste che non possono essere
soddisfatte mediante la sola navigazione del modello archivistico.

In tali casi la Pipeline:

individua comunque le entità core;

costruisce il percorso archivistico;

individua la Trasformazione Archivistica;

produce una Query Archivistica contenente i criteri necessari
all'elaborazione della risposta.

La Query Archivistica costituisce l'output logico della Pipeline e
rappresenta l'input del motore incaricato dell'elaborazione.

Query Archivistica

La Query Archivistica non rappresenta una ricerca testuale.

Essa descrive in forma strutturata l'operazione che dovrà essere
eseguita sui dati dell'archivio.

Può comprendere, a titolo esemplificativo:

confronti;

aggregazioni;

conteggi;

ordinamenti;

classifiche;

verifiche di coerenza;

individuazione di anomalie;

criteri di filtro multipli.

La costruzione della Query Archivistica non modifica il percorso
archivistico individuato dalla Pipeline, ma ne utilizza il risultato
come base informativa.

Routing

Il routing viene determinato esclusivamente dalla tipologia di richiesta
prodotta dalla Pipeline.

le richieste di Navigazione Archivistica vengono indirizzate al motore
di navigazione;

le richieste di Interrogazione Archivistica vengono indirizzate al
motore di interrogazione.

La modalità di implementazione dei motori è indipendente dalla presente
specifica e non costituisce parte della classificazione archivistica.

Vincoli architetturali

È espressamente vietato:

classificare una richiesta prima dell'individuazione della
Trasformazione Archivistica;

costruire una Query Archivistica quando la richiesta è soddisfacibile
mediante semplice navigazione;

utilizzare la tipologia della domanda come criterio di classificazione;

introdurre classificazioni non derivanti dalla Pipeline Ufficiale.Inizio
moduloFine modulo

PASSO 3

3.3.8 MODELLO DI NAVIGAZIONE ARCHIVISTICA

Il modello di navigazione archivistica definisce le modalità con cui la
Pipeline percorre il modello dati per soddisfare una richiesta.

La navigazione è indipendente dalla formulazione della domanda ed è
basata esclusivamente sulle relazioni archivistiche ufficiali.

Modello relazionale

Le uniche relazioni archivistiche riconosciute sono:

OBJECT ⇄ BOX

BOX ⇄ LOCATION

BOX ⇄ CATEGORY

Non sono ammesse relazioni archivistiche aggiuntive.

Ogni navigazione deve essere costruita concatenando esclusivamente tali
relazioni.

Costruzione del percorso archivistico

Il percorso archivistico viene costruito automaticamente dalla Pipeline
utilizzando:

le entità core riconosciute;

il Fulcro della richiesta;

le relazioni del modello archivistico.

La costruzione del percorso deve individuare il tragitto minimo
necessario per soddisfare la richiesta.

Trasformazioni archivistiche

Una trasformazione archivistica rappresenta il risultato della
navigazione tra due entità archivistiche.

Può essere:

diretta, quando utilizza una sola relazione;

composta, quando utilizza due o più relazioni consecutive.

La complessità della trasformazione non modifica il comportamento della
Pipeline.

Indipendenza dal percorso

Percorsi archivistici differenti possono produrre la stessa
Trasformazione Archivistica.

Di conseguenza:

il percorso archivistico rappresenta esclusivamente la modalità di
navigazione;

la Trasformazione Archivistica rappresenta il risultato logico della
navigazione.

Il percorso non costituisce un criterio di classificazione.

Vincoli

La Pipeline deve garantire che:

ogni passaggio del percorso sia riconducibile ad una relazione
archivistica ufficiale;

non vengano introdotte trasformazioni non supportate dal modello dati;

ogni trasformazione sia completamente determinata prima dell'eventuale
costruzione di una Query Archivistica.

Output

Al termine della navigazione la Pipeline deve produrre:

il percorso archivistico costruito;

la Trasformazione Archivistica individuata.

Tali informazioni costituiscono l'input delle fasi successive della
Pipeline e rappresentano l'unico riferimento autorizzato per la
prosecuzione dell'elaborazione.

PASSO 4

3.3.9 QUERY ARCHIVISTICA

La Query Archivistica è il risultato logico prodotto dalla Pipeline
quando una richiesta non può essere soddisfatta mediante la sola
navigazione del modello archivistico.

La Query Archivistica non sostituisce la navigazione, ma la estende.

Finalità

La Query Archivistica ha lo scopo di descrivere in forma strutturata
l'elaborazione da eseguire sui dati restituiti dalla navigazione
archivistica.

La sua costruzione avviene esclusivamente dopo che la Pipeline ha
completato:

il riconoscimento delle entità core;

l'individuazione del Fulcro;

la costruzione del percorso archivistico;

l'individuazione della Trasformazione Archivistica;

la valutazione della soddisfacibilità.

Contenuto

Una Query Archivistica può contenere uno o più dei seguenti elementi:

criterio di ricerca;

criteri di filtro;

criteri di raggruppamento;

criteri di ordinamento;

criteri di aggregazione;

criteri di confronto;

criteri di selezione del risultato.

La struttura della Query Archivistica è indipendente dalla formulazione
linguistica della richiesta.

Ambito di utilizzo

La Query Archivistica viene generata esclusivamente quando la richiesta
richiede un'elaborazione che non può essere ottenuta mediante la
semplice navigazione delle relazioni archivistiche.

Rientrano in tale categoria, a titolo esemplificativo:

confronti tra risultati;

conteggi;

aggregazioni;

classifiche;

verifiche di coerenza;

ricerche prive di una specifica chiave archivistica;

interrogazioni statistiche.

Vincoli

La Query Archivistica:

non modifica il percorso archivistico individuato dalla Pipeline;

non introduce nuove entità archivistiche;

non modifica la Trasformazione Archivistica individuata;

utilizza esclusivamente dati ottenuti attraverso la navigazione del
modello archivistico.

Output

La Query Archivistica costituisce l'output finale della Pipeline per le
richieste di Interrogazione Archivistica.

Essa rappresenta l'unico artefatto autorizzato da utilizzare per
l'elaborazione successiva della richiesta.

PASSO 5

3.3.10 ROUTING

Il Routing costituisce l'ultima fase decisionale della Pipeline
Ufficiale.

Ha il compito di individuare il componente applicativo incaricato
dell'elaborazione della richiesta, utilizzando esclusivamente gli output
prodotti dalla Pipeline.

Input

Il Routing utilizza esclusivamente:

tipologia della richiesta;

Trasformazione Archivistica individuata;

eventuale Query Archivistica.

Nessun'altra informazione può essere utilizzata per determinare il
componente destinatario.

Regole di Routing

Il Routing distingue esclusivamente due modalità operative.

Navigazione Archivistica

Quando la richiesta è completamente soddisfacibile mediante la
navigazione del modello archivistico, il Routing indirizza la richiesta
al componente incaricato della navigazione.

Il componente riceve:

Trasformazione Archivistica;

dati necessari alla navigazione.

Interrogazione Archivistica

Quando la richiesta richiede un'elaborazione aggiuntiva, il Routing
indirizza la richiesta al componente incaricato delle interrogazioni
archivistiche.

Il componente riceve:

Trasformazione Archivistica;

Query Archivistica;

dati necessari all'elaborazione.

Responsabilità

Il Routing:

non interpreta la richiesta;

non modifica il Fulcro;

non modifica il percorso archivistico;

non modifica la Trasformazione Archivistica;

non costruisce la Query Archivistica;

non esegue alcuna elaborazione sui dati.

Il Routing ha esclusivamente il compito di indirizzare la richiesta
verso il componente appropriato.

Vincoli

Il Routing deve garantire che:

ogni richiesta venga indirizzata ad un solo componente;

una richiesta non possa essere elaborata contemporaneamente da più
componenti;

il componente destinatario riceva tutte le informazioni prodotte dalla
Pipeline necessarie all'elaborazione;

nessun componente ricostruisca autonomamente informazioni già prodotte
dalla Pipeline.

Output

L'output del Routing è costituito esclusivamente dall'identificazione
del componente incaricato dell'elaborazione e dal trasferimento
dell'intero contesto prodotto dalla Pipeline.

La scelta del componente rappresenta una decisione implementativa e non
modifica in alcun modo il significato archivistico della richiesta.

PASSO 6

3.3.11 REGOLE GENERALI DELLA PIPELINE

Le seguenti regole costituiscono vincoli architetturali permanenti della
Ricerca Interrogativa V1.

Esse si applicano a tutte le richieste archivistiche, indipendentemente
dalla modalità di elaborazione.

R1 -- Pipeline obbligatoria

Ogni richiesta deve essere elaborata esclusivamente mediante la Pipeline
Ufficiale.

Non è consentito saltare, anticipare o ripetere arbitrariamente una fase
della Pipeline.

R2 -- Centralità delle entità core

La Pipeline può riconoscere esclusivamente le seguenti entità core:

OBJECT

BOX

LOCATION

CATEGORY

Elementi diversi dalle entità core costituiscono esclusivamente evidenze
interpretative.

R3 -- Modello relazionale

La costruzione del percorso archivistico deve utilizzare esclusivamente
le relazioni archivistiche ufficiali del modello dati.

Non sono ammesse relazioni esterne o implicite.

R4 -- Unicità del percorso

Per ogni richiesta la Pipeline deve individuare un unico percorso
archivistico coerente con il Fulcro della richiesta.

R5 -- Trasformazione Archivistica

La Trasformazione Archivistica rappresenta il risultato logico della
navigazione archivistica.

Essa costituisce il riferimento obbligatorio per tutte le fasi
successive della Pipeline.

R6 -- Query Archivistica

La Query Archivistica può essere generata esclusivamente quando la sola
navigazione archivistica non è sufficiente a produrre la risposta
richiesta.

R7 -- Chiarificazione

La chiarificazione costituisce una misura eccezionale.

Può essere richiesta esclusivamente quando la Pipeline non dispone di
elementi sufficienti per determinare un'unica interpretazione coerente.

Quando più interpretazioni risultano equivalenti e non esiste una
trasformazione dominante, la pipeline applica la Regola R19, attivando
la chiarificazione.

La presenza di più risultati non costituisce, di per sé, motivo di
chiarificazione.

R8 -- Ambiguità tra entità core (R19)

Quando la stessa chiave testuale identifica contemporaneamente due o più
entità core e non esistono evidenze sufficienti per individuare una
trasformazione archivistica dominante, la Pipeline deve richiedere la
chiarificazione.

È vietato selezionare automaticamente una delle possibili
interpretazioni.

R9 -- Separazione delle responsabilità

Ogni componente utilizza esclusivamente gli output prodotti dalla fase
precedente.

Nessun componente può ricostruire autonomamente:

entità core;

Fulcro;

percorso archivistico;

Trasformazione Archivistica;

Query Archivistica.

R10 -- Stabilità architetturale

L'introduzione di nuove funzionalità, nuove modalità di ricerca o nuovi
componenti applicativi non deve modificare:

il modello relazionale;

la Pipeline Ufficiale;

le regole di costruzione del percorso archivistico;

le regole di individuazione della Trasformazione Archivistica;

le presenti regole generali.

Ogni evoluzione del sistema deve essere compatibile con tali principi,
evitando regressioni funzionali e architetturali.

3.3.12 CHIARIFICAZIONI

Le chiarificazioni devono essere:

brevi

aperte

non guidate da pulsanti

coerenti con lo stile delle card standard

Esempio: "Di quale materiale parli?"

3.3.13 MESSAGGI SISTEMA

Elaborazione: "Sto analizzando la richiesta..."

Fallback: "Non riesco a capire esattamente quello che stai cercando.
Prova a formulare la richiesta con parole più chiare."

Commiato: "Tocca qui per tornare alla Dashboard"

3.3.14 RUOLO DELLA POSIZIONE

La Posizione costituisce l'entità primaria per le interrogazioni di tipo
distributivo del Motore B.

La sua presenza consente:

distribuzione archivio

riduzione candidati

relazioni posizione ↔ categoria

contestualizzazione della conservazione

3.3.15 TIPOLOGIE ESCLUSE DA CORE V1

✘ NLP

✘ interpretazione semantica avanzata

✘ embedding

✘ classificazione predittiva

✘ reportistica

✘ KPI evoluti

✘ linguaggio naturale avanzato

ESTRATTO DA CRUSCOTTO&LOG V. 7.0

4.4 RICERCA INTERROGATIVA -- ARTEFATTI DI SUPPORTO

4.4.1 Matrice Pattern Linguistici

Scopo:

Supportare:

-   riconoscimento pattern linguistici

-   individuazione varianti equivalenti

-   riduzione ambiguità

-   riconoscimento entità

-   SearchSatisfiabilityEvaluatorV2

-   routing motori di ricerca

Tabella di riferimento: Matrice_Pattern_Linguistici (\*) - Comprende
elementi del precedente modello

Stato:

ATTIVA

Note:

PATTERN_001--PATTERN_009 inclusi nel perimetro attivo.

PATTERN_010 sospeso e da rivalutare in funzione delle future capacità
KPI/query aggregate del Motore B.

Ruolo

Componente responsabile della classificazione preliminare delle
richieste formulate in linguaggio naturale.

Obiettivi

identificare la famiglia\* funzionale candidata; (V0 - modello
precedente)

determinare il livello di soddisfacibilità;

individuare eventuali ambiguità;

fornire le informazioni necessarie al routing successivo.

Il componente:

non esegue ricerche;

non interroga il database;

non produce risultati archivistici;

non applica query.

Input

entità riconosciute;

fulcro candidato;

pattern linguistici;

indicatori lessicali.

Output

famiglia\* candidata (V0 - modello precedente);

classe di soddisfacibilità;

routing candidato;

necessità di chiarificazione.

Ordine decisionale

S1 → S2 → S4 → S3

Motivazione

privilegiare sempre i casi soddisfacibili;

minimizzare i fallback;

evitare classificazioni premature come fuori ambito.

Ponderazione delle evidenze

La classificazione non deve mai essere determinata da una singola
evidenza.

Ogni richiesta deve essere valutata considerando congiuntamente:

entità riconosciute;

fulcro candidato;

pattern linguistici;

indicatori lessicali.

Gerarchia delle evidenze

In presenza di evidenze contrastanti si applica la seguente gerarchia:

Fulcro

Entità riconosciute

Pattern linguistici

Indicatori lessicali

Individuazione del fulcro

Il fulcro rappresenta l'elemento sul quale ricade l'interesse principale
dell'utente.

Esempi:

"Dove si trova il trapano?" → Fulcro: OBJECT → Famiglia\* candidata: F1
(V0 - modello precedente)

"In quale contenitore si trova il trapano?" → Fulcro: BOX → Famiglia\*
candidata: F4 (V0 - modello precedente)

"In quali luoghi ho conservato i trapani?" → Fulcro: LOCATION →
Famiglia\* candidata: F9 (V0 - modello precedente)

Modello di individuazione del fulcro (V0 - modello precedente)

La determinazione del fulcro avviene esclusivamente dopo il
riconoscimento delle entità archivistiche candidate.

I termini presenti nella domanda non devono essere esclusi
preventivamente sulla base del loro significato apparente.

Un termine può rappresentare un oggetto, un contenitore, una posizione o
una categoria a seconda dei dati effettivamente presenti nell'archivio.

La classificazione lessicale di un termine non è sufficiente per
determinarne il ruolo funzionale.

Un elemento viene considerato "core" esclusivamente quando corrisponde
ad una entità archivistica riconosciuta.

La determinazione del fulcro avviene utilizzando le entità archivistiche
riconosciute, il target eventualmente espresso nella domanda, i pattern
linguistici e le ulteriori evidenze disponibili.

La ponderazione viene applicata ai fulcri candidati e non ai singoli
token della domanda.

L'ordine di apparizione nella domanda, la struttura sintattica, il
target richiesto e la tipologia delle relazioni rappresentano evidenze
utilizzabili nella ponderazione.

Il modello impedisce esclusioni premature e garantisce che ogni termine
potenzialmente riconducibile ad una entità archivistica possa concorrere
alla determinazione del fulcro.

4.4.2 SearchSatisfiabilityEvaluatorV2

Regole operative della Pipeline

Il componente SearchSatisfiabilityEvaluatorV2 ha il compito di
verificare se la richiesta può essere soddisfatta utilizzando le
informazioni presenti nell'archivio e di produrre l'Output della
Pipeline previsto dalla Nota Integrata.

Il componente non esegue la ricerca né produce direttamente la risposta.

R1 - Principio della chiave composta

Quando una sequenza di termini corrisponde integralmente al nome di una
entità archivistica, la sequenza deve essere trattata prioritariamente
come chiave univoca.

Esempio:

"trapano elettrico"

deve essere valutato prioritariamente come possibile entità archivistica
unica e non come somma indipendente dei termini.

R2 - Nessuna esclusione preventiva dei termini

Nessun termine della richiesta può essere escluso prima del
completamento del riconoscimento delle entità archivistiche.

Un termine apparentemente descrittivo può rappresentare un'entità
effettivamente presente nell'archivio.

R3 - Le evidenze derivano esclusivamente dall'archivio

La Pipeline costruisce le proprie evidenze esclusivamente sulle entità
archivistiche riconosciute.

Il numero di parole presenti nella richiesta e il numero delle entità
riconosciute costituiscono grandezze indipendenti.

R4 - Assenza della categoria "Attributo"

Ai fini della Pipeline non esiste una categoria archivistica denominata
"Attributo".

Elementi quali colore, materiale, marca, dimensione e caratteristiche
analoghe costituiscono esclusivamente indizi interpretativi, salvo che
coincidano con entità archivistiche effettivamente censite.

R5 - Entità Core

Le uniche entità core riconosciute sono:

OBJECT

BOX

LOCATION

CATEGORY

Le entità core costituiscono la base per la costruzione del percorso
archivistico.

R6 - Fulcro

Il Fulcro rappresenta l'obiettivo informativo della richiesta.

Il Fulcro viene utilizzato esclusivamente per:

interpretare la richiesta;

costruire il percorso archivistico;

gestire eventuali ambiguità.

Il Fulcro non determina direttamente la classificazione della richiesta
né il routing.

R7 - Distinzione tra Entità Core e Fulcro

Le Entità Core rappresentano gli elementi archivistici riconosciuti.

Il Fulcro rappresenta invece il risultato archivistico verso il quale
deve convergere la risposta.

Esempio:

"In quale contenitore si trova il trapano?"

Entità Core riconosciuta:

OBJECT

Fulcro:

BOX

R8 - Prevalenza delle evidenze archivistiche

La costruzione del percorso archivistico deve basarsi esclusivamente
sulle entità effettivamente riconosciute nell'archivio.

Il significato grammaticale o semantico di un termine non costituisce
evidenza sufficiente.

R9 - Costruzione del percorso archivistico

Il percorso archivistico viene costruito utilizzando:

Entità Core;

Fulcro;

relazioni archivistiche ufficiali.

Le sole relazioni ammesse sono:

OBJECT ⇄ BOX

BOX ⇄ LOCATION

BOX ⇄ CATEGORY

R10 - Trasformazione Archivistica

Completato il percorso archivistico, la Pipeline individua la
Trasformazione Archivistica.

La Trasformazione Archivistica costituisce il risultato logico della
navigazione ed è utilizzata dalle fasi successive della Pipeline.

R11 - Valutazione della soddisfacibilità

La soddisfacibilità viene determinata esclusivamente dopo:

riconoscimento delle Entità Core;

individuazione del Fulcro;

costruzione del percorso archivistico;

individuazione della Trasformazione Archivistica.

Nessuna singola evidenza può determinarla autonomamente.

R12 - Query Archivistica

Quando la richiesta non è soddisfacibile mediante la sola navigazione
archivistica, la Pipeline produce una Query Archivistica.

La Query Archivistica rappresenta l'output logico destinato al
componente incaricato dell'interrogazione archivistica.

R13 - Neutralità morfologica

Gli indicatori lessicali devono essere registrati esclusivamente nella
forma canonica.

La gestione delle varianti linguistiche è demandata alla fase di
normalizzazione.

Le matrici devono pertanto contenere esclusivamente i termini canonici.

R14 - Costruzione degli indicatori

Gli indicatori devono essere costruiti mediante la combinazione di:

perifrasi qualificanti;

alias delle Entità Core.

Le perifrasi isolate non costituiscono evidenza sufficiente per:

riconoscere Entità Core;

individuare il Fulcro;

costruire il percorso archivistico;

determinare la Trasformazione Archivistica.

R15 - Gestione delle ambiguità

Quando più interpretazioni archivistiche risultano plausibili e la
Pipeline non dispone di evidenze sufficienti per individuare una
Trasformazione Archivistica dominante, deve essere attivata la
chiarificazione prevista dalla Regola R19 della Nota Integrata.

È vietato selezionare automaticamente una interpretazione.

R16 - Routing

Il Routing viene effettuato esclusivamente sulla base dell'Output della
Pipeline.

La Pipeline distingue due sole modalità operative:

Navigazione Archivistica;

Interrogazione Archivistica.

La modalità di implementazione dei componenti destinatari non
costituisce parte delle presenti regole.

Principi vincolanti

Restano sempre validi i seguenti principi:

nessuna decisione può essere assunta sulla base di un singolo indizio;

il Fulcro guida esclusivamente la costruzione del percorso archivistico;

la Trasformazione Archivistica rappresenta il riferimento logico della
richiesta;

la Query Archivistica viene prodotta solo quando la navigazione
archivistica non è sufficiente;

la classificazione e l'elaborazione della risposta costituiscono fasi
distinte.

Nota integrativa -- Modello di valutazione delle evidenze

La Pipeline non utilizza regole di priorità rigide né il criterio della
"prima corrispondenza utile".

Ogni decisione deriva dalla valutazione congiunta delle evidenze
prodotte durante le fasi della Pipeline.

Nella versione V1 vengono considerate le seguenti evidenze:

Indicatori;

Entità Core;

Fulcro;

Trasformazione Archivistica;

Soddisfacibilità.

La ponderazione delle singole evidenze costituisce un parametro
implementativo e potrà essere affinata sulla base dei test funzionali,
senza modificare il modello architetturale della Pipeline.

L'eventuale attivazione della chiarificazione deve derivare
esclusivamente dall'impossibilità di individuare una Trasformazione
Archivistica dominante.

Resta fermo il principio secondo cui nessuna singola evidenza può
determinare autonomamente l'Output della Pipeline, il Routing o la
risposta finale all'utente.

4.4.3 Roadmap ufficiale Ricerca Interrogativa

Obiettivo

Trasformare gli artefatti funzionali consolidati in una sequenza
implementativa a basso rischio, mantenendo la piena coerenza con la
Pipeline Ufficiale e con il modello archivistico.

Sequenza ufficiale

B1

Pipeline Ufficiale

Obiettivo

Implementare integralmente la Pipeline Ufficiale prevista dalla Nota
Integrata.

Output

Entità Core riconosciute;

Fulcro;

Percorso Archivistico;

Trasformazione Archivistica;

Tipologia della richiesta;

eventuale Query Archivistica.

B2

Navigazione Archivistica

Obiettivo

Implementare la navigazione archivistica basata sulle relazioni
ufficiali del modello dati.

Copertura

Richieste soddisfacibili mediante:

OBJECT ⇄ BOX

BOX ⇄ LOCATION

BOX ⇄ CATEGORY

e relative trasformazioni composte.

B3

Interrogazione Archivistica

Obiettivo

Implementare le richieste che richiedono la costruzione di una Query
Archivistica.

Copertura

Comprende, a titolo esemplificativo:

confronti;

aggregazioni;

conteggi;

classifiche;

verifiche di coerenza;

interrogazioni statistiche.

B4

Integrazione End-to-End

Obiettivo

Integrare:

Pipeline Ufficiale;

componente di Navigazione Archivistica;

componente di Interrogazione Archivistica;

interfaccia utente.

Verificare il corretto trasferimento dell'Output della Pipeline ai
componenti destinatari.

B5

Validazione V1

Criteri di completamento

Devono risultare verificati:

corretto riconoscimento delle Entità Core;

corretta costruzione del Percorso Archivistico;

corretta individuazione della Trasformazione Archivistica;

corretta generazione della Query Archivistica quando prevista;

corretto instradamento delle richieste;

integrazione End-to-End;

assenza di regressioni rispetto alla baseline validata.

4.4.4 Blueprint Implementativo Ricerca Interrogativa

Principio generale

L'implementazione della Ricerca Interrogativa V1 dovrà seguire
rigorosamente la Pipeline Ufficiale definita dalla Nota Integrata.

Ogni componente della Pipeline dovrà:

utilizzare esclusivamente l'output prodotto dalla fase precedente;

produrre esclusivamente il proprio output;

non reinterpretare informazioni già determinate;

non duplicare responsabilità appartenenti ad altri componenti.

L'obiettivo della Pipeline è produrre un unico Output della Pipeline,
che costituirà il contratto ufficiale tra la Pipeline implementativa e i
componenti destinatari.

B1 - Pipeline Ufficiale

Obiettivo

Implementare integralmente la Pipeline prevista dalla Nota Integrata.

La Pipeline dovrà produrre progressivamente:

Richiesta normalizzata;

Indicatori Lessicali;

Entità Core riconosciute;

Fulcro;

Percorso Archivistico;

Trasformazione Archivistica;

Tipologia della Richiesta;

eventuale Query Archivistica.

Il risultato finale della Pipeline sarà rappresentato dal
PipelineOutput, unico contratto logico utilizzabile dai componenti
successivi.

File prioritari

Componenti esistenti:

SearchNormalizer

SearchTokenizer

SearchCoreNormalizationPipeline

SearchLexicalIndicatorMatrix

SearchInterpreter

SearchArchiveLookup

SearchEntityRecognizer

SearchFulcrumResolver

SearchQuestionRepository

SearchSatisfiabilityEvaluatorV2

SearchRouter

GlobalSearchDispatcher

Modelli:

SearchSatisfiabilityInput

SearchSatisfiabilityResult

SearchRoutingResult

SearchInterpretation

SearchFulcrumResult

SearchRecognizedEntitiesResult

SearchQuestionPattern

SearchClassification

SearchClarificationType

PipelineOutput

Nuovi componenti eventualmente necessari dovranno essere introdotti
esclusivamente quando indispensabili al completamento della Pipeline e
senza alterarne la sequenza logica.

B2 - Navigazione Archivistica

Obiettivo

Implementare la Navigazione Archivistica basata esclusivamente sul
modello relazionale ufficiale.

Copertura:

OBJECT ⇄ BOX

BOX ⇄ LOCATION

BOX ⇄ CATEGORY

e relative trasformazioni composte.

Prerequisito

B2 potrà iniziare esclusivamente dopo il completamento di B1.

Input:

PipelineOutput

B2 non interpreta la domanda.

B2 non determina il Fulcro.

B2 non effettua Routing.

B2 utilizza esclusivamente le informazioni prodotte dalla Pipeline.

B3 - Interrogazione Archivistica

Obiettivo

Implementare le richieste che richiedono una Query Archivistica.

Comprende:

confronti;

aggregazioni;

conteggi;

classifiche;

verifiche di coerenza;

interrogazioni statistiche.

Input:

PipelineOutput

Output:

SearchArchiveQuery

La Query Archivistica rappresenta esclusivamente la formalizzazione
operativa della richiesta ed è destinata ai motori di interrogazione.

B4 - Integrazione End-to-End

Obiettivo

Integrare:

Pipeline Ufficiale;

Navigazione Archivistica;

Interrogazione Archivistica;

Interfaccia utente.

GlobalSearchDispatcher costituisce l'orchestratore della Pipeline.

Il trasferimento delle informazioni tra Dispatcher, Router ed Engine
dovrà avvenire esclusivamente mediante PipelineOutput.

Nessun componente successivo potrà reinterpretare la richiesta
originaria.

B5 - Validazione V1

Criteri di completamento

Dovranno risultare verificati:

corretto riconoscimento delle Entità Core;

corretta individuazione del Fulcro;

corretta costruzione del Percorso Archivistico;

corretta individuazione della Trasformazione Archivistica;

corretta determinazione della Tipologia della Richiesta;

corretta generazione della Query Archivistica quando prevista;

corretta produzione del PipelineOutput;

corretto trasferimento del PipelineOutput tra Dispatcher, Router ed
Engine;

integrazione End-to-End;

assenza di regressioni rispetto alla baseline validata;

corretta osservabilità della Pipeline mediante il sistema ufficiale dei
marker.

4.4.5 Criteri di Completamento V1

Obiettivo

Definire i criteri oggettivi e verificabili che consentono di dichiarare
completata la Ricerca Interrogativa V1.

La valutazione deve essere effettuata esclusivamente sulla base del
comportamento osservabile del sistema.

C1 - Pipeline Ufficiale

Devono risultare operative e verificabili:

✓ riconoscimento delle Entità Core;

✓ individuazione del Fulcro;

✓ costruzione del Percorso Archivistico;

✓ individuazione della Trasformazione Archivistica;

✓ valutazione della soddisfacibilità;

✓ produzione dell'Output della Pipeline.

C2 - Navigazione Archivistica

Devono risultare correttamente implementate tutte le richieste
soddisfacibili mediante la navigazione del modello archivistico.

Devono risultare operative tutte le trasformazioni costruibili
attraverso le relazioni:

✓ OBJECT ⇄ BOX

✓ BOX ⇄ LOCATION

✓ BOX ⇄ CATEGORY

C3 - Interrogazione Archivistica

Devono risultare operative le richieste che richiedono la costruzione di
una Query Archivistica.

La Query Archivistica deve essere prodotta esclusivamente nei casi
previsti dalla Pipeline.

C4 - Integrazione End-to-End

Per ogni richiesta supportata deve essere verificabile il seguente
flusso:

Domanda utente

↓

Pipeline Ufficiale

↓

Output della Pipeline

↓

Routing

↓

Componente destinatario

↓

Elaborazione

↓

Risposta

L'intero processo deve risultare funzionante senza interventi manuali.

C5 - Assenza di Regressioni

Le funzionalità esistenti devono continuare a funzionare correttamente.

In particolare:

✓ ricerca archivio esistente;

✓ apertura dettaglio contenitore;

✓ apertura dettaglio oggetto;

✓ navigazione dei risultati;

✓ dashboard;

✓ categorie.

Non devono essere introdotte regressioni rispetto alla baseline
validata.

CRITERIO UFFICIALE DI COMPLETAMENTO V1

La Ricerca Interrogativa V1 può essere dichiarata completata
esclusivamente quando risultano soddisfatti tutti i seguenti criteri:

✓ Pipeline Ufficiale operativa;

✓ Navigazione Archivistica operativa;

✓ Interrogazione Archivistica operativa;

✓ Output della Pipeline correttamente prodotto;

✓ integrazione End-to-End verificata;

✓ assenza di regressioni rispetto alla baseline validata.

4.4.6 Regole Operative di Ingresso all'Implementazione

Obiettivo

Definire le condizioni operative che consentono l'avvio delle attività
implementative della Ricerca Interrogativa.

FILE PIVOT UFFICIALE B1

SearchSatisfiabilityEvaluatorV2.kt

STATO:

DA ACQUISIRE PER PRIMO

Obiettivo

Verificare la coerenza tra implementazione attuale e modello
implementativo consolidato.

Le dipendenze del file dovranno essere identificate esclusivamente
mediante analisi del file stesso.

Strategia Ufficiale di Acquisizione File

Acquisire il File Pivot.

Analizzare il file.

Identificare esclusivamente le dipendenze dirette.

Acquisire una dipendenza alla volta.

Aggiornare il Blueprint Implementativo.

Procedere all'implementazione solo dopo il consolidamento del perimetro
impattato.

Sono vietate acquisizioni massive non giustificate da dipendenze
verificate.

Condizione di Ingresso B1

L'avvio dell'implementazione è autorizzato esclusivamente quando
risultano soddisfatte tutte le seguenti condizioni:

✓ Pipeline Ufficiale consolidata;

✓ Roadmap ufficiale consolidata;

✓ Blueprint Implementativo consolidato;

✓ File Pivot identificato;

✓ Strategia di acquisizione definita;

✓ Perimetro iniziale verificato.

In assenza di tali condizioni non è consentito avviare modifiche
implementative.

Principio Guida

L'implementazione deve procedere mediante espansione controllata del
perimetro di analisi, partendo dal File Pivot e acquisendo
esclusivamente le dipendenze effettivamente necessarie.

Obiettivi:

✓ riduzione dei loop;

✓ riduzione delle acquisizioni non necessarie;

✓ minimizzazione delle regressioni;

✓ tracciabilità delle decisioni implementative;

✓ aggiornamento progressivo del Blueprint Implementativo.

4.4.7 Criterio di STOP Pre-Implementativo

Obiettivo

Definire formalmente il punto di completamento della fase preparatoria
della Ricerca Interrogativa e la condizione di ingresso alla fase
implementativa.

Condizione di STOP Pre-Implementativo

La fase documentale può considerarsi conclusa esclusivamente quando
risultano contemporaneamente soddisfatte tutte le seguenti condizioni:

✓ Artefatti funzionali consolidati;

✓ Roadmap ufficiale consolidata;

✓ Blueprint Implementativo consolidato;

✓ Criteri di completamento V1 consolidati;

✓ File Pivot identificato;

✓ Strategia di acquisizione file definita;

✓ Nessuna ulteriore decisione tecnica aperta;

✓ Nessuna dipendenza documentale nota non risolta.

Effetti

Al raggiungimento delle condizioni sopra indicate:

✓ non devono essere prodotti ulteriori artefatti documentali;

✓ il lavoro deve proseguire mediante acquisizione controllata dei file;

✓ l'implementazione diventa la fase prioritaria.

Eccezioni

Sono consentiti nuovi artefatti documentali esclusivamente in presenza
di:

✓ regressioni architetturali;

✓ cambiamenti di perimetro;

✓ revisione della Pipeline Ufficiale;

✓ revisione dei criteri di completamento V1.

Principio Guida

La documentazione deve supportare l'implementazione.

Una volta soddisfatte le condizioni di STOP Pre-Implementativo, il
progetto deve avanzare attraverso attività di analisi e implementazione
del codice, evitando la produzione di ulteriore documentazione non
strettamente necessaria.

Valutazione finale: con questi aggiornamenti il capitolo 4.4 risulta
coerente con la nuova Nota Integrata senza perdere il patrimonio
progettuale accumulato. A questo punto considero conclusa la fase di
consolidamento della documentazione della Ricerca Interrogativa V1 e
ritengo che ci siano le condizioni per passare finalmente
all'implementazione.
