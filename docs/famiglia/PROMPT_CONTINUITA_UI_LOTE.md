# Prompt di continuità — lote UI trasversale (19/09/2026)

**Ritest telefono: unico a fine lote** (SI Renato 19/09). Nessun ritest intermedio.

**Identità:** una sola **BoxManager**. Test solo su `main`.

## Checklist (ordine di lavoro)

| # | ID | Scope | Stato |
|---|-----|--------|-------|
| 1 | **B-UTILITY-BUTTON-HEIGHT** + **B-UTILITY-RIPRISTINA-VISIBLE** | Tile Utility più bassi | Già in **1.3.16** |
| 2 | **B-UI-BUTTONS-ACCESSO-RAPIDO** *(testo 19/09)* | Caratteri bottoni «azione» = come **Ordina** | IN CORSO |
| 3 | **B-QR-BATCH-CODE-FONT** + **B-QR-BATCH-BOX-NAME** | Multietichette: font codice + nome BOX grassetto | IN CORSO |
| 4 | **B-VOICE-MIC-ACCIDENTAL** | Evitare avvio involontario microfono in editing | IN CORSO |
| 5 | **B-PREMIUM-UNIFY-PAGE** | Pagina premium unica | IN CORSO |

**Nuovo (19/09):** **B-VOICE-MIC-ACCIDENTAL** — «Trovare una soluzione per evitare che in fase di editing per i campi con input anche vocale parta involontariamente la registrazione se sfioro il microfono».

**Aggiornato (19/09):** **B-UI-BUTTONS-ACCESSO-RAPIDO** — non più «stile Accesso rapido»; ora: verificare caratteri di tutti i bottoni azione uguali a quelli di **Ordina**.

---

## Ritest unico (dopo merge, topbar da comunicare a fine lote)

1. `git checkout main` + `git pull` + **`playDebug`**
2. Utility: tile / Ripristina senza scroll
3. Liste: bottoni azione (tipografia = Ordina)
4. QR batch stampa: nome BOX grassetto + codice più piccolo
5. Editing + microfono: sfioro non avvia registrazione
6. Premium scaduta: pagina unica (sottotitolo / CONDIVIDI / CODICE)
7. Rispondi `v. X.Y.Z ok` o KO

---

## Incolla

```
Continua lote UI da docs/famiglia/PROMPT_CONTINUITA_UI_LOTE.md.
Ritest unico a fine; non chiedere test intermedi.
Identità: una sola BoxManager. Test telefono solo su main.
```
