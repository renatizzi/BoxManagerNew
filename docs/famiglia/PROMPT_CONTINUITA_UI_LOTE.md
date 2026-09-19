# Prompt di continuità — lote UI trasversale (19/09/2026)

**Identità:** una sola **BoxManager**. Test solo su `main`.  
**Build:** **`v. 1.3.21`** (vc **25**).

## Checklist

| # | ID | Scope | Stato |
|---|-----|--------|--------|
| 1 | **B-UTILITY-*** | Tile Utility a weight (griglia piena) | **1.3.20** |
| 2 | **B-UI-BUTTONS-ACCESSO-RAPIDO** | ORDINA = theme Button 14sp | **1.3.19** SI |
| 3 | **B-QR-BATCH-*** | Nome BOX + codice piccolo | **1.3.17** |
| 4 | **B-VOICE-MIC-ACCIDENTAL** | Voice solo long-press | **1.3.17** |
| 5 | **B-PREMIUM-UNIFY-PAGE** | Premium senza elenco funzioni | **1.3.18** |
| 6 | **QR BATCH Utility** | Tile + selezione contestuale; niente ETICHETTE sempre in lista | **1.3.21** (fix messaggio sticky) |

---

## Ritest — topbar `v. 1.3.21`

1. `git checkout main` + `git pull` + Run **`playDebug`**
2. Topbar **`v. 1.3.21`**
3. **Utility → QR BATCH:** lista + messaggio contestuale «Seleziona i contenitori» (resta visibile)
4. Seleziona schede → **ETICHETTE QR** in barra selezione
5. Rispondi `1.3.21 ok` o elenco KO

---

## Incolla

```
Continua lote UI da docs/famiglia/PROMPT_CONTINUITA_UI_LOTE.md.
Identità: una sola BoxManager. Test telefono solo su main.
```
