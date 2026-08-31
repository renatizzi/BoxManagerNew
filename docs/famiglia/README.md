# Documentazione Merge famiglia (beta)

| Documento | Contenuto |
|-----------|-----------|
| [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) | Modello prodotto e regole merge |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Fix trasversali (igiene file, sync Play) |
| [PROMPT_CONTINUITA_B5.md](PROMPT_CONTINUITA_B5.md) | Storico prompt sessione B5 (chiusa) |
| [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) | Assessment interventi correttivi (P0–P2) |
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | Play test + famiglia: sync bugfix, non feature |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Allineamento bugfix 1.2 ↔ beta famiglia |
| [DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md) | Come far convivere Play 1.2 e beta in famiglia |
| [INSTALLA_SENZA_RUN.md](INSTALLA_SENZA_RUN.md) | Se Run / Terminale non funzionano (script `.bat`) |

**Fetta B5:** **CONVALIDATO** (SI Renato, 31/08/2026). **Correttivo T2:** build **1.3-famigliaB5.3** su branch `cursor/family-unione-unificata-e5b5` (attende SI ritest).

## Aggiornare la beta (solo Renato)

```bash
cd BoxManagerNew
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
```

Android Studio: **famigliaDebug** → Run (se il ▶ sparisce: Invalidate Caches).

Play 1.2 resta su `main` — non mescolare i branch.
