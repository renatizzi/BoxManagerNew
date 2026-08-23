# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: Motore B F7–F9 / CATEGORY / LOCATION CONVALIDATO 23/08/2026 (Allegato 4.17)."""
from __future__ import annotations

import re
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs"
)
DOC = ROOT / "Nota_Integrata_9.1_B7.docx"

W = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"
XML_SPACE = "{http://www.w3.org/XML/1998/namespace}space"


def ptext(el: ET.Element) -> str:
    return "".join((t.text or "") for t in el.iter(W + "t")).strip()


def make_p(text: str, title_level: int | None = None) -> ET.Element:
    p = ET.Element(W + "p")
    if title_level is not None:
        ppr = ET.SubElement(p, W + "pPr")
        style = ET.SubElement(ppr, W + "pStyle")
        style.set(W + "val", f"Titolo{title_level}")
        r = ET.SubElement(p, W + "r")
        t = ET.SubElement(r, W + "t")
        t.set(XML_SPACE, "preserve")
        t.text = text
        return p
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


def find_idx(
    children: list[ET.Element], startswith: str, after: int = -1
) -> int:
    for i, el in enumerate(children):
        if i > after and ptext(el).startswith(startswith):
            return i
    raise RuntimeError(f"Not found: {startswith}")


def replace_p_text(el: ET.Element, new_text: str) -> None:
    first = None
    for t in el.iter(W + "t"):
        if first is None:
            first = t
        else:
            t.text = ""
    if first is None:
        raise RuntimeError("No w:t in paragraph")
    first.set(XML_SPACE, "preserve")
    first.text = new_text


def register_namespaces(xml: str) -> None:
    for prefix, uri in re.findall(r'xmlns:([A-Za-z0-9]+)="([^"]+)"', xml[:8000]):
        if prefix.lower().startswith("xml") or re.fullmatch(r"ns\d+", prefix):
            continue
        ET.register_namespace(prefix, uri)


