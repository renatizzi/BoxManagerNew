# -*- coding: utf-8 -*-
"""Sidecar 9.1_B5: B5 Import MERGE CONVALIDATO 22/08/2026."""
from __future__ import annotations

import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.sax.saxutils import escape

SRC = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs\Nota_Integrata_9.1_B5.docx"
)
DST = SRC


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


def catalog_import() -> str:
    return p_body(
        "IMPORTA DATI (catalogo 22/08/2026 — B5): "
        "Controlli preliminari KO: esistenza del file; formato corretto; "
        "struttura conforme al modello ufficiale; presenza dei campi obbligatori. "
        "Frase: Se uno qualsiasi dei controlli fallisce, l'importazione viene annullata "
        "senza modificare l'archivio. Errore bloccante, area messaggi Importa dati. "
        "Dipendenze KO: un Contenitore non può essere importato se fa riferimento a una "
        "Categoria o a una Posizione inesistente. un Oggetto non può essere importato se "
        "fa riferimento a un Contenitore inesistente. qualsiasi violazione delle relazioni "
        "previste dal modello dati comporta l'annullamento dell'importazione. "
        "Cartella Backup non accessibile: stesso testo di B1 "
        "(Cartella non accessibile. Scegli di nuovo la cartella.). "
        "File ZIP già esistente: stesso dialogo di B1 (File già esistente. Sostituirlo?). "
        "Backup automatico OK: Backup completato (catalogo B1) + nome file PRE_IMPORT_ e cartella. "
        "Report 3.4.3: record letti; record importati; record ignorati (duplicati); "
        "record scartati per errore. Nessuna frase di esito inventata."
    )


def section_410() -> str:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return "".join(
        [
            p_title(
                2,
                "4.10 B5 Importa dati MERGE (congelato 22/08/2026)",
            ),
            p_body(
                "B5 Importa dati (MERGE) è CONVALIDATO il 22/08/2026. "
                "3.4.3 resta la fonte funzionale. "
                "Due azioni: Genera Modello di Importazione e Importa dati. "
                "Flusso Importa: selezione file, controlli, dipendenze, anteprima, "
                "backup automatico PRE_IMPORT_ggMMaa_HHmm.zip nella cartella Backup B1, "
                "MERGE transazionale all-or-nothing, report 3.4.3. "
                "I contenitori nuovi ricevono permanentId sull'insert B4. "
                "3.4.4 QR, Motore B, ricerca semplice, B6 e B7 restano fuori."
            ),
            p_body(
                "Duplicato contenitore: stesso nome, categoria e posizione già in archivio "
                "o nello stesso file. Duplicato oggetto: stesso nome, contenitore, "
                "descrizione e quantità. I duplicati si ignorano; quantità non numerica "
                "annulla il MERGE. L'etichetta cartella usa il percorso del tree SAF "
                "(es. Download/Boxmanager_Bck), non il solo nome documents."
            ),
            p_body(
                f"Aggiornamento documentale sidecar 9.1_B5 {stamp}."
            ),
        ]
    )


def patch_document_xml(xml: str) -> str:
    xml = replace_once(
        xml,
        "Il MERGE e il report 3.4.3 restano da implementare.",
        "MERGE CONVALIDATO 22/08/2026: transazione all-or-nothing; "
        "report 3.4.3 (letti / importati / ignorati duplicati / scartati per errore); "
        "permanentId sui contenitori nuovi.",
    )
    xml = replace_once(
        xml,
        "B5 MERGE (controlli, dipendenze, anteprima, backup automatico, "
        "transazione all-or-nothing, report) non è chiuso. "
        "3.4.4 QR non si tocca. B6 e B7 restano fuori.",
        "B5 MERGE è CONVALIDATO il 22/08/2026 (Allegato 4.10). "
        "3.4.4 QR non si tocca. B6 e B7 restano fuori.",
    )
    xml = replace_once(
        xml,
        "B5 — Importa dati: MERGE, template CSV ufficiale V1, transazione all-or-nothing.",
        "B5 — Importa dati CONVALIDATO 22/08/2026: MERGE, template CSV ufficiale V1, "
        "transazione all-or-nothing, report 3.4.3.",
    )
    xml = replace_once(
        xml,
        "D0, B1, B2, B3 e B4 convalidati. Prossimo: B5 — Importa dati (MERGE), "
        "subordinato a sez. 1.11. L'ordine dei blocchi è quello del quadro sintetico in 4.1.",
        "D0, B1, B2, B3, B4 e B5 convalidati. Prossimo: B6 — Strumenti contestuali, "
        "subordinato a sez. 1.11. L'ordine dei blocchi è quello del quadro sintetico in 4.1.",
    )
    xml = insert_after_text(
        xml,
        "Categorie, luoghi e oggetti restano su Conferma eliminazione?",
        catalog_import(),
    )
    xml = insert_after_text(
        xml,
        "B4 Codice QR CONVALIDATO 21/08/2026 (Allegato 4.8). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
        p_body(
            "B5 Import MERGE CONVALIDATO 22/08/2026 (Allegato 4.10). "
            "Motore B in V2. Ricerca semplice: fine-tuning."
        ),
    )
    xml = insert_after_text(
        xml,
        "Aggiornamento documentale sidecar 9.1_B5 22/08/2026 07:30.",
        section_410(),
    )
    return xml


def main() -> None:
    if not SRC.exists():
        raise SystemExit(f"Missing {SRC}")
    with zipfile.ZipFile(SRC) as zin:
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
