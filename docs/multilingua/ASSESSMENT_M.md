# Assessment filone M — inglese in BoxManager

**Data:** 01/09/2026  
**SI Renato:** mini-assessment approvato (sessione continuità)  
**Obiettivo prodotto:** BoxManager in **italiano + inglese** (non 29 lingue in V1). Non è un’altra app.  
**Strumento:** **Cursor** (repo + Nota ufficiale); Play Console solo canale publish a test chiuso  
**Riferimento prodotto:** Nota Integrata **3.6.6** Scelta lingua (Impostazioni — «Prossime implementazioni»)  
**Timing:** in **parallelo** al test Play 1.2 (SI 01/09/2026). La 1.2 su `main` resta identica; a test chiuso questa BoxManager **sostituisce** la 1.2. Non mescolare le sessioni con i correttivi P2.

---

## 1. Riepilogo esecutivo

| Domanda | Risposta |
|---------|----------|
| Play Console traduce l’app? | Solo `strings.xml` nel bundle; **non** Kotlin, **non** ricerca avanzata |
| Basta listing EN su Play? | Aiuta **scoperta**, non **uso** |
| IT + EN è realistico? | **Sì**, in **due strati** separati |
| Ricerca avanzata = traduzione UI? | **No** — motore linguistico con tabelle ufficiali (Nota 1.3.3, 2.6, matrice indicatori) |
| Dove si lavora? | **Tutto in Cursor**; checkpoint umani **3** (vedi §6) |

**Ordine obbligatorio:** infrastruttura + UI (M1) → estensione Nota EN + motore ricerca (M2).  
**Vietato:** tradurre a memoria gli alias italiani; importare elenco intero da fonte ufficiale aggiornata.

---

## 2. Stato attuale (analisi codebase)

### 2.1 Panoramica numerica

| Strato | Dove | Volume indicativo | Localizzabile con `strings.xml` solo? |
|--------|------|-------------------|----------------------------------------|
| Layout XML | `app/src/main/res/layout/` | **86** `android:text` hardcoded in **23** file | Sì (dopo estrazione) |
| Risorse esistenti | `values/strings.xml` | **~32** righe | Già lì |
| Letterali Kotlin | `app/src/main/java/**/*.kt` | **~1277** match `"..."` (include log, chiavi, formati) | **No** — refactor |
| Titoli activity | Kotlin `title = "..."` | **~27** | Refactor |
| Oggetti *Copy* / *Configuration* | vedi §2.3 | **~15** file centrali + DialogUtils | Refactor mirato |
| Dominio ricerca | `domain/search/` | **62** file Kotlin | **Locale-aware**, non stringhe |
| Alias Core ufficiali | `SearchCoreAliases.kt` | **~149** termini IT | Tabella EN **separata** in Nota |
| Test ricerca | `app/src/test/.../search/` | **102** `@Test` (pipeline ~2125 righe solo OfficialPipeline) | Duplicare/estendere per EN |
| Flavor famiglia | `FamilyMergeCopy`, ecc. | ~73 righe copy famiglia | Come UI (M1), dopo o con M1 |

**Conclusione:** l’app è **italiano-centrica by design**; meno del **5%** del testo utente passa oggi da `strings.xml`.

### 2.2 Cosa NON va tradotto (dati utente)

Restano nella lingua scelta dall’utente al censimento:

- Nomi **contenitori**, **oggetti**, **categorie**, **posizioni**
- File CSV import/export (header italiani nel modello ufficiale `Modello_Importazione.csv` — decisione separata se header bilingue)
- QR payload / ID permanenti

La **Scelta lingua** (3.6.6) riguarda **UI + motore ricerca**, non riscrive l’archivio.

### 2.3 Inventario testi per area (priorità M1)

#### A — Catalogo 2.6 (vincolante, importare non riformulare)

| File | Ruolo | Note EN |
|------|-------|---------|
| `SearchConfiguration.kt` | Messaggi ricerca interrogativa | **M2** — duplicato EN da Nota 2.6 EN |
| `QrConfiguration.kt` | Testi stampa QR 3.4.4 | M1 o M2 (fuori ricerca pipeline) |

