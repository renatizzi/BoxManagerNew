# Prompt di continuità — Filone M (Multilingua IT/EN)

**SI Renato, 01/09/2026** — mini-assessment approvato.  
**Ingresso unico** per sessioni agente sul multilingua. Non confondere con [PROMPT_CONTINUITA_CORRETTIVI.md](../famiglia/PROMPT_CONTINUITA_CORRETTIVI.md).

---

## Stato

| Voce | Valore |
|------|--------|
| **Assessment** | [ASSESSMENT_M.md](ASSESSMENT_M.md) |
| **Lingue V1** | Italiano (default) + English |
| **Play Console traduzione app** | **Non usare** — lavoro in Cursor |
| **Ricerca avanzata** | Filone **M2** separato — Nota ufficiale prima del codice |
| **Timing** | Preferibile **dopo** test Play verde; M0 listing opzionale anytime |
| **Checkpoint umani** | **3** soltanto: CK0, CK1, CK2 (vedi assessment §6) |

---

## Regole non negoziabili

1. **Fonti ufficiali:** alias, messaggi 2.6, matrice indicatori EN → importare da Nota/Excel, non tradurre a memoria.
2. **Pipeline 0–10:** invariata; solo input locale-aware (`pipeline-ufficiale.mdc`).
3. **Dati utente:** nomi archivio non tradotti.
4. **Test Play aperto:** branch M dedicato; merge `main` solo con SI / post-test.
5. **CK0 obbligatorio** prima del primo commit che tocca `domain/search` per EN.

---

## Pacchetti autonomi (ordine)

| ID | Contenuto | Checkpoint |
|----|-----------|------------|
| **M0** | `docs/play/store-listing-en.md` | — |
| **M1a** | Infrastruttura Scelta lingua (3.6.6) | — |
| **M1b** | Layout → `strings.xml` + `values-en` | — |
| **M1c** | Copy/Configuration + DialogUtils → risorse | — |
| **M1d** | **Guida rapida in-app** (`QuickStartGuideCopy`, topbar «Guida») + Premium + Famiglia + sweep Kotlin | **CK1** |
| **M2a** | Bozza tabelle EN in Nota | **CK0** (prima di M2b) |
| **M2b** | Motore ricerca locale-aware EN | — |
| **M2c** | UI ricerca + test suite EN | **CK2** |
| **M3** | Screenshot Play, merge main, release | SI prodotto |

L’agente esegue **M1a→M1d** in sequenza senza fermarsi, salvo KO test/build.

---

## Comandi utili

```bash
cd BoxManagerNew
git fetch origin
git checkout cursor/family-unione-unificata-e5b5   # o branch M dedicato
git pull

# Dopo M1/M2
./gradlew :app:assemblePlayDebug
./gradlew :app:assembleFamigliaDebug
./gradlew :app:testPlayDebugUnitTest
./gradlew :app:testFamigliaDebugUnitTest
./gradlew :app:testPlayDebugUnitTest --tests "com.example.boxmanagernew.search.*"
```

---

## Branch naming

`cursor/multilingua-<pacchetto>-5409` (es. `cursor/multilingua-m1a-5409`).

---

## Messaggio tipo sessione

```
Continua filone M da docs/multilingua/PROMPT_CONTINUITA_M.md
Pacchetto: M1b
Leggi ASSESSMENT_M.md §4–§6
CK0 non ancora richiesto (no domain/search EN)
```

---

## Guida rapida in-app (non dimenticare)

La **Guida** da topbar (`QuickStartGuideCopy` / `QuickStartGuideActivity`) fa parte di **M1 obbligatorio**, non è opzionale né posticipabile a M2. Include §8 famiglia su flavor `famiglia`. CK1 deve coprire lettura completa in EN.

## Fuori scope V1

- Lingue oltre IT/EN
- Motore B multilingua (V1 = Motore A)
- Traduzione automatica Play App strings
- Header CSV import bilingue (salvo SI CK0)
