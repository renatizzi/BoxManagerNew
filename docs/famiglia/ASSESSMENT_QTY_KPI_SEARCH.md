# Assessment — Quantità oggetto come chiave KPI / Ricerca avanzata (B-QTY-KPI-SEARCH)

**Stato:** CONVALIDA analisi/requisiti (passo 3) **SI Renato 08/09/2026** («Ok su tutto» = QTY-Q1–Q8 come raccomandato). Documento: [REQUISITI_QTY_KPI_SEARCH.md](REQUISITI_QTY_KPI_SEARCH.md) — in attesa CONVALIDA aggiornamento documento. Zero codice.  
**Processo:** passo **4** in corso.  
**ID Promemoria:** **B-QTY-KPI-SEARCH**.  
**Testo Renato (fonte):** convertire la **quantità** oggetti (ora **facoltativa**) in **«chiave»** per **nuovi KPI** e **nuove query** Ricerca avanzata.  
**Policy codice:** STOP fino a fine test Play / SI «apri fetta codice».

---

## 0. AS-IS (verificato in codice)

| Voce | Stato attuale |
|------|----------------|
| Modello | `Object.quantity: Int?` / `ObjectEntity.quantity: Int?` — **nullable** |
| UI lista | Se `quantity == null` non mostra etichetta quantità (`ObjectAdapter`) |
| Form | quantità opzionale in insert/update |
| Import CSV | colonna quantità già nel tracciato |
| Ricerca | Pipeline 0–10; Motore A → lista Contenitori; Motore B → risposta in pagina |
| Dashboard D | CONVALIDATO no-code; S1 senza quantità-as-key |
| PATTERN_010 / KPI aggregati ricerca | sospesi / fuori primo Motore B V1 |

---

## 1. Letture «chiave» (chiuse in CONVALIDA)

| Lettura | Esito |
|---------|--------|
| **K1 — KPI** | **In scope** |
| **K2 — Query Ricerca avanzata** | **In scope** |
| **K3 — Obbligatorietà quantità** | **Fuori** prima fetta |

---

## 2. Checklist FEEDBACK — CONVALIDATA (passo 3, 08/09/2026)

| # | Domanda | Esito |
|---|---------|--------|
| **QTY-Q1** | Ambito | **K1+K2**; K3 no |
| **QTY-Q2** | Null in somme KPI | **Come 0**; ricerca «senza quantità» ≠ 0 |
| **QTY-Q3** | Ricerca | Prima KPI + pochi pattern Motore B; filtri lista dopo |
| **QTY-Q4** | Obbligatoria? | **No** |
| **QTY-Q5** | Default UI | **Lasciare vuoto**; KPI usa 0 |
| **QTY-Q6** | Premium | **Sì** |
| **QTY-Q7** | Catalogo 2.6 | Elenco SI **prima** del codice |
| **QTY-Q8** | Relazione D | D resta no-code; K1 non apre D |

---

## 3. Fuori scope

- Riaprire F7–F9 senza SI. Vision/foto. Multi-unità. Codice in test Play. Aprire D S1 per i KPI.

---

## 4. Esito

**Merito chiuso.** Documento: [REQUISITI_QTY_KPI_SEARCH.md](REQUISITI_QTY_KPI_SEARCH.md) — attendere **CONVALIDA aggiornamento documento**.  
**Zero codice.**