#### B — Copy prodotto (non 2.6, traducibili in M1)

| File | Righe indicative | Contenuto |
|------|------------------|-----------|
| **`QuickStartGuideCopy.kt`** | **~78 stringhe** | **Guida rapida in-app** (topbar «Guida») — **obbligatoria M1**: titoli, intro, 7 sezioni CONFIG/CENSUS/USAGE, esempio CSV, footer premium, **sezione archivio condiviso** se `FAMILY_BETA` |
| `ArchivioCompletoCopy.kt` | ~208 | Premium / prova / paywall |
| `FamilyMergeCopy.kt` | ~73 | Condividi Archivio (visibile se `FAMILY_BETA`) |
| `PrivacyPolicy.kt` | ~26 | Privacy (Play) |
| `ViewOutputConfiguration.kt` | titoli stampa/export | M1 |
| `BackupConfiguration.kt` | messaggi backup/restore | M1 |
| `ImportConfiguration.kt` | messaggi import + report | M1 (header CSV = decisione CK0 opzionale) |
| `StorageFolderConfiguration.kt` | messaggi cartella | M1 |
| `DialogUtils.kt` | ~575 | Dialoghi condivisi (nome file, SI/NO, delete…) |

#### C — Layout (M1b)

Tutti i file in `res/layout/` con testo inline: dashboard, utility, settings, box detail, search, import, restore, dialog_*, bottom nav, topbar.

#### D — Kotlin sparso (M1c)

Activity e ViewModel con messaggi one-off (`Toast`, `appendLine` report, placeholder search card in `SearchResultActivity`, ecc.) — grep `"` per sweep finale.

### 2.4 Ricerca avanzata — perché è un filone a sé (M2)

Componenti **intrinsecamente linguistici** (non derivabili da Gemini Play):

| Componente | Italiano oggi | Lavoro EN |
|------------|---------------|-----------|
| `SearchCoreAliases` | 4× set ufficiali Nota 1.3.3 | **Elenco EN ufficiale** (Excel/Nota) — ~4 righe Core + espansioni |
| `SearchLexicalIndicatorMatrix` | `dove`, `quali`, `tutti`, `uguale`, … | Set EN (`where`, `which`, `all`, `same`, …) da Nota |
| `SearchNormalizer` | Contrazioni `dov'è`, `cos'è`, … | Regole EN (`what's`, `where's`, …) se previste in Nota |
| `SearchNameMatcher` / `SearchEngineA` | Stopwords + imperativi IT (`trova`, `cerca`, …) | Set EN (`find`, `search`, …) |
| `SearchSatisfiabilityEvaluator` | Termini confronto | Allineamento matrice |
| `SearchF7Pattern` / `SearchF8Pattern` | **Varianti domanda intere IT** (5+5) | Variant EN da Matrice Test Ricerca |
| `SearchConfiguration` | Catalogo 2.6 | Messaggi EN catalogo 2.6 |
| `GlobalSearchDispatcher` + pipeline 0–10 | Invariante | **Locale** in input a normalizer/alias |

**Partial EN oggi:** in `boxTerms` esistono già `box`, `container`, `containers`, `cover`, … — insufficiente per domande naturali EN.

**Test:** `SearchOfficialPipelineTest` (~2125 righe) è la rete di sicurezza; M2 richiede **`SearchOfficialPipelineTestEn`** o parametrizzazione locale — stessa copertura casi Nota, lingua EN.

**Regola workspace:** prima di codice alias/messaggi EN → aprire artefatto ufficiale (Nota / Excel progetto) e recepire **elenco intero** (`.cursor/rules/fonti-ufficiali.mdc`, `pipeline-ufficiale.mdc`).

### 2.5 Due flavor Gradle, una sola app

