# Documentazione Merge famiglia (beta)

| Documento | Contenuto |
|-----------|-----------|
| [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) | Modello prodotto e regole merge |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Allineamento bugfix 1.2 ↔ beta famiglia |
| [DUE_APP_CINQUE_TELEFONI.md](DUE_APP_CINQUE_TELEFONI.md) | Come far convivere Play 1.2 e beta in famiglia |
| [SOLO_TU.md](SOLO_TU.md) | Checklist minima: solo attività di Renato |

**Fetta corrente sul branch:** B0 + B1 (catalogo categorie/luoghi).

Build sideload:

```bash
./gradlew :app:assembleFamigliaDebug
```

Play 1.2 resta su `main` / flavor `play` — non pubblicare `famiglia`.
