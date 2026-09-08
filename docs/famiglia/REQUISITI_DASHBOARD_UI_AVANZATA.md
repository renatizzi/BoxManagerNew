# Requisiti — Dashboard / UI avanzata (Progetto 2 / D)

**Stato:** **CONVALIDATO** (documento + requisiti) — 08/09/2026.  
**CONVALIDA passo 3 e 4:** SI Renato 08/09/2026 — D-Q1/2/4/5/6 come raccomandato; **D-Q3 = U0**; **D-Q7 = nessun codice**; conferma esplicita «D-Q3: U0 e D-Q7: NO codice».  
**Uso:** preservare i requisiti Nota §4.1.6 senza aprire fetta applicativa.  
**Assessment:** [ASSESSMENT_DASHBOARD_UI_AVANZATA.md](ASSESSMENT_DASHBOARD_UI_AVANZATA.md).  
**STOP codice** su D finché non c’è SI esplicito di apertura (D-Q7).  
**Correlato:** [REQUISITI_QTY_KPI_SEARCH.md](REQUISITI_QTY_KPI_SEARCH.md) (B-QTY-KPI-SEARCH — merito SI; in attesa CONVALIDA documento).

---

## 0. Decisioni congelate (passo 3)

| Voce | Decisione |
|------|-----------|
| Perimetro | Solo bullet Nota; **swipe escluso** (già V1 / fuori V2) |
| Statistiche (se un giorno codice) | Forma prevista **S1** — card conteggi semplici (N box, oggetti, luoghi, categorie usate) |
| UI avanzata | **U0** — nessuna micro-interazione / header dinamico in questa voce |
| Premium | Se si aprisse S1 in futuro → gate Archivio completo |
| Priorità | Dopo A1–A3; più bassa di B–C; **non core** |
| Motore B | **Vietato** usare D per PATTERN_010 / report Motore B |
| Codice ora | **No** — solo recepimento / preservazione requisiti |

Correlato (altra voce): quantità oggetto come chiave KPI/query → **B-QTY-KPI-SEARCH** ([ASSESSMENT](ASSESSMENT_QTY_KPI_SEARCH.md)), non questa D.

---

## 1. Fonte Nota 4.1.6 (elenco intero)

**DASHBOARD AVANZATA** (prio bassa): statistiche · indicatori avanzati.  
**UI AVANZATA** (prio bassa): header dinamico · micro-interazioni; swipe già V1 e fuori V2.

---

## 2. Requisiti funzionali

### R1 — Nessuna implementazione in questa chiusura
Nessuna card KPI, nessun header dinamico, nessuna micro-interazione finché Renato non dà SI di apertura fetta D.

### R2 — Forma S1 (solo se SI futuro)
Se si aprisse il codice: riepilogo numerico semplice in Dashboard (conteggi anagrafici da Room), senza grafici/trend e senza Motore B.

### R3 — U0
Non investire in UI avanzata (header/micro) in Progetto 2 per questa voce.

### R4 — Fuori scope D
Swipe; Motore B; quantità-as-key (→ B-QTY-KPI-SEARCH); Export/Import; Foto; QR; Cestino.

---

## 3. Recepimento ufficiale

In Nota: D resta NON core / prio bassa; swipe fuori V2; nessun obbligo di codice immediato.  
Questo file congela la decisione **zero codice ora**.

### Processo

1. ANALISI — assessment D  
2. FEEDBACK — D-Q + U0 + no codice  
3. CONVALIDA merito — **SI 08/09/2026**  
4. AGGIORNAMENTO DOCUMENTO — **questo file**; **CONVALIDA aggiornamento documento SI 08/09/2026** (D-Q3=U0; D-Q7=NO codice)
