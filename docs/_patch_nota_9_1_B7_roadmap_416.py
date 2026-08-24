# -*- coding: utf-8 -*-
"""Roadmap 4.1.6: swipe già in V1; elenco backup in Ripristino (non in Backup)."""
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


REPLACES = [
    (
        "swipe navigazione",
        "swipe tra tab principali: già in V1 (Dashboard–Contenitori–Categorie–"
        "Utility–Impostazioni). Non si estende alle pagine interne. "
        "Fuori dalla roadmap V2.",
    ),
    (
        "Verifica architettura navigazione; uniformazione context card; "
        "AppShell globale; formalizzazione aree consolidate e sensibili; "
        "freeze aree mature; regression scope; matrice impatti.",
        "AppShell globale, context card e navigazione tab CONVALIDATI 24/08/2026 "
        "(chiusura V1). Freeze aree mature, regression scope e matrice impatti "
        "restano governance di progetto, non funzioni utente. "
        "Non sono fette V2 applicative.",
    ),
    (
        "Elenco card dei Backup nella pagina Backup (ex M4.B) non è in B1: "
        "resta eventuale evoluzione, non core V1.",
        "Elenco file di backup: non si implementa nella pagina Backup (ex M4.B "
        "chiuso, non in V2). La scelta del file da ripristinare resta nella "
        "pagina Ripristino, che già elenca gli ZIP della cartella (nome e data). "
        "Backup = crea la copia; Ripristino = scegli quale usare.",
    ),
    (
        "Swipe tra tab già presente: resta NON core; non si estende.",
        "Swipe tra tab già presente in V1: non si estende; fuori dalla roadmap V2.",
    ),
]


def patch_body(root: ET.Element) -> int:
    n = 0
    for el in root.iter(W + "p"):
        text = ptext(el)
        for old, new in REPLACES:
            if text == old:
                replace_p_text(el, new)
                n += 1
                break
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
    n = patch_body(root)
    if n != len(REPLACES):
        raise SystemExit(f"Expected {len(REPLACES)} replaces, got {n}")
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
    print("Patched", DOC, "replaces", n)


if __name__ == "__main__":
    main()
