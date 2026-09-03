# -*- coding: utf-8 -*-
"""Verifica M2a: la colonna IT della bozza CK0 coincide con la Nota (elenchi interi)."""
from __future__ import annotations

import re
import sys
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parent
DOC = ROOT.parent / "Nota_Integrata_9.1_B7.docx"
BOZZA = ROOT / "BOZZA_TABELLE_EN_CK0.md"
W = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"

OBJECT_IT = [
    "oggetto",
    "articolo",
    "elemento",
    "utensile",
    "cosa",
    "affare",
    "roba",
    "prodotto",
    "arnese",
]
BOX_IT = [
    "contenitore",
    "box",
    "boxes",
    "scatola",
    "scatolone",
    "cassetta",
    "confezione",
    "baule",
    "busta",
    "bustone",
    "cassetto",
    "barattolo",
    "vaso",
    "bacinella",
    "recipiente",
    "cassa",
    "cassone",
    "bidone",
    "cassonetto",
    "cassaforte",
    "portafoglio",
    "portaoggetti",
    "portagioie",
    "portadocumenti",
    "container",
    "involucro",
    "custodia",
    "cover",
    "imballaggio",
    "armadio",
    "guardaroba",
    "stipo",
    "libreria",
    "scaffale",
]
LOCATION_IT = [
    "posizione",
    "luogo",
    "posto",
    "ubicazione",
    "sito",
    "area",
    "zona",
    "perimetro",
    "spazio",
    "ambiente",
    "città",
    "paese",
    "località",
    "punto",
]
CATEGORY_IT = [
    "categoria",
    "classe",
    "classificazione",
    "gruppo",
    "aggregato",
    "raggruppamento",
    "specie",
    "famiglia",
    "ordine",
    "divisione",
    "grado",
    "fascia",
    "tipo",
    "tipologia",
    "qualità",
    "genere",
]
F7_IT = [
    "Cerca tutti i contenitori che contengono doppioni",
    "In quali contenitori ci sono oggetti uguali",
    "Elenco dei contenitori che hanno oggetti uguali",
    "Dove trovo lo stesso tipo di oggetti",
    "Trova i contenitori che hanno almeno un oggetto uguale",
]
F8_IT = [
    "Cerca i contenitori con categoria diversa che contengono lo stesso tipo di oggetto",
    "Quali contenitori hanno categoria diversa e contengono oggetti uguali",
    "Trova contenitori con categoria diversa e oggetti uguali",
    "Elenco contenitori con categoria diversa e oggetti uguali",
]
MSG_IT = [
    "Sto analizzando la richiesta...",
    "Non riesco a capire esattamente quello che stai cercando. Prova a formulare la richiesta con parole più chiare.",
    "Tocca qui per tornare alla Dashboard",
    "Non ho compreso la richiesta.",
    "Puoi formulare la richiesta in modo più preciso?",
    "Nessun risultato trovato.",
    "Questo tipo di richiesta non è ancora disponibile.",
]


def ptext(el: ET.Element) -> str:
    return "".join((t.text or "") for t in el.iter(W + "t")).strip()


def nota_paragraphs() -> list[str]:
    with zipfile.ZipFile(DOC) as z:
        raw = z.read("word/document.xml")
    root = ET.fromstring(raw)
    out: list[str] = []
    for el in root.iter(W + "p"):
        t = ptext(el)
        if t:
            out.append(t)
    return out


def split_aliases(row: str) -> list[str]:
    return [p.strip() for p in row.split(",") if p.strip()]


def find_startswith(paras: list[str], prefix: str) -> str:
    for t in paras:
        if t.startswith(prefix):
            return t
    raise SystemExit(f"Nota: missing paragraph starting with {prefix!r}")


def extract_table_column(
    md: str,
    heading: str,
    it_col: int,
    numbered_only: bool = False,
) -> list[str]:
    """Parse the markdown table that follows a ## / ### heading."""
    if heading.startswith("#"):
        marker = heading
        i = md.find(marker)
    else:
        i = -1
        marker = ""
        for prefix in ("### ", "## "):
            candidate = prefix + heading
            i = md.find(candidate)
            if i >= 0:
                marker = candidate
                break
    if i < 0:
        raise SystemExit(f"Bozza: missing heading {heading!r}")
    chunk = md[i:]
    nxt = re.search(r"\n#{2,3} ", chunk[len(marker) :])
    if nxt:
        chunk = chunk[: len(marker) + nxt.start()]
    rows: list[str] = []
    for line in chunk.splitlines():
        if not line.startswith("|"):
            continue
        cells = [c.strip() for c in line.strip("|").split("|")]
        if not cells or cells[0] in {
            "#",
            "IT ufficiale",
            "IT ufficiale (1.3.3)",
            "Ruolo 4.5",
            "Core",
            "IT (3.3.5)",
            "IT",
            "IT (4.4)",
            "Pezzo IT oggi in codice",
        }:
            continue
        if set(cells[0]) <= {"-", ":"}:
            continue
        if numbered_only and not cells[0].isdigit():
            continue
        if len(cells) <= it_col:
            continue
        val = cells[it_col]
        val = val.replace("\\<", "<").replace("\\>", ">")
        if val in {"IT ufficiale", "IT"}:
            continue
        rows.append(val)
    return rows


def numbered_it_column(md: str, heading: str) -> list[str]:
    """Tables with | # | IT | EN | → IT is column 1; ignore following heading tables."""
    return extract_table_column(md, heading, 1, numbered_only=True)


def pair_it_column(md: str, heading: str) -> list[str]:
    """Tables with | IT | EN | → IT is column 0."""
    return extract_table_column(md, heading, 0)


