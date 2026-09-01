# Nota B0 — Merge famiglia (BoxManager)

**Stato:** adottata (SI Renato, sessione continuità post Alpha 1.2). **B4 CONVALIDATO** (SI Renato, 31/08/2026, build **1.3-famigliaB4.10**). **B5 CONVALIDATO** (SI Renato, 31/08/2026, build **1.3-famigliaB5.2**). **T2 CONVALIDATO** (SI Renato, 01/09/2026, build **1.3-famigliaB5.3**).  
**Ambito:** solo build **flavor `famiglia`** (betatest locale). **Non** pubblicare su Play Store.  
**Track Play / Alpha:** resta **1.2 (versionCode 3)** su `main` — non modificare il comportamento release Play da questa Nota.  
**Fonte ufficiale:** `docs/Nota_Integrata_9.2.docx` su **`main`**, **Allegato 4.20** (quadro 4.1 al 01/09/2026). Questo file è copia di lavoro; in caso di conflitto **prevale la Nota**.

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
| A — Tabelle condivise | Categorie + posizioni | Setup una volta; modifiche eccezionali |
| B — Inventario | Contenitori + oggetti | Lavoro quotidiano offline per membro |
| C — Condivisione | Pacchetto esplicito (file) | Periodico (file / condivisione) |

Niente archivio inventariale cloud unico (fuori scope; «no C» prodotto).  
Cloud eventuale solo come canale di scambio file/catalogo — non in B1.

---

## 3. Identità e regole merge (inventario)

### 3.1 Identificativi

| Entità | ID stabile | Note |
|--------|------------|------|
| Contenitore | `permanentId` (già in app / QR) | Deve viaggiare nel pacchetto unione (B2) |
| Oggetto | `objectPermanentId` (introdotto in B2) | Stesso ruolo del contenitore |
| Categoria / posizione | nome normalizzato (trim + case-fold) | Catalogo famiglia; non ID UUID in B1 |

### 3.2 Regole unione inventario (B2 — questa fetta)

| Caso | Comportamento |
|------|----------------|
| ID assente in archivio locale | **Insert** |
| Stesso ID, payload diverso | **Update** se `lastModified` remoto > locale; altrimenti ignora o anteprima conflitto |
| Stesso ID, identico | Ignora |
| Cancellazione | **Non propagata** in automatico nella prima fetta; **B5 famiglia:** ogni delete locale scrive tombstone e viaggia in `CANCELLAZIONI` al prossimo Invia Archivio (archivio unico, nessuna opzione «solo qui») |
| Ripristino ZIP | Resta **replace wipe** — **vietato** come strumento di unione |

### 3.3 CSV import V1 attuale

Il merge CSV odierno (`ImportMergePlanner`) resta valido come **ponte operativo** (insert-or-ignore per chiavi testo) finché B2 non espone Unisci-per-ID.  
Non sostituisce B2; non va usato come sync di aggiornamenti.

---

## 4. Unione famiglia unificata (B3 — questa fetta)

### 4.1 Formato file

```
formato;BoxManager_FamilyMerge;1
sezione;CATEGORIE
nome;icona
…
sezione;POSIZIONI
nome
…
sezione;CONTENITORI
permanentId;nome;categoria;posizione;lastModified;createdBy
…
sezione;OGGETTI
objectPermanentId;boxPermanentId;tipo;descrizione;quantita;lastModified;createdBy
…
sezione;CANCELLAZIONI
entityType;permanentId;deletedAt;deletedBy
…
```

- Separatore `;`, UTF-8 con BOM.
- `createdBy` e sezione `CANCELLAZIONI` sono **opzionali in lettura** (file B4 senza colonna/sezione restano validi). Export B5+ li scrive sempre.
- Nome file proposto (B3, storico): `Unione_Famiglia_ddMMyy_HHmm.csv`.
- In **B4** i nomi proposti principali sono `Tabelle_Condivise_ddMMyy_HHmm.csv` e `Condivisione_Archivio_ddMMyy_HHmm.csv` (vedi §4bis).
- Accetta anche file legacy B1 (`BoxManager_FamilyCatalog`) e B2 (`BoxManager_FamilyInventory`).

