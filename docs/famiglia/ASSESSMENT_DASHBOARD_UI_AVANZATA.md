# Assessment — Dashboard / UI avanzata (Progetto 2 / D)

**Stato:** FEEDBACK parziale — 08/09/2026. **Non CONVALIDATO** (mancano D-Q3 e D-Q7). Zero codice.  
**Processo:** passo **2** di 4.  
**Posizione:** dopo A1–A3 e B–C (requisiti congelati). Codice P2 in STOP fino a fine test Play.  
**Fonte:** Nota **9.2 §4.1.6** — **DASHBOARD AVANZATA** e **UI AVANZATA** (Priorità: **bassa**).  
**Annotazione correlata (fuori D):** [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md) **B-QTY-KPI-SEARCH** — quantità oggetto come chiave KPI / query ricerca (non Motore B in questa voce: D-Q6).

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

## 5. Checklist FEEDBACK (D-Q) — parziale 08/09

| # | Domanda | Esito |
|---|---------|--------|
| **D-Q1** | Perimetro Nota; swipe escluso | **SI** |
| **D-Q2** | Statistiche S1 | **SI — S1** |
| **D-Q3** | UI U0 / U1 / U2 | **Aperto** — raccomandazione U0 (alt. U1) |
| **D-Q4** | Premium se si implementa S1 | **SI** |
| **D-Q5** | Dopo A1–A3; prio più bassa | **SI** |
| **D-Q6** | No Motore B / PATTERN_010 in D | **SI** |
| **D-Q7** | Fetta minima S1 vs solo Nota senza codice | **Aperto** |
| **B-QTY-KPI-SEARCH** | Quantità → chiave KPI / query ricerca | **Annotato** in Promemoria — **fuori** da D (vedi D-Q6); ANALISI dedicata solo con SI |

---

## 6. Esito — FEEDBACK incompleto

Chiusi: D-Q1, D-Q2 (S1), D-Q4, D-Q5, D-Q6.  
**Servono ancora:**

- **D-Q3:** **U0** (niente UI avanzata) oppure **U1** (header contestuale minimo)?  
- **D-Q7:** **(a)** fetta minima con card S1 in Dashboard, oppure **(b)** solo recepire in Nota / preservare requisiti **senza** codice finché non serve?

Poi CONVALIDA merito → documento requisiti. **Zero codice.**
