# Requisiti — Export / Import avanzati (Progetto 2 / B–C)

**Stato:** CONGELATO 08/09/2026 (SI Renato).  
**CONVALIDA analisi/requisiti (passo 3):** SI Renato 08/09/2026 (EI-Q1–EI-Q10 come raccomandato + vincolo **non core** + preferenza **trascodifica fogli/DB esterni**).  
**CONVALIDA aggiornamento documento (passo 4):** SI Renato 08/09/2026.  
**Uso:** riferimento funzionale e tecnico-architetturale fino all’implementazione.  
**Destinazione:** Nota Integrata (rinvio §4.1.6 EXPORT/IMPORT AVANZATO).  
**Assessment:** [ASSESSMENT_EXPORT_IMPORT_AVANZATO.md](ASSESSMENT_EXPORT_IMPORT_AVANZATO.md) (storico; prevale **questo** file).  
**Sequenza codice:** dopo A1 QR + A2 Cestino + A3 Foto. **STOP codice** fino a fine test Play / SI «apri fetta codice».  
**Priorità prodotto:** **importanti ma non core** (allineato a Nota: medio-bassa).

---

## 0. Principi (SI Renato 08/09)

### P1 — Non core
Export/Import avanzati **non** sono nel flusso core V1 né priorità alta rispetto a QR / Cestino / Foto. In roadmap restano dopo A1–A3; in dubbio di scope, **ridurre** piuttosto che espandere.

### P2 — Trascodifica semplificata verso esterni
A parità di condizioni, preferire scelte che agevolano il passaggio **semplice** tra archivio BoxManager e **fogli / database esterni diffusi** (Google Sheets, Microsoft Excel / 365, e simili):

- **CSV** resta il canale primario verso esterni (apribile/modificabile in Sheets ed Excel senza strumenti proprietari).  
- Separatore, encoding e colonne **stabili e documentati** (Modello ufficiale).  
- Evitare formati che complicano il round-trip con fogli (es. niente “solo ZIP obbligatorio” per chi vuole solo editare in Sheets).  
- Il pacchetto **ZIP** (CSV + foto) è **complementare** (ricchezza BoxManager / Foto T5), non sostituto del CSV verso Google/Microsoft.  
- Non introdurre in prima fetta formati binari proprietari (.xlsx nativo, Google API, ecc.) salvo SI successivo: il CSV ben fatto è la trascodifica semplificata.

### P3 — Premium
Gate `PremiumFeature.EXPORT` / `IMPORT` (Archivio completo), come già in V1 e vincolo Progetto 2.

### P4 — Canali distinti (non confondere)
| Canale | Ruolo |
|--------|--------|
| Esporta **vista** | Contestuale alle liste (resta) |
| **Esporta dati** (nuovo Utility) | Archivio intero / selezione |
| **Importa Dati** | MERGE da CSV (e ZIP in fase successiva) |
| Invia/Ricevi Archivio | Famiglia — fuori da questa voce |
| Backup / Ripristina | ZIP replace — fuori da questa voce |

---

## 1. Obiettivo prodotto

Rendere Export/Import **più controllabili e chiari** (selezione, file, validazione, errori), restando **non core**, e ottimizzati per chi lavora anche su **fogli esterni** (Google, Microsoft).

Perimetro = i quattro bullet Nota **9.2 §4.1.6**:
- Export: selezione contenuti · gestione file avanzata  
- Import: validazione dati estesa · gestione errori dettagliata  

---

## 2. Requisiti funzionali (CONVALIDA passo 3)

### R1 — Perimetro (EI-Q1)
Solo i 4 bullet. Niente SMB, famiglia, Motore B, Dashboard/UI (voce D separata).

### R2 — Export: selezione (EI-Q2 = E3)
Profili:
1. **Vista corrente** — comportamento Esporta vista già in V1 (resta).  
2. **Archivio intero** — tutti i contenitori/oggetti esportabili nel tracciato.  
3. **Selezione…** — scelta esplicita di contenitori (e relativi oggetti) da includere.

### R3 — Export: Utility (EI-Q6)
Card **Esporta dati** in Utility (oltre Esporta vista contestuale).

