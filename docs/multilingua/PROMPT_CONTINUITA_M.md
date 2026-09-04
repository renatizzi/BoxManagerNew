# Prompt di continuità — Filone M (inglese in BoxManager)

**SI Renato, 01/09/2026** — mini-assessment e piano **approvati**; **Guida rapida in-app** obbligatoria in M1/CK1.  
**Ingresso unico** per sessioni agente sull’**inglese** (funzione della stessa BoxManager). Non confondere con [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md).

**Identità:** non esiste un’«app multilingue». Si lavora su **BoxManager**. La 1.2 è la copia dei tester; questa linea è la stessa app con le funzioni in più. Fonte: [STRATEGIA_UNIFICAZIONE.md](../famiglia/STRATEGIA_UNIFICAZIONE.md), `.cursor/rules/identita-app.mdc`.

### Backlog aperto (in evidenza — non è questa fetta)

Fonte viva: [PROMEMORIA](../famiglia/PROMEMORIA_INTERVENTI_TRASVERSALI.md). Restano visibili fino alla presa in carico.

| ID | Indicazione |
|----|-------------|
| **B-SEL-CARTELLA** | Selettore cartella anche su drive non visti da Android (NAS); SAF non basta da solo — valutazione aperta |

---

## Nuova sessione — sì o no?

| Situazione | Nuova sessione? |
|------------|-----------------|
| Inizi **M0**, **M1** o **M2** (codice o doc filone M) | **SÌ** — contesto pulito, solo filone M |
| Solo correttivi P2 / bug Play / famiglia B5.7 | **No** — usa [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md) |
| Attesa test Play chiuso, nessun lavoro M | **No** — non aprire sessione M ancora |
| **SI Renato 01/09/2026** — M in parallelo al test (~2 sett.) | **SÌ** — nuova sessione filone M (M1a…) |

**Regola:** una sessione = un filone (M **oppure** correttivi), per non mescolare regole pipeline / checkpoint.

**Bug Play durante il test:** solo se **bloccante**. Allora si aggiorna la **1.2** (`main`), non si mescola lo sviluppo. Sessione [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md). Il filone M non si ferma.

---

## Stato al 04/09/2026

