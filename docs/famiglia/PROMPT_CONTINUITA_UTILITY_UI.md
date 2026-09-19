# Prompt di continuità — Utility UI backlog (19/09/2026)

**Ingresso unico** per Utility (altezza tile / Ripristina visibile). Non mescolare con Premium unify o QR batch senza SI.

**Identità:** una sola **BoxManager**.  
**Test telefono:** solo su `main`.  
**B–C:** chiuso — [PROMPT_CONTINUITA_BC_EXPORT.md](PROMPT_CONTINUITA_BC_EXPORT.md).

### Backlog (questa linea)

| ID | Indicazione | Stato |
|----|-------------|-------|
| **B-UTILITY-BUTTON-HEIGHT** | Ridurre altezza bottoni Utility (niente scroll) | **IN CORSO** → **1.3.16** |
| **B-UTILITY-RIPRISTINA-VISIBLE** | Ripristina visibile senza scroll | Stessa fetta (tile più bassi) |
| **B-PREMIUM-UNIFY-PAGE** | — | Dopo Utility, con SI |
| **B-QR-BATCH-*** | — | Dopo Premium, con SI |

---

## Ritest (dopo merge `main`) — topbar `v. 1.3.16`

1. `git checkout main` + `git pull origin main` + Run **`playDebug`**
2. Topbar **`v. 1.3.16`**
3. Utility: **Backup e Ripristina** visibili senza scroll; tutte le card più basse
4. Scroll non necessario (o minimo) per vedere Cestino
5. Rispondi `1.3.16 ok` o KO

---

## Incolla in chat

```
Continua Utility UI da docs/famiglia/PROMPT_CONTINUITA_UTILITY_UI.md.
Priorità: altezza bottoni / Ripristina; non aprire Premium/QR senza SI.
Identità: una sola BoxManager. Test telefono solo su main.
```