| Aspetto | Regola M |
|---------|----------|
| Scelta lingua | **Comune** (Impostazioni, entrambi i flavor) |
| Copy archivio condiviso (`FamilyMergeCopy`) | Localizzare in M1; visibile se `FAMILY_BETA` |
| Rapporto con Play 1.2 | Durante il test: l’inglese **non** entra in 1.2. A test chiuso: questa BoxManager **è** l’ufficiale. [STRATEGIA_UNIFICAZIONE.md](../famiglia/STRATEGIA_UNIFICAZIONE.md) |
| Branch lavoro | `cursor/multilingua-m*-5409` dalla base di sviluppo — **mai** da `main` durante il test |

### 2.6 Play Console — valore effettivo per BoxManager

| Servizio Console | Utilità BoxManager | Cursor |
|------------------|-------------------|--------|
| Store listing Gemini | Bassa–media (testo scheda) | **Preferito:** `docs/play/store-listing-en.md` |
| App strings Gemini | **Nulla finché M1 non finisce** | Estrazione + `values-en` |
| Traduzione ricerca | **Impossibile** | Unico percorso |

---

## 3. Architettura target (IT + EN)

```
┌─────────────────────────────────────────────────────────────┐
│ LocalePreference (Impostazioni 3.6.6)                        │
│   it | en  →  AppCompatDelegate / attachBaseContext         │
└───────────────────────────┬─────────────────────────────────┘
                            │
         ┌──────────────────┴──────────────────┐
         ▼                                      ▼
┌─────────────────────┐              ┌─────────────────────┐
│ UI: strings.xml     │              │ SearchLocaleContext │
│ values / values-en  │              │ alias + matrix +    │
│ + Copy → @string    │              │ normalizer + 2.6    │
└─────────────────────┘              └─────────────────────┘
```

**Default:** italiano (comportamento attuale).  
**Switch:** persistito in `SharedPreferences` (chiave da allineare a Nota 3.6.6).

---

## 4. Piano di lavoro — pacchetti autonomi

Ogni pacchetto è **chiudibile dall’agente** senza intervento Renato, salvo i **checkpoint** in §6.

### M0 — Scheda Play EN (opzionale, zero codice)

| | |
|-|-|
| **Deliverable** | `docs/play/store-listing-en.md` (titolo, short, full description, release notes template) |
| **Autonomia** | Totale |
| **Test** | Revisione testuale Renato opzionale (non blocca M1) |
| **Durata relativa** | Piccola |

---

### M1 — UI bilingue + Scelta lingua

#### M1a — Infrastruttura locale

| | |
|-|-|
| **Scope** | `LocaleManager` / preferenza; voce Impostazioni; applicazione locale a Activity; default IT |
| **File tipici** | `SettingsActivity`, `BaseActivity`, nuovo `LocalePreference` |
| **Test** | Unit: persistenza + resolve locale; smoke: label Impostazioni cambia |
| **Non toccare** | Pipeline ricerca |

#### M1b — Estrazione layout → `strings.xml`

| | |
|-|-|
| **Scope** | **86** testi layout → `@string/`; creare `values-en/strings.xml` (traduzione EN) |
| **Ordine** | bottom nav → topbar → dashboard → flussi Utility (backup/import/restore) → CRUD |
| **Test** | Layout lint; snapshot manuale IT/EN |

#### M1c — Estrazione Copy/Configuration + DialogUtils

| | |
|-|-|
| **Scope** | Spostare testi utente da §2.3 B in risorse; Kotlin usa `getString(R.string....)` |
| **Escluso** | `SearchConfiguration`, `SearchCoreAliases`, matrici ricerca (M2) |
| **Test** | `:app:testPlayDebugUnitTest` + `:app:testFamigliaDebugUnitTest` verdi |

#### M1d — Guida rapida in-app + Premium + archivio condiviso + sweep

| | |
|-|-|
| **Scope** | **`QuickStartGuideCopy` per intero** (guida «online» in-app da topbar — IT/EN); `ArchivioCompletoCopy`, `FamilyMergeCopy`; Kotlin residuo |
| **Nota** | La guida **non** è catalogo 2.6: traduzione prodotto con revisione Renato; aggiornare `QuickStartGuideCopyTest` se cambiano conteggi sezioni |
| **Test** | Topbar **Guida** → tutte le sezioni leggibili in EN; sezione archivio condiviso visibile se `FAMILY_BETA`; footer Archivio completo coerente |