| Voce | Valore |
|------|--------|
| **Assessment** | [ASSESSMENT_M.md](ASSESSMENT_M.md) — PR **#16** |
| **Piano** | **Approvato SI Renato** |
| **Lingue V1** | Italiano (default) + English |
| **Play Console traduzione app** | **Non usare** — lavoro in Cursor |
| **M1 / CK1** | **CONVALIDATO SI Renato device 03/09/2026** (M1a–M1d su PR **#18**) |
| **M2a** | **EN verificata** su delega Renato 03/09/2026 — [BOZZA_TABELLE_EN_CK0.md](BOZZA_TABELLE_EN_CK0.md) + Allegato **4.21** |
| **M2b** | **Fatto** — motore locale-aware EN (S1–S3 SI 03/09/2026). [SEMANTICA_EN_EQUIVOCI.md](SEMANTICA_EN_EQUIVOCI.md) |
| **M2c** | **Fatto** — messaggi 2.6 e card ricerca; locale UI allineato (PR **#21**, fix PR **#22**) |
| **CK2** | **CONVALIDATO SI Renato device 04/09/2026** — 10/10 domande EN OK (dopo fix locale) |
| **Prossimo pacchetto** | **M3** solo a test Play chiuso. Promemoria: resta **B-SEL-CARTELLA** |
| **Branch lavoro** | `cursor/promemoria-fix-d69a` |
| **Branch base** | `cursor/promemoria-backlog-d69a` |
| **Play** | BoxManager **1.2** su `main`, identica per tutto il test. Lo **stesso** BoxManager di sviluppo (archivio condiviso + inglese) a test chiuso **sostituisce** la 1.2. Si tocca 1.2 **solo** per bug bloccanti. |
| **Build sviluppo** | Topbar **1.3-famigliaB5.11** (etichetta di build, non un altro nome di app). |
| **Ricerca avanzata EN** | Pipeline 0–10 invariata; **niente** traduttore EN→IT; **niente** interprete semantico (Nota 3.3.9) |
| **Checkpoint** | **CK0** ✅; **S1–S3 SI**; **CK1** ✅; **CK2** ✅ |

### Documenti vincolanti (leggere prima di codice)

| File | Ruolo |
|------|--------|
| [ASSESSMENT_M.md](ASSESSMENT_M.md) | Piano M0–M3, inventario, architettura |
| [BOZZA_TABELLE_EN_CK0.md](BOZZA_TABELLE_EN_CK0.md) | Bozza EN 1:1 da Nota (CK0) |
| [SEMANTICA_EN_EQUIVOCI.md](SEMANTICA_EN_EQUIVOCI.md) | Tipi di domanda EN equivoci; decisioni S1–S3 prima del motore |
| `.cursor/rules/fonti-ufficiali.mdc` | Elenchi alias/messaggi — importare, non riassumere |
| `.cursor/rules/pipeline-ufficiale.mdc` | Ricerca: pipeline 0–10 invariata |
| Nota Integrata **3.6.6** | Scelta lingua Impostazioni |
| Nota **1.3.3** / Allegato **4.21** | Alias Core EN **bozza** — import in codice solo dopo CK0 |

---

## Regole non negoziabili

1. **Fonti ufficiali:** alias, messaggi 2.6, matrice indicatori EN → importare da Nota/Excel, non tradurre a memoria.
2. **Pipeline 0–10:** invariata; solo input locale-aware (`pipeline-ufficiale.mdc`).
3. **Dati utente:** nomi archivio non tradotti.
4. **Test Play aperto:** la 1.2 su `main` resta identica. Lo sviluppo **non** si copia su `main` a metà test. A test chiuso quella BoxManager **è** la nuova ufficiale (sostituisce 1.2). Unica eccezione: bug **bloccante** tester → aggiornamento 1.2. Vietato dire «merge su main solo con SI» e vietato parlare di «app famiglia» / «app multilingue». Dettaglio: [STRATEGIA_UNIFICAZIONE.md](../famiglia/STRATEGIA_UNIFICAZIONE.md).
5. **CK0 obbligatorio** prima del primo commit che tocca `domain/search` per EN.
6. **Guida rapida in-app** (`QuickStartGuideCopy`, topbar «Guida»): **M1 obbligatorio**, verificata in **CK1** (sezione archivio condiviso inclusa). **Ritocchi testuali** (semplificazione) possono essere integrati da Renato in M1d prima del CK1.
7. **Branch base:** tutto il nuovo sviluppo va sui branch dedicati (e feature `cursor/...`), **non** su `main`. `main` è la 1.2 del test Play.
8. **Allineamento obbligatorio prima di ogni sessione:** `git fetch origin` e verificare che il branch di lavoro contenga i commit più recenti della base di sviluppo. Se c'è uno scarto, allineare **prima** di scrivere codice.
9. **Scelta lingua:** la voce IT/EN si trova in **Impostazioni** — non spostarla altrove.
10. **Nome:** l’app si chiama **BoxManager** (`topbar_app_title`). Non esiste un secondo nome di prodotto. `app_name` del flavor Gradle non è un’altra app.

## Istruzioni CK2 per Renato (device — chiusura filone M)

Il codice inglese è **chiuso** (M1 + M2b + M2c). Manca solo il **SI** sul telefono. **M3** (Play al posto della 1.2) **non** è questo passo.

### 1. Pull nel Terminale di Android Studio

Apri il progetto `BoxManagerNew`. In basso: **Terminal**. Incolla **tutto il blocco**, Invio:

```
git fetch origin
git checkout -B cursor/promemoria-fix-d69a origin/cursor/promemoria-fix-d69a
```

Poi: Sync Gradle se lo chiede; variante **famigliaDebug** (non `play`); **Run** sul telefono. Topbar attesa: **1.3-famigliaB5.11**.

**Ritest B5.11:** «categorie usate» → solo usate; «tutti gli oggetti» → report oggetti; F7 = lista contenitori; altro Motore B = messaggio+stampa.

### 2. Archivio da inserire (se è vuoto)

Posizioni e categorie **ci sono già** al primo avvio (Cantina, Garage, Alimenti e Bevande, Attrezzi…). **Non** crearne altre. Censisci solo quanto sotto. Nomi **esatti**, spazi compresi.

**Cinque contenitori** (tutti in posizione **Cantina**):

| Nome contenitore | Categoria |
|------------------|-----------|
| Box | Attrezzi, Strumenti e Ferramenta |
| box prova | Attrezzi, Strumenti e Ferramenta |
| Box 1 | Attrezzi, Strumenti e Ferramenta |
| Cassetta 1 | Attrezzi, Strumenti e Ferramenta |
| prova | Alimenti e Bevande |

**Quattro oggetti** (due si chiamano uguale **Vite**):

| Nome oggetto | Dentro |
|--------------|--------|
| Box | Cassetta 1 |
| Trapano elettrico | Box 1 |
| Vite | Cassetta 1 |
| Vite | prova |

Attenzione: `Box 1` con spazio (non `Box1`); `box prova` minuscolo; un contenitore **e** un oggetto si chiamano entrambi `Box`.

### 3. Cosa è cambiato rispetto a CK1

CK1 era la **lingua delle schermate**. **Nuovo:** le **risposte** della Ricerca avanzata (chiarificazione, nessun risultato, F7/F8) in inglese.

### 4. Domande (dopo Impostazioni → English)

Archivio completo → Ricerca avanzata. Digitare **esatto**:

| # | Domanda | Esito |
|---|---------|-------|
| 1 | Find box | Chiarificazione in inglese (*Rephrase the question so it is clear whether you mean an object or a container.*) |
| 2 | Find container box | Lista: Box, box prova, Box 1 |
| 3 | Where is the trapano elettrico? | Si apre **Box 1** |
| 4 | Where do I find the same type of objects | Elenco F7 in inglese (Cassetta 1 e prova). *Where* non è un luogo |
| 5 | Search the containers with a different category that contain the same type of object | Elenco F8 in inglese (stesso, categorie diverse) |
| 6 | In order to find the trapano elettrico | Come il punto 3; *order* non è categoria |
| 7 | Search all the containers that contain duplicates | Altra F7 |
| 8 | Find containers with a different category and identical objects | Altra F8 |
| 9 | List of the containers that have identical objects | Variante F7 |
| 10 | Find the trapano elettrico | Come il punto 3, senza *where* |

Italiano: Impostazioni → Italiano; *Trova box* e *Nessun risultato trovato.* come prima.

### Cosa **non** è questo test

- Cartella NAS / disco di rete (**B-SEL-CARTELLA** — ancora aperto)
- Pubblicare su Play (**M3**)
- Ritestare i 10 punti CK2 (già CONVALIDATO)

## Istruzioni CK0 per Renato (nessun device)

M2a **non** cambia l’app. La verifica delle tabelle EN è **già fatta**. **S1–S3 SI** 03/09/2026. M2b/M2c codice **fatto**.

## Regole per le istruzioni di test a Renato

Renato non programma e non gestisce branch da riga di comando. Le istruzioni di test devono:

- **Non** richiedere operazioni git manuali.
- Indicare solo **cosa fa Android Studio in automatico** (pull al Run, o `INSTALLA_FAMIGLIA.bat`).
- Descrivere **solo le differenze rispetto al test precedente** (cosa è cambiato, dove guardare).
- Elencare schermata per schermata cosa si vede in EN e cosa ancora in IT (normale, senza allarme).
- Omettere commit SHA o nomi branch nelle istruzioni operative.

---

## Pacchetti autonomi (ordine)

| ID | Contenuto | Checkpoint |
|----|-----------|------------|
| **M0** | `docs/play/store-listing-en.md` | — |
| **M1a** | Infrastruttura Scelta lingua (3.6.6) | — |
| **M1b** | Layout → `strings.xml` + `values-en` | — |
| **M1c** | Copy/Configuration + DialogUtils → risorse | — |
| **M1d** | **Guida rapida in-app** + Premium + testi archivio condiviso + sweep Kotlin | **CK1** |
| **M2a** | Bozza tabelle EN in Nota | **CK0** (prima di M2b) |
| **M2b** | Motore ricerca locale-aware EN | — |
| **M2c** | UI ricerca + test suite EN | **CK2** |
| **M3** | Passaggio in Play della BoxManager ufficiale (sviluppo al posto della 1.2), screenshot, versionCode | Via Console a **test chiuso** |

L’agente esegue **M1a→M1d** in sequenza senza fermarsi, salvo KO test/build.

---

## Sequenza consigliata per l’agente

### Fase 1 — Allineamento (obbligatoria)

1. `git fetch origin`
2. Confrontare `git log --oneline -5 origin/<branch-base>` con HEAD del branch di lavoro. Se il branch di lavoro è indietro, allineare con merge/rebase **prima** di toccare file.
3. Leggere [ASSESSMENT_M.md](ASSESSMENT_M.md) §4–§7 e tabella checkpoint §6.
4. Branch: `git checkout -b cursor/multilingua-<pacchetto>-5409` dalla base di sviluppo concordata (mai da `main`).

### Fase 2 — Esecuzione pacchetto

4. Implementare il pacchetto indicato nel messaggio sessione (M0 / M1a / …).
5. **Non** toccare `domain/search` per EN finché **S1–S3** in [SEMANTICA_EN_EQUIVOCI.md](SEMANTICA_EN_EQUIVOCI.md) non sono SI (tabelle CK0 già verificate). **S1–S3 sono SI** (03/09/2026).
6. Test: `assemblePlayDebug` + `assembleFamigliaDebug` + unit test verdi.
7. Commit, push, PR draft; aggiornare questo file se cambia «prossimo pacchetto».

### Fase 3 — Checkpoint

8. **CK1** solo a fine **M1d** (device: lingua + **Guida intera EN** + flussi core).
9. **CK0** tabelle EN: **verificate**. **S1–S3 SI** (03/09/2026). Motore EN in M2b.
10. **CK2** a fine **M2c** (device: campione domande EN Matrice Test).

---

## Comandi utili

```bash
cd BoxManagerNew
git fetch origin
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
git checkout -b cursor/multilingua-m1a-5409

./gradlew :app:assemblePlayDebug
./gradlew :app:assembleFamigliaDebug
./gradlew :app:testPlayDebugUnitTest
./gradlew :app:testFamigliaDebugUnitTest
```

---

## Branch naming

`cursor/multilingua-<pacchetto>-5409` (es. `cursor/multilingua-m1a-5409`).

---

## Messaggio tipo per **nuova sessione** (copia-incolla)

**Ora:** filone M codice + **CK2 CONVALIDATO** 04/09/2026. M3 solo a test Play chiuso.

```
Continua filone M — inglese in BoxManager da docs/multilingua/PROMPT_CONTINUITA_M.md
Pacchetto: fix ritest promemoria B5.11; CK2 CONVALIDATO; attesa M3 a test Play chiuso
Branch: cursor/promemoria-fix-d69a
Vincoli: non toccare main/1.2; B-SEL-CARTELLA solo con SI prodotto; niente Motore B nuovo.
```

---

## Fuori scope V1

- Lingue oltre IT/EN
- Motore B multilingua (V1 = Motore A)
- Traduzione automatica Play App strings
- Header CSV import bilingue (salvo SI CK0)

---

## Backlog annotato (non perdere)

**Aperti — fonte viva in cima al [PROMEMORIA](../famiglia/PROMEMORIA_INTERVENTI_TRASVERSALI.md):**

| ID | Richiesta Renato | Note |
|----|------------------|------|
| **B-SEL-CARTELLA** | Selettore cartella anche su drive non visti da Android (NAS / disco di rete) | **Aperto** — SAF non elenca SMB da solo |

Storico (chiusi / note agente):

| ID | Richiesta | Note |
|----|-----------|------|
| **B-NOME-APP-BAT** / **B-NOME-AUTO-SAVE** / **B-DEFAULT-IT-EN** / **B-F7-FORMATO-LISTA** / **B-RICERCA-SENZA-SPECIFICHE** / **B-VOCE-OGGETTO** / **M0** | Presa in carico 04/09 | **Fatto** su `cursor/promemoria-backlog-d69a` |
| **B-PALETTE-ACCENT** | Colori card funzione = palette Impostazioni, indipendentemente dalla lingua | Risolto (`ThemeAccentTextViews`) |
| **B-FAMILY-DOMAIN-ERR** | Errori dominio merge archivio condiviso ancora IT in path rari | Opzionale; non bloccano CK1 |

---

## Riferimenti chiusi (non riaprire in sessione M)

- Filone correttivi P0/P1 — [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md)
- Archivio condiviso B0–B5 — CONVALIDATO (funzione di BoxManager, non un’altra app)