def section_417() -> list[ET.Element]:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return [
        make_p(
            "4.17 Motore B F7–F9 / CATEGORY / LOCATION (congelato 23/08/2026)",
            2,
        ),
        make_p(
            "Motore B avviato. Pipeline 0–10 resta l'unico avvio. "
            "Fase 8 Interrogazione → Motore B. Risposta in pagina Ricerca "
            "avanzata, non lista Contenitori. Vuoto: Nessun risultato trovato. "
            "Motore A (lista Contenitori) invariato. PATTERN_010 resta SOSPESO. "
            "PATTERN_005 / F5 resta BACKLOG V2. 4.1.6 resta NON core. "
            "D0–B7 e Allegato 4.16 non si riaprono. Non classificare dalla "
            "formulazione né estendere il matcher F8 al posto del percorso."
        ),
        make_p(
            "F7 / PATTERN_007 CONVALIDATO il 23/08/2026 e congelato. "
            "Cinque varianti ufficiali (Allegato 1, elenco intero, non "
            "ricomposto): Cerca tutti i contenitori che contengono doppioni; "
            "In quali contenitori ci sono oggetti uguali; "
            "Elenco dei contenitori che hanno oggetti uguali; "
            "Dove trovo lo stesso tipo di oggetti; "
            "Trova i contenitori che hanno almeno un oggetto uguale. "
            "Entità attese Allegato 1: OBJECT, BOX. Fulcro: OBJECT. "
            "Heading: Elenco dei contenitori che hanno oggetti uguali. "
            "Regola 2: stesso nome oggetto in tutto l'archivio. "
            "Regola 1 (contenitore nominato): non in questa fetta. "
            "F7-04: «dove» è indicatore, non instrada."
        ),
        make_p(
            "CATEGORY nel Motore B CONVALIDATO il 23/08/2026. Alias categoria + "
            "contenitori, senza nome di categoria d'archivio: fasi 3–6 mettono "
            "CATEGORY e BOX in Output; percorso BOX⇄CATEGORY; trasformazione "
            "BOX → CATEGORY; Query COMPARE sulle coppie contenitore-categoria. "
            "Con ≥2 categorie distinte tiene solo i contenitori la cui categoria "
            "non è condivisa. Una sola categoria → Nessun risultato trovato. "
            "«Elenco dei contenitori con categoria diversa» non è una variante "
            "ufficiale F8. Motore A nominato (contenitori della categoria "
            "Generico): CATEGORY → BOX, lista Contenitori, invariato."
        ),
        make_p(
            "F8 / PATTERN_008 CONVALIDATO il 23/08/2026 e congelato. "
            "Varianti ufficiali (Allegato 1, elenco intero, non ricomposto): "
            "Cerca i contenitori con categoria diversa che contengono lo stesso "
            "tipo di oggetto; Quali contenitori hanno categoria diversa e "
            "contengono oggetti uguali; Trova contenitori con categoria diversa "
            "e oggetti uguali; Elenco contenitori con categoria diversa e "
            "oggetti uguali. Entità attese Allegato 1: OBJECT, BOX, CATEGORY. "
            "Fulcro: OBJECT. Percorso composto OBJECT⇄BOX + BOX⇄CATEGORY. "
            "Trasformazione OBJECT → CATEGORY. Query: stesso nome oggetto sui "
            "dati di quella navigazione, in contenitori di categoria diversa. "
            "Heading: Elenco dei contenitori che hanno categoria diversa e "
            "contengono oggetti uguali."
        ),
        make_p(
            "LOCATION nel Motore B CONVALIDATO il 23/08/2026. Alias posizione / "
            "luogo / posto (tabella 1.3.3, non «dove») + contenitori, senza nome "
            "di luogo d'archivio: fasi 3–6 mettono LOCATION e BOX in Output; "
            "percorso BOX⇄LOCATION; trasformazione BOX → LOCATION; Query COMPARE "
            "sulle coppie contenitore-posizione. Con ≥2 posizioni distinte tiene "
            "solo i contenitori la cui posizione non è condivisa. Una sola "
            "posizione → Nessun risultato trovato. Motore A nominato "
            "(contenitori in Cantina): LOCATION → BOX, lista Contenitori, "
            "invariato."
        ),
        make_p(
            "F6 / PATTERN_006 e F9 / PATTERN_009 CONVALIDATI il 23/08/2026 e "
            "congelati come stessa Query sul percorso composto OBJECT⇄BOX + "
            "BOX⇄LOCATION (stesso tipo di oggetto su quei dati). Trasformazione "
            "OBJECT → LOCATION. Entità attese Allegato 1: OBJECT, BOX, LOCATION. "
            "Fulcro: LOCATION. Varianti ufficiali F6 (elenco intero): Cerca in "
            "quali posti sono conservati <oggetto1..n>; Dove ho conservato "
            "<oggetto1..n>; In quali luoghi ho conservato <oggetto1..n>; Elenco "
            "dei posti dove sono conservati <oggetto1..n>. Varianti ufficiali F9 "
            "(elenco intero): Cerca i luoghi dove ho conservato <oggetto1..n>; "
            "Dove ho conservato <oggetto1..n>; Elenco dei posti dove ho "
            "conservato <oggetto1..n>; Trova i posti dove sono <oggetto1..n>. "
            "Domanda Motore B senza chiave nominata: alias posizione + "
            "contenitori + oggetto (non «tipo»: in 1.3.3 è alias CATEGORY e "
            "apre F8). Oggetto o luogo nominato senza quel percorso resta "
            "Motore A (confine F1 / 4.3; Allegato 1: confine F6/F9 da "
            "verificare, non riaperto). Nessuna frase nuova in catalogo 2.6."
        ),
        make_p(
            "R19 invariato rispetto ad Allegato 4.7. F7 non si riapre. "
            "Card / stampa contestuale: parcheggiata; nessun layout nuovo. "
            "3.3.8 «relazioni posizione ↔ categoria» non è una famiglia "
            "Allegato 1: non implementata."
        ),
        make_p(
            "Prossimo: nessuna fetta Motore B successiva in perimetro Allegato 1. "
            "PATTERN_010 resta SOSPESO. PATTERN_005 / F5 resta BACKLOG V2. "
            "4.1.6 resta NON core."
        ),
        make_p(
            f"Aggiornamento documentale sidecar 9.1_B7 {stamp}."
        ),
    ]


