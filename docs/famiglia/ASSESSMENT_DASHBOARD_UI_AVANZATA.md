# Assessment — Dashboard / UI avanzata (Progetto 2 / D)

**Stato:** **CONVALIDATO** (passi 3–4) — SI Renato 08/09/2026. Documento: [REQUISITI_DASHBOARD_UI_AVANZATA.md](REQUISITI_DASHBOARD_UI_AVANZATA.md). Zero codice su D (D-Q7).  
**Decisioni:** D-Q1/2/4/5/6 come raccomandato; **D-Q3 = U0**; **D-Q7 = NO codice**.  
**Presa in carico correlata:** **B-QTY-KPI-SEARCH** → [ASSESSMENT_QTY_KPI_SEARCH.md](ASSESSMENT_QTY_KPI_SEARCH.md) (ANALISI / FEEDBACK).

---

## 1. Fonte ufficiale — elenco intero

### DASHBOARD AVANZATA
| Elemento | Valore |
|----------|--------|
| Priorità | **bassa** |
| Bullet 1 | **statistiche** |
| Bullet 2 | **indicatori avanzati** |

### UI AVANZATA
| Elemento | Valore |
|----------|--------|
| Priorità | **bassa** |
| Swipe tab | **Già in V1**; non si estende alle pagine interne; **fuori dalla roadmap V2** |
| Bullet residui | **header dinamico** · **micro-interazioni** |

### Esplicitamente non V2 applicativa (4.1.6)
Governance AppShell / freeze / matrice — già chiuse o non funzioni utente.  
Elenco file in pagina Backup — **non** in V2.

---

## 2. AS-IS (indizi, non requisiti nuovi)

- Dashboard: accessi rapidi, ricerca per ambito, conteggi/liste base; **non** un pannello KPI evoluto.  
- Dark / palette TopBar già CONVALIDATI (Allegato 4.16).  
- Swipe tra tab principali: già V1.  
- KPI in Motore B / ricerca: Nota distingue KPI aggregati; PATTERN_010 sospeso — **non** mescolare qui senza SI.

---

## 3. Lettura bozza

### Dashboard — «statistiche» / «indicatori avanzati»

| Opzione | Descrizione |
|---------|-------------|
| **S1** | Card riepilogo numeriche semplici (N contenitori, N oggetti, N luoghi, N categorie usate) |
| **S2** | S1 + breakdown leggero (es. oggetti per categoria / contenitori per luogo) — **senza** Motore B nuovo |
| **S3** | KPI evoluti / grafici / trend nel tempo | **Fuori** prima fetta (prio bassa + rischio overlap Motore B) |

**Raccomandazione:** **S1** in prima fetta; S2 solo se SI; S3 no.

### UI — header dinamico / micro-interazioni

| Opzione | Descrizione |
|---------|-------------|
| **U0** | **Niente** in Progetto 2 applicativo (swipe già chiuso; Dark già fatto) — solo preservare requisiti |
| **U1** | Header di pagina con sottotitolo contestuale leggero (conteggio risultati / filtro attivo) dove ancora statico |
| **U2** | Micro-interazioni (animazioni, feedback haptics extra) | Rischio “ornamento”; prio bassa |

**Raccomandazione:** **U0** o al massimo **U1** mirato; **U2 fuori**. Swipe: **non riaprire**.

---

## 4. Impatto (se S1)

| Area | Invasività |
|------|------------|
| Dashboard layout | Bassa–media (card conteggi da query Room esistenti) |
| Motore B / Pipeline | **Nessuna** se solo conteggi anagrafici |
| Room | Nessuna migrazione |
| Premium | Da SI (D-Q4) |

---

## 5. Checklist FEEDBACK — CONVALIDATA (passo 3, 08/09/2026)

| # | Domanda | Esito |
|---|---------|--------|
| **D-Q1** | Perimetro; swipe escluso | **SI** |
| **D-Q2** | Statistiche S1 (forma se un giorno codice) | **SI — S1** |
| **D-Q3** | UI | **SI — U0** |
| **D-Q4** | Premium se S1 futuro | **SI** |
| **D-Q5** | Priorità bassa / dopo A1–A3 | **SI** |
| **D-Q6** | No Motore B in D | **SI** |
| **D-Q7** | Codice ora | **SI — nessun codice**; preservare requisiti |
| **B-QTY-KPI-SEARCH** | Quantità → KPI / query | **Presa in carico** → assessment dedicato (non D) |

---

## 6. Esito — D chiuso (documento + no codice); filone quantità in FEEDBACK

**D:** U0 + no codice; S1 solo come forma futura se SI apertura.  
**Documento D:** [REQUISITI_DASHBOARD_UI_AVANZATA.md](REQUISITI_DASHBOARD_UI_AVANZATA.md) — **CONGELATO**.  
**Attivo:** [ASSESSMENT_QTY_KPI_SEARCH.md](ASSESSMENT_QTY_KPI_SEARCH.md) — FEEDBACK QTY-Q1–Q8.
