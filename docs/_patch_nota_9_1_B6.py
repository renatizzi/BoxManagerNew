# -*- coding: utf-8 -*-
"""Sidecar 9.1_B6: B6 stampa/export vista CONVALIDATO 22/08/2026."""
from __future__ import annotations

import shutil
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.sax.saxutils import escape

ROOT = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs"
)
SRC = ROOT / "Nota_Integrata_9.1_B5.docx"
DST = ROOT / "Nota_Integrata_9.1_B6.docx"


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


def section_411() -> str:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return "".join(
        [
            p_title(
                2,
                "4.11 B6 Stampa ed Esporta della vista corrente (congelato 22/08/2026)",
            ),
            p_body(
                "B6 Output (stampa ed export della vista corrente) è CONVALIDATO il 22/08/2026. "
                "3.4.5 resta la fonte: gli strumenti non stanno in Utility; operano sulla vista visibile. "
                "Contenitori: stampa A4 e CSV analogo a Importa (CONTENITORI visibili + OGGETTI di quei box). "
                "Oggetti nel box e Oggetti trovati: solo gli oggetti che soddisfano la chiave; "
                "totale N. Oggetti in testa; subtotali per box solo su Oggetti trovati. "
                "Categorie e Luoghi: sola stampa, senza CSV (il tracciato Import non ha quelle sezioni). "
                "Lista vuota: Nessun risultato trovato. Nessun foglio e nessun file."
            ),
            p_body(
                "Nome file Esporta proposto: ESPORTA_ddMMyy_HHmm.csv (stesso criterio data/ora del Backup). "
                "Dopo il primo CONSENTI Android si riusa la cartella Backup. "
                "Nel box del nome: Confermi? se il nome è libero; "
                "File già esistente. Sostituirlo? se il nome c'è già; SI/NO. "
                "La ricerca vocale di 3.4.5 / 4.1.4 non è in questo sidecar: resta l'ultima funzione V1. "
                "3.4.4 QR non si tocca. B7 igiene resta dopo."
            ),
            p_body(
                f"Aggiornamento documentale sidecar 9.1_B6 {stamp}."
            ),
        ]
    )


def catalog_b6() -> str:
    return p_body(
        "STAMPA / ESPORTA VISTA (catalogo 22/08/2026 — B6): "
        "Nessun risultato trovato. "
        "Cartella non accessibile. Scegli di nuovo la cartella. "
        "File già esistente. Sostituirlo? "
        "Confermi? (solo se il nome file Esporta è libero). "
        "Conteggi foglio: N. Contenitori; N. Oggetti; N. Categorie; N. Posizioni."
    )


def patch_document_xml(xml: str) -> str:
    xml = replace_once(
        xml,
        "B6 — Strumenti contestuali: ricerca vocale, stampa, export della vista corrente. "
        "Non appartengono a Utility.",
        "B6 — Strumenti contestuali CONVALIDATO 22/08/2026 per stampa ed export della vista corrente "
        "(Allegato 4.11). Non appartengono a Utility. "
        "Ricerca vocale di 3.4.5: non implementata; resta l'ultima funzione V1.",
    )
    xml = replace_once(
        xml,
        "D0, B1, B2, B3, B4 e B5 convalidati. Prossimo: B6 — Strumenti contestuali, "
        "subordinato a sez. 1.11. L'ordine dei blocchi è quello del quadro sintetico in 4.1.",
        "D0, B1, B2, B3, B4, B5 e B6 (stampa/export vista) convalidati. "
        "Ricerca vocale ancora da fare (ultima funzione V1). "
        "Prossimo: B7 — Igiene e governance, subordinato a sez. 1.11.",
    )
    xml = replace_once(
        xml,
        "B5 MERGE è CONVALIDATO il 22/08/2026 (Allegato 4.10). "
        "3.4.4 QR non si tocca. B6 e B7 restano fuori.",
        "B5 MERGE è CONVALIDATO il 22/08/2026 (Allegato 4.10). "
        "B6 stampa/export vista è CONVALIDATO il 22/08/2026 (Allegato 4.11). "
        "3.4.4 QR non si tocca. Ricerca vocale e B7 restano fuori da B5.",
    )
    xml = insert_after_text(
        xml,
        "B5 Import MERGE CONVALIDATO 22/08/2026 (Allegato 4.10). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
        p_body(
            "B6 stampa/export vista CONVALIDATO 22/08/2026 (Allegato 4.11). "
            "Ricerca vocale non implementata (ultima funzione V1). "
            "Motore B in V2. Ricerca semplice: fine-tuning."
        ),
    )
    xml = insert_after_text(
        xml,
        "Nessuna frase di esito inventata.",
        catalog_b6(),
    )
    xml = insert_after_text(
        xml,
        "Aggiornamento documentale sidecar 9.1_B5 22/08/2026 12:07.",
        section_411(),
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
