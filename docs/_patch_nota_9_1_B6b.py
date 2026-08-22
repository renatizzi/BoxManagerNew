# -*- coding: utf-8 -*-
"""Sidecar 9.1_B6b: B6/2 ricerca vocale CONVALIDATO 22/08/2026."""
from __future__ import annotations

import shutil
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.sax.saxutils import escape

ROOT = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs"
)
SRC = ROOT / "Nota_Integrata_9.1_B6.docx"
DST = ROOT / "Nota_Integrata_9.1_B6b.docx"


def p_body(text: str) -> str:
    return (
        "<w:p><w:r><w:rPr><w:sz w:val=\"20\"/><w:szCs w:val=\"20\"/></w:rPr>"
        f"<w:t xml:space=\"preserve\">{escape(text)}</w:t></w:r></w:p>"
    )


def p_title(level: int, text: str) -> str:
    return (
        f"<w:p><w:pPr><w:pStyle w:val=\"Titolo{level}\"/></w:pPr>"
        f"<w:r><w:t xml:space=\"preserve\">{escape(text)}</w:t></w:r></w:p>"
    )


def replace_once(xml: str, old: str, new: str) -> str:
    n = xml.count(old)
    if n != 1:
        raise RuntimeError(f"Anchor count {n} (expected 1): {old[:80]}")
    return xml.replace(old, new, 1)


def insert_after_text(xml: str, unique: str, block: str) -> str:
    i = xml.find(unique)
    if i < 0:
        raise RuntimeError(f"Anchor not found: {unique[:80]}")
    if xml.find(unique, i + 1) >= 0:
        raise RuntimeError(f"Anchor not unique: {unique[:80]}")
    j = xml.find("</w:p>", i)
    if j < 0:
        raise RuntimeError("No paragraph end after anchor")
    j += len("</w:p>")
    return xml[:j] + block + xml[j:]


def section_412() -> str:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return "".join(
        [
            p_title(
                2,
                "4.12 B6/2 Ricerca vocale (congelato 22/08/2026)",
            ),
            p_body(
                "B6/2 Ricerca vocale è CONVALIDATO il 22/08/2026. "
                "3.4.5 resta la fonte: input al posto della tastiera sulla vista corrente; "
                "tap sull'icona; non sta in Utility. "
                "Contenitori, Oggetti e Categorie: il testo riconosciuto va nel campo Cerca già presente; "
                "filtro LIVE invariato; al ritorno dal dialogo la tastiera è chiusa. "
                "Dashboard: stesso invio di Fatto. "
                "Ambito (Contenitori, Oggetti, Categorie, Posizione) apre la lista mediata dalla card. "
                "Ricerca avanzata apre la pagina domanda."
            ),
            p_body(
                "Il campo domanda di Ricerca avanzata resta senza icona vocale: "
                "lì si riformula a tastiera (chiarificazione). "
                "Luoghi: nessun campo cerca, nessuna voce. "
                "Il dialogo vocale è di sistema. Nessuna frase nuova nel catalogo 2.6. "
                "Annulla o riconoscitore assente: nessun inserimento. "
                "3.4.4 QR non si tocca. B7 igiene resta dopo."
            ),
            p_body(
                f"Aggiornamento documentale sidecar 9.1_B6b {stamp}."
            ),
        ]
    )


def catalog_b6b() -> str:
    return p_body(
        "RICERCA VOCALE (catalogo 22/08/2026 — B6/2): "
        "nessuna frase nuova. "
        "Il dialogo vocale è di sistema. "
        "Annulla o riconoscitore assente: nessun inserimento, nessun messaggio."
    )


