# Assessment — Dashboard / UI avanzata (Progetto 2 / D)

**Stato:** ANALISI (bozza) — 08/09/2026. **Non CONVALIDATO.** Zero codice.  
**Processo:** passo 1 di 4.  
**Posizione:** dopo A1–A3 e B–C (requisiti congelati). Codice P2 in STOP fino a fine test Play.  
**Fonte:** Nota **9.2 §4.1.6** — **DASHBOARD AVANZATA** e **UI AVANZATA** (Priorità: **bassa**).  
**Premium:** da FEEDBACK (Progetto 2 finora = Archivio completo; queste voci sono priorità bassa / non core — conferma se gate uguale).

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

## 5. Checklist FEEDBACK (D-Q)

| # | Domanda | Raccomandazione |
|---|---------|-----------------|
| **D-Q1** | Perimetro = solo bullet Nota (statistiche, indicatori; header/micro)? | **Sì**; swipe **escluso** (già V1 / fuori V2) |
| **D-Q2** | Statistiche: S1 / S2 / S3 | **S1** |
| **D-Q3** | UI: U0 / U1 / U2 | **U0** (o U1 se vuoi un tocco minimo) |
| **D-Q4** | Premium? | **Sì** se si implementa qualcosa; se U0+nessuna card → n/a |
| **D-Q5** | Priorità vs B–C / A1–A3 | **Dopo** A1–A3 in codice; B–C non core — D ancora più basso; analisi ora ok |
| **D-Q6** | Overlap Motore B / KPI ricerca | **Vietato** riusare questa voce per PATTERN_010 / report Motore B |
| **D-Q7** | Vale la pena una fetta dedicata o solo “preservare in Nota”? | Se S1+U0: **fette minima** o addirittura **solo recepimento Nota senza codice** finché non c’è bisogno utente — da SI |

---

## 6. Esito ANALISI — in attesa FEEDBACK

Ipotesi: Dashboard = conteggi semplici (S1); UI avanzata = **non** investire (U0) salvo header minimo.  
**Prossimo:** FEEDBACK **D-Q1–D-Q7**. Zero codice.
