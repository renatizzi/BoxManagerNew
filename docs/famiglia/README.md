# Documentazione Merge famiglia (beta)

| Documento | Contenuto |
|-----------|-----------|
| [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) | Modello prodotto e regole merge |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Fix trasversali (igiene file, sync Play) |
| [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md) | **Ingresso sessione** — filone correttivi post-B5 (SI Renato) |
| [PROMPT_CONTINUITA_B5.md](PROMPT_CONTINUITA_B5.md) | Storico B5 (chiuso) |
| [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) | Assessment interventi correttivi (P0–P2) |
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | **Congelato:** 1.2 identica in test; sviluppo parallelo diventa l’ufficiale; 1.2 si tocca solo per bug bloccanti |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Allineamento bugfix 1.2 ↔ beta famiglia |
| [DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md) | Come far convivere Play 1.2 e beta in famiglia |
| [INSTALLA_SENZA_RUN.md](INSTALLA_SENZA_RUN.md) | Se Run / Terminale non funzionano (script `.bat`) |

**Fetta B5:** **CONVALIDATO** (SI Renato, 31/08/2026). **T2 CONVALIDATO** B5.3. **P1 CONVALIDATO** B5.7 (01/09/2026). Play 1.2 su `main` **identica** per il test (`cursor/versione-test-5409`) — si tocca solo per bug bloccanti. A test chiuso lo sviluppo **sostituisce** la 1.2.

## Aggiornare la beta (solo Renato)

```bash
cd BoxManagerNew
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
```

Android Studio: **famigliaDebug** → Run (se il ▶ sparisce: Invalidate Caches).

Play 1.2 resta su `main` — non mescolare i branch.
