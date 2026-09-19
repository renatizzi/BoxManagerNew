# Prompt di continuità — lote UI trasversale (19/09/2026)

**Identità:** una sola **BoxManager**. Test solo su `main`.  
**Build:** **`v. 1.3.20`** (vc **24**).

## Checklist

| # | ID | Scope | Stato |
|---|-----|--------|--------|
| 1 | **B-UTILITY-*** | Tile Utility a weight (griglia piena) | **1.3.20** |
| 2 | **B-UI-BUTTONS-ACCESSO-RAPIDO** | ORDINA = theme Button 14sp | **1.3.19** SI |
| 3 | **B-QR-BATCH-*** | Nome BOX + codice piccolo | **1.3.17** |
| 4 | **B-VOICE-MIC-ACCIDENTAL** | Voice solo long-press | **1.3.17** |
| 5 | **B-PREMIUM-UNIFY-PAGE** | Premium senza elenco funzioni | **1.3.18** |
| 6 | **QR BATCH Utility** | Tile + selezione contestuale; niente ETICHETTE sempre in lista | **1.3.20** |

---

## Ritest — topbar `v. 1.3.20`

1. `git checkout main` + `git pull` + Run **`playDebug`**
2. Topbar **`v. 1.3.20`**
3. **Utility:** griglia 4×2 (Codice QR | QR BATCH; Condividi | Cestino); tile distribuiti in altezza
4. **QR BATCH** (con contenitori): apre lista + messaggio «Seleziona i contenitori» → seleziona → **ETICHETTE QR** in barra selezione
5. Lista Contenitori **senza** selezione: niente bottone ETICHETTE QR fuori contesto
6. Rispondi `1.3.20 ok` o elenco KO

---

## Incolla

```
Continua lote UI da docs/famiglia/PROMPT_CONTINUITA_UI_LOTE.md.
Identità: una sola BoxManager. Test telefono solo su main.
```