def patch_document_xml(xml: str) -> str:
    xml = replace_once(
        xml,
        "B6 — Strumenti contestuali CONVALIDATO 22/08/2026 per stampa ed export della vista corrente "
        "(Allegato 4.11). Non appartengono a Utility. "
        "Ricerca vocale di 3.4.5: non implementata; resta l'ultima funzione V1.",
        "B6 — Strumenti contestuali CONVALIDATO 22/08/2026: stampa/export vista (Allegato 4.11) "
        "e ricerca vocale (Allegato 4.12). Non appartengono a Utility.",
    )
    xml = replace_once(
        xml,
        "D0, B1, B2, B3, B4, B5 e B6 (stampa/export vista) convalidati. "
        "Ricerca vocale ancora da fare (ultima funzione V1). "
        "Prossimo: B7 — Igiene e governance, subordinato a sez. 1.11.",
        "D0, B1, B2, B3, B4, B5 e B6 (stampa/export vista e ricerca vocale) convalidati. "
        "Prossimo: B7 — Igiene e governance, subordinato a sez. 1.11.",
    )
    xml = replace_once(
        xml,
        "3.4.4 QR non si tocca. Ricerca vocale e B7 restano fuori da B5.",
        "3.4.4 QR non si tocca. B6/2 ricerca vocale CONVALIDATO (Allegato 4.12). "
        "B7 resta fuori da B5.",
    )
    xml = replace_once(
        xml,
        "B6 stampa/export vista CONVALIDATO 22/08/2026 (Allegato 4.11). "
        "Ricerca vocale non implementata (ultima funzione V1). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
        "B6 stampa/export vista CONVALIDATO 22/08/2026 (Allegato 4.11). "
        "B6/2 ricerca vocale CONVALIDATO 22/08/2026 (Allegato 4.12). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
    )
    xml = replace_once(
        xml,
        "Ricerca vocale, stampa, export della vista: assenti.",
        "Ricerca vocale, stampa, export della vista: CONVALIDATI 22/08/2026 (B6 / B6/2).",
    )
    xml = replace_once(
        xml,
        "La ricerca vocale di 3.4.5 / 4.1.4 non è in questo sidecar: resta l'ultima funzione V1. "
        "3.4.4 QR non si tocca. B7 igiene resta dopo.",
        "B6/2 ricerca vocale è CONVALIDATO il 22/08/2026 (Allegato 4.12). "
        "3.4.4 QR non si tocca. B7 igiene resta dopo.",
    )
    xml = insert_after_text(
        xml,
        "STAMPA / ESPORTA VISTA (catalogo 22/08/2026 — B6): "
        "Nessun risultato trovato. "
        "Cartella non accessibile. Scegli di nuovo la cartella. "
        "File già esistente. Sostituirlo? "
        "Confermi? (solo se il nome file Esporta è libero). "
        "Conteggi foglio: N. Contenitori; N. Oggetti; N. Categorie; N. Posizioni.",
        catalog_b6b(),
    )
    xml = insert_after_text(
        xml,
        "Priorità: medio-alta: Funzione prevista per sopperire al disagio dell’uso della tastiera "
        "in ambienti scomodi (garage, cantina, magazzino).",
        p_body(
            "B6/2 CONVALIDATO 22/08/2026 (Allegato 4.12): icona sul campo Cerca di "
            "Contenitori, Oggetti, Categorie e Dashboard. "
            "Campo domanda di Ricerca avanzata senza icona vocale."
        ),
    )
    xml = insert_after_text(
        xml,
        "Aggiornamento documentale sidecar 9.1_B6 22/08/2026 16:51.",
        section_412(),
    )
    return xml


def main() -> None:
    if not SRC.exists():
        raise SystemExit(f"Missing {SRC}")
    shutil.copyfile(SRC, DST)
    with zipfile.ZipFile(DST) as zin:
        names = zin.namelist()
        xml = zin.read("word/document.xml").decode("utf-8")
        xml = patch_document_xml(xml)
        data_by_name = {
            name: (xml.encode("utf-8") if name == "word/document.xml" else zin.read(name))
            for name in names
        }
    with zipfile.ZipFile(DST, "w", compression=zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, data_by_name[name])
    print("Wrote", DST, "bytes", DST.stat().st_size)


if __name__ == "__main__":
    main()
