# -*- coding: utf-8 -*-
"""Ripulisce il cap. 4 ROADMAP in 9.1_B7: 4.1.1–4.1.5 operativi; storico in 4.14/4.15.

Il quadro 4.1 non si tocca (aggiornamento utente).
"""
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
    raise RuntimeError(f"Heading not found: {startswith}")


def find_last_idx(children: list[ET.Element], startswith: str) -> int:
    found = None
    for i, el in enumerate(children):
        if ptext(el).startswith(startswith):
            found = i
    if found is None:
        raise RuntimeError(f"Heading not found: {startswith}")
    return found


def register_namespaces(xml: str) -> None:
    head = xml[:8000]
    for prefix, uri in re.findall(r'xmlns:([A-Za-z0-9]+)="([^"]+)"', head):
        ET.register_namespace(prefix, uri)


def patch_body(root: ET.Element) -> None:
    body = root.find(W + "body")
    if body is None:
        raise RuntimeError("No body")
    children = list(body)

    roadmap = find_last_idx(children, "4 ROADMAP UFFICIALE DEL PROGETTO")
    i_411 = find_idx(children, "4.1.1 Dettaglio", after=roadmap)
    i_412 = find_idx(children, "4.1.2 Dettaglio", after=i_411)
    i_4121 = find_idx(children, "4.1.2.1 Allineamento", after=i_412)
    i_413 = find_idx(children, "4.1.3 Dettaglio", after=i_4121)
    i_4131 = find_idx(children, "4.1.3.1 Stato", after=i_413)
    i_414 = find_idx(children, "4.1.4 Dettaglio", after=i_4131)
    i_415 = find_idx(children, "4.1.5 Dettaglio", after=i_414)
    i_416 = find_idx(children, "4.1.6 Dettaglio", after=i_415)
    i_all1 = find_idx(children, "1. ALLEGATO 1", after=i_416)
    i_413_title = find_idx(children, "4.13 B7 Igiene", after=i_all1)
    i_stamp = None
    for i, el in enumerate(children):
        if ptext(el).startswith("Aggiornamento documentale sidecar 9.1_B7"):
            i_stamp = i
    if i_stamp is None:
        raise RuntimeError("B7 stamp not found")

    moved_pipeline = children[i_4121:i_413]
    moved_backup = children[i_4131:i_414]

    new_411 = [
        children[i_411],
        make_p(
            "Dashboard — Revisione UI: completata. "
            "Navigazione, scrolling e atterraggio Importa dati sono nel nucleo già operativo. "
            "La tabella TO DO non è più in roadmap."
        ),
    ]
    new_412 = [
        children[i_412],
        make_p(
            "Ricerca avanzata V1: Motore A CONVALIDATO 21/08/2026 (Allegato 4.7). "
            "Motore B (Interrogazione / Query Archivistica) resta in V2. "
            "Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13). "
            "La tabella TO DO (B3/B4/B5 con la numerazione ante Motore A) e il paragrafo "
            "4.1.2.1 sono storici: Allegato 4.14, non vigenti."
        ),
    ]
    new_413 = [
        children[i_413],
        make_p(
            "Utility CONVALIDATE: Backup B1, Ripristino REPLACE B2, "
            "Import MERGE B5 (Allegato 4.10), Codice QR B4 (Allegato 4.8). "
            "Encoding dati contenitore non si applica (4.8). "
            "Etichetta QR: ⋮ della card Contenitore (Allegato 4.13). "
            "4.1.3.1 e 4.1.3.2 (registro componenti e M3–M8) sono storici: "
            "Allegato 4.15, non vigenti. Il Registro Backup non prevale sul codice."
        ),
    ]
    new_414 = [
        children[i_414],
        make_p(
            "Strumenti contestuali CONVALIDATI 22/08/2026: stampa ed export della vista "
            "(Allegato 4.11) e ricerca vocale (Allegato 4.12). "
            "Non appartengono a Utility. La tabella TO DO (Stampa / Esporta / Voce ❌) "
            "non è più in roadmap."
        ),
    ]
    new_415 = [
        children[i_415],
        make_p(
            "Igiene e governance B7 CONVALIDATO 22/08/2026 (Allegato 4.13): "
            "catalogo 2.6, ricerca semplice, Genera Modello cartella Backup, "
            "menu QR sulla card, Room senza wipe, allowBackup=false, orfani rimossi. "
            "Le voci 4.1.5 non eseguite in B7 (AppShell, freeze, matrice, context card, "
            "navigazione) non sono core V1: spostate in 4.1.6."
        ),
    ]

    extra_416 = [
        make_p("GOVERNANCE / UI NON V1 (spostate da 4.1.5)"),
        make_p(
            "Verifica architettura navigazione; uniformazione context card; "
            "AppShell globale; formalizzazione aree consolidate e sensibili; "
            "freeze aree mature; regression scope; matrice impatti."
        ),
        make_p(
            "Elenco card dei Backup nella pagina Backup (ex M4.B) non è in B1: "
            "resta eventuale evoluzione, non core V1."
        ),
    ]

    storico = [
        make_p("4.14 Storico allineamento pipeline (ante B3)", 2),
        make_p(
            "Testo storico della precedente 4.1.2.1. Non è fonte operativa. "
            "Prevalgono Allegato 4.7 e il quadro 4.1. "
            "La numerazione B1–B5 di questo storico non è quella dei blocchi D0–B7."
        ),
        *moved_pipeline,
        make_p("4.15 Storico roadmap Backup (ante B1)", 2),
        make_p(
            "Testo storico della precedente 4.1.3.1–4.1.3.2 (registro componenti, "
            "sequenza M3.B–M8, intervento Codex 7/8/2026). Non è fonte operativa. "
            "Prevalgono B1 CONVALIDATO, 3.4.1 e il quadro 4.1. "
            "Il Registro Backup COMPLETO/CERTIFICATA non prevale sul codice."
        ),
        *moved_backup,
    ]

    rebuilt: list[ET.Element] = []
    rebuilt.extend(children[:i_411])
    rebuilt.extend(new_411)
    rebuilt.extend(new_412)
    rebuilt.extend(new_413)
    rebuilt.extend(new_414)
    rebuilt.extend(new_415)
    rebuilt.extend(children[i_416:i_all1])
    rebuilt.extend(extra_416)
    rebuilt.extend(children[i_all1 : i_stamp + 1])
    rebuilt.extend(storico)
    rebuilt.extend(children[i_stamp + 1 :])

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
    print("Patched", DOC, "bytes", DOC.stat().st_size)


if __name__ == "__main__":
    main()
