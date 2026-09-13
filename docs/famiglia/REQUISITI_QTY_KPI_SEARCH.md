# Requisiti — Quantità oggetto come chiave KPI / Ricerca avanzata (B-QTY-KPI-SEARCH)

**Stato:** **CONGELATO** 08/09/2026 (SI Renato).  
**CONVALIDA analisi/requisiti (passo 3):** SI Renato 08/09/2026 — «Ok su tutto» = QTY-Q1–Q8 come raccomandato.  
**CONVALIDA aggiornamento documento (passo 4):** SI Renato 08/09/2026 («Convalido»).  
**Uso:** riferimento funzionale fino all’implementazione.  
**Assessment:** [ASSESSMENT_QTY_KPI_SEARCH.md](ASSESSMENT_QTY_KPI_SEARCH.md) (storico; prevale **questo** file).  
**STOP codice** fino a fine test Play / SI «apri fetta codice».  
**Relazione D:** Dashboard/UI resta [REQUISITI_DASHBOARD_UI_AVANZATA.md](REQUISITI_DASHBOARD_UI_AVANZATA.md) (U0, no codice) — **questa voce non apre D**.

---

## 0. Decisioni congelate (passo 3)

| Voce | Decisione |
|------|-----------|
| Ambito | **K1 + K2** (KPI su quantità + query Ricerca avanzata); **K3 no** (quantità resta facoltativa) |
| Null in somme KPI | Trattare come **0** |
| Ricerca «senza quantità» | Distinta da 0 (salvo SI futuro) |
| Ordine funzionale | Prima KPI (K1) + **pochi** pattern Motore B chiari; filtri lista Motore A dopo |
| Default UI se vuoto | **Lasciare vuoto**; KPI usa 0 (niente default 1 al salvataggio) |
| Premium | **Sì** — Archivio completo |
| Catalogo 2.6 | Nessuna frase nuova senza **elenco ufficiale SI prima del codice** |
| Relazione con D | D resta no-code; K1 **non** obbliga ad aprire Dashboard S1 |

---

## 1. Obiettivo

Usare la **quantità** oggetto (`Int?`, oggi facoltativa) come **chiave** per:

1. **K1 — KPI** — indicatori aggregati (pezzi totali, pezzi per luogo/categoria, …) da somma `quantity`.  
2. **K2 — Ricerca avanzata** — nuove query/pattern che ragionano sulla quantità (es. «quanti pezzi…», soglie «più di N…»), restando nella **Pipeline 0–10**.

Non rendere la quantità obbligatoria (K3 escluso).

---

## 2. Requisiti funzionali

### R1 — Campo quantità (QTY-Q4, QTY-Q5)
- Resta **facoltativo** in UI e modello (`Int?`).  
- Campo vuoto → resta `null` (niente default 1 al salvataggio).  
- Lista oggetti: se null, nessuna etichetta quantità (comportamento AS-IS).

### R2 — KPI (K1) (QTY-Q1, QTY-Q2, QTY-Q8)
- Indicatori basati su **somma** delle quantità (es. pezzi totali; breakdown per luogo/categoria se in scope della fetta).  
- In somma: `null` → **0**.  
- Implementazione KPI **non** richiede aprire fetta D (S1); se un giorno D/S1 si apre, i KPI quantità restano di questa voce o si collegano solo con SI.

### R3 — Ricerca avanzata (K2) (QTY-Q1, QTY-Q3)
- Nuove query/pattern su quantità **solo** via Pipeline 0–10 (nessun planner/matching parallelo).  
- Prima fetta: **pochi** pattern Motore B chiari (aggregati / soglie); filtri lista Motore A su quantità **dopo**, con SI di ampliamento.  
- Domanda «senza quantità» ≠ quantità 0, salvo SI.

### R4 — Catalogo 2.6 (QTY-Q7)
- Prima di qualsiasi codice su frasi/query: elenco ufficiale delle frasi (o rinvio Nota) con **SI Renato**.  
- Vietato inventare frasi a memoria.

### R5 — Premium (QTY-Q6)
- KPI e query quantità = gate **Archivio completo** (allineato a ricerca avanzata / Progetto 2).

### R6 — Export / Import
- Colonna quantità già nel tracciato CSV: restare allineati a [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) (validazione tipi/quantità in v2).  
- Nessuna obbligarietà quantità in import (compatibile con R1).

### R7 — Fuori scope
- Riaprire F7–F9 Motore B già CONVALIDATI senza SI.  
- Unità di misura multiple.  
- Vision / foto.  
- Aprire D / S1 «di striscio» per ospitare i KPI.  
- Codice durante test Play (salvo SI «apri fetta codice»).

---

## 3. Sequenza implementativa (quando si apre il codice)

Dopo A1 → A2 → A3 (e dopo eventuale B–C se già in corso); **non** prima di SI apertura.  
Ordine interno suggerito: **K1 (KPI Room)** → elenco frasi 2.6 SI → **K2 (pattern Motore B)** → eventuali filtri lista.

### Processo

1. ANALISI — assessment QTY  
2. FEEDBACK — QTY-Q1–Q8  
3. CONVALIDA merito — **SI 08/09/2026** («Ok su tutto»)  
4. AGGIORNAMENTO DOCUMENTO — **questo file**; **CONVALIDA aggiornamento documento SI 08/09/2026**