### R4 — Export: file (EI-Q3 = F3) + P2
- **Prima:** CSV migliorato (Utility, selezione, nome datato, riuso cartella) — priorità trascodifica Sheets/Excel.  
- **Poi (con Foto in codice):** opzione ZIP = CSV + `photos/objects/` (allineato REQUISITI Foto T5); ZIP **non** sostituisce il CSV per chi esporta verso fogli.  
- Nome: `ESPORTA_ddMMyy_HHmm.csv` / `.zip`; criterio `salvataggio-file.mdc`.

### R5 — Import: validazione (EI-Q4 = V3) + P2
- Controlli estesi sul CSV (tipi, quantità, obbligatori, dipendenze) — utili anche a file riesportati da Sheets/Excel.  
- Supporto ZIP + riaggancio per id stabili quando il pacchetto Foto esiste.  
- CSV **v1** resta importabile (compatibilità).

### R6 — Import: errori (EI-Q5 = G1)
- Report **riga-per-riga** (schermata e/o file scaricabile): sezione, riga, motivo.  
- **Niente** apply parziale in questa voce (resta blocco se errori bloccanti, come V1). G2/G3 solo con SI di revisione.

### R7 — Formato CSV v2 (EI-Q7) + P2
- Nuova **versione formato** (v2) + `Modello_Importazione` aggiornato.  
- Include **id stabili** dove serve al match (`permanentId` / `objectPermanentId`) senza rompere l’import v1.  
- Colonne e ordine **documentati** per copia/incolla o apertura in Google Sheets / Excel.  
- Encoding UTF-8 (BOM come oggi se già consolidato); separatore ufficiale invariato salvo SI (`;` oggi).

### R8 — Categorie/posizioni in selezione export (EI-Q10)
**Fuori** dalla prima fetta di questa voce (restano prerequisito anagrafica come in Import V1).

### R9 — Ordine implementativo (EI-Q9)
Codice **dopo** QR avanzato + Cestino + Foto. Analisi/documenti ammessi prima.

### R10 — Premium (EI-Q8)
Come P3.

---

## 3. Requisiti tecnico-architetturali

### T1 — Stack
Activity + XML + MVVM + Room. No Compose/Hilt obbligatori.

### T2 — Compatibilità
- Import: detector versione formato; branch v1 / v2.  
- Export v2: produrre file che Sheets/Excel aprono senza passaggi extra (un CSV testuale).  
- ZIP: stesso layout foto di REQUISITI Foto quando applicabile.

### T3 — Non toccare
Pipeline ricerca / Motore B; famiglia merge; Backup replace; Disco di rete (salvo riuso helper cartella).

### T4 — Room
Nessuna migrazione obbligatoria per sola UI export; id già in modello. No `fallbackToDestructiveMigration()`; `allowBackup=false`.

---

## 4. Fuori scope (prima fetta)

- Apply parziale import (G2).  
- Export/import nativo `.xlsx` / API Google Drive.  
- Categorie/posizioni nel picker export.  
- Sostituire Invia Archivio o Backup.  
- Obbligare ZIP per ogni export verso fogli.

---

## 5. Criterio di accettazione (ritest futuro)

1. Utility → Esporta dati: intero e selezione; vista resta.  
2. CSV v2 apribile in Sheets/Excel; Modello aggiornato; import v1 ancora ok.  
3. Import: report errori riga-per-riga; blocco se errori bloccanti.  
4. Dopo Foto: export/import ZIP con foto opzionale; CSV puro resta disponibile.  
5. Gate premium; famiglia/Backup non regressi.

---

## 6. Recepimento ufficiale

In Nota: dettaglio sotto §4.1.6 EXPORT/IMPORT AVANZATO (o Allegato), con P1–P2 espliciti.  
Questo file è la fonte fino all’implementazione.

### Processo

1. ANALISI — [ASSESSMENT_EXPORT_IMPORT_AVANZATO.md](ASSESSMENT_EXPORT_IMPORT_AVANZATO.md)  
2. FEEDBACK — EI-Q1–EI-Q10 + non core + trascodifica esterni  
3. CONVALIDA analisi/requisiti — **SI Renato 08/09/2026**  
4. AGGIORNAMENTO DOCUMENTO — **questo file**; **CONVALIDA aggiornamento documento SI Renato 08/09/2026**
