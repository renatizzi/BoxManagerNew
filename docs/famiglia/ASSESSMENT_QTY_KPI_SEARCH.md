# Assessment — Quantità oggetto come chiave KPI / Ricerca avanzata (B-QTY-KPI-SEARCH)

**Stato:** ANALISI completa → **FEEDBACK** — 08/09/2026. **Non CONVALIDATO.** Zero codice.  
**Processo:** passo **2** di 4 (attesa risposte Renato su QTY-Q).  
**Presa in carico:** SI Renato 08/09/2026 («prendere in carico la richiesta prima formulata» + conferma in chiusura D).  
**ID Promemoria:** **B-QTY-KPI-SEARCH**.  
**Testo Renato (fonte, non ricomposto):** considerare di convertire la **quantità** degli oggetti (ora **facoltativa**) in **«chiave»** per creare **nuovi KPI** e **nuove query** per Ricerca avanzata.  
**Vincoli:** fuori da D UI (D congelato U0 / no codice); Pipeline 0–10 unico avvio ricerca; Motore B V1 F7–F9 non riaprire senza SI; catalogo 2.6 = fonte ufficiale per frasi.  
**Policy codice:** STOP fino a fine test Play / SI «apri fetta codice».

---

## 0. AS-IS (verificato in codice)

| Voce | Stato attuale |
|------|----------------|
| Modello | `Object.quantity: Int?` / `ObjectEntity.quantity: Int?` — **nullable** |
| UI lista | Se `quantity == null` non mostra etichetta quantità (`ObjectAdapter`) |
| Form | quantità opzionale in insert/update repository |
| Import CSV | colonna quantità già nel tracciato ufficiale (allineare eventuali evoluzioni B–C) |
| Ricerca avanzata | Pipeline 0–10; Motore A → lista Contenitori; Motore B → risposta in pagina (F7–F9) |
| Dashboard D | **CONVALIDATO no-code**; S1 futuro = conteggi **senza** quantità come chiave |
| PATTERN_010 / KPI aggregati ricerca | sospesi / fuori primo Motore B V1 |

---

## 1. Cosa significa «chiave» (tre letture)

Non esclusive — da scegliere in FEEDBACK:

| Lettura | Idea | Impatto |
|---------|------|---------|
| **K1 — KPI** | Indicatori: pezzi totali, pezzi per luogo/categoria (somma `quantity`) | Medio; query Room; **non** obbliga Motore B né aprire D |
| **K2 — Query Ricerca avanzata** | Domande «quanti pezzi…», «più di N…», filtri su quantità | **Alto** — Pipeline / Motore B / catalogo 2.6 / Matrice test |
| **K3 — Obbligatorietà** | Quantità non più facoltativa (sempre valorizzata) | Medio — UX + import + merge |

**Ipotesi analisi:** interessano **K1 e K2**; **K3 no** in prima fetta.

---

## 2. Tensioni

| Tema | Domanda aperta |
|------|----------------|
| Null | Somme KPI: null = 0 / 1 / escluso? Ricerca «senza quantità» distinta da 0? |
| Motore B | Nuove famiglie/pattern vs solo KPI Room? |
| Catalogo 2.6 | Nessuna frase nuova senza SI + elenco ufficiale pre-codice |
| Premium | KPI/query quantità in Archivio completo? |
| Ordine | Analisi ora ok; codice dopo A1–A3 (e dopo eventuale SI apertura) |
| Relazione D | D resta chiusa; K1 ≠ aprire S1/D |

---

## 3. Checklist FEEDBACK (QTY-Q) — rispondere SI/variante

| # | Domanda | Raccomandazione analisi |
|---|---------|-------------------------|
| **QTY-Q1** | Ambito prima fetta: **K1** / **K2** / **K1+K2** / anche **K3**? | **K1+K2**; K3 solo SI esplicito |
| **QTY-Q2** | Null in somme KPI | **Come 0**; in ricerca «senza quantità» ≠ 0 salvo SI |
| **QTY-Q3** | Ricerca: prima aggregati Motore B o anche filtri lista Motore A? | **Prima** KPI (K1) + **pochi** pattern Motore B chiari; filtri lista dopo |
| **QTY-Q4** | Quantità obbligatoria (K3)? | **No** — resta facoltativa |
| **QTY-Q5** | Default UI se campo vuoto | **Lasciare vuoto**; KPI tratta 0 (**alternativa:** default 1 al salvataggio — solo se SI) |
| **QTY-Q6** | Premium Archivio completo? | **Sì** |
| **QTY-Q7** | Frasi catalogo 2.6 | Elenco ufficiale **SI prima** di qualsiasi codice |
| **QTY-Q8** | Relazione con D | D **resta no-code**; K1 non apre D — voce propria o sotto-Dashboard solo con SI futuro |

---

## 4. Fuori scope (bozza)

- Riaprire F7–F9 Motore B già CONVALIDATI senza SI.  
- Vision / foto.  
- Unità di misura multiple.  
- Codice durante test Play.  
- Implementare D S1 «di striscio» per fare KPI quantità.

---

## 5. Esito ANALISI — in attesa FEEDBACK

**Prossimo:** Renato risponde a **QTY-Q1–Q8** (minimo Q1, Q2, Q4).  
Poi CONVALIDA merito → scrittura `REQUISITI_QTY_KPI_SEARCH.md` → CONVALIDA documento.

**Zero codice** su questa voce.
