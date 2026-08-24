# -*- coding: utf-8 -*-
"""Estende riga BOX tabella 1.3.3: armadio, guardaroba, stipo, libreria, scaffale."""
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

OLD = (
    "contenitore, box, boxes, scatola, scatolone, cassetta, confezione, baule, "
    "busta, bustone, cassetto, barattolo, vaso, bacinella, recipiente, cassa, "
    "cassone, bidone, cassonetto, cassaforte, portafoglio, portaoggetti, "
    "portagioie, portadocumenti, container, involucro, custodia, cover, "
    "imballaggio"
)

NEW = (
    OLD
    + ", armadio, guardaroba, stipo, libreria, scaffale"
)


def ptext(el: ET.Element) -> str:
    return "".join((t.text or "") for t in el.iter(W + "t")).strip()


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


def main() -> None:
    if not DOC.exists():
        raise SystemExit(f"Missing {DOC}")
    with zipfile.ZipFile(DOC) as zin:
        names = zin.namelist()
        raw = zin.read("word/document.xml").decode("utf-8")
        data = {n: zin.read(n) for n in names}
    register_namespaces(raw)
    root = ET.fromstring(raw.encode("utf-8"))
    n = 0
    for el in root.iter(W + "p"):
        if ptext(el) == OLD:
            replace_p_text(el, NEW)
            n += 1
    if n != 1:
        raise SystemExit(f"Expected 1 BOX row replace, got {n}")
    xml_out = ET.tostring(root, encoding="unicode")
    if not xml_out.startswith("<?xml"):
        xml_out = (
            '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
            + xml_out
        )
    data["word/document.xml"] = xml_out.encode("utf-8")
    with zipfile.ZipFile(DOC, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    print("Patched BOX aliases in", DOC)


if __name__ == "__main__":
    main()
