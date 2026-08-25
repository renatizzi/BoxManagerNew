# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: Google Play — Archivio completo e paywall
CONVALIDATO 25/08/2026 (Allegato 4.19). Fette Play 1–3."""
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


def section_419() -> list[ET.Element]:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return [
        make_p(
            "4.19 Google Play — Archivio completo e paywall "
            "(congelato 25/08/2026)",
            2,
        ),
        make_p(
            "CONVALIDATO il 25/08/2026. Progetto parallelo a V2: preparare "
            "il primo binario Play senza riaprire D0–B7, pipeline 0–10, "
            "Motore A/B né catalogo 2.6. I testi paywall non fanno parte "
            "del catalogo 2.6."
        ),
        make_p(
            "Modello commerciale CONVALIDATO: scarico gratis; pacchetto "
            "Archivio completo a pagamento (IAP Google Play: non ancora "
            "integrato nel codice). Base gratis: archivio, ricerca semplice "
            "per ambito, backup/ripristino, stampa. Gate a pagamento: "
            "Ricerca avanzata, Codice QR (scan), Etichetta QR, Importa dati, "
            "Esporta dati. Il primo AAB su Play nasce già con i lucchetti."
        ),
        make_p(
            "Fetta 1 CONVALIDATA: applicationId it.renatizzi.boxmanager "
            "(namespace Kotlin com.example.boxmanagernew invariato). "
            "Fetta 2 CONVALIDATA: icone launcher PNG allineate a "
            "boxmanager_launcher; fallback categoria outline_browse_24; "
            "hardening camera QR (QrScanController / QRActivity). "
            "Fetta 3 CONVALIDATA: paywall ArchivioCompletoActivity + gate "
            "su ingressi ricerca avanzata, QR, etichetta QR, import, export "
            "(incluso export da MainActivity e ViewOutputController)."
        ),
        make_p(
            "Anteprima paywall: titolo = nome funzione; sottotitolo "
            "«Funzione a pagamento»; contatore prove nel testo; bottone "
            "PROVA senza numero; prove esaurite → ACQUISTA + hint pacchetto "
            "(toast acquisto non ancora attivo). Nei messaggi paywall non si "
            "elenca cosa resta gratis. «Provala!» solo quando il bottone è "
            "PROVA, non con ACQUISTA. Esempi sempre tra parentesi, testo "
            "esempio in corsivo. Importa/Esporta: messaggio incrociato "
            "(sistema aperto verso archivi CSV su foglio elettronico). "
            "Bottom bar presente su ArchivioCompletoActivity, ancorata alla "
            "tab di provenienza."
        ),
        make_p(
            "Prove gratuite (limiti codice): Ricerca avanzata 3 (una per "
            "domanda in GlobalSearchActivity); Codice QR 1; Etichetta QR 1; "
            "Esporta dati 1; Importa dati 0 (nessuna prova su file reali). "
            "Debug solo build debug: switch Archivio completo e azzera prove "
            "in Impostazioni."
        ),
        make_p(
            "Prossimo Play (non codice V1): privacy policy URL, form Data "
            "safety, keystore/AAB, pulizia permessi fusi ML Kit se applicabile, "
            "closed test 12 tester × 14 giorni (account personale post-2023), "
            "integrazione Google Billing. Motore B resta assente in V1. "
            "4.1.6 resta NON core."
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
        "Motore B F7",
    )
    replace_p_text(
        children[i_mb],
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B F7–F9 / CATEGORY / LOCATION CONVALIDATI 23/08/2026 "
        "(Allegato 4.17). Inventario Motore A e stampa contestuale Ricerca "
        "avanzata CONVALIDATI 23/08/2026 (Allegato 4.18). Google Play "
        "Archivio completo e paywall CONVALIDATI 25/08/2026 (Allegato 4.19). "
        "PATTERN_010 resta SOSPESO. Fuori dal flusso core restano le "
        "evoluzioni elencate in 4.1.6. Vietato anticipare un blocco "
        "successivo prima della convalida utente (sez. 1.11).",
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
        "CONVALIDATI 23/08/2026 (Allegato 4.18). Google Play paywall "
        "Archivio completo CONVALIDATO 25/08/2026 (Allegato 4.19). "
        "PATTERN_010 resta SOSPESO. Ricerca semplice CONVALIDATA 22/08/2026 "
        "(Allegato 4.13). La tabella TO DO (B3/B4/B5 con la numerazione ante "
        "Motore A) e il paragrafo 4.1.2.1 sono storici: Allegato 4.14, non vigenti.",
    )

    i_appshell = find_idx(
        children,
        "AppShell globale, context card e navigazione tab CONVALIDATI 24/08/2026",
    )
    replace_p_text(
        children[i_appshell],
        "AppShell globale, context card e navigazione tab CONVALIDATI "
        "24/08/2026 (chiusura V1). Google Play — Archivio completo e paywall "
        "CONVALIDATI 25/08/2026 (Allegato 4.19). Freeze aree mature, "
        "regression scope e matrice impatti restano governance di progetto, "
        "non funzioni utente. Non sono fette V2 applicative.",
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
        "CONVALIDATI 23/08/2026 (Allegato 4.18). Google Play paywall "
        "CONVALIDATO 25/08/2026 (Allegato 4.19). PATTERN_010 resta SOSPESO. "
        "4.1.6 resta NON core.",
    )

    if any(
        ptext(el).startswith("4.19 Google Play")
        for el in children
    ):
        print("4.19 already present — skip insert")
        return

    i_414 = find_idx(children, "4.14 Storico allineamento pipeline")
    rebuilt: list[ET.Element] = []
    rebuilt.extend(children[:i_414])
    rebuilt.extend(section_419())
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
