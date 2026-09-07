# -*- coding: utf-8 -*-
"""Nota Integrata 9.2 — recepisce Disco di rete (B-SEL-CARTELLA) e aggiorna Roadmap.

Aggiorna: 3.6.6 Prossime implementazioni, quadro 4.1, titolo Roadmap,
4.1.3 Utility, paragrafo Prossimo 4.19/4.20, nuovo Allegato 4.22.
"""
from __future__ import annotations

import re
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parent
DOC = ROOT / "Nota_Integrata_9.2.docx"

W = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"
XML_SPACE = "{http://www.w3.org/XML/1998/namespace}space"
STAMP = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")


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


def replace_if_startswith(
    children: list[ET.Element],
    start: str,
    new_text: str,
) -> int:
    for el in children:
        if ptext(el).startswith(start):
            replace_p_text(el, new_text)
            return 1
    return 0


def replace_in_text(children: list[ET.Element], old: str, new: str) -> int:
    n = 0
    for el in children:
        t = ptext(el)
        if old in t:
            replace_p_text(el, t.replace(old, new, 1))
            n += 1
    return n


def make_title(level: int, text: str) -> ET.Element:
    p = ET.Element(W + "p")
    ppr = ET.SubElement(p, W + "pPr")
    style = ET.SubElement(ppr, W + "pStyle")
    style.set(W + "val", f"Titolo{level}")
    r = ET.SubElement(p, W + "r")
    t = ET.SubElement(r, W + "t")
    t.set(XML_SPACE, "preserve")
    t.text = text
    return p


def make_normale(text: str) -> ET.Element:
    p = ET.Element(W + "p")
    ppr = ET.SubElement(p, W + "pPr")
    style = ET.SubElement(ppr, W + "pStyle")
    style.set(W + "val", "Normale")
    r = ET.SubElement(p, W + "r")
    t = ET.SubElement(r, W + "t")
    t.set(XML_SPACE, "preserve")
    t.text = text
    return p


def find_idx(children: list[ET.Element], start: str) -> int:
    for i, el in enumerate(children):
        if ptext(el).startswith(start):
            return i
    raise RuntimeError(f"Not found: {start[:80]}")


def patch_settings_table(children: list[ET.Element]) -> int:
    """Sostituisce «Prossime implementazioni: Scelta lingua» nella tabella 3.6.6."""
    old = "Prossime implementazioni: Scelta lingua"
    new = (
        "Stato Impostazioni al 07/09/2026: Scelta lingua CONVALIDATA "
        "(filone M / CK1–CK2). Disco di rete CONVALIDATO "
        "(card Impostazioni → Disco di rete; app CIFS Documents Provider; "
        "Backup / Esporta / Invia su cartella di rete — Allegato 4.22). "
        "«Prossime implementazioni» Impostazioni: nessuna voce aperta in V1 "
        "in attesa della fine del test Play."
    )
    for el in children:
        if el.tag.endswith("}tbl"):
            for t_node in el.iter(W + "t"):
                if t_node.text and old in t_node.text:
                    t_node.text = t_node.text.replace(old, new)
                    return 1
            # testo spezzato su più w:t
            full = "".join((t.text or "") for t in el.iter(W + "t"))
            if old in full:
                # riscrivi l’ultimo paragrafo della cella che contiene Prossime
                for p in el.iter(W + "p"):
                    pt = "".join((t.text or "") for t in p.iter(W + "t"))
                    if "Prossime implementazioni" in pt:
                        replace_p_text(p, new)
                        return 1
    return 0