### 4.2 Semantica import unione (B3 — sostituita da B4 per il catalogo)

1. **Catalogo additivo** (B3): aggiunge categorie/posizioni mancanti; non cancella voci locali.
2. **Guarigione da contenitori**: se un contenitore in arrivo referenzia categoria/posizione assente in locale, la voce viene **ricreata** prima dell'inventario (icona categoria = default).
3. **Inventario per ID stabili**: insert / update / conflitto come B2. **B5:** delete esplicito via tombstone / sezione `CANCELLAZIONI` (non automatico dalla sola assenza nel file).

In **B4** il catalogo additivo non è più applicato da Ricevi Archivio: usare **Invia/Ricevi tabelle condivise**.

### 4.3 Flusso famiglia (B3)

1. Ogni membro censisce offline contenitori e oggetti sul proprio telefono.
2. Periodicamente un membro **Invia Archivio** → condivide il CSV.
3. Gli altri: **Ricevi Archivio** → archivio domestico allineato senza rifare tutto il censimento da zero.

---

## 4bis. Condivisione archivio in due passi (B4 — questa fetta)

Pagina unica **Condivisione Archivio**, due operazioni distinte.

### 4bis.1 Tabelle condivise (categorie e posizioni)

| Azione | File | Semantica |
|--------|------|-----------|
| Invia tabelle condivise | `Tabelle_Condivise_ddMMyy_HHmm.csv` (`BoxManager_FamilyCatalog`) | Esporta le tabelle locali (categorie + posizioni) |
| Ricevi tabelle condivise | stesso formato | **Allinea/sostituisce** le tabelle locali al file condiviso (anteprima SI/NO). Blocca la rimozione se contenitori locali usano ancora quella categoria/posizione |

- **Passo 1** (setup famiglia): condividere le tabelle.
- **Passo 3** (ripristino): dopo reinstallazione o per correggere errori nelle tabelle locali.

### 4bis.2 Archivio (contenitori e oggetti)

| Azione | File | Semantica |
|--------|------|-----------|
| Invia Archivio | `Condivisione_Archivio_ddMMyy_HHmm.csv` (`BoxManager_FamilyMerge`) | Esporta tabelle di riferimento + inventario |
| Ricevi Archivio | stesso formato | Unisce inventario per ID stabili. **Non** importa additivamente le categorie/posizioni del file: solo **guarigione** da contenitori in arrivo |

- **Passo 2** (periodico): aggiornare contenitori e oggetti in famiglia.

Nessun master/slave: il file nella cartella condivisa è il riferimento; ogni membro può inviare o ricevere.

### 4bis.3 Pagina UI (B4.3)

Pagina unica **Condivisione Archivio** (`FamilyCatalogActivity` / `activity_family_catalog.xml`), non «Unione famiglia».

Due sezioni, griglia 2 colonne (Invia | Ricevi), card allineate a Utility:

- `MaterialCardView` altezza `180dp`, `layout_margin` `6dp`, padding contenitore `16dp`
- `cardCornerRadius` `16dp`, `cardElevation` `5dp`, sfondo `@color/elevated_surface`
- testo centrato `20sp` bold (telefono e tablet)
- nessuna card cartella/SFOGLIA: riuso cartella SAF (`KEY_FAMILY_SHARE`) dopo il primo CONSENTI (criterio Esporta/Backup); pulsante **Cartella** nel box nome file; box **Salvataggio completato.** con OK post-Invia (senza toast né messaggio inline)

Terminologia in pagina: **tabelle condivise**, **tabelle locali**, **categorie e posizioni**. Non usare «struttura».

---

## 5. Catalogo famiglia legacy (B1)

### 5.1 Formato file

```
formato;BoxManager_FamilyCatalog;1
sezione;CATEGORIE
nome;icona
…
sezione;POSIZIONI
nome
…
```

