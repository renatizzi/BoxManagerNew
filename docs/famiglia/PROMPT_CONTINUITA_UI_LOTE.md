# Prompt di continuità — lote UI trasversale (19/09/2026)

**Ritest telefono: unico a fine lote** (SI Renato 19/09).

**Identità:** una sola **BoxManager**. Test solo su `main`.  
**Build:** **`v. 1.3.18`** (vc **22**).

## Checklist

| # | ID | Scope | Stato codice |
|---|-----|--------|----------------|
| 1 | **B-UTILITY-*** | Tile Utility 92dp | **1.3.16** (incluso) |
| 2 | **B-UI-BUTTONS-ACCESSO-RAPIDO** | Bottoni azione stile **ORDINA** (14sp bold allCaps) | **1.3.17** |
| 3 | **B-QR-BATCH-*** | Nome BOX grassetto + codice più piccolo | **1.3.17** |
| 4 | **B-VOICE-MIC-ACCIDENTAL** | Voice solo con long-press sul mic | **1.3.17** |
| 5 | **B-PREMIUM-UNIFY-PAGE** | Sottotitolo scaduta + CODICE in evidenza | **1.3.17** |

**Annotato 19/09:** **B-VOICE-MIC-ACCIDENTAL**.  
**Aggiornato 19/09:** **B-UI-BUTTONS-ACCESSO-RAPIDO** → caratteri = Ordina.

---

## Ritest unico — topbar `v. 1.3.18`

1. `git checkout main` + `git pull origin main` + Run **`playDebug`**
2. Topbar **`v. 1.3.18`**
3. **Utility:** Backup e Ripristina senza scroll; tile più bassi
4. **Liste:** ORDINA / SPOSTA / ELIMINA (e altri Button azione) stesso stile carattere
5. **QR batch:** lista Contenitori → tocca **area testo** scheda (non icona) → **ETICHETTE QR** (sempre visibile) → stampa/PDF: nome grassetto + codice piccolo
   - Se premi ETICHETTE QR senza selezione: toast con istruzione
6. **Mic:** tap breve sul microfono in Cerca **non** avvia; **pressione prolungata** sì
7. **Premium** scaduta: sottotitolo «Funzione Premium scaduta»; messaggio prova **senza** elenco funzioni (solo Archivio completo); CONDIVIDI; «Se hai un CODICE…»
8. Rispondi `1.3.18 ok` o elenco KO

---

## Incolla

```
Continua lote UI da docs/famiglia/PROMPT_CONTINUITA_UI_LOTE.md.
Ritest unico a fine; non chiedere test intermedi.
Identità: una sola BoxManager. Test telefono solo su main.
```
