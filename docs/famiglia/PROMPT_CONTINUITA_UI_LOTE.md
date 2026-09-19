# Prompt di continuità — lote UI trasversale (19/09/2026)

**Identità:** una sola **BoxManager**. Test solo su `main`.  
**Build:** **`v. 1.3.22`** (vc **26**).

## Checklist

| # | ID | Scope | Stato |
|---|-----|--------|--------|
| 1 | **B-UTILITY-*** | Tile Utility a weight (griglia piena) | **1.3.20** |
| 2 | **B-UI-BUTTONS-ACCESSO-RAPIDO** | ORDINA = theme Button 14sp | **1.3.19** SI |
| 3–5 | QR PDF / mic / premium | | SI codice lote |
| 6 | **QR BATCH Utility** | Dialog intro su Utility (neutro) → lista; ETICHETTE in selezione | **1.3.22** |

---

## Ritest — topbar `v. 1.3.22`

1. `git checkout main` + `git pull` + Run **`playDebug`**
2. Topbar **`v. 1.3.22`**
3. Utility → **QR BATCH** → dialog (non riquadro rosso; ORDINA resta intatto) → OK → lista
4. Seleziona schede → **ETICHETTE QR**
5. Rispondi `1.3.22 ok` o elenco KO

---

## Incolla

```
Continua lote UI da docs/famiglia/PROMPT_CONTINUITA_UI_LOTE.md.
Identità: una sola BoxManager. Test telefono solo su main.
```
