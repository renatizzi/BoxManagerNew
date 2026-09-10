# -*- coding: utf-8 -*-
"""Nota Integrata 9.2 — recepimento B-PIANO-RILASCIO-RP (SI Renato 08/09/2026).

- Quadro Roadmap 4.1 → 08/09/2026 (classe I pronta M3; P2 requisiti congelati)
- §4.1.6: stato U0/D; rinvii REQUISITI; voci CESTINO, FOTO, QUANTITÀ
- Allegato 4.21 (EN) riportato da 9.1_B7 e allineato a M2 CONVALIDATO
- Allegato 4.23 Progetto 2 requisiti + piano rilascio RP

Fonte decisioni: docs/famiglia/REQUISITI_*.md (congelati) + REQUISITI_PIANO_RILASCIO_ROADMAP.md.
Zero codice applicativo.
"""
from __future__ import annotations

import re
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parent
DOC = ROOT / "Nota_Integrata_9.2.docx"

W = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"
XML_SPACE = "{http://www.w3.org/XML/1998/namespace}space"
STAMP = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")


def ptext(el: ET.Element) -> str:
    return "".join((t.text or "") for t in el.iter(W + "t")).strip()


def register_namespaces(xml: str) -> None:
    for prefix, uri in re.findall(r'xmlns:([A-Za-z0-9]+)="([^"]+)"', xml[:8000]):
        if prefix.lower().startswith("xml") or re.fullmatch(r"ns\d+", prefix):
            continue
        ET.register_namespace(prefix, uri)


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


def replace_if_startswith(
    children: list[ET.Element],
    start: str,
    new_text: str,
) -> int:
    for el in children:
        if ptext(el).startswith(start):
            replace_p_text(el, new_text)
            return 1
    return 0


def make_title(level: int, text: str) -> ET.Element:
    p = ET.Element(W + "p")
    ppr = ET.SubElement(p, W + "pPr")
    style = ET.SubElement(ppr, W + "pStyle")
    style.set(W + "val", f"Titolo{level}")
    r = ET.SubElement(p, W + "r")
    t = ET.SubElement(r, W + "t")
    t.set(XML_SPACE, "preserve")
    t.text = text
    return p


def make_normale(text: str) -> ET.Element:
    p = ET.Element(W + "p")
    ppr = ET.SubElement(p, W + "pPr")
    style = ET.SubElement(ppr, W + "pStyle")
    style.set(W + "val", "Normale")
    r = ET.SubElement(p, W + "r")
    t = ET.SubElement(r, W + "t")
    t.set(XML_SPACE, "preserve")
    t.text = text
    return p


def find_idx(children: list[ET.Element], start: str) -> int:
    for i, el in enumerate(children):
        if ptext(el).startswith(start):
            return i
    raise RuntimeError(f"Not found: {start[:80]}")


def find_exact(children: list[ET.Element], exact: str) -> int:
    for i, el in enumerate(children):
        if ptext(el) == exact:
            return i
    raise RuntimeError(f"Exact not found: {exact[:80]}")


def insert_after(children: list[ET.Element], after_exact: str, elems: list[ET.Element]) -> int:
    i = find_exact(children, after_exact)
    for j, el in enumerate(elems):
        children.insert(i + 1 + j, el)
    return len(elems)


