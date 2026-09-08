# Prompt di continuità — sessione 07/09/2026 (Disco di rete + Foto + processo)

**Ingresso unico** per riprendere **questa** linea di lavoro. Non mescolare con filone M o Motore B.

**Identità:** una sola **BoxManager**. Non esiste un’«app famiglia». Fonte: [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md), `.cursor/rules/identita-app.mdc`.

### Backlog aperto (in evidenza — non è questa fetta)

Fonte viva: [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md) — **Checklist V1 in attesa fine test Google Play**.

| ID | Indicazione |
|----|-------------|
| *(nessuna voce B-* aperta)* | P2 sync continuo; M3 solo a test Play chiuso; B-FAMILY-DOMAIN-ERR opzionale |

---

## Stato al 07/09/2026

| Voce | Valore |
|------|--------|
| **Branch lavoro** | `cursor/qr-avanzato-analisi-8374` (analisi A1; base `cursor/sel-cartella-nas-d69a` / PR **#25**) |
| **Base tipica** | `cursor/promemoria-fix-d69a` (catena sviluppo; **non** `main`) |
| **Build sviluppo** | Topbar **1.3-famigliaB5.24** (versionCode **1343**) — etichetta di build, non nome app |
| **Play** | BoxManager **1.2** su `main` — test chiuso aperto; **identica** salvo bug bloccanti |
| **B-SEL-CARTELLA Disco di rete** | **CONVALIDATO** — Nota Integrata **9.2** Allegato **4.22** + Roadmap 07/09/2026 + [GUIDA_DISCO_RETE.md](GUIDA_DISCO_RETE.md) |
| **A1 QR avanzato** | **CONGELATO** — [REQUISITI_QR_AVANZATO.md](REQUISITI_QR_AVANZATO.md). Premium Progetto 2. Zero codice |
| **A2 Cestino** | **CONGELATO** — [REQUISITI_CESTINO.md](REQUISITI_CESTINO.md) (CONVALIDA documento SI 07/09). Zero codice |
| **Premium Progetto 2** | SI Renato 07/09: **A1** (R10) / **A2** (§0) / **A3 Foto** (R6+T6bis) = **Archivio completo** — verificato |
| **Foto oggetto** | Requisiti **congelati** + **CONVALIDA aggiornamento documento** 07/09/2026 — [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md); **zero codice** finché non si apre la fetta (dopo QR avanzato + Cestino) |
| **Processo analisi** | **4 fasi** + due CONVALIDA — regola `.cursor/rules/processo-analisi.mdc` — **CONVALIDATO** in documento 07/09/2026 |

---

## Cosa è stato chiuso in questa sessione

### 1. Disco di rete (B-SEL-CARTELLA) — CONVALIDATO

- Picker cartella Backup/export/share: SAF + provider terzi (**CIFS Documents Provider**); **niente** SMB nativo in V1.
- Impostazioni → card **Disco di rete** + assistente installazione/apertura CIFS.
- UX minimale (campi a–f) concordata con Renato; `<queries>` per visibilità package Android 11+.
- Backup `createFile` con estensione `.zip` (provider di rete non aggiungono MIME); Restore elenca anche `BCK_` senza estensione.
- Ricevi tabelle: errori bloccanti → dialog con titolo; niente doppio titolo «Importazione non riuscita».
- Device: File Explorer Plus **non** espone SMB a BoxManager; CIFS sì; spesso serve **Tutte le cartelle…**.
- Idea prodotto (non implementata): suggerire Host = gateway Wi‑Fi — solo con SI esplicito.
- **B5.24:** nomi CSV in salvataggio — estensione **sempre** `.csv` (omissione o altra estensione digitata → forzata), come `.zip` sul Backup; helper `CsvFileNames`.

### 2. Foto oggetto — ANALISI + FEEDBACK + CONVALIDA requisiti + **CONVALIDA aggiornamento documento**

Fonte congelata: [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md).  
Assessment storico: [ASSESSMENT_FOTO_MINIATURA.md](ASSESSMENT_FOTO_MINIATURA.md) (prevale il file requisiti su conflitti).  
**Passo 4 chiuso:** CONVALIDA aggiornamento documento SI Renato **07/09/2026** (incluso T1, T3–T5/T8, processo 4 fasi).

**Merito congelato (non riaprire senza SI):**

- Solo **oggetti** (contenitori restano QR); galleria + fotocamera; opzionale; niente vision/ML.
- Lista: thumb al posto icona fissa (`iconArea` 32dp); tap → anteprima display.
- Compressione: display **800px JPEG 75**, thumb **160px JPEG 70**.
- File su disco per `objectPermanentId` — **no** BLOB in Room.
- Persistenza: Backup ZIP + Invia/Ricevi Archivio ZIP; CSV V1 senza foto; estensione futura Import/Export **ZIP** con `photos/objects/`.
- **T1** allineamento DB (migrazione, orfani, delete/cestino, ricongiunzione).
- **T3–T5 / T8** tracciati file espliciti (`{id}.jpg` / `{id}_thumb.jpg`).

**Sequenza implementativa Progetto 2 (concordata):**  
1 QR avanzato → 2 Cestino → **3 Foto** → poi B/C/D (export/import avanzati, analytics, UI, eventuale SMB nativo più avanti).

### 3. Processo analisi — 4 fasi (obbligatorio d’ora in poi)

Regola: `.cursor/rules/processo-analisi.mdc` · anche in [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md).

1. **ANALISI** — bozza scritta, senza codice salvo SI  
2. **FEEDBACK** — chiarimenti e SI Renato in chat  
3. **CONVALIDA** — dell’**analisi** e dei **requisiti condivisi** (merito); nessun aggiornamento ufficiale prima  
4. **AGGIORNAMENTO DOCUMENTO** — scrittura/congelamento del documento; chiusura con **CONVALIDA dell’aggiornamento del documento**

Due CONVALIDA distinte: (3) merito analisi/requisiti · (4 chiusura) testo del documento aggiornato.

---

## Prossima sessione — cosa fare

| Priorità | Azione | Note |
|----------|--------|------|
| **STOP codice P2** | Niente implementazione A1/A2/A3 finché **test Play chiuso** (≈ ancora una settimana) **oppure** SI esplicito «apri fetta codice» | Il «Sì» del 07/09 sera era **solo conferma documenti/premium**, non apertura sviluppo. Commit A1 **revertato** sul branch lavoro; WIP su `cursor/qr-avanzato-wip-parked-8374`. |
| **A** | **FEEDBACK** D Dashboard/UI | Risposte D-Q1–D-Q7 in [ASSESSMENT_DASHBOARD_UI_AVANZATA.md](ASSESSMENT_DASHBOARD_UI_AVANZATA.md) (riassunto in chat se serve) |
| **B** | Checklist V1 | P2 sync bug Play → sviluppo; bug bloccanti solo su `main` |
| **C** | Codice A1/A2/A3 | **STOP** fino a fine test Play (salvo SI esplicito); WIP QR su `cursor/qr-avanzato-wip-parked-8374` |
| **D** | M3 / merge `main` | Solo a test Play **chiuso** |

### Sequenza agente (allineamento)

1. `git fetch origin` e checkout `cursor/qr-avanzato-analisi-8374` (o successore).
2. Leggere questo prompt + [PROMEMORIA](PROMEMORIA_INTERVENTI_TRASVERSALI.md) + [ASSESSMENT_QR_AVANZATO.md](ASSESSMENT_QR_AVANZATO.md) + regola `processo-analisi.mdc`.
3. In FEEDBACK: annotare SI sulle Q; **non** codice; **non** aggiornare Nota ufficiale prima della CONVALIDA merito.
4. Non toccare Foto in codice; non riaprire Disco di rete senza nuova evidenza.

---

## Documenti vincolanti

| File | Ruolo |
|------|--------|
| [REQUISITI_FOTO_OGGETTO.md](REQUISITI_FOTO_OGGETTO.md) | Freeze Foto — fonte fino all’implementazione |
| [REQUISITI_EXPORT_IMPORT_AVANZATO.md](REQUISITI_EXPORT_IMPORT_AVANZATO.md) | Requisiti B–C — in attesa CONVALIDA documento |
| [ASSESSMENT_EXPORT_IMPORT_AVANZATO.md](ASSESSMENT_EXPORT_IMPORT_AVANZATO.md) | Storico B–C Export/Import |
| [REQUISITI_CESTINO.md](REQUISITI_CESTINO.md) | Freeze A2 Cestino |
| [ASSESSMENT_CESTINO.md](ASSESSMENT_CESTINO.md) | Storico ANALISI/FEEDBACK A2 |
| [REQUISITI_QR_AVANZATO.md](REQUISITI_QR_AVANZATO.md) | Freeze A1 QR avanzato |
| [GUIDA_DISCO_RETE.md](GUIDA_DISCO_RETE.md) | Guida utente/dev Disco di rete |
| Nota Integrata **9.2** Allegato **4.22** | Fonte ufficiale Disco di rete |
| [PROMEMORIA_INTERVENTI_TRASVERSALI.md](PROMEMORIA_INTERVENTI_TRASVERSALI.md) | Checklist V1 + processo + backlog |
| [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md) | Una BoxManager; Play freeze |
| `.cursor/rules/processo-analisi.mdc` | 4 fasi + due CONVALIDA |
| `.cursor/rules/salvataggio-file.mdc` | Criterio cartelle/file |
| `.cursor/rules/annotazioni-renato.mdc` | Indicazioni fuori contesto → Promemoria |

Altri ingressi (filoni diversi):

- Correttivi / P2 sync → [PROMPT_CONTINUITA_CORRETTIVI.md](PROMPT_CONTINUITA_CORRETTIVI.md)
- Inglese M → [../multilingua/PROMPT_CONTINUITA_M.md](../multilingua/PROMPT_CONTINUITA_M.md)

---

## Vincoli non negoziabili

- **Processo 4 fasi** su ogni nuova voce requisiti/Progetto 2.
- Flavor `famiglia` = sviluppo; **non** su Play finché il test 1.2 è aperto.
- Room: **no** `fallbackToDestructiveMigration()`; `allowBackup=false`.
- Motore B / pipeline ricerca: **non** riaprire (B7 chiuso).
- SMB nativo: **fuori V1** (resta CIFS esterno).
- Chiusura voci solo con SI / CONVALIDA Renato.

---

## Comandi utili

```bash
cd BoxManagerNew
git fetch origin
git checkout cursor/qr-avanzato-analisi-8374
git pull origin cursor/qr-avanzato-analisi-8374

./gradlew :app:assembleFamigliaDebug
./gradlew :app:installFamigliaDebug
```

---

*Aggiornato 07/09/2026 (sera) — A1/A2/A3 requisiti congelati; codice P2 in STOP fino a fine test Play; fraintendimento «Sì»→codice corretto (revert).*
