# Documentazione — funzioni BoxManager in sviluppo (archivio condiviso)

Una sola app: **BoxManager**. Questi file coprono l’**archivio condiviso** e i correttivi, non un secondo prodotto. Inglese: [../multilingua/PROMPT_CONTINUITA_M.md](../multilingua/PROMPT_CONTINUITA_M.md). Identità: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

| Documento | Contenuto |
|-----------|-----------|
| [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) | Modello prodotto e regole merge archivio |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Fix trasversali (igiene file, sync 1.2) |
| [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md) | **Ingresso sessione** — correttivi |
| [PROMPT_CONTINUITA_B5.md](PROMPT_CONTINUITA_B5.md) | Storico B5 (chiuso) |
| [ASSESSMENT_CORRETTIVI.md](ASSESSMENT_CORRETTIVI.md) | Assessment interventi correttivi (P0–P2) |
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | **Congelato:** una BoxManager; 1.2 identica in test; sviluppo = ufficiale a fine test |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Allineare un fix 1.2 sulla copia di sviluppo |
| [DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md) | Due *installazioni* durante il test (1.2 Store + sviluppo) |
| [INSTALLA_SENZA_RUN.md](INSTALLA_SENZA_RUN.md) | Se Run / Terminale non funzionano (script `.bat`) |

**Archivio condiviso B5:** **CONVALIDATO** (SI Renato, 31/08/2026). **T2 CONVALIDATO** B5.3. **P1 CONVALIDATO** B5.7 (01/09/2026). Play 1.2 su `main` **identica** per il test — si tocca solo per bug bloccanti. A test chiuso lo sviluppo **sostituisce** la 1.2.

## Aggiornare la BoxManager di sviluppo (solo Renato)

```bash
cd BoxManagerNew
git checkout cursor/family-unione-unificata-e5b5
git pull origin cursor/family-unione-unificata-e5b5
```

Android Studio: variante **famigliaDebug** → Run (se il ▶ sparisce: Invalidate Caches). È la stessa BoxManager, build di sviluppo.

Durante il test: Play 1.2 resta su `main` (identica). A test chiuso questa linea **sostituisce** la 1.2 — [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).