def main() -> None:
    if DOC.exists() is False:
        raise SystemExit(f"Missing {DOC}")

    # Idempotenza: se 4.23 già presente, stop
    with zipfile.ZipFile(DOC) as zin:
        probe = zin.read("word/document.xml").decode("utf-8")
    if "4.23 Progetto 2" in probe:
        raise SystemExit("Allegato 4.23 già presente — patch già applicata?")

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

    # --- Quadro 4.1 ---
    n += replace_if_startswith(
        children,
        "Questa è l'unica Roadmap ufficiale (Nota Integrata 9.2). Quadro al ",
        "Questa è l'unica Roadmap ufficiale (Nota Integrata 9.2). Quadro al "
        "08/09/2026. I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
        "in caso di conflitto prevale questo quadro. Recepimento analisi "
        "Progetto 2 e piano rilascio: Allegato 4.23. Filone EN: Allegato 4.21.",
    )

    n += replace_if_startswith(
        children,
        "ROADMAP DEL PROGETTO AL ",
        "ROADMAP DEL PROGETTO AL 08/09/2026",
    )

    n += replace_if_startswith(
        children,
        "Stato al 07/09/2026 (Nota Integrata 9.2).",
        "Stato al 08/09/2026 (Nota Integrata 9.2). Flusso core V1 chiuso "
        "(D0–B7; Allegati 4.7–4.18; AppShell 24/08/2026). Google Play — "
        "fase binario e accesso Archivio completo CONVALIDATA "
        "(Allegato 4.19): applicationId it.renatizzi.boxmanager; "
        "Play 1.2 (versionCode 3) in test chiuso Console; su main solo "
        "bugfix tester; prova a tempo (default 14 gg); rinnovo via "
        "CONDIVIDI; codice locale; Google Billing congelato. Merge "
        "famiglia B0–B5 CONVALIDATO (Allegato 4.20): flavor famiglia. "
        "Filone M Scelta lingua + ricerca EN CONVALIDATO (CK1–CK2; "
        "Allegato 4.21). Disco di rete CONVALIDATO 07/09/2026 "
        "(Allegato 4.22). B5.24 estensione .csv forzata in salvataggio "
        "CSV CONVALIDATA (build 1.3-famigliaB5.24). P1 igiene file "
        "CONVALIDATO (B5.7). Classe I (già in codice sviluppo: archivio "
        "condiviso, EN, disco, correttivi) pronta per cutover M3 a fine "
        "test (R1 — Allegato 4.23 / REQUISITI_PIANO_RILASCIO_ROADMAP). "
        "Roadmap vigente ora (Progetto 1 — Play): closed test in corso; "
        "dopo 1–2 mesi di utilizzo, rivalutare donazioni o micro-AdMob — "
        "non IAP finché non c’è quadro fiscale. A test Play chiuso: la "
        "BoxManager di sviluppo sostituisce la 1.2 (M3). Progetto 2 — "
        "requisiti congelati (zero codice fino a fine Play): dettaglio "
        "4.1.6 + Allegato 4.23; sequenza A1→A2→A3→B–C; D no-code; "
        "B-QTY dopo; rilascio P2 in più versioni Play (R2). PATTERN_010 "
        "resta SOSPESO. Motore B già in V1 per F7–F9 / CATEGORY / "
        "LOCATION (4.17). Vietato anticipare un blocco successivo prima "
        "della convalida utente (sez. 1.11).",
    )

    n += replace_if_startswith(
        children,
        "Prossimo (Roadmap vigente, Progetto 1): Play 1.2 in test chiuso",
        "Prossimo (Roadmap vigente, Progetto 1): Play 1.2 in test chiuso "
        "Console; su main solo bugfix tester. Dopo 1–2 mesi di utilizzo: "
        "rivalutare donazioni (Ko-fi / Buy Me a Coffee) o micro-AdMob. "
        "Billing IAP solo se/quando il quadro fiscale lo consente. "
        "Merge famiglia = Allegato 4.20. Scelta lingua / EN = Allegato "
        "4.21 CONVALIDATO. Disco di rete = Allegato 4.22 CONVALIDATO. "
        "P1 igiene file CONVALIDATO. A test chiuso: M3 cutover (R1) poi "
        "sessione Console C1–C8 (Allegato 4.23). Motore B evoluzioni oltre "
        "4.17 e voci 4.1.6 = Progetto 2 dopo Play verde (sequenza e "
        "premium in Allegato 4.23).",
    )

    # --- 4.1.6 annotazioni su voci esistenti ---
    n += insert_after(
        children,
        "indicatori avanzati",
        [
            make_normale(
                "Stato 08/09/2026 (REQUISITI_DASHBOARD_UI_AVANZATA): "
                "decisione U0 — nessun codice ora; forma futura S1 "
                "(conteggi anagrafici) solo con SI di apertura; se S1 → "
                "premium Archivio completo; vietato usare D per Motore B / "
                "PATTERN_010. Dettaglio: Allegato 4.23."
            )
        ],
    )

    n += insert_after(
        children,
        "gestione batch",
        [
            make_normale(
                "Requisiti congelati 07/09/2026: "
                "docs/famiglia/REQUISITI_QR_AVANZATO.md (batch etichette "
                "L0+L1, PDF unico, share; nome QR_ddMMyy_HHmm.pdf; "
                "premium). Zero codice fino a fine test Play / SI «apri "
                "fetta codice». Sintesi: Allegato 4.23. Baseline V1: "
                "§3.4.4 + Allegato 4.8 invariati."
            )
        ],
    )

    n += insert_after(
        children,
        "gestione file avanzata",
        [
            make_normale(
                "Requisiti congelati 08/09/2026: "
                "docs/famiglia/REQUISITI_EXPORT_IMPORT_AVANZATO.md "
                "(selezione E3, Utility Esporta dati, CSV primario verso "
                "Sheets/Excel, ZIP complementare con Foto; non core; "
                "premium EXPORT). Zero codice fino a SI. Allegato 4.23."
            )
        ],
    )

    n += insert_after(
        children,
        "gestione errori dettagliata",
        [
            make_normale(
                "Requisiti congelati 08/09/2026: stesso file "
                "REQUISITI_EXPORT_IMPORT_AVANZATO.md (validazione V3, "
                "errori riga-per-riga G1, niente apply parziale; premium "
                "IMPORT). Zero codice fino a SI. Allegato 4.23."
            )
        ],
    )

    n += insert_after(
        children,
        "micro-interazioni",
        [
            make_normale(
                "Stato 08/09/2026: U0 con Dashboard — nessuna micro-"
                "interazione / header dinamico in Progetto 2 per questa "
                "voce (REQUISITI_DASHBOARD_UI_AVANZATA). Swipe già fuori "
                "V2. Allegato 4.23."
            ),
            # Nuove voci Roadmap assenti prima
            make_normale("CESTINO (soft-delete)"),
            make_normale("Priorità: medio-alta (dopo QR avanzato)"),
            make_normale(
                "soft-delete contenitori e oggetti · ripristino · "
                "svuota / scadenza 30 gg · ingresso Utility · premium"
            ),
            make_normale(
                "Requisiti congelati 07/09/2026: "
                "docs/famiglia/REQUISITI_CESTINO.md. Distinto dai "
                "tombstone famiglia (B5). Zero codice fino a SI. "
                "Allegato 4.23."
            ),
            make_normale("FOTO OGGETTO"),
            make_normale("Priorità: media (dopo QR avanzato e Cestino)"),
            make_normale(
                "foto opzionale per oggetto · galleria/fotocamera · "
                "miniatura in lista · Backup/ZIP · premium"
            ),
            make_normale(
                "Requisiti congelati 07/09/2026: "
                "docs/famiglia/REQUISITI_FOTO_OGGETTO.md. Contenitori "
                "restano sul QR. Niente vision/ML. Zero codice fino a SI. "
                "Allegato 4.23."
            ),
            make_normale("QUANTITÀ — KPI / RICERCA (B-QTY)"),
            make_normale("Priorità: dopo A1–A3 e B–C (non apre Dashboard D)"),
            make_normale(
                "quantità resta facoltativa · KPI somma pezzi (null=0) · "
                "pochi pattern Motore B · premium · frasi catalogo 2.6 "
                "solo con elenco SI pre-codice"
            ),
            make_normale(
                "Requisiti congelati 08/09/2026: "
                "docs/famiglia/REQUISITI_QTY_KPI_SEARCH.md (K1+K2; K3 no). "
                "Zero codice fino a SI. Allegato 4.23."
            ),
        ],
    )

    # --- Allegato 4.21 prima di 4.22 ---
    idx_422 = find_idx(
        children, "4.22 Disco di rete / B-SEL-CARTELLA"
    )
    allegato_421 = [
        make_title(
            2,
            "4.21 Tabelle EN ricerca avanzata / filone M "
            "(congelato 08/09/2026 in 9.2)",
        ),
        make_normale(
            "CONVALIDATO filone M (CK1–CK2, 03–04/09/2026). Riportato in "
            "Nota 9.2 il 08/09/2026 (prima solo in 9.1_B7). Pipeline 0–10 "
            "invariata. Solo IT + EN in V1. Dati utente non si traducono. "
            "Fonte viva di supporto: docs/multilingua/BOZZA_TABELLE_EN_CK0.md. "
            "Matrice Core = 1.3.3. Alias EN = una sola parola (il motore "
            "spezza sugli spazi)."
        ),
        make_normale(
            "OBJECT EN (ordine 1.3.3): object, article, item, utensil, "
            "thing, affair, stuff, product, tool."
        ),
        make_normale(
            "BOX EN (ordine 1.3.3): container, box, boxes, box, carton, "
            "crate, pack, trunk, envelope, mailer, drawer, jar, vase, "
            "basin, receptacle, chest, coffer, bin, dumpster, safe, "
            "wallet, organizer, jewelbox, briefcase, container, wrapping, "
            "case, cover, packaging, closet,wardrobe, cabinet, bookcase, "
            "shelf."
        ),
        make_normale(
            "LOCATION EN (ordine 1.3.3): location, place, spot, location, "
            "site, area, zone, perimeter, space, room, city, town, "
            "locality, point. «locale» non è in 1.3.3; resta D8 "
            "(Allegato 4.3 / codice)."
        ),
        make_normale(
            "CATEGORY EN (ordine 1.3.3): category, class, classification, "
            "group, aggregate, grouping, species, family, order, "
            "division, grade, tier, type, typology, quality, kind."
        ),
        make_normale(
            "Perifrasi EN (1.3.3): OBJECT which; BOX which, in which, "
            "where; LOCATION where, in which; CATEGORY which, in which, "
            "to which."
        ),
        make_normale(
            "Indicatori EN (esempi 3.3.5 senza «ecc.»): confronto "
            "identical, same, duplicate, different, different, "
            "comparison; aggregazione all, list, which; dove → where. "
            "doppione (variante ufficiale F7, non in 3.3.5) → duplicate."
        ),
        make_normale(
            "Messaggi ricerca EN (catalogo 2.6 / Allegato 4.5, elenco "
            "intero): Analyzing the request… / I cannot understand "
            "exactly what you are looking for. Try rephrasing the "
            "request with clearer words. / Tap here to return to the "
            "Dashboard / I did not understand the request. / Can you "
            "phrase the request more precisely? / No results found. / "
            "This type of request is not yet available. I tre testi già "
            "in values-en (M1) non si riscrivono."
        ),
        make_normale(
            "F7 EN (cinque varianti 4.17, elenco intero): Search all the "
            "containers that contain duplicates; In which containers are "
            "there identical objects; List of the containers that have "
            "identical objects; Where do I find the same type of "
            "objects; Find the containers that have at least one "
            "identical object. Heading: List of the containers that have "
            "identical objects."
        ),
        make_normale(
            "F8 EN (quattro varianti 4.17, elenco intero): Search the "
            "containers with a different category that contain the same "
            "type of object; Which containers have a different category "
            "and contain identical objects; Find containers with a "
            "different category and identical objects; List of "
            "containers with a different category and identical objects. "
            "Heading: List of the containers that have a different "
            "category and contain identical objects. Non importare le "
            "varianti extra della famiglia F8 nel codice IT."
        ),
        make_normale(
            "CSV V1: header di Modello_Importazione.csv restano italiani; "
            "il nome file resta fisso. PATTERN_005 / F5 resta BACKLOG V2. "
            "PATTERN_010 resta SOSPESO. F1–F6 / F9 EN: bozza markdown "
            "supporto CK2, metodo 1:1 da Allegato 1 §1.1 / 4.17."
        ),
        make_normale(
            f"Aggiornamento documentale Nota Integrata 9.2 Allegato 4.21 "
            f"(EN / filone M) {STAMP}."
        ),
    ]
    for j, el in enumerate(allegato_421):
        children.insert(idx_422 + j, el)
    n += len(allegato_421)

    # --- Allegato 4.23 prima di sectPr ---
    sect = next(
        i for i, el in enumerate(children) if el.tag.endswith("}sectPr")
    )
    allegato_423 = [
        make_title(
            2,
            "4.23 Progetto 2 — requisiti congelati e piano rilascio "
            "(congelato 08/09/2026)",
        ),
        make_normale(
            "CONVALIDATO recepimento SI Renato 08/09/2026 "
            "(B-PIANO-RILASCIO-RP). Merito funzionale: file "
            "docs/famiglia/REQUISITI_*.md (prevalgono su conflitti). "
            "Questo allegato non riapre il merito; non autorizza codice "
            "applicativo durante il test Play 1.2."
        ),
        make_normale(
            "Classi. I — già in codice sviluppo (archivio condiviso, EN, "
            "disco di rete, correttivi, B5.24, Motore B F7–F9, Dark): "
            "entrano nel cutover M3. II — solo analisi congelata (A1 QR "
            "avanzato, A2 Cestino, A3 Foto, B–C Export/Import avanzati, "
            "D Dashboard U0, B-QTY): codice dopo fine test Play / SI "
            "«apri fetta codice»; Play a pezzi (R2). III — Play 1.2 su "
            "main: freeze salvo bug bloccanti."
        ),
        make_normale(
            "Rilascio. R1: a fine test, un AAB flavor play "
            "(it.renatizzi.boxmanager) sostituisce la 1.2 (M3) — "
            "STRATEGIA_UNIFICAZIONE Fase C. R2: dopo M3, fette P2 in più "
            "versioni Play. Console M3: checklist C1–C8 in "
            "docs/famiglia/REQUISITI_PIANO_RILASCIO_ROADMAP.md e "
            "docs/play/README.md. Mai caricare flavor famiglia / "
            ".famiglia su Play."
        ),
        make_normale(
            "Priorità codice post-Play: M3 → A1 QR avanzato → A2 Cestino "
            "→ A3 Foto → B–C Export/Import; D resta no-code (U0); B-QTY "
            "dopo (prima KPI K1, poi pochi pattern Motore B; frasi "
            "catalogo 2.6 solo con elenco SI pre-codice)."
        ),
        make_normale(
            "Premium. Linea Progetto 2 (A1, A2, A3, B–C, D se un giorno "
            "codice, B-QTY) = Archivio completo (Allegato 4.19); nessuna "
            "eccezione in questa chiusura. Base free invariata (CRUD, "
            "ricerca semplice, Backup, stampa, Disco, …)."
        ),
        make_normale(
            "Rinvii requisiti (fonte viva fino all’implementazione): "
            "REQUISITI_QR_AVANZATO.md; REQUISITI_CESTINO.md; "
            "REQUISITI_FOTO_OGGETTO.md; "
            "REQUISITI_EXPORT_IMPORT_AVANZATO.md; "
            "REQUISITI_DASHBOARD_UI_AVANZATA.md; "
            "REQUISITI_QTY_KPI_SEARCH.md; "
            "REQUISITI_PIANO_RILASCIO_ROADMAP.md. Processo analisi 4 fasi: "
            ".cursor/rules/processo-analisi.mdc."
        ),
        make_normale(
            "WIP A1 prematuro: branch "
            "cursor/qr-avanzato-wip-parked-8374 — non entra in M3 finché "
            "non ripristinato e chiuso dopo SI codice."
        ),
        make_normale(
            f"Aggiornamento documentale Nota Integrata 9.2 Allegato 4.23 "
            f"(Progetto 2 + piano rilascio) e quadro Roadmap 4.1 / "
            f"§4.1.6 {STAMP}."
        ),
    ]
    for j, el in enumerate(allegato_423):
        children.insert(sect + j, el)
    n += len(allegato_423)

    for child in list(body):
        body.remove(child)
    for child in children:
        body.append(child)

    out_xml = ET.tostring(root, encoding="utf-8", xml_declaration=True)
    data["word/document.xml"] = out_xml

    tmp = DOC.with_suffix(".docx.tmp")
    with zipfile.ZipFile(tmp, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    tmp.replace(DOC)
    print(f"Patched {DOC.name}: ~{n} structural edits. Stamp {STAMP}")


if __name__ == "__main__":
    main()