**Esito M1:** app navigabile in EN; ricerca avanzata **ancora solo IT** (messaggio chiaro se locale EN? — opzionale: banner «advanced search Italian only until M2»).

---

### M2 — Ricerca avanzata EN

#### M2a — Documentazione ufficiale EN (blocco legale/prodotto)

| | |
|-|-|
| **Scope** | Allegato / tabella Excel: alias Core EN, indicatori, messaggi 2.6 EN, varianti F7/F8 EN |
| **Output** | [BOZZA_TABELLE_EN_CK0.md](BOZZA_TABELLE_EN_CK0.md) + sidecar Nota Allegato **4.21** — **prerequisito codice** |
| **Agente** | Bozza 1:1 da IT ufficiale (03/09/2026). **CK0** tabelle verificate. **S1–S3 SI** 03/09/2026. |

#### M2b — Implementazione motore EN

| | |
|-|-|
| **Scope** | `SearchLocale` + repository alias/matrix per `en`; normalizer EN; `SearchConfiguration` locale-aware |
| **Vincolo** | Pipeline 0–10 invariata; stessi test logici, input EN |
| **Test** | `SearchCoreAliasesTest`, `SearchOfficialPipelineTest` estesi; campione EN 0–10 |
| **Stato** | **Fatto** 03/09/2026 |

#### M2c — UI ricerca + messaggi runtime

| | |
|-|-|
| **Scope** | Hint, placeholder, card `GlobalSearchActivity`, messaggi dispatcher in EN |
| **Test** | E2E manuale campione Matrice Test Ricerca EN |
| **Stato** | **Fatto** 03/09/2026 — messaggi 2.6 sul locale UI; `SearchUiLocaleTest`. **CK2** attende SI device |

---

### M3 — Consolidamento (post M1+M2)

| | |
|-|-|
| **Scope** | A test Play **chiuso**: la BoxManager di sviluppo **sostituisce** la 1.2 come ufficiale; screenshot EN; versionCode |
| **Dipende** | Fine test + via Console (non un secondo «SI vuoi l’inglese in Play?») |

---

## 5. Rischi e mitigazioni

| Rischio | Mitigazione |
|---------|-------------|
| Traduzione ad hoc alias EN | CK0 + regola fonti-ufficiali |
| Regressioni pipeline IT | Test IT invariati; locale esplicito nei test |
| Dimenticare stringhe in Kotlin | Checklist grep `"[A-ZÀ-9]` post M1 |
| Scope creep (29 lingue) | **Fuori scope** V1 — solo IT/EN |
| Conflitto con test Play | M1/M2 su branch dedicato. `main` / 1.2 identica fino a fine test, salvo bug bloccanti. Poi lo sviluppo **è** l’ufficiale. |
| CSV / import header italiani | Decidere in CK0 se header restano IT (consigliato V1) |

---

## 6. Checkpoint umani (solo 3)

L’agente lavora in autonomia **tra** un checkpoint e l’altro.

| ID | Quando | Cosa chiediamo a Renato | Blocca |
|----|--------|-------------------------|--------|
| **CK0** | Prima del **primo commit M2b codice** (motore ricerca) | Tabelle EN verificate su delega; **SI procedere** 03/09/2026. **S1–S3 SI** 03/09/2026. | Motore EN |
| **CK1** | M1 completo (M1a–M1d) | **SI device 03/09/2026** — CONVALIDATO | — (sbloccato M2a / CK0) |
| **CK2** | M2 completo | **SI device**: campione **10 domande EN** da Matrice Test (stessi esiti attesi che IT) | Chiusura filone M |

**M0** e lavoro interno M1a–M1c **non** richiedono checkpoint intermedî.

---

## 7. Criteri di done