def main() -> None:
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

    n += patch_settings_table(children)

    n += replace_if_startswith(
        children,
        "Questa è l'unica Roadmap ufficiale (Nota Integrata 9.2). Quadro al ",
        "Questa è l'unica Roadmap ufficiale (Nota Integrata 9.2). Quadro al "
        "07/09/2026. I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
        "in caso di conflitto prevale questo quadro.",
    )

    n += replace_if_startswith(
        children,
        "ROADMAP DEL PROGETTO AL ",
        "ROADMAP DEL PROGETTO AL 07/09/2026",
    )

    n += replace_if_startswith(
        children,
        "Stato al 01/09/2026 (Nota Integrata 9.2).",
        "Stato al 07/09/2026 (Nota Integrata 9.2). Flusso core V1 chiuso "
        "(D0–B7; Allegati 4.7–4.18; AppShell 24/08/2026). Google Play — "
        "fase binario e accesso Archivio completo CONVALIDATA "
        "(Allegato 4.19): applicationId it.renatizzi.boxmanager; "
        "Play 1.2 (versionCode 3) in test chiuso Console; su main solo "
        "bugfix tester; prova a tempo (default 14 gg); rinnovo via "
        "CONDIVIDI; codice locale; Google Billing congelato. Merge "
        "famiglia B0–B5 CONVALIDATO (Allegato 4.20): flavor famiglia. "
        "Filone M Scelta lingua + ricerca EN CONVALIDATO (CK1–CK2, "
        "03–04/09/2026). Disco di rete CONVALIDATO 07/09/2026 "
        "(Allegato 4.22; build 1.3-famigliaB5.22). P1 igiene file "
        "CONVALIDATO (B5.7). Roadmap vigente ora (Progetto 1 — Play): "
        "closed test in corso; dopo 1–2 mesi di utilizzo, rivalutare "
        "donazioni o micro-AdMob — non IAP finché non c’è quadro fiscale. "
        "A test Play chiuso: la BoxManager di sviluppo (archivio "
        "condiviso + inglese + disco di rete) sostituisce la 1.2 "
        "(filone M3). Progetto 2 — V2 (dopo Play verde), dettaglio in "
        "4.1.6. PATTERN_010 resta SOSPESO. Motore B già in V1 per "
        "F7–F9 / CATEGORY / LOCATION (4.17). Vietato anticipare un "
        "blocco successivo prima della convalida utente (sez. 1.11).",
    )

    n += replace_if_startswith(
        children,
        "Utility CONVALIDATE: Backup B1, Ripristino REPLACE B2",
        "Utility CONVALIDATE: Backup B1, Ripristino REPLACE B2, Import "
        "MERGE B5 (Allegato 4.10), Codice QR B4 (Allegato 4.8). Encoding "
        "dati contenitore non si applica (4.8). Etichetta QR: ⋮ della "
        "card Contenitore (Allegato 4.13). Disco di rete CONVALIDATO "
        "07/09/2026 (Allegato 4.22): Impostazioni → Disco di rete; "
        "selettore cartelle Backup/Esporta/Invia su unità di rete via "
        "CIFS Documents Provider; file Backup con estensione .zip. "
        "4.15.1 e 4.15.2 sono storici: Allegato 4.15, non vigenti. "
        "Il Registro Backup non prevale sul codice.",
    )

    n += replace_if_startswith(
        children,
        "Prossimo (Roadmap vigente, Progetto 1): Play 1.2 in test chiuso",
        "Prossimo (Roadmap vigente, Progetto 1): Play 1.2 in test chiuso "
        "Console; su main solo bugfix tester. Dopo 1–2 mesi di utilizzo: "
        "rivalutare donazioni (Ko-fi / Buy Me a Coffee) o micro-AdMob. "
        "Billing IAP solo se/quando il quadro fiscale lo consente. "
        "Merge famiglia = Allegato 4.20. Scelta lingua = filone M "
        "CONVALIDATO (CK1–CK2). Disco di rete = Allegato 4.22 "
        "CONVALIDATO. P1 igiene file CONVALIDATO. A test chiuso: M3 "
        "(sviluppo sostituisce 1.2). Motore B evoluzioni oltre 4.17 e "
        "voci 4.1.6 = Progetto 2 dopo Play verde.",
    )

    # Allegato 4.22 prima di sectPr
    sect = next(
        i for i, el in enumerate(children) if el.tag.endswith("}sectPr")
    )
    allegato = [
        make_title(
            2,
            "4.22 Disco di rete / B-SEL-CARTELLA (congelato 07/09/2026)",
        ),
        make_normale(
            "CONVALIDATO SI Renato (device, build 1.3-famigliaB5.21–B5.22). "
            "Obiettivo: salvare Backup e altri file di archivio "
            "(Esporta, Invia tabelle, Invia archivio) su un disco o PC "
            "condiviso in Wi‑Fi di casa. BoxManager non implementa SMB "
            "nativo in V1: usa il selettore cartelle Android sulle unità "
            "rese visibili da un’app Documents Provider. App consigliata: "
            "CIFS Documents Provider (com.wa2c.android.cifsdocumentsprovider)."
        ),
        make_normale(
            "UI. Impostazioni → card Disco di rete. Dialoghi minimalisti: "
            "(1) se CIFS non è installata — istruzioni + campi essenziali "
            "(Nome unità, Storage, Host, Port, User/Password, Folder) + "
            "Installa l’app (Play Store); (2) se già installata — Apri "
            "l’app / Annulla. Rilevamento pacchetto via <queries> "
            "(Android 11+). Guida rapida in-app: voce Utility disco di "
            "rete. Bozza operativa: docs/famiglia/GUIDA_DISCO_RETE.md "
            "(non prevale su questo allegato)."
        ),
        make_normale(
            "Flusso utente. Configura CIFS una volta → Utility → Backup "
            "(o altro salvataggio) → Sfoglia → Tutte le cartelle… se "
            "serve → scegli cartella CIFS → CONSENTI. Dalla volta "
            "successiva riuso cartella (criterio B7 / salvataggio-file). "
            "File Backup creati con estensione .zip anche su provider "
            "di rete (createFile con nome completo)."
        ),
        make_normale(
            "Limiti V1. Niente SMB dentro BoxManager. Doppioni di "
            "connessione CIFS dopo disinstallazione/reinstallazione: "
            "gestiti in CIFS (eventuale Auto Backup Android), non da "
            "BoxManager. File Explorer Plus e simili spesso non espongono "
            "il disco ad altre app: non sostituiscono CIFS come ponte."
        ),
        make_normale(
            f"Aggiornamento documentale Nota Integrata 9.2 Allegato 4.22 "
            f"(Disco di rete) e quadro Roadmap 4.1 {STAMP}."
        ),
    ]
    for j, el in enumerate(allegato):
        children.insert(sect + j, el)
    n += len(allegato)

    # Riscrivi body
    for child in list(body):
        body.remove(child)
    for child in children:
        body.append(child)

    out_xml = ET.tostring(root, encoding="utf-8", xml_declaration=True)
    data["word/document.xml"] = out_xml

    tmp = DOC.with_suffix(".docx.tmp")
    with zipfile.ZipFile(tmp, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data[name])
    tmp.replace(DOC)
    print(f"Patched {DOC.name}: {n} edits (+ Allegato 4.22). Stamp {STAMP}")


if __name__ == "__main__":
    main()
