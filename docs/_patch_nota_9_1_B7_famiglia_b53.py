# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: Allegato 4.20 — merge famiglia B0–B5 + correttivi B5.3.

Fonte da recepire (non riassumere): docs/famiglia/NOTA_B0_MERGE_FAMIGLIA.md,
ASSESSMENT_CORRETTIVI.md, STRATEGIA_UNIFICAZIONE.md, PROMEMORIA (T2 SI 01/09/2026).
La Nota su main resta l'unica fonte ufficiale; i markdown famiglia puntano qui.
"""
from __future__ import annotations

import re
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parent
DOC = ROOT / "Nota_Integrata_9.1_B7.docx"

W = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"
XML_SPACE = "{http://www.w3.org/XML/1998/namespace}space"

TITLE_420 = (
    "4.20 Merge famiglia B0–B5 e correttivi B5.3 (congelato 01/09/2026)"
)


def ptext(el: ET.Element) -> str:
    return "".join((t.text or "") for t in el.iter(W + "t")).strip()


def make_p(text: str) -> ET.Element:
    p = ET.Element(W + "p")
    r = ET.SubElement(p, W + "r")
    rpr = ET.SubElement(r, W + "rPr")
    sz = ET.SubElement(rpr, W + "sz")
    sz.set(W + "val", "20")
    szcs = ET.SubElement(rpr, W + "szCs")
    szcs.set(W + "val", "20")
    t = ET.SubElement(r, W + "t")
    t.set(XML_SPACE, "preserve")
    t.text = text
    return p


def replace_p_text(el: ET.Element, new_text: str) -> None:
    first = None
    for t in el.iter(W + "t"):
        if first is None:
            first = t
        else:
            t.text = ""
    if first is None:
        raise RuntimeError("No w:t")
    first.set(XML_SPACE, "preserve")
    first.text = new_text


def register_namespaces(xml: str) -> None:
    for prefix, uri in re.findall(r'xmlns:([A-Za-z0-9]+)="([^"]+)"', xml[:8000]):
        if prefix.lower().startswith("xml") or re.fullmatch(r"ns\d+", prefix):
            continue
        ET.register_namespace(prefix, uri)


def replace_startswith(
    children: list[ET.Element],
    start: str,
    new_text: str,
) -> int:
    for el in children:
        if ptext(el).startswith(start):
            replace_p_text(el, new_text)
            return 1
    raise RuntimeError(f"Not found: {start[:80]}")


def allegato_420_paragraphs(stamp: str) -> list[str]:
    return [
        TITLE_420,
        "CONVALIDATO. Fette B0–B5 (SI Renato, 31/08/2026). Correttivo T2 "
        "categoria/icona Lista Oggetti e Lista Oggetti Trovati CONVALIDATO "
        "01/09/2026 (SI Renato, ritest device, tre prove OK) su build "
        "1.3-famigliaB5.3 (versionCode 1322). Codice e UI solo flavor "
        "famiglia. Non pubblicare APK/AAB famiglia su Play Console. Play "
        "resta 1.2 (versionCode 3) su main — Allegato 4.19. Questo allegato "
        "è la sede ufficiale del merge famiglia; i markdown in docs/famiglia/ "
        "sono copie di lavoro e non prevalgono.",
        "Ambito: solo build flavor famiglia (betatest sideload). Track Play / "
        "Alpha: 1.2 su main. Durante il test chiuso Console su main si "
        "accettano solo bugfix tester; dopo ogni fix Play, merge main → "
        "branch famiglia. Mai invertire l’ordine. Mai caricare artifact "
        "famiglia sulla Console.",
        "Contesto prodotto. (1) In Setup si condividono categorie e luoghi "
        "abituali di custodia (cambiano solo per fatti eccezionali). "
        "(2) Ciò che personalizza la gestione sono contenitori e oggetti: "
        "ogni componente censisce ciò che conosce (uso comune + effetti "
        "personali). (3) Gli effetti «personali» restano di pubblico dominio "
        "familiare dopo l’unione (chi li inserisce ≠ chi può vederli). "
        "(4) Il merge serve a ripartire il peso del censimento, non a fare "
        "sync cloud continuo. Niente archivio inventariale cloud unico "
        "(fuori scope).",
        "Architettura a strati. A — Tabelle condivise: categorie + posizioni; "
        "setup una volta, modifiche eccezionali. B — Inventario: contenitori "
        "+ oggetti; lavoro quotidiano offline per membro. C — Condivisione: "
        "pacchetto esplicito (file), periodico. Cloud eventuale solo come "
        "canale di scambio file, non come archivio unico.",
        "Identificativi. Contenitore: permanentId (già in app / QR); deve "
        "viaggiare nel pacchetto unione. Oggetto: objectPermanentId (stesso "
        "ruolo). Categoria / posizione: nome normalizzato (trim + case-fold); "
        "catalogo famiglia, non ID UUID.",
        "Regole unione inventario. ID assente in archivio locale: Insert. "
        "Stesso ID, payload diverso: Update se lastModified remoto > locale; "
        "altrimenti ignora o anteprima conflitto. Stesso ID, identico: Ignora. "
        "Cancellazione: non propagata in automatico dalla sola assenza nel "
        "file; B5 famiglia: ogni delete locale scrive tombstone e viaggia in "
        "CANCELLAZIONI al prossimo Invia Archivio (archivio unico, nessuna "
        "opzione «solo qui»). Ripristino ZIP: resta replace wipe — vietato "
        "come strumento di unione.",
        "Il merge CSV V1 (ImportMergePlanner, insert-or-ignore per chiavi "
        "testo) resta ponte operativo e non sostituisce l’unione per ID; "
        "non va usato come sync di aggiornamenti.",
        "Formato file unione (BoxManager_FamilyMerge;1), separatore «;», "
        "UTF-8 con BOM. Sezioni: CATEGORIE (nome;icona); POSIZIONI (nome); "
        "CONTENITORI (permanentId;nome;categoria;posizione;lastModified;"
        "createdBy); OGGETTI (objectPermanentId;boxPermanentId;tipo;"
        "descrizione;quantita;lastModified;createdBy); CANCELLAZIONI "
        "(entityType;permanentId;deletedAt;deletedBy). createdBy e "
        "CANCELLAZIONI sono opzionali in lettura (file B4 senza colonna/"
        "sezione restano validi). Export B5+ li scrive sempre. Accetta "
        "anche file legacy B1 (BoxManager_FamilyCatalog) e B2 "
        "(BoxManager_FamilyInventory).",
        "Nomi file proposti. B4: Tabelle_Condivise_ddMMyy_HHmm.csv e "
        "Condivisione_Archivio_ddMMyy_HHmm.csv. Storico B3: "
        "Unione_Famiglia_ddMMyy_HHmm.csv. Legacy B1: "
        "Catalogo_Famiglia_ddMMyy_HHmm.csv. Legacy B2: "
        "Inventario_Famiglia_ddMMyy_HHmm.csv.",
        "Condivisione Archivio in due passi (B4 CONVALIDATO, build "
        "1.3-famigliaB4.10). Pagina unica Condivisione Archivio "
        "(FamilyCatalogActivity), non «Unione famiglia». Nessun "
        "master/slave: il file nella cartella condivisa è il riferimento; "
        "ogni membro può inviare o ricevere. Terminologia: tabelle "
        "condivise, tabelle locali, categorie e posizioni. Non usare "
        "«struttura».",
        "Passo 1 / passo 3 — Tabelle condivise (categorie e posizioni). "
        "Invia: esporta le tabelle locali (formato BoxManager_FamilyCatalog). "
        "Ricevi: allinea/sostituisce le tabelle locali al file condiviso "
        "(anteprima SI/NO). Blocca la rimozione se contenitori locali usano "
        "ancora quella categoria/posizione. Passo 1 = setup famiglia. "
        "Passo 3 = ripristino dopo reinstallazione o correzione errori nelle "
        "tabelle locali.",
        "Passo 2 — Archivio (contenitori e oggetti), periodico. Invia "
        "Archivio: esporta tabelle di riferimento + inventario (formato "
        "BoxManager_FamilyMerge). Ricevi Archivio: unisce inventario per ID "
        "stabili. Non importa additivamente le categorie/posizioni del file: "
        "solo guarigione da contenitori in arrivo (se un contenitore "
        "referenzia categoria/posizione assente in locale, la voce viene "
        "ricreata prima dell’inventario; icona categoria = default).",
        "Semantica inventario per ID: Insert se l’ID stabile non esiste in "
        "locale; Update se stesso ID e lastModified remoto > locale; "
        "Conflitto (anteprima, non sovrascritto) se payload diverso e remoto "
        "≤ locale; Ignora se identico. Delete solo via tombstone / sezione "
        "CANCELLAZIONI (B5), non automatico dalla sola assenza nel file.",
        "UI pagina (B4.3–B4.10). Due sezioni, griglia 2 colonne (Invia | "
        "Ricevi), card allineate a Utility: MaterialCardView altezza 180dp, "
        "layout_margin 6dp, padding contenitore 16dp, cardCornerRadius 16dp, "
        "cardElevation 5dp, sfondo elevated_surface, testo centrato 20sp "
        "bold (telefono e tablet). Nessuna card cartella/SFOGLIA: riuso "
        "cartella SAF (KEY_FAMILY_SHARE) dopo il primo CONSENTI (criterio "
        "Esporta/Backup); pulsante Cartella nel box nome file; box "
        "Salvataggio completato. con OK post-Invia (senza toast né messaggio "
        "inline).",
        "Catalogo famiglia legacy B1. Formato BoxManager_FamilyCatalog;1. "
        "Sezioni CATEGORIE (nome;icona) e POSIZIONI (nome). Semantica "
        "import: aggiunge categorie/posizioni mancanti (match nome "
        "case-insensitive); duplicati ignorati (nessun overwrite icona); "
        "non cancella voci locali assenti dal file; non tocca contenitori/"
        "oggetti. Ancora leggibile da Ricevi Archivio (solo tabelle "
        "condivise).",
        "Inventario famiglia legacy B2. Formato BoxManager_FamilyInventory;1. "
        "Sezioni CONTENITORI (permanentId;nome;categoria;posizione;"
        "lastModified) e OGGETTI (objectPermanentId;boxPermanentId;tipo;"
        "descrizione;quantita;lastModified). Ancora leggibile da Ricevi "
        "Archivio (solo inventario; categorie/posizioni guarite dai "
        "contenitori). Delete non propagato in B2. Pagina B2 (Invia unione / "
        "Ricevi unione) superata da Condivisione Archivio in B4.",
        "Attribuzione — nome utente già in app. Non introdurre un secondo "
        "«membro famiglia». Si riusa il nome utente già in Impostazioni "
        "(SharedPreferences chiave username), oggi usato come etichetta "
        "locale e per il check admin Archivio completo. Topbar: mostra il "
        "nome salvato. Nuovo contenitore/oggetto: alla creazione si "
        "memorizza createdBy = nome utente corrente (trim); immutabile dopo "
        "create salvo SI. Merge: il campo viaggia nel pacchetto; utile per "
        "«chi ha censito», non per nascondere dati. Nome vuoto: fallback "
        "Utente in UI; in beta famiglia si invita a impostare un nome "
        "distinto per ciascun familiare. Niente ACL: dopo il merge tutto "
        "resta dominio famiglia.",
        "Fette. B0: questa Nota di prodotto + policy sync beta. B1: catalogo "
        "export/import (legacy) + Guida + flavor. B2: pacchetto inventario "
        "per ID (legacy) + anteprima. B3: unione unificata (tabelle + "
        "inventario, guarigione) — superata da B4. B4: tabelle condivise + "
        "archivio separati; UI card = Utility; Invia/Ricevi SAF e feedback "
        "OK — CONVALIDATO 31/08/2026, build 1.3-famigliaB4.10. B5: origine = "
        "nome utente Impostazioni su contenitori/oggetti; delete propagabile "
        "(tombstone + CANCELLAZIONI); T1 Backup Directory — CONVALIDATO "
        "31/08/2026, build 1.3-famigliaB5.2. Nessuna fetta famiglia su Play.",
        "Vincoli non negoziabili. Flavor famiglia: applicationId "
        "it.renatizzi.boxmanager.famiglia — installazione affiancata a Play "
        "1.2. FAMILY_BETA / UI famiglia solo su flavor famiglia. Room: no "
        "fallbackToDestructiveMigration(); allowBackup=false. Nessun upload "
        "AAB/APK famiglia sulla Console Play. Bugfix rilevati su 1.2: "
        "atterraggio su main, poi merge nel branch famiglia.",
        "Correttivi P0 (beta famiglia). T1 Backup Directory — nome cartella "
        "illeggibile (id opaco SAF): CONVALIDATO B5.2 (SI Renato, 31/08/2026); "
        "SafFolderLabel via DISPLAY_NAME. T2 Lista Oggetti / Lista Oggetti "
        "Trovati — categoria o icona assenti o diverse in stampa/export: "
        "root cause race BoxDetailActivity (box prima delle categorie) e "
        "SearchResultActivity export senza risoluzione categoryId; fix "
        "refreshHeader e resolveCategoryForGroup in B5.3. Ritest device "
        "01/09/2026, tre prove OK (SI Renato): (1) Contenitore → Lista "
        "Oggetti, header categoria + icona al primo caricamento; (2) "
        "Dashboard → ambito Oggetti → Lista Oggetti Trovati, categoria "
        "corretta per ogni gruppo contenitore; (3) Stampa e Esporta da (2), "
        "stessa categoria a schermo. T3: secondo bug citato in chat non "
        "recuperato — chiuso; riaprire solo con nuova evidenza.",
        "P1 igiene salvataggio file (regola B7, criterio Esporta già "
        "convalidato): aperto dopo chiusura P0. Nome proposto datato "
        "(prefisso_ddMMyy_HHmm); riuso cartella dopo primo CONSENTI Android; "
        "box unico nome + domanda + SI/NO; Modello_Importazione.csv resta "
        "nome fisso. Già OK: Backup, Esporta vista Play, Import auto-backup, "
        "Invia/Ricevi famiglia B4. Da rivedere: PRE_RESTORE (nome non "
        "editabile); Genera Modello (usa cartella import/export, Nota B7 "
        "chiede riuso cartella Backup). Etichetta QR → PDF: flusso dedicato, "
        "fuori criterio Esporta. Nessun blocco per closed test Play.",
        "P2 sync continuo. Play = flavor play su main, 1.2.x. Famiglia = "
        "flavor famiglia sul branch di unione, 1.3-famigliaB5.x. Unificazione "
        "in questa fase = stesso motore bugfix su main riportato su famiglia, "
        "non un solo APK. Fix nato solo su famiglia: cherry-pick su main solo "
        "se lo stesso bug esiste in Play. Filone M Scelta lingua (Nota 3.6.6 "
        "Impostazioni, Prossime implementazioni) resta fuori da questo "
        "allegato — da pianificare a parte.",
        f"Aggiornamento documentale sidecar 9.1_B7 Allegato 4.20 "
        f"(merge famiglia B0–B5 + T2 B5.3) {stamp}.",
    ]


def main() -> None:
    if not DOC.exists():
        raise SystemExit(f"Missing {DOC}")
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    with zipfile.ZipFile(DOC) as zin:
        names = zin.namelist()
        raw = zin.read("word/document.xml").decode("utf-8")
        data = {n: zin.read(n) for n in names}
    register_namespaces(raw)
    root = ET.fromstring(raw.encode("utf-8"))
    body = root.find(W + "body")
    if body is None:
        raise SystemExit("No body")
    children = list(body)
    n = 0

    n += replace_startswith(
        children,
        "Questa è l'unica Roadmap ufficiale. Quadro al ",
        "Questa è l'unica Roadmap ufficiale. Quadro al 01/09/2026. "
        "I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
        "in caso di conflitto prevale questo quadro.",
    )
    n += replace_startswith(
        children,
        "ROADMAP DEL PROGETTO AL ",
        "ROADMAP DEL PROGETTO AL 01/09/2026",
    )
    n += replace_startswith(
        children,
        "Stato al ",
        "Stato al 01/09/2026. Flusso core V1 chiuso (D0–B7; Allegati 4.7–4.18; "
        "AppShell 24/08/2026). Google Play — fase binario e accesso Archivio "
        "completo CONVALIDATA (Allegato 4.19): applicationId "
        "it.renatizzi.boxmanager; Play 1.2 (versionCode 3) in test chiuso "
        "Console; su main solo bugfix tester; prova a tempo (default 14 gg); "
        "rinnovo via CONDIVIDI (default +7 gg / N amici); codice locale; "
        "parametri admin solo «Renato Stefanizzi»; Google Billing congelato "
        "(vincolo fiscale). Merge famiglia B0–B5 CONVALIDATO (Allegato 4.20): "
        "flavor famiglia, applicationId it.renatizzi.boxmanager.famiglia, "
        "build 1.3-famigliaB5.3; T2 CONVALIDATO 01/09/2026 (SI Renato); "
        "non pubblicare famiglia su Play. Roadmap vigente ora (Progetto 1 — "
        "Play): closed test in corso; dopo 1–2 mesi di utilizzo, rivalutare "
        "donazioni (Ko-fi / Buy Me a Coffee) o micro-AdMob — non IAP finché "
        "non c’è quadro fiscale. Progetto 2 — V2 (dopo Play verde), dettaglio "
        "in 4.1.6: QR avanzato / stampa multipla; cestino; foto miniatura; "
        "ricerca data/KPI solo via pipeline 0–10; storico; condivisione "
        "selettiva file. PATTERN_010 resta SOSPESO. Motore B già in V1 per "
        "F7–F9 / CATEGORY / LOCATION (4.17); evoluzioni ulteriori Motore B "
        "restano fuori dal primo Play. Filone M Scelta lingua resta in 3.6.6. "
        "Vietato anticipare un blocco successivo prima della convalida utente "
        "(sez. 1.11).",
    )
    n += replace_startswith(
        children,
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso.",
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso. "
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B F7–F9 / CATEGORY / LOCATION CONVALIDATI 23/08/2026 "
        "(Allegato 4.17). Inventario e stampa contestuale Ricerca avanzata "
        "CONVALIDATI 23/08/2026 (Allegato 4.18). Google Play accesso Archivio "
        "completo CONVALIDATO 26/08/2026 (Allegato 4.19). Merge famiglia "
        "B0–B5 CONVALIDATO 31/08/2026 e T2 CONVALIDATO 01/09/2026 "
        "(Allegato 4.20) — solo flavor famiglia, non su Play. Play 1.2 in "
        "test chiuso; su main solo bugfix tester. PATTERN_010 resta SOSPESO. "
        "4.1.6 resta NON core (backlog V2) salvo quanto già congelato in "
        "4.20. Filone M Scelta lingua resta in 3.6.6.",
    )
    n += replace_startswith(
        children,
        "AppShell globale, context card e navigazione tab CONVALIDATI "
        "24/08/2026 (chiusura V1). Google Play — Archivio completo e accesso "
        "a tempo CONVALIDATI 26/08/2026 (Allegato 4.19).",
        "AppShell globale, context card e navigazione tab CONVALIDATI "
        "24/08/2026 (chiusura V1). Google Play — Archivio completo e accesso "
        "a tempo CONVALIDATI 26/08/2026 (Allegato 4.19). Merge famiglia B0–B5 "
        "e T2 B5.3 CONVALIDATI (Allegato 4.20): solo flavor famiglia, non "
        "core Play. Freeze aree mature, regression scope e matrice impatti "
        "restano governance di progetto, non funzioni utente. Non sono fette "
        "V2 applicative.",
    )
    n += replace_startswith(
        children,
        "Prossimo (Roadmap vigente, Progetto 1):",
        "Prossimo (Roadmap vigente, Progetto 1): Play 1.2 in test chiuso "
        "Console; su main solo bugfix tester. Dopo 1–2 mesi di utilizzo: "
        "rivalutare donazioni (Ko-fi / Buy Me a Coffee) o micro-AdMob. "
        "Billing IAP solo se/quando il quadro fiscale lo consente. Merge "
        "famiglia = Allegato 4.20 (flavor famiglia, mai su Console). "
        "P1 igiene file aperto dopo T2. Motore B evoluzioni oltre 4.17 e "
        "voci 4.1.6 = Progetto 2 dopo Play verde. Filone M Scelta lingua "
        "(3.6.6) da pianificare a parte.",
    )

    already = any(ptext(el).startswith("4.20 Merge famiglia") for el in children)
    insert_at = None
    for i, el in enumerate(children):
        if ptext(el).startswith("4.14 Storico allineamento pipeline"):
            insert_at = i
            break
    if insert_at is None:
        raise SystemExit("4.14 not found")
    if not already:
        extra = [make_p(text) for text in allegato_420_paragraphs(stamp)]
        for offset, p in enumerate(extra):
            children.insert(insert_at + offset, p)
        n += len(extra)

    for child in list(body):
        body.remove(child)
    for child in children:
        body.append(child)

    xml_out = ET.tostring(root, encoding="unicode")
    if not xml_out.startswith("<?xml"):
        xml_out = (
            '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' + xml_out
        )
    data["word/document.xml"] = xml_out.encode("utf-8")
    with zipfile.ZipFile(DOC, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    print("Patched", DOC, "updates", n, "already_420", already)


if __name__ == "__main__":
    main()