### M1 done
- [x] Impostazioni → Scelta lingua IT/EN persistente
- [x] **Guida rapida in-app (`QuickStartGuideActivity`) tradotta per intero**, inclusa la sezione archivio condiviso se `FAMILY_BETA`
- [x] Nessun testo utente visibile solo in italiano nelle schermate core (dashboard, contenitori, categorie, utility, impostazioni)
- [x] Test unitari play + famiglia verdi
- [x] CK1 **SI Renato** (device 03/09/2026)

### M2 done
- [x] Domande EN attraversano pipeline 0–10 come IT
- [x] Suite test ricerca EN verde (`SearchOfficialPipelineEnTest` + `SearchUiLocaleTest`)
- [ ] CK2 **SI Renato** (device — istruzioni in [PROMPT_CONTINUITA_M.md](PROMPT_CONTINUITA_M.md))
- [x] Nota ufficiale aggiornata e referenziata in repo (Allegato 4.21 + bozza CK0)
- [x] M2a bozza tabelle EN **verificata in EN** su delega Renato — 03/09/2026
- [x] S1–S3 **SI** 03/09/2026 (niente traduttore EN→IT; rumore fase 1 `in order to`; non strippare `type of`)
- [x] M2c UI messaggi ricerca sul locale Impostazioni

---

## 8. Sequenza consigliata vs filone correttivi

| Periodo | Filone |
|---------|--------|
| **Ora (test Play 1.2 aperto)** | Codice M **chiuso** (M1+M2). Attesa **CK2** device. La 1.2 su `main` **non si tocca**. A fine test questa linea **sostituisce** la 1.2. |
| **Parallelo** | M0 listing EN (doc) se utile |
| **Segnalazioni tester Play** | Solo se **bloccanti** → aggiornamento **1.2** (`main`). Sessione [PROMPT_CONTINUITA_CORRETTIVI](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md) |
| **Fine test Play** | La BoxManager di sviluppo **diventa** l’ufficiale e sostituisce la 1.2. Via Console in sessione dedicata. |
| **Non fare** | Funzioni nuove su `main` durante il test. Parlare di «app famiglia» / «app multilingue». Chiedere «SI per merge M su main». Tenere la 1.2 come ufficiale dopo il test. |

---

## 8b. Chiusura sessione pianificazione (01/09/2026)

**SI Renato:** piano M approvato; Guida rapida in-app esplicita in M1/CK1; prossima sessione = **filone M** (messaggio in PROMPT_CONTINUITA_M). Ritocchi guida (semplificazione copy IT) possono arrivare da Renato entro M1d.

---

## 9. Riferimenti

| Documento | Ruolo |
|-----------|--------|
| [PROMPT_CONTINUITA_M.md](PROMPT_CONTINUITA_M.md) | Ingresso sessione agente filone M |
| [BOZZA_TABELLE_EN_CK0.md](BOZZA_TABELLE_EN_CK0.md) | Bozza EN 1:1 — **CK0** |
| [../famiglia/STRATEGIA_UNIFICAZIONE.md](../famiglia/STRATEGIA_UNIFICAZIONE.md) | Fase C — filone M |
| [../famiglia/PROMEMORIA_INTERVENTI_TRASVERSALI.md](../famiglia/PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Tracking filoni paralleli |
| `.cursor/rules/fonti-ufficiali.mdc` | Import elenchi, non riassumere |
| `.cursor/rules/pipeline-ufficiale.mdc` | Sequenza 0–10 invariata |
| Nota Integrata 3.6.6 | Scelta lingua UI |
| Nota 1.3.3 / Excel Matrice | Alias e indicatori |

---

## 10. Messaggio tipo per avviare M1 (dopo SI timing)

```
Filone M — inglese in BoxManager da docs/multilingua/PROMPT_CONTINUITA_M.md
Pacchetto: M1a (infrastruttura Scelta lingua)
Branch: cursor/multilingua-m1a-5409
Una sola app: BoxManager. Non toccare domain/search finché CK0 non è chiuso per M2.
```
