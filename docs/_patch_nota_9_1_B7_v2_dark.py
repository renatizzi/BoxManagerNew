# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16)."""
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


def section_416() -> list[ET.Element]:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return [
        make_p(
            "4.16 Dark e contrasto UI (congelato 23/08/2026)",
            2,
        ),
        make_p(
            "Fine-tuning Dark e contrasto UI CONVALIDATO il 23/08/2026. "
            "3.5 / 3.6 restano la fonte: switch Light/Dark sulla TopBar; "
            "tre palette Arancione / Blu / Verde solo su TopBar, accent e tab attiva; "
            "fondi, card e BottomNav restano neutri. "
            "D0–B7, Motore A, 3.4.4 stampa/PDF etichetta e 4.1.6 non si riaprono. "
            "Output Motore A = lista Contenitori: invariato."
        ),
        make_p(
            "Switch Dark di app: AppCompatDelegate.setDefaultNightMode e preferenza persistita. "
            "Stesso stato su tutte le schermate e al riavvio. "
            "Lo switch reagisce solo al tap; la pagina che si ricrea non deve più "
            "accendere/spegnere da sola. "
            "In Dark il brand non diventa viola: restano le tre palette. "
            "ThemeManager non congela i fondi del giorno: BottomNav usa i token night."
        ),
        make_p(
            "Liste Contenitori / Categorie / Oggetti: in Light il drawable di selezione "
            "resta quello già convalidato; in Dark riga scura, non bianca. "
            "Card Dashboard e Utility: in Light stesso fondo di prima; in Dark un tono "
            "più chiaro del fondo pagina. "
            "Icona luogo in schermata e dialogo: chiara in Dark, nera in Light; "
            "il PDF etichetta/stampa resta nero. "
            "Font, raggi card e ombre non sono stati allineati (rischio regressione)."
        ),
        make_p(
            "Prossimo: Motore B (Interrogazione Archivistica). "
            "Pipeline 0–10 resta l'unico avvio; fase 8 Interrogazione va al Motore B. "
            "Prima famiglia da importare: F7 / PATTERN_007 (Allegato 1 e Matrice Test Ricerca). "
            "PATTERN_010 resta SOSPESO. 4.1.6 resta NON core."
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

    i_quadro = find_idx(
        children,
        "Questa è l'unica Roadmap ufficiale. Quadro al 22/08/2026.",
    )
    replace_p_text(
        children[i_quadro],
        "Questa è l'unica Roadmap ufficiale. Quadro al 23/08/2026. "
        "I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
        "in caso di conflitto prevale questo quadro.",
    )

    i_mb = find_idx(children, "Motore B resta in V2. Fuori dal flusso core")
    replace_p_text(
        children[i_mb],
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B resta il prossimo passo V2. "
        "Fuori dal flusso core restano le evoluzioni elencate in 4.1.6. "
        "Vietato anticipare un blocco successivo prima della convalida utente (sez. 1.11).",
    )

    i_412 = find_idx(
        children,
        "Ricerca avanzata V1: Motore A CONVALIDATO 21/08/2026",
    )
    replace_p_text(
        children[i_412],
        "Ricerca avanzata V1: Motore A CONVALIDATO 21/08/2026 (Allegato 4.7). "
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B (Interrogazione / Query Archivistica) è il prossimo passo V2. "
        "Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13). "
        "La tabella TO DO (B3/B4/B5 con la numerazione ante Motore A) e il paragrafo "
        "4.1.2.1 sono storici: Allegato 4.14, non vigenti.",
    )

    i_next = find_idx(
        children,
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso.",
    )
    replace_p_text(
        children[i_next],
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso. "
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Prossimo: Motore B. 4.1.6 resta NON core.",
    )

    i_stamp = find_idx(
        children,
        "Aggiornamento documentale sidecar 9.1_B7 22/08/2026",
    )
    i_414 = find_idx(children, "4.14 Storico allineamento pipeline", after=i_stamp)

    rebuilt: list[ET.Element] = []
    rebuilt.extend(children[:i_414])
    rebuilt.extend(section_416())
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
