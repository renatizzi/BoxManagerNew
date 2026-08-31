# Documentazione Merge famiglia (beta)

| Documento | Contenuto |
|-----------|-----------|
| [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) | Modello prodotto e regole merge |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Fix trasversali (igiene file, sync Play) |
| [PROMPT_CONTINUITA_B5.md](PROMPT_CONTINUITA_B5.md) | Storico prompt sessione B5 (chiusa) |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Allineamento bugfix 1.2 ↔ beta famiglia |
| [DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md) | Come far convivere Play 1.2 e beta in famiglia |
| [INSTALLA_SENZA_RUN.md](INSTALLA_SENZA_RUN.md) | Se Run / Terminale non funzionano (script `.bat`) |

**Fetta corrente:** **B5 CONVALIDATO** (SI Renato, 31/08/2026, build **1.3-famigliaB5.2**). Branch di riferimento: `cursor/family-b5-createdby-delete-7b83` (PR #9) → merge su `cursor/family-unione-unificata-e5b5`.

## Aggiornare la beta (solo Renato)

Come per la 1.2: **nessun token**, solo pull del branch famiglia.

```bash
cd BoxManagerNew
git checkout cursor/family-b5-createdby-delete-7b83
git pull origin cursor/family-b5-createdby-delete-7b83
```

Oppure, dopo merge su unione: `cursor/family-unione-unificata-e5b5`.

Android Studio: **famigliaDebug** → Run (se il ▶ sparisce: Invalidate Caches).

Play 1.2 resta su `main` — non mescolare i branch.
