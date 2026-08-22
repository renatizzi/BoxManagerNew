# -*- coding: utf-8 -*-
"""Sostituisce il quadro 4.1 con la tabella ROADMAP AL 22/08/2026. 4.1.1+ intatti."""
from __future__ import annotations

import re
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs"
)
DOC = ROOT / "Nota_Integrata_9.1_B7.docx"

W = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"
XML_SPACE = "{http://www.w3.org/XML/1998/namespace}space"

# Larghezze dxa (A4 utile ~ 10000)
COLS = (2200, 3200, 1600, 900, 1400)
HEADERS = ("MACROAREA", "MODULO", "STATO", "AS-IS", "TO-DO")

# (macroarea, modulo) — vMerge sulla prima colonna
ROWS = [
    ("Home Page", "Dashboard - UI"),
    ("Home Page", "Dashboard - Ricerca Avanzata"),
    ("Gestione Archivio", "Contenitori"),
    ("Gestione Archivio", "Oggetti"),
    ("Gestione Archivio", "Posizione"),
    ("Gestione Archivio", "Categorie"),
    ("Impostazione & Configurazione", "Impostazioni"),
    ("Utility", "Utility - QR Code"),
    ("Utility", "Utility - Backup"),
    ("Utility", "Utility - Ripristino"),
    ("Utility", "Utility - Importa Dati"),
    ("Strumenti Contestuali", "Strumenti Contestuali - Filtri"),
    ("Strumenti Contestuali", "Strumenti Contestuali - Ricerca Semplice"),
    ("Strumenti Contestuali", "Strumenti Contestuali - Esporta Dati"),
    ("Strumenti Contestuali", "Strumenti Contestuali - Ricerca Vocale"),
    ("Strumenti Contestuali", "Strumenti Contestuali - Stampa"),
]


def ptext(el: ET.Element) -> str:
    return "".join((t.text or "") for t in el.iter(W + "t")).strip()


def find_last_idx(children: list[ET.Element], startswith: str) -> int:
    found = None
    for i, el in enumerate(children):
        if ptext(el).startswith(startswith):
            found = i
    if found is None:
        raise RuntimeError(f"Not found: {startswith}")
    return found


def find_idx(children: list[ET.Element], startswith: str, after: int) -> int:
    for i, el in enumerate(children):
        if i > after and ptext(el).startswith(startswith):
            return i
    raise RuntimeError(f"Not found: {startswith}")


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


def _cell(
    text: str,
    *,
    width: int,
    bold: bool = False,
    color: str | None = None,
    fill: str | None = None,
    merge: str | None = None,
    center: bool = False,
) -> ET.Element:
    tc = ET.Element(W + "tc")
    tcpr = ET.SubElement(tc, W + "tcPr")
    tcw = ET.SubElement(tcpr, W + "tcW")
    tcw.set(W + "w", str(width))
    tcw.set(W + "type", "dxa")
    if merge == "restart":
        vm = ET.SubElement(tcpr, W + "vMerge")
        vm.set(W + "val", "restart")
    elif merge == "cont":
        ET.SubElement(tcpr, W + "vMerge")
    if fill:
        shd = ET.SubElement(tcpr, W + "shd")
        shd.set(W + "val", "clear")
        shd.set(W + "color", "auto")
        shd.set(W + "fill", fill)
    p = ET.SubElement(tc, W + "p")
    if center:
        ppr = ET.SubElement(p, W + "pPr")
        jc = ET.SubElement(ppr, W + "jc")
        jc.set(W + "val", "center")
    r = ET.SubElement(p, W + "r")
    rpr = ET.SubElement(r, W + "rPr")
    if bold:
        ET.SubElement(rpr, W + "b")
    if color:
        c = ET.SubElement(rpr, W + "color")
        c.set(W + "val", color)
    sz = ET.SubElement(rpr, W + "sz")
    sz.set(W + "val", "18")
    t = ET.SubElement(r, W + "t")
    t.set(XML_SPACE, "preserve")
    t.text = text
    return tc


def make_quadro_table() -> ET.Element:
    tbl = ET.Element(W + "tbl")
    tblpr = ET.SubElement(tbl, W + "tblPr")
    tblw = ET.SubElement(tblpr, W + "tblW")
    tblw.set(W + "w", "9300")
    tblw.set(W + "type", "dxa")
    borders = ET.SubElement(tblpr, W + "tblBorders")
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        el = ET.SubElement(borders, W + edge)
        el.set(W + "val", "single")
        el.set(W + "sz", "4")
        el.set(W + "space", "0")
        el.set(W + "color", "808080")
    grid = ET.SubElement(tbl, W + "tblGrid")
    for w in COLS:
        col = ET.SubElement(grid, W + "gridCol")
        col.set(W + "w", str(w))

    header = ET.SubElement(tbl, W + "tr")
    for i, h in enumerate(HEADERS):
        header.append(
            _cell(h, width=COLS[i], bold=True, fill="D9E2F3", center=True)
        )

    prev = None
    for macro, modulo in ROWS:
        tr = ET.SubElement(tbl, W + "tr")
        if macro != prev:
            tr.append(
                _cell(
                    macro,
                    width=COLS[0],
                    bold=True,
                    fill="FFF2CC",
                    merge="restart",
                )
            )
            prev = macro
        else:
            tr.append(
                _cell("", width=COLS[0], fill="FFF2CC", merge="cont")
            )
        tr.append(_cell(modulo, width=COLS[1]))
        tr.append(
            _cell(
                "COMPLETATO",
                width=COLS[2],
                bold=True,
                color="548235",
                center=True,
            )
        )
        tr.append(_cell("✔", width=COLS[3], color="548235", center=True))
        tr.append(_cell("", width=COLS[4]))
    return tbl


def register_namespaces(xml: str) -> None:
    for prefix, uri in re.findall(r'xmlns:([A-Za-z0-9]+)="([^"]+)"', xml[:8000]):
        if prefix.lower().startswith("xml") or re.fullmatch(r"ns\d+", prefix):
            continue
        ET.register_namespace(prefix, uri)


def patch_body(root: ET.Element) -> None:
    body = root.find(W + "body")
    if body is None:
        raise RuntimeError("No body")
    children = list(body)
    roadmap = find_last_idx(children, "4 ROADMAP UFFICIALE DEL PROGETTO")
    i_41 = find_idx(children, "4.1 Quadro sintetico", after=roadmap)
    i_411 = find_idx(children, "4.1.1 Dettaglio", after=i_41)

    replacement = [
        children[i_41],
        make_p(
            "Questa è l'unica Roadmap ufficiale. Quadro al 22/08/2026. "
            "I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
            "in caso di conflitto prevale questo quadro."
        ),
        make_p("ROADMAP DEL PROGETTO AL 22/08/2026", 2),
        make_quadro_table(),
        make_p(
            "Motore B resta in V2. Fuori dal flusso core restano le evoluzioni "
            "elencate in 4.1.6. Vietato anticipare un blocco successivo prima "
            "della convalida utente (sez. 1.11)."
        ),
    ]

    rebuilt = children[:i_41] + replacement + children[i_411:]
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
        xml_out = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' + xml_out
    data["word/document.xml"] = xml_out.encode("utf-8")
    with zipfile.ZipFile(DOC, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    print("Patched quadro", DOC, "bytes", DOC.stat().st_size)


if __name__ == "__main__":
    main()
