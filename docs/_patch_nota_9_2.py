# -*- coding: utf-8 -*-
"""Nota Integrata 9.2: copertina, roadmap 01/09/2026, allegati in ordine, Titolo2 pulito.

Parte da Nota_Integrata_9.1_B7.docx (se presente) o da 9.2 già scritta.
Scrive sempre docs/Nota_Integrata_9.2.docx.
"""
from __future__ import annotations

import re
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parent
SRC_91 = ROOT / "Nota_Integrata_9.1_B7.docx"
DST = ROOT / "Nota_Integrata_9.2.docx"

W = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"
XML_SPACE = "{http://www.w3.org/XML/1998/namespace}space"
TITLE_420 = (
    "4.20 Merge famiglia B0–B5 e correttivi B5.3 (congelato 01/09/2026)"
)


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


def find_idx(children: list[ET.Element], start: str) -> int:
    for i, el in enumerate(children):
        if ptext(el).startswith(start):
            return i
    raise RuntimeError(f"Not found: {start[:80]}")


def is_para(el: ET.Element) -> bool:
    return el.tag == W + "p" or el.tag.endswith("}p")


def is_empty_para(el: ET.Element) -> bool:
    return is_para(el) and not ptext(el)


def make_title2(text: str) -> ET.Element:
    """Stesso markup di 4.19: solo pStyle Titolo2, nessun rPr sul run."""
    p = ET.Element(W + "p")
    ppr = ET.SubElement(p, W + "pPr")
    style = ET.SubElement(ppr, W + "pStyle")
    style.set(W + "val", "Titolo2")
    r = ET.SubElement(p, W + "r")
    t = ET.SubElement(r, W + "t")
    t.set(XML_SPACE, "preserve")
    t.text = text
    return p


def apply_normale(el: ET.Element) -> None:
    """Corpo 4.20: stile Normale, senza sz 10pt che sporca Titolo2/indice."""
    ppr = el.find(W + "pPr")
    if ppr is None:
        ppr = ET.Element(W + "pPr")
        el.insert(0, ppr)
    st = ppr.find(W + "pStyle")
    if st is None:
        st = ET.SubElement(ppr, W + "pStyle")
    st.set(W + "val", "Normale")
    for r in el.findall(W + "r"):
        rpr = r.find(W + "rPr")
        if rpr is not None:
            r.remove(rpr)


def reorder_allegati(children: list[ET.Element]) -> None:
    """4.13, 4.14, 4.15, 4.16 … 4.19, 4.20 (niente 4.20 poi 4.14)."""
    i14 = find_idx(children, "4.14 Storico allineamento pipeline")
    i16 = find_idx(children, "4.16 Dark e contrasto UI")
    if i14 < i16:
        return
    i15 = find_idx(children, "4.15 Storico roadmap Backup")
    sect = next(
        i for i, el in enumerate(children) if el.tag.endswith("}sectPr")
    )
    end15 = sect - 1
    while end15 > i15 and is_empty_para(children[end15]):
        end15 -= 1
    block14 = children[i14:i15]
    block15 = children[i15 : end15 + 1]
    del children[i14 : end15 + 1]
    i16 = find_idx(children, "4.16 Dark e contrasto UI")
    children[i16:i16] = block14 + block15


def clean_420(children: list[ET.Element], stamp: str) -> None:
    i20 = find_idx(children, "4.20 Merge famiglia")
    children[i20] = make_title2(TITLE_420)
    i_next = None
    for j in range(i20 + 1, len(children)):
        t = ptext(children[j])
        if t.startswith("4.") and not t.startswith("4.20"):
            i_next = j
            break
        if children[j].tag.endswith("}sectPr"):
            i_next = j
            break
    if i_next is None:
        i_next = len(children)
    for j in range(i20 + 1, i_next):
        el = children[j]
        if not is_para(el) or not ptext(el):
            continue
        apply_normale(el)
        if ptext(el).startswith("Aggiornamento documentale sidecar"):
            replace_p_text(
                el,
                "Aggiornamento documentale Nota Integrata 9.2 Allegato 4.20 "
                f"(merge famiglia B0–B5 + T2 B5.3) {stamp}.",
            )


def main() -> None:
    src = SRC_91 if SRC_91.exists() else DST
    if not src.exists():
        raise SystemExit(f"Missing source {src}")
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    with zipfile.ZipFile(src) as zin:
        names = zin.namelist()
        raw = zin.read("word/document.xml").decode("utf-8")
        data = {n: zin.read(n) for n in names}
    register_namespaces(raw)
    root = ET.fromstring(raw.encode("utf-8"))
    body = root.find(W + "body")
    if body is None:
        raise SystemExit("No body")
    children = list(body)

    replace_startswith(
        children,
        "BoxManager",
        "BoxManager – Nota Integrata v 9.2",
    )
    replace_startswith(
        children,
        "ultimo aggiornamento del",
        "ultimo aggiornamento del 01/09/2026",
    )
    replace_startswith(
        children,
        "Questa è l'unica Roadmap ufficiale. Quadro al ",
        "Questa è l'unica Roadmap ufficiale (Nota Integrata 9.2). "
        "Quadro al 01/09/2026. "
        "I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
        "in caso di conflitto prevale questo quadro.",
    )
    replace_startswith(
        children,
        "ROADMAP DEL PROGETTO AL ",
        "ROADMAP DEL PROGETTO AL 01/09/2026",
    )
    # Stato al: already 01/09 from 4.20 patch; keep and prefix 9.2 if needed
    for el in children:
        t = ptext(el)
        if t.startswith("Stato al "):
            if "Nota Integrata 9.2" not in t:
                replace_p_text(
                    el,
                    "Stato al 01/09/2026 (Nota Integrata 9.2). " + t.split(". ", 1)[1]
                    if ". " in t
                    else t.replace("Stato al 01/09/2026.", "Stato al 01/09/2026 (Nota Integrata 9.2)."),
                )
            break

    reorder_allegati(children)
    clean_420(children, stamp)

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
    with zipfile.ZipFile(DST, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    print("Wrote", DST, "bytes", DST.stat().st_size)


if __name__ == "__main__":
    main()