def patch_body(root: ET.Element) -> None:
    body = root.find(W + "body")
    if body is None:
        raise RuntimeError("No body")
    children = list(body)

    i_mb = find_idx(
        children,
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B:",
    )
    replace_p_text(
        children[i_mb],
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B: F7, CATEGORY Query, F8, LOCATION Query, F6/F9 CONVALIDATI "
        "23/08/2026 (Allegato 4.17). PATTERN_010 resta SOSPESO. "
        "Fuori dal flusso core restano le evoluzioni elencate in 4.1.6. "
        "Vietato anticipare un blocco successivo prima della convalida utente "
        "(sez. 1.11).",
    )

    i_412 = find_idx(
        children,
        "Ricerca avanzata V1: Motore A CONVALIDATO 21/08/2026",
    )
    replace_p_text(
        children[i_412],
        "Ricerca avanzata V1: Motore A CONVALIDATO 21/08/2026 (Allegato 4.7). "
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B: F7 / PATTERN_007, CATEGORY Query, F8 / PATTERN_008, "
        "LOCATION Query, F6 / PATTERN_006 e F9 / PATTERN_009 CONVALIDATI "
        "23/08/2026 (Allegato 4.17). PATTERN_010 resta SOSPESO. "
        "Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13). "
        "La tabella TO DO (B3/B4/B5 con la numerazione ante Motore A) e il "
        "paragrafo 4.1.2.1 sono storici: Allegato 4.14, non vigenti.",
    )

    i_next = find_idx(
        children,
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso.",
    )
    replace_p_text(
        children[i_next],
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso. "
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B: F7, F8, CATEGORY, LOCATION, F6/F9 CONVALIDATI 23/08/2026 "
        "(Allegato 4.17). PATTERN_010 resta SOSPESO. 4.1.6 resta NON core.",
    )

    i_417 = find_idx(children, "4.17 Motore B")
    i_414 = find_idx(children, "4.14 Storico allineamento pipeline")
    if i_417 >= i_414:
        raise RuntimeError("4.17 not before 4.14")

    rebuilt: list[ET.Element] = []
    rebuilt.extend(children[:i_417])
    rebuilt.extend(section_417())
    rebuilt.extend(children[i_414:])

    for child in list(body):
        body.remove(child)
    for child in rebuilt:
        body.append(child)


def replace_matching_paragraphs(root: ET.Element) -> int:
    n = 0
    for el in root.iter(W + "p"):
        text = ptext(el)
        if text == "ROADMAP DEL PROGETTO AL 22/08/2026" or text.startswith(
            "ROADMAP DEL PROGETTO AL 22/08/2026"
        ):
            replace_p_text(el, "ROADMAP DEL PROGETTO AL 23/08/2026")
            n += 1
        elif text.startswith(
            "Ripristino REPLACE (B2) convalidato. Codice QR (B4) "
            "CONVALIDATO 21/08/2026. Import MERGE: ancora da fare"
        ):
            replace_p_text(
                el,
                "Ripristino REPLACE (B2) CONVALIDATO. "
                "Codice QR (B4) CONVALIDATO 21/08/2026 (Allegato 4.8). "
                "Import MERGE (B5) CONVALIDATO 22/08/2026 (Allegato 4.10).",
            )
            n += 1
        elif text.startswith(
            "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 "
            "(vedi Allegato 4.7); Motore B in V2"
        ):
            replace_p_text(
                el,
                "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 "
                "(Allegato 4.7). Motore B: F7, CATEGORY Query, F8, "
                "LOCATION Query, F6/F9 CONVALIDATI 23/08/2026 "
                "(Allegato 4.17). PATTERN_010 resta SOSPESO. "
                "Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13).",
            )
            n += 1
    return n


def main() -> None:
    if not DOC.exists():
        raise SystemExit(f"Missing {DOC}")
    with zipfile.ZipFile(DOC) as zin:
        names = zin.namelist()
        raw = zin.read("word/document.xml").decode("utf-8")
        data = {n: zin.read(n) for n in names}
    register_namespaces(raw)
    root = ET.fromstring(raw.encode("utf-8"))
    extra = replace_matching_paragraphs(root)
    patch_body(root)
    extra += replace_matching_paragraphs(root)
    xml_out = ET.tostring(root, encoding="unicode")
    if not xml_out.startswith("<?xml"):
        xml_out = (
            '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' + xml_out
        )
    data["word/document.xml"] = xml_out.encode("utf-8")
    with zipfile.ZipFile(DOC, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    print("Patched", DOC, "bytes", DOC.stat().st_size, "extra", extra)


if __name__ == "__main__":
    main()
