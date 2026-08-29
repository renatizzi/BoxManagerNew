# Nota B0 — Merge famiglia (BoxManager)

**Stato:** adottata (SI Renato, sessione continuità post Alpha 1.2)  
**Ambito:** solo build **flavor `famiglia`** (betatest locale). **Non** pubblicare su Play Store.  
**Track Play / Alpha:** resta **1.2 (versionCode 3)** su `main` — non modificare il comportamento release Play da questa Nota.

---

## 1. Contesto prodotto

Contesto familiare:

1. In **Setup** si condividono **categorie** e **luoghi** abituali di custodia (cambiano solo per fatti eccezionali).
2. Ciò che personalizza la gestione sono **contenitori e oggetti**: ogni componente censisce ciò che conosce (uso comune + effetti personali).
3. Gli effetti «personali» restano di **pubblico dominio familiare** dopo l’unione (chi li inserisce ≠ chi può vederli).
4. Il **merge** serve a **ripartire il peso del censimento**, non a fare sync cloud continuo.

---

## 2. Architettura a strati

| Strato | Contenuto | Ciclo di vita |
|--------|-----------|----------------|
| A — Struttura famiglia | Categorie + posizioni | Setup una volta; modifiche eccezionali |
| B — Inventario | Contenitori + oggetti | Lavoro quotidiano offline per membro |
| C — Unione | Pacchetto merge esplicito | Periodico (file / condivisione) |

Niente archivio inventariale cloud unico (fuori scope; «no C» prodotto).  
Cloud eventuale solo come canale di scambio file/catalogo — non in B1.

---

## 3. Identità e regole merge (inventario)

### 3.1 Identificativi

| Entità | ID stabile | Note |
|--------|------------|------|
| Contenitore | `permanentId` (già in app / QR) | Deve viaggiare nel pacchetto unione (B2) |
| Oggetto | `objectPermanentId` (da introdurre in B2) | Stesso ruolo; oggi assente |
| Categoria / posizione | nome normalizzato (trim + case-fold) | Catalogo famiglia; non ID UUID in B1 |

### 3.2 Regole unione inventario (B2+)

| Caso | Comportamento |
|------|----------------|
| ID assente in archivio locale | **Insert** |
| Stesso ID, payload diverso | **Update** se `lastModified` remoto > locale; altrimenti ignora o anteprima conflitto |
| Stesso ID, identico | Ignora |
| Cancellazione | **Non propagata** in automatico nella prima fetta; eventuale «rimuovi anche in famiglia» esplicito dopo |
| Ripristino ZIP | Resta **replace wipe** — **vietato** come strumento di unione |

### 3.3 CSV import V1 attuale

Il merge CSV odierno (`ImportMergePlanner`) resta valido come **ponte operativo** (insert-or-ignore per chiavi testo) finché B2 non espone Unisci-per-ID.  
Non sostituisce B2; non va usato come sync di aggiornamenti.

---

## 4. Catalogo famiglia (B1 — questa fetta)

### 4.1 Formato file

```
formato;BoxManager_FamilyCatalog;1
sezione;CATEGORIE
nome;icona
…
sezione;POSIZIONI
nome
…
```

- Separatore `;`, UTF-8 con BOM, allineato allo stile Import V1.
- Nome file proposto: `Catalogo_Famiglia_ddMMyy_HHmm.csv`.

### 4.2 Semantica import catalogo

- Aggiunge categorie/posizioni **mancanti** (match nome case-insensitive).
- Duplicati: ignorati (nessun overwrite icona in B1).
- Non cancella voci locali assenti dal file.
- Non tocca contenitori/oggetti.

### 4.3 Flusso Setup famiglia

1. Un membro (o insieme) definisce categorie + luoghi sul proprio telefono.
2. **Esporta catalogo famiglia** → condivide il CSV.
3. Gli altri: **Importa catalogo famiglia** → struttura allineata.
4. Poi ciascuno censisce contenitori/oggetti; unione inventario = B2.

---

## 5. Attribuzione — nome utente già in app

**Non introdurre un secondo “membro famiglia”.** Si riusa il **nome utente** già in Impostazioni (`SharedPreferences` chiave `username`), oggi usato come etichetta locale (e per il check admin Archivio completo).

| Uso | Comportamento previsto (B2/B3, flavor `famiglia`) |
|-----|--------------------------------------------------|
| Topbar | Mostra il nome salvato (non un default fisso) |
| Nuovo contenitore / oggetto | Alla creazione si memorizza `createdBy` = nome utente corrente (trim); immutabile dopo create salvo SI |
| Merge | Il campo viaggia nel pacchetto unione; utile per «chi ha censito», **non** per nascondere dati |
| Nome vuoto | Fallback `Utente` in UI; in beta famiglia si invita a impostare un nome **distinto** per ciascun familiare (es. Marco, Anna) |

Niente ACL: dopo il merge tutto resta dominio famiglia. Il nome serve a ripartire il lavoro e a leggere l’origine.

---

## 6. Fette

| Fetta | Deliverable | Play |
|-------|-------------|------|
| **B0** | Questa Nota + policy sync beta | No |
| **B1** | Catalogo famiglia export/import + Guida + flavor | No |
| **B2** | Pacchetto unione per ID (insert+update) + anteprima | No |
| **B3** | Origine = **nome utente** Impostazioni su contenitori/oggetti; delete esplicito propagabile | No |

---

## 7. Vincoli non negoziabili

- Flavor `famiglia`: `applicationId` `it.renatizzi.boxmanager.famiglia` — installazione **affiancata** a Play 1.2.
- Nessun upload AAB/APK `famiglia` sulla Console Play.
- Bugfix rilevati su 1.2: atterraggio su `main`, poi merge in branch famiglia (vedi `BETA_SYNC_POLICY.md`).
