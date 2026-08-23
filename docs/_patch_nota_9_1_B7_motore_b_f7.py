# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: Motore B F7 CONVALIDATO 23/08/2026 (Allegato 4.17)."""
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
            "4.17 Motore B F7 / PATTERN_007 (congelato 23/08/2026)",
            2,
        ),
        make_p(
            "Motore B avviato. F7 / PATTERN_007 (F7-01…05) CONVALIDATO il 23/08/2026. "
            "Pipeline 0–10 resta l'unico avvio. Fase 8 Interrogazione → Motore B. "
            "Risposta in pagina Ricerca avanzata, non lista Contenitori. "
            "Heading ufficiale: Elenco dei contenitori che hanno oggetti uguali. "
            "Vuoto: Nessun risultato trovato. "
            "Motore A (lista Contenitori) invariato. PATTERN_010 resta SOSPESO. "
            "4.1.6 resta NON core. D0–B7 e Allegato 4.16 non si riaprono."
        ),
        make_p(
            "Cinque varianti ufficiali (Allegato 1, elenco intero, non ricomposto): "
            "Cerca tutti i contenitori che contengono doppioni; "
            "In quali contenitori ci sono oggetti uguali; "
            "Elenco dei contenitori che hanno oggetti uguali; "
            "Dove trovo lo stesso tipo di oggetti; "
            "Trova i contenitori che hanno almeno un oggetto uguale. "
            "Entità attese Allegato 1: OBJECT, BOX. Fulcro: OBJECT."
        ),
        make_p(
            "Regole F7 congelate. Contenitori in generale + doppioni/oggetti uguali → "
            "regola 2: stesso nome oggetto in tutto l'archivio; elenco di tutti i "
            "contenitori che hanno quel nome. Domanda su un contenitore nominato → "
            "regola 1 (solo dentro quel box): non implementata in questa fetta. "
            "Confronto: solo nome (non descrizione). Quantità mai chiave (campo "
            "facoltativo). Accenti e caratteri speciali: CanonicalNormalizer "
            "(Caffè = Caffe = CAFFE'). Singolare/plurale se una sola parola, come "
            "Motore A (Vite = Viti); nomi a più parole senza inflessione token."
        ),
        make_p(
            "R19 invariato rispetto ad Allegato 4.7: chiarificazione solo se nella "
            "domanda c'è un nome che in archivio cade su 2+ core. Un selettore di "
            "tipo in domanda (contenitore X) chiude il bivio. F7-04 ufficiale "
            "(Dove trovo lo stesso tipo di oggetti) non apre il bivio "
            "contenitore/posizione: in frase non c'è il nome; «dove» è indicatore, "
            "non instrada."
        ),
        make_p(
            "F8 / PATTERN_008 NON CONVALIDATO. In Allegato 1 le entità attese sono "
            "OBJECT, BOX, CATEGORY; fulcro OBJECT. Un tentativo in codice "
            "(matcher sulla formulazione + confronto categoryId sui record) non "
            "porta CATEGORY nell'Output della Pipeline quando la domanda non "
            "nomina una categoria d'archivio. In Motore A la categoria resta un "
            "filtro sui contenitori solo se nominata (Allegato 4.7). "
            "«Elenco dei contenitori con categoria diversa» non è una variante "
            "ufficiale F8 e oggi dà Nessun risultato trovato: fase 3 vuota, "
            "nessun percorso BOX⇄CATEGORY."
        ),
        make_p(
            "Prossimo: CATEGORY come entità core nel Motore B, relazione "
            "BOX⇄CATEGORY, anche senza nome di categoria nella domanda. "
            "La Query Archivistica usa solo i dati di quella navigazione. "
            "Poi F8 (stesso tipo di oggetto su quel risultato). "
            "LOCATION per le interrogazioni distributive del Motore B (3.3.8) "
            "dopo CATEGORY. F7 non si riapre. Non classificare dalla formulazione "
            "né estendere il matcher F8 al posto del percorso."
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
        "Questa è l'unica Roadmap ufficiale. Quadro al 23/08/2026.",
    )
    replace_p_text(
        children[i_quadro],
        "Questa è l'unica Roadmap ufficiale. Quadro al 23/08/2026. "
        "I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
        "in caso di conflitto prevale questo quadro.",
    )

    i_mb = find_idx(
        children,
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B resta il prossimo passo V2.",
    )
    replace_p_text(
        children[i_mb],
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B avviato: F7 / PATTERN_007 CONVALIDATO 23/08/2026 "
        "(Allegato 4.17). F8 NON CONVALIDATO. "
        "Prossimo: CATEGORY nel Motore B (BOX⇄CATEGORY). "
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
        "Motore B: F7 / PATTERN_007 CONVALIDATO 23/08/2026 (Allegato 4.17). "
        "F8 / PATTERN_008 NON CONVALIDATO. "
        "Prossimo: CATEGORY come core nel Motore B (BOX⇄CATEGORY), poi F8. "
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
        "Motore B: F7 CONVALIDATO 23/08/2026 (Allegato 4.17); F8 no. "
        "Prossimo: CATEGORY nel Motore B. 4.1.6 resta NON core.",
    )

    i_414 = find_idx(children, "4.14 Storico allineamento pipeline")

    rebuilt: list[ET.Element] = []
    rebuilt.extend(children[:i_414])
    rebuilt.extend(section_417())
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