- Nome file proposto: `Catalogo_Famiglia_ddMMyy_HHmm.csv`.
- Ancora leggibile da Ricevi Archivio (solo tabelle condivise).

### 5.2 Semantica import catalogo

- Aggiunge categorie/posizioni **mancanti** (match nome case-insensitive).
- Duplicati: ignorati (nessun overwrite icona).
- Non cancella voci locali assenti dal file.
- Non tocca contenitori/oggetti.

---

## 6. Inventario famiglia legacy (B2)

### 6.1 Formato file

```
formato;BoxManager_FamilyInventory;1
sezione;CONTENITORI
permanentId;nome;categoria;posizione;lastModified
…
sezione;OGGETTI
objectPermanentId;boxPermanentId;tipo;descrizione;quantita;lastModified
…
```

- Nome file proposto: `Inventario_Famiglia_ddMMyy_HHmm.csv`.
- Ancora leggibile da Ricevi Archivio (solo inventario; categorie/posizioni guarite dai contenitori).

### 6.2 Semantica import inventario

- **Insert** se l'ID stabile non esiste in locale.
- **Update** se stesso ID e `lastModified` remoto > locale.
- **Conflitto** (anteprima, non sovrascritto) se payload diverso e remoto ≤ locale.
- **Ignora** se identico.
- **Delete** non propagato.
- Categoria/posizione devono esistere (in B3: guarigione automatica).

### 6.3 UI (flavor `famiglia`)

Pagina B2 (superata): Unione famiglia → Invia unione / Ricevi unione. In B4 la pagina è **Condivisione Archivio** (vedi §4bis).

---

## 7. Attribuzione — nome utente già in app

**Non introdurre un secondo “membro famiglia”.** Si riusa il **nome utente** già in Impostazioni (`SharedPreferences` chiave `username`), oggi usato come etichetta locale (e per il check admin Archivio completo).

| Uso | Comportamento previsto (B2/B3, flavor `famiglia`) |
|-----|--------------------------------------------------|
| Topbar | Mostra il nome salvato (non un default fisso) |
| Nuovo contenitore / oggetto | Alla creazione si memorizza `createdBy` = nome utente corrente (trim); immutabile dopo create salvo SI |
| Merge | Il campo viaggia nel pacchetto unione; utile per «chi ha censito», **non** per nascondere dati |
| Nome vuoto | Fallback `Utente` in UI; in beta famiglia si invita a impostare un nome **distinto** per ciascun familiare (es. Marco, Anna) |

Niente ACL: dopo il merge tutto resta dominio famiglia. Il nome serve a ripartire il lavoro e a leggere l’origine.

---

## 8. Fette

| Fetta | Deliverable | Play |
|-------|-------------|------|
| **B0** | Questa Nota + policy sync beta | No |
| **B1** | Catalogo famiglia export/import (legacy) + Guida + flavor | No |
| **B2** | Pacchetto inventario per ID (legacy) + anteprima | No |
| **B3** | Unione famiglia unificata (tabelle + inventario, guarigione) | No — superata da B4 |
| **B4** | **Tabelle condivise** + **Archivio** separati; UI card = Utility (B4.3); Invia/Ricevi SAF e feedback OK (B4.10) | No — **CONVALIDATO** 31/08/2026 (SI Renato, build 1.3-famigliaB4.10) |
| **B5** | Origine = **nome utente** Impostazioni su contenitori/oggetti; delete propagabile (tombstone + `CANCELLAZIONI`); T1 Backup Directory; T2 categoria lista (B5.3) | No — **CONVALIDATO** 31/08/2026 (B5.2); **T2 CONVALIDATO** 01/09/2026 (B5.3) |

---

## 9. Vincoli non negoziabili

- Flavor `famiglia`: `applicationId` `it.renatizzi.boxmanager.famiglia` — installazione **affiancata** a Play 1.2.
- Nessun upload AAB/APK `famiglia` sulla Console Play.
- Bugfix rilevati su 1.2: atterraggio su `main`, poi merge in branch famiglia (vedi `BETA_SYNC_POLICY.md`).
