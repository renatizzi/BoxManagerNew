# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: inventario Motore A + stampa contestuale Ricerca avanzata
CONVALIDATI 23/08/2026 (Allegato 4.18). Include anche delete oggetti."""
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


def section_418() -> list[ET.Element]:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return [
        make_p(
            "4.18 Inventario Motore A e stampa contestuale Ricerca avanzata "
            "(congelato 23/08/2026)",
            2,
        ),
        make_p(
            "CONVALIDATO il 23/08/2026. Pipeline 0–10 resta l'unico avvio. "
            "Allegato 4.17 (Motore B F7–F9 / CATEGORY / LOCATION) non si riapre. "
            "D0–B7 e Allegato 4.16 non si riaprono. PATTERN_010 resta SOSPESO. "
            "PATTERN_005 / F5 resta BACKLOG V2. 4.1.6 resta NON core. "
            "Nessuna frase nuova in catalogo 2.6. Non classificare da «elenco» "
            "o «tutti»: restano solo indicatori 3.3.5."
        ),
        make_p(
            "Stampa contestuale da output Motore B CONVALIDATA. Risposta Motore B "
            "soddisfatta: in header della pagina Ricerca avanzata le stesse icone "
            "Stampa/Esporta di 3.4.5 / Allegato 4.11. Tap Stampa → "
            "ContainerViewSnapshot NESTED dai box/oggetti del risultato (non dal "
            "testo della card) → ViewOutputController / ViewPrintPdf A4 → "
            "anteprima PrintManager di sistema. Esporta: stesso CSV Contenitori + "
            "oggetti e criterio cartella/nome di B6. Chiarificazione, fallback e "
            "Nessun risultato trovato: nessuna icona. Motore A nominato: lista "
            "Contenitori invariata. Nessun layout card nuovo."
        ),
        make_p(
            "Inventario archivio (fotografia totale) CONVALIDATO via type-only "
            "su un solo Core 1.3.3 senza chiave nominata d'archivio. "
            "Alias BOX o OBJECT da soli: percorso [BOX], trasformazione NONE, "
            "Motore A → lista Contenitori intera; stampa NESTED + Esporta. "
            "Alias CATEGORY da solo: percorso [CATEGORY, BOX], trasformazione "
            "CATEGORY → BOX, Motore A → lista Contenitori; stampa aggregata "
            "CATEGORY_GROUPS (icona/nome categoria, sotto i contenitori di "
            "quella categoria), sola stampa senza CSV (come Categorie in 4.11). "
            "Alias LOCATION da solo: percorso [LOCATION, BOX], trasformazione "
            "LOCATION → BOX; stampa aggregata PLACE_GROUPS (icona luogo, sotto "
            "i contenitori), sola stampa. Coppie CATEGORY+BOX / LOCATION+BOX "
            "senza nome restano Query Motore B di Allegato 4.17 (valori unici). "
            "Output Motore A a video resta lista Contenitori (4.1 A)."
        ),
        make_p(
            "Delete oggetti CONVALIDATO nella stessa fetta operativa: "
            "ObjectRepositoryImpl.delete / deleteByIds e ObjectDao.deleteById / "
            "deleteByIds; ObjectViewModel.deleteObjects usa deleteByIds. "
            "Lo stub vuoto di delete (post ricerca) non si riapre."
        ),
        make_p(
            "Prossimo: PATTERN_010 resta SOSPESO. PATTERN_005 / F5 resta "
            "BACKLOG V2. 4.1.6 resta NON core. Eventuali residui UI/governance "
            "NON core restano in 4.1.6."
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
        "Motore B F7–F9 / CATEGORY / LOCATION CONVALIDATI 23/08/2026 "
        "(Allegato 4.17). Inventario Motore A e stampa contestuale Ricerca "
        "avanzata CONVALIDATI 23/08/2026 (Allegato 4.18). PATTERN_010 resta "
        "SOSPESO. Fuori dal flusso core restano le evoluzioni elencate in "
        "4.1.6. Vietato anticipare un blocco successivo prima della convalida "
        "utente (sez. 1.11).",
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
        "23/08/2026 (Allegato 4.17). Inventario Motore A (type-only un Core) "
        "e stampa contestuale da output Motore B / aggregati categoria-luogo "
        "CONVALIDATI 23/08/2026 (Allegato 4.18). PATTERN_010 resta SOSPESO. "
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
        "Motore B F7–F9 / CATEGORY / LOCATION CONVALIDATI 23/08/2026 "
        "(Allegato 4.17). Inventario e stampa contestuale Ricerca avanzata "
        "CONVALIDATI 23/08/2026 (Allegato 4.18). PATTERN_010 resta SOSPESO. "
        "4.1.6 resta NON core.",
    )

    # Close the parked line inside frozen 4.17 without rewriting the whole section.
    for el in children:
        text = ptext(el)
        if text.startswith(
            "R19 invariato rispetto ad Allegato 4.7. F7 non si riapre. "
            "Card / stampa contestuale: parcheggiata"
        ):
            replace_p_text(
                el,
                "R19 invariato rispetto ad Allegato 4.7. F7 non si riapre. "
                "Card / stampa contestuale: chiusa in Allegato 4.18 "
                "(CONVALIDATO 23/08/2026). 3.3.8 «relazioni posizione ↔ "
                "categoria» non è una famiglia Allegato 1: non implementata.",
            )
            break

    i_414 = find_idx(children, "4.14 Storico allineamento pipeline")
    try:
        i_418 = find_idx(children, "4.18 Inventario Motore A")
    except RuntimeError:
        i_418 = -1

    if i_418 >= 0:
        rebuilt: list[ET.Element] = []
        rebuilt.extend(children[:i_418])
        rebuilt.extend(section_418())
        rebuilt.extend(children[i_414:])
    else:
        rebuilt = []
        rebuilt.extend(children[:i_414])
        rebuilt.extend(section_418())
        rebuilt.extend(children[i_414:])

    for child in list(body):
        body.remove(child)
    for child in rebuilt:
        body.append(child)


def replace_matching_paragraphs(root: ET.Element) -> int:
    n = 0
    for el in root.iter(W + "p"):
        text = ptext(el)
        if text.startswith(
            "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 "
            "(Allegato 4.7). Motore B: F7, CATEGORY Query, F8, "
            "LOCATION Query, F6/F9 CONVALIDATI 23/08/2026 "
            "(Allegato 4.17)."
        ) and "4.18" not in text:
            replace_p_text(
                el,
                "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 "
                "(Allegato 4.7). Motore B F7–F9 / CATEGORY / LOCATION "
                "CONVALIDATI 23/08/2026 (Allegato 4.17). Inventario Motore A "
                "e stampa contestuale Ricerca avanzata CONVALIDATI 23/08/2026 "
                "(Allegato 4.18). PATTERN_010 resta SOSPESO. "
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
