# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: Allegato 4.21 bozza tabelle EN ricerca (M2a, attesa CK0)."""
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

TITLE = "4.21 Bozza tabelle EN ricerca avanzata (M2a — attesa CK0)"


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


def register_namespaces(xml: str) -> None:
    for prefix, uri in re.findall(r'xmlns:([A-Za-z0-9]+)="([^"]+)"', xml[:8000]):
        if prefix.lower().startswith("xml") or re.fullmatch(r"ns\d+", prefix):
            continue
        ET.register_namespace(prefix, uri)


def section_421() -> list[ET.Element]:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return [
        make_p(TITLE, 2),
        make_p(
            "BOZZA filone M2a, 03/09/2026. Non congelata. "
            "CK0 SI Renato obbligatorio prima di qualsiasi commit su "
            "domain/search per l'inglese. Pipeline 0–10 invariata. "
            "Solo IT + EN in V1. Dati utente (nomi archivio) non si traducono. "
            "Fonte viva di revisione: docs/multilingua/BOZZA_TABELLE_EN_CK0.md "
            "(elenchi interi 1:1). Excel query_operative_V4.xlsx non è in repo; "
            "la matrice Core usata è la copia già in 1.3.3."
        ),
        make_p(
            "OBJECT EN (bozza, ordine 1.3.3): object, article, element, "
            "utensil, thing, affair, stuff, product, tool."
        ),
        make_p(
            "BOX EN (bozza, ordine 1.3.3): container, box, boxes, box, carton, "
            "crate, pack, trunk, envelope, large envelope, drawer, jar, vase, "
            "basin, receptacle, chest, large chest, bin, dumpster, safe, "
            "wallet, storage case, jewelry box, document holder, container, "
            "wrapping, case, cover, packaging, cupboard, wardrobe, cabinet, "
            "bookcase, shelf."
        ),
        make_p(
            "LOCATION EN (bozza, ordine 1.3.3): location, place, spot, "
            "location, site, area, zone, perimeter, space, environment, city, "
            "town, locality, point. "
            "«locale» non è in 1.3.3; resta D8 (Allegato 4.3 / codice)."
        ),
        make_p(
            "CATEGORY EN (bozza, ordine 1.3.3): category, class, "
            "classification, group, aggregate, grouping, species, family, "
            "order, division, grade, band, type, typology, quality, kind."
        ),
        make_p(
            "Perifrasi EN (bozza, 1.3.3): OBJECT which; BOX which, in which, "
            "where; LOCATION where, in which; CATEGORY which, in which, "
            "to which."
        ),
        make_p(
            "Indicatori EN (bozza, esempi 3.3.5 senza «ecc.»): confronto "
            "equal, same, duplicate, different, different, comparison; "
            "aggregazione all, list, which; dove → where. "
            "doppione (variante ufficiale F7, non in 3.3.5) → duplicate."
        ),
        make_p(
            "Messaggi ricerca EN (bozza, catalogo 2.6 / Allegato 4.5, elenco "
            "intero): Analyzing the request… / I cannot understand exactly "
            "what you are looking for. Try rephrasing the request with "
            "clearer words. / Tap here to return to the Dashboard / "
            "I did not understand the request. / Can you phrase the request "
            "more precisely? / No results found. / This type of request is "
            "not yet available. I tre testi già in values-en (M1) non si "
            "riscrivono."
        ),
        make_p(
            "F7 EN (bozza, cinque varianti 4.17, elenco intero): "
            "Find all the containers that contain duplicates; "
            "In which containers are there equal objects; "
            "List of the containers that have equal objects; "
            "Where do I find the same type of objects; "
            "Find the containers that have at least one equal object. "
            "Heading: List of the containers that have equal objects."
        ),
        make_p(
            "F8 EN (bozza, quattro varianti 4.17, elenco intero): "
            "Find the containers with a different category that contain the "
            "same type of object; "
            "Which containers have a different category and contain equal "
            "objects; "
            "Find containers with a different category and equal objects; "
            "List of containers with a different category and equal objects. "
            "Heading: List of the containers that have a different category "
            "and contain equal objects. "
            "Non importare le varianti extra della famiglia F8 nel codice IT."
        ),
        make_p(
            "CSV V1 (proposta CK0 D7): header di Modello_Importazione.csv "
            "restano italiani; il nome file resta fisso. "
            "PATTERN_005 / F5 resta BACKLOG V2. PATTERN_010 resta SOSPESO. "
            "F1–F6 / F9 EN sono nella bozza markdown (supporto CK2), stesso "
            "metodo 1:1 da Allegato 1 §1.1 / 4.17."
        ),
        make_p(
            f"Aggiornamento documentale sidecar 9.1_B7 Allegato 4.21 {stamp}."
        ),
    ]


def patch_body(root: ET.Element) -> None:
    body = root.find(W + "body")
    if body is None:
        raise RuntimeError("No body")
    children = list(body)

    if any(ptext(el).startswith("4.21 Bozza tabelle EN") for el in children):
        print("4.21 already present — skip insert")
        return

    i_414 = find_idx(children, "4.14 Storico allineamento pipeline")
    rebuilt: list[ET.Element] = []
    rebuilt.extend(children[:i_414])
    rebuilt.extend(section_421())
    rebuilt.extend(children[i_414:])

    for child in list(body):
        body.remove(child)
    for child in rebuilt:
        body.append(child)


def main() -> None:
    if not DOC.exists():
        raise SystemExit(f"Missing {DOC}")
    with zipfile.ZipFile(DOC) as zin:
        names = zin.namelist()
        raw = zin.read("word/document.xml").decode("utf-8")
        data = {n: zin.read(n) for n in names}
    register_namespaces(raw)
    root = ET.fromstring(raw.encode("utf-8"))
    patch_body(root)
    xml_out = ET.tostring(root, encoding="unicode")
    if not xml_out.startswith("<?xml"):
        xml_out = (
            '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' + xml_out
        )
    data["word/document.xml"] = xml_out.encode("utf-8")
    with zipfile.ZipFile(DOC, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    print("Patched", DOC, "bytes", DOC.stat().st_size)


if __name__ == "__main__":
    main()
