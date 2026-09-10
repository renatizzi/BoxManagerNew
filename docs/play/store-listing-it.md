# BoxManager — Play Store listing (Italiano)

**Stato:** bozza M0.5 per **cutover M3** (classe I → ufficiale **1.3**).  
**Package:** `it.renatizzi.boxmanager`  
**Lingua primaria Store:** italiano (tenere); inglese = lingua aggiuntiva (`store-listing-en.md`).  
**Fonte:** [PROMPT_CONTINUITA_M3_MERGE_PLAY.md](../famiglia/PROMPT_CONTINUITA_M3_MERGE_PLAY.md) §2.3 (CONVALIDATO 10/09/2026).

---

## Descrizione breve (≤ 80 caratteri)

Organizza contenitori e oggetti. Trova tutto con una domanda semplice.

## Descrizione completa

BoxManager ti aiuta a ricordare dove hai messo le cose.

Crea contenitori, assegna categorie e posizioni, e elenca gli oggetti in ciascuno. Con la fotocamera leggi l’etichetta QR di un contenitore e lo apri subito — anche in cantina o garage.

La ricerca semplice filtra l’elenco mentre digiti. La ricerca avanzata sull’archivio (prova / Archivio completo) capisce domande in linguaggio naturale in italiano o in inglese, ad esempio «Dov’è il trapano?» o «Trova contenitore box», senza tradurre i nomi che hai già inserito nell’archivio.

In Impostazioni scegli la lingua dell’app (italiano o inglese). I nomi di oggetti e contenitori restano come li hai scritti: BoxManager non traduce i tuoi dati.

Backup e ripristino dell’archivio, import/export CSV, stampa o condivisione della vista. La condivisione archivio consente a più persone della stessa casa di tenere allineati cataloghi e inventari. Puoi anche salvare Backup e CSV su una cartella di rete (CIFS / disco mappato) quando il dispositivo la raggiunge.

Pensata per uso personale e domestico.

## Novità della release — M3 / 1.3

Novità in questa versione:

- Lingua app italiano e inglese (Impostazioni)
- Ricerca avanzata anche in inglese (stessa pipeline dell’archivio)
- Archivio condiviso in famiglia
- Disco di rete (CIFS) per Backup / CSV
- Correttivi e stabilità

## Note Console

- Nome app: **BoxManager**
- Icona / feature graphic / screenshot: `docs/play/README.md`
- Aggiornare o aggiungere almeno uno screenshot con UI EN o Impostazioni → lingua (senza dati personali)
- **Non** sostituire `strings.xml` / tabelle ricerca EN con «Traduci con Play Console» (C12)
- **Mai** caricare AAB flavor `famiglia` / applicationId `.famiglia` (C8)
- Se resta test chiuso: codice `BOXMANAGER-TESTER` (messaggio in README)
