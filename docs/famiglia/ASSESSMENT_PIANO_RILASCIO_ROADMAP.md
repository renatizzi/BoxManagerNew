# Assessment — Piano rilascio / Play Console / Roadmap / Premium / Nota (RP)

**Stato:** **CONVALIDATO** (passi 3–4) — SI Renato 08/09/2026. Documento: [REQUISITI_PIANO_RILASCIO_ROADMAP.md](REQUISITI_PIANO_RILASCIO_ROADMAP.md) (**CONGELATO**). Zero codice.  
**Processo:** chiuso.  
**ID:** **B-PIANO-RILASCIO-RP**.  
**Richiesta Renato:** piano su (1) rilascio già implementate, (2) Play Console, (3) Roadmap, (4) priorità/premium, (5) verifica Nota.  
**Vincolo:** SI codice P2 a fine test Play.  
**Prossimo:** recepimento Nota/Roadmap solo con SI esecuzione patch.

---

## 0. Distinzione obbligatoria (AS-IS)

| Classe | Cosa include oggi | Dove sta |
|--------|-------------------|----------|
| **I — Già in codice sviluppo** | Archivio condiviso, EN (M1–M2), Disco di rete, correttivi, B5.24 CSV, Motore B F7–F9, Dark, … | Flavor `famiglia` `1.3-famigliaB5.24` / vc 1343; **non** su Play durante il test |
| **II — Solo analisi congelata** | A1 QR, A2 Cestino, A3 Foto, B–C, D (U0), B-QTY | `docs/famiglia/REQUISITI_*.md` |
| **III — Play attuale** | BoxManager **1.2** su `main` | Test chiuso; freeze salvo bloccanti |

Strategia Fase C: a fine test lo sviluppo **sostituisce** la 1.2 ([STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md)).

---

## 1–5. Sintesi analisi (pre-CONVALIDA)

- **Rilascio:** R1 M3 per classe I; R2 versioni successive per classe II.  
- **Console:** C1–C8 a M3; C9–C12 per ogni release P2.  
- **Roadmap/Nota:** gap su Cestino, Foto, QTY, dettaglio A1/B–C/D, Allegato 4.21 assente in 9.2; Disco/4.19/4.20 ok.  
- **Priorità:** M3 → A1→A2→A3→B–C; D no; B-QTY dopo.  
- **Premium:** linea P2 = Archivio completo; base free 4.19 invariata; nessuna eccezione.

Dettaglio operativo congelato in [REQUISITI_PIANO_RILASCIO_ROADMAP.md](REQUISITI_PIANO_RILASCIO_ROADMAP.md).

---

## 6. Checklist FEEDBACK — CONVALIDATA (passo 3, 08/09/2026)

| # | Domanda | Esito |
|---|---------|--------|
| **RP-Q1** | M3 unico cutover (R1) | **SI** |
| **RP-Q2** | P2 in più versioni dopo (R2) | **SI** |
| **RP-Q3** | Sessione M3 Console C1–C8 | **SI** |
| **RP-Q4** | Patch Nota/Roadmap prima di nuovo filone | **SI** |
| **RP-Q5** | Priorità M3→A1→A2→A3→B–C; D no; B-QTY dopo | **SI** |
| **RP-Q6** | Linea P2 = premium Archivio completo | **SI** |
| **RP-Q7** | Eccezioni premium | **Nessuna** |
| **RP-Q8** | Prossimo lavoro = recepimento Nota (no feature) | **SI** |

---

## 7. Esito

**Documento chiuso.** [REQUISITI_PIANO_RILASCIO_ROADMAP.md](REQUISITI_PIANO_RILASCIO_ROADMAP.md) — **CONGELATO**.  
**Prossimo:** CONVALIDA aggiornamento Nota 9.2 (Allegati 4.21+4.23, quadro 08/09). **Zero codice applicativo.**