def check(cond: bool, msg: str, errors: list[str]) -> None:
    if not cond:
        errors.append(msg)
    else:
        print("OK", msg)


def main() -> int:
    if not DOC.exists():
        print("Missing", DOC, file=sys.stderr)
        return 2
    if not BOZZA.exists():
        print("Missing", BOZZA, file=sys.stderr)
        return 2

    paras = nota_paragraphs()
    md = BOZZA.read_text(encoding="utf-8")
    errors: list[str] = []

    object_row = None
    box_row = None
    loc_row = None
    cat_row = None
    for i, t in enumerate(paras):
        if t == "oggetto, articolo, elemento, utensile, cosa, affare, roba, prodotto, arnese":
            object_row = t
        if t.startswith("contenitore, box, boxes, scatola") and "scaffale" in t:
            box_row = t
        if t.startswith("posizione, luogo, posto, ubicazione") and "punto" in t and "locale" not in t:
            loc_row = t
        if t.startswith("categoria, classe, classificazione") and "genere" in t:
            cat_row = t

    check(object_row is not None, "Nota 1.3.3 OBJECT row found", errors)
    check(box_row is not None, "Nota 1.3.3 BOX row found", errors)
    check(loc_row is not None, "Nota 1.3.3 LOCATION row found", errors)
    check(cat_row is not None, "Nota 1.3.3 CATEGORY row found", errors)

    if object_row:
        check(
            split_aliases(object_row) == OBJECT_IT,
            f"OBJECT 1.3.3 count={len(OBJECT_IT)}",
            errors,
        )
    if box_row:
        check(
            split_aliases(box_row) == BOX_IT,
            f"BOX 1.3.3 count={len(BOX_IT)}",
            errors,
        )
    if loc_row:
        check(
            split_aliases(loc_row) == LOCATION_IT,
            f"LOCATION 1.3.3 count={len(LOCATION_IT)} (no locale)",
            errors,
        )
    if cat_row:
        check(
            split_aliases(cat_row) == CATEGORY_IT,
            f"CATEGORY 1.3.3 count={len(CATEGORY_IT)}",
            errors,
        )

    check(
        numbered_it_column(md, "A1 OBJECT (9)") == OBJECT_IT,
        "Bozza A1 OBJECT = 1.3.3",
        errors,
    )
    check(
        numbered_it_column(md, "A2 BOX (34)") == BOX_IT,
        "Bozza A2 BOX = 1.3.3",
        errors,
    )
    check(
        numbered_it_column(md, "A3 LOCATION (14)") == LOCATION_IT,
        "Bozza A3 LOCATION = 1.3.3",
        errors,
    )
    check(
        numbered_it_column(md, "A4 CATEGORY (16)") == CATEGORY_IT,
        "Bozza A4 CATEGORY = 1.3.3",
        errors,
    )

    f7_417 = find_startswith(paras, "F7 / PATTERN_007 CONVALIDATO")
    for v in F7_IT:
        check(v in f7_417, f"F7 in 4.17: {v}", errors)
    check(
        numbered_it_column(md, "E — F7 / PATTERN_007 (Allegato 4.17, elenco intero)")
        == F7_IT,
        "Bozza E F7 = 4.17 (5)",
        errors,
    )

    f8_417 = find_startswith(paras, "F8 / PATTERN_008 CONVALIDATO")
    for v in F8_IT:
        check(v in f8_417, f"F8 in 4.17: {v}", errors)
    check(
        numbered_it_column(md, "F — F8 / PATTERN_008 (Allegato 4.17, elenco intero)")
        == F8_IT,
        "Bozza F F8 = 4.17 (4)",
        errors,
    )

    msg_45 = " ".join(
        t
        for t in paras
        if t.startswith(
            (
                "Elaborazione:",
                "Fallback:",
                "Commiato:",
                "Non compreso:",
                "Chiarificazione:",
                "Nessun risultato:",
                "Motore B / non in V1:",
            )
        )
    )
    for v in MSG_IT:
        check(v.rstrip(".") in msg_45 or v in msg_45, f"4.5 contains: {v[:40]}", errors)

    bozza_msg = extract_table_column(
        md,
        "D — Messaggi ricerca (catalogo 2.6 / Allegato 4.5)",
        2,
    )
    check(bozza_msg == MSG_IT, f"Bozza D messages = 4.5 (got {bozza_msg!r})", errors)
    check(
        "locale" not in LOCATION_IT,
        "locale excluded from official LOCATION row",
        errors,
    )
    check(
        "locale" in md and "non è in questa riga" in md.lower() or "non** è in questa riga" in md,
        "Bozza flags locale as D8 / not in 1.3.3",
        errors,
    )

    domain = Path(__file__).resolve().parents[2] / "app/src/main/java/com/example/boxmanagernew/domain/search"
    # M2a must not require domain/search edits; this script only reads Nota + bozza.
    check(domain.is_dir(), "domain/search exists (untouched by this check)", errors)
    check(
        any(t.startswith("4.21 Bozza tabelle EN") for t in paras),
        "Nota Allegato 4.21 heading present",
        errors,
    )

    print()
    print(
        "counts OBJECT/BOX/LOCATION/CATEGORY/F7/F8/MSG =",
        len(OBJECT_IT),
        len(BOX_IT),
        len(LOCATION_IT),
        len(CATEGORY_IT),
        len(F7_IT),
        len(F8_IT),
        len(MSG_IT),
    )
    if errors:
        print("FAIL", len(errors))
        for e in errors:
            print(" -", e)
        return 1
    print("PASS M2a CK0 bozza aligned to Nota")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
