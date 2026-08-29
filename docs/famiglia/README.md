# Documentazione Merge famiglia (beta)

| Documento | Contenuto |
|-----------|-----------|
| [NOTA_B0_MERGE_FAMIGLIA.md](NOTA_B0_MERGE_FAMIGLIA.md) | Modello prodotto e regole merge |
| [BETA_SYNC_POLICY.md](BETA_SYNC_POLICY.md) | Allineamento bugfix 1.2 ↔ beta famiglia |

**Fetta corrente sul branch:** B0 + B1 (catalogo categorie/luoghi).

Build sideload:

```bash
./gradlew :app:assembleFamigliaDebug
```

Play 1.2 resta su `main` / flavor `play` — non pubblicare `famiglia`.
