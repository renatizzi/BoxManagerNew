# -*- coding: utf-8 -*-
"""Sidecar 9.1_B5: recepisce il tracciato CSV V1. MERGE B5 non chiuso."""
from __future__ import annotations

import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.sax.saxutils import escape

SRC = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs\Nota_Integrata_9.1_B4.docx"
)
DST = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs\Nota_Integrata_9.1_B5.docx"
)


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


def section_track() -> str:
    return "".join(
        [
            p_body(
                "Tracciato ufficiale V1 del Modello di Importazione (CSV), "
                "congelato il 22/08/2026 con la convalida di Genera Modello. "
                "Un solo file, separatore punto e virgola, UTF-8 con BOM, fine riga CRLF, "
                "nessuna riga di esempio. Nome file: Modello_Importazione.csv."
            ),
            p_body(
                "formato;BoxManager_Import;1"
            ),
            p_body(
                "sezione;CONTENITORI"
            ),
            p_body(
                "nome;categoria;posizione"
            ),
            p_body(
                "sezione;OGGETTI"
            ),
            p_body(
                "nome;contenitore;descrizione;quantita"
            ),
            p_body(
                "Si compilano Contenitori e Oggetti. "
                "Categoria e Posizione non si creano dal file: devono già esistere in archivio "
                "(Categorie; Impostazioni → Luoghi) e i nomi nel CSV devono coincidere. "
                "Niente icona, id Room, permanentId, lastModified. "
                "Il permanentId dei contenitori nuovi lo assegna l'insert (B4) in sede di MERGE. "
                "Il foglio Excel dell'utente deve essere compatibile con questo tracciato; "
                "non si importa un Excel con struttura diversa. "
                "Il MERGE e il report 3.4.3 restano da implementare."
            ),
        ]
    )


def section_49() -> str:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return "".join(
        [
            p_title(
                2,
                "4.9 Tracciato CSV Import V1 (congelato 22/08/2026)",
            ),
            p_body(
                "Genera Modello di Importazione è CONVALIDATO il 22/08/2026. "
                "Il tracciato CSV ufficiale V1 è recepito in 3.4.3. "
                "B5 MERGE (controlli, dipendenze, anteprima, backup automatico, "
                "transazione all-or-nothing, report) non è chiuso. "
                "3.4.4 QR non si tocca. B6 e B7 restano fuori."
            ),
            p_body(
                f"Aggiornamento documentale sidecar 9.1_B5 {stamp}."
            ),
        ]
    )


def patch_document_xml(xml: str) -> str:
    xml = insert_after_text(
        xml,
        "Il modello costituisce l'unico formato ufficiale supportato "
        "per l'importazione dei dati nella V1.",
        section_track(),
    )
    xml = insert_after_text(
        xml,
        "Aggiornamento documentale sidecar 9.1_B4 21/08/2026 23:35.",
        section_49(),
    )
    return xml


def main() -> None:
    if not SRC.exists():
        raise SystemExit(f"Missing {SRC}")
    with zipfile.ZipFile(SRC) as zin:
        names = zin.namelist()
        xml = zin.read("word/document.xml").decode("utf-8")
        xml = patch_document_xml(xml)
        with zipfile.ZipFile(DST, "w", compression=zipfile.ZIP_DEFLATED) as zout:
            for name in names:
                data = xml.encode("utf-8") if name == "word/document.xml" else zin.read(name)
                zout.writestr(name, data)
    print("Wrote", DST, "bytes", DST.stat().st_size)


if __name__ == "__main__":
    main()
