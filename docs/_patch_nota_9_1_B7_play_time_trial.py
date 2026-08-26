# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: aggiorna Allegato 4.19 — prova a tempo + parametri admin."""
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


def make_p(text: str) -> ET.Element:
    p = ET.Element(W + "p")
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
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    with zipfile.ZipFile(DOC) as zin:
        names = zin.namelist()
        raw = zin.read("word/document.xml").decode("utf-8")
        data = {n: zin.read(n) for n in names}
    register_namespaces(raw)
    root = ET.fromstring(raw.encode("utf-8"))
    body = root.find(W + "body")
    if body is None:
        raise SystemExit("No body")
    children = list(body)

    n = 0
    insert_at = None
    for i, el in enumerate(children):
        text = ptext(el)
        if text.startswith(
            "Prove gratuite (limiti codice): Ricerca avanzata 3"
        ):
            replace_p_text(
                el,
                "Modello accesso CONVALIDATO 26/08/2026 (revisione parametri "
                "prevista dopo 1–2 mesi di distribuzione): prova unica a tempo "
                "su tutto Archivio completo (default 14 giorni dal primo uso "
                "di una funzione avanzata). Esaurita la prova: CONDIVIDI "
                "(Intent nativo, controllo locale) concede altri giorni "
                "(default +7) dopo N amicizie di condivisione (default 1), "
                "con cooldown 48 ore tra un rinnovo e il successivo. Codice "
                "locale BOXMANAGER-AMICO = sblocco permanente sul dispositivo. "
                "Niente Google Billing / prezzi in denaro. Messaggi: "
                "«Funzione avanzata», non «a pagamento».",
            )
            n += 1
            insert_at = i + 1
        if text.startswith("Prossimo Play (non codice V1):"):
            replace_p_text(
                el,
                "Prossimo Play (non codice V1): privacy policy URL, form Data "
                "safety, keystore/AAB, closed test 12×14, eventuale Billing "
                "solo se/quando il quadro fiscale lo consente. Fallback "
                "economico da rivalutare dopo 1–2 mesi: donazioni spontanee "
                "(Ko-fi / Buy Me a Coffee) o micro-AdMob. Motore B resta "
                "assente in V1. 4.1.6 resta NON core.",
            )
            n += 1

    if insert_at is not None:
        # Avoid duplicate insert if re-run
        already = any(
            ptext(el).startswith(
                "Parametri amministratore (solo utente «Renato Stefanizzi»)"
            )
            for el in children
        )
        if not already:
            extra = [
                make_p(
                    "Parametri amministratore (solo utente «Renato Stefanizzi» "
                    "in Impostazioni, nome esatto): tabella locale con periodo "
                    "di prova (giorni), rinnovo per condivisione (giorni), "
                    "numero amici da condividere. Default 14 / 7 / 1. Clamp "
                    "1–365, 1–90, 1–20. Non compare per altri utenti."
                ),
                make_p(
                    f"Aggiornamento documentale sidecar 9.1_B7 Allegato 4.19 "
                    f"(prova a tempo + admin) {stamp}."
                ),
            ]
            for offset, p in enumerate(extra):
                children.insert(insert_at + offset, p)
            n += len(extra)

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
    with zipfile.ZipFile(DOC, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    print("Patched", DOC, "updates", n)


if __name__ == "__main__":
    main()
