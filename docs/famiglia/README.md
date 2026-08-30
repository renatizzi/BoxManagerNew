# Documentazione Merge famiglia (beta)

| Documento | Contenuto |
|-----------|-----------|
| [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) | Modello prodotto e regole merge |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Allineamento bugfix 1.2 ↔ beta famiglia |
| [DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md) | Come far convivere Play 1.2 e beta in famiglia |
| [INSTALLA_SENZA_RUN.md](INSTALLA_SENZA_RUN.md) | Se Run / Terminale non funzionano (script `.bat`) |

**Fetta corrente sul branch:** B0–B3 (superate) + **B4** (tabelle condivise + archivio; **CONVALIDA in corso**, non CONVALIDATO). UI pulsanti = stile Utility da `1.3-famigliaB4.3`.

## Aggiornare la beta (solo Renato)

Come per la 1.2: **nessun token**, solo pull del branch famiglia in corso.

```bash
cd BoxManagerNew
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
```

Android Studio: **famigliaDebug** → Run (se il ▶ sparisce: Invalidate Caches).

Play 1.2 resta su `main` — non mescolare i branch.
