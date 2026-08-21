# -*- coding: utf-8 -*-
"""Build Nota 9.1_B4 sidecar: freeze B4 QR CONVALIDATO. Media untouched."""
from __future__ import annotations

import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.sax.saxutils import escape

SRC = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs\Nota_Integrata_9.1_B3d.docx"
)
DST = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs\Nota_Integrata_9.1_B4.docx"
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


def replace_once(xml: str, old: str, new: str) -> str:
    n = xml.count(old)
    if n != 1:
        raise RuntimeError(f"Anchor count {n} (expected 1): {old[:80]}")
    return xml.replace(old, new, 1)


def insert_after_text(xml: str, unique: str, block: str) -> str:
    i = xml.find(unique)
    if i < 0:
        raise RuntimeError(f"Anchor not found: {unique[:80]}")
    j = xml.find("</w:p>", i)
    if j < 0:
        raise RuntimeError("No paragraph end after anchor")
    j += len("</w:p>")
    return xml[:j] + block + xml[j:]


def flip_sdt_cross(xml: str, tag: str) -> str:
    anchor = f'<w:tag w:val="{tag}"/>'
    i = xml.find(anchor)
    if i < 0:
        raise RuntimeError(f"SDT tag not found: {tag}")
    j = xml.find("<w:t>❌</w:t>", i)
    if j < 0 or j - i > 800:
        raise RuntimeError(f"No nearby cross after {tag}")
    return xml[:j] + "<w:t>✔</w:t>" + xml[j + len("<w:t>❌</w:t>") :]


def section_48() -> str:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    parts = [
        p_title(
            2,
            "4.8 B4 Codice QR (congelato 21/08/2026)",
        ),
        p_body(
            "B4 Codice QR è CONVALIDATO il 21/08/2026. "
            "3.4.4 resta la fonte funzionale (non riscritta). "
            "QR batch (4.1.6), Import MERGE (B5), Motore B e ricerca semplice restano fuori da B4."
        ),
        p_body(
            "Il QR contiene solo l'identificativo permanente del contenitore "
            "(payload versionato src=boxmanager, ver, id). "
            "Categoria, posizione, descrizione e oggetti si leggono dall'archivio. "
            "L'id Room non è l'identificativo permanente: è stato introdotto un id tecnico non visibile, "
            "rigenerato in etichetta, non salvato come immagine nel database, trasportato in archive.json (formatVersion resta 1)."
        ),
        p_body(
            "Due ingressi: Dashboard / Utility → Codice QR → fotocamera → Dettaglio Contenitore; "
            "Dettaglio Contenitore → Visualizza etichetta QR → un layout V1 (QR + codice identificativo) "
            "→ Stampa etichetta oppure Esporta PDF. "
            "La voce 4.1.3 «Encoding dati contenitore» non si applica: non si codificano i dati del contenitore nel QR."
        ),
        p_body(
            "Messaggi 3.4.4 importati nel catalogo 2.6: "
            "Il codice QR non appartiene ad un archivio BoxManager. "
            "Il contenitore associato a questo codice QR non è presente nell'archivio. "
            "Impossibile leggere il codice QR. Riprovare. "
            "Se elimini il contenitore, l'etichetta QR non sarà più utilizzabile. Confermi l'eliminazione? "
            "Il «messaggio conclusivo» di stampa/PDF non ha testo in 3.4.4: non è stato inventato."
        ),
        p_body(
            f"Aggiornamento documentale sidecar 9.1_B4 {stamp}."
        ),
    ]
    return "".join(parts)


def catalog_b4() -> str:
    return p_body(
        "CODICE QR (catalogo 21/08/2026 — B4): "
        "QR non riconosciuto: Il codice QR non appartiene ad un archivio BoxManager. "
        "Errore bloccante, area messaggi della schermata Codice QR. FeedbackUtils.alert(). "
        "Contenitore non presente: Il contenitore associato a questo codice QR non è presente nell'archivio. "
        "Errore bloccante, stessa area. FeedbackUtils.alert(). "
        "Errore di lettura: Impossibile leggere il codice QR. Riprovare. "
        "Errore bloccante, stessa area. FeedbackUtils.alert(). "
        "Eliminazione contenitore: Se elimini il contenitore, l'etichetta QR non sarà più utilizzabile. Confermi l'eliminazione? "
        "Dialog. NO a sinistra, SI a destra. "
        "Se il contenitore ha oggetti, dopo SI resta il dialogo 2.6 sugli oggetti. "
        "Categorie, luoghi e oggetti restano su Conferma eliminazione?"
    )


def patch_document_xml(xml: str) -> str:
    xml = insert_after_text(
        xml,
        "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 (lista Contenitori; R19 su chiavi complete di fase 3, Allegato 4.7). Motore B in V2. Ricerca semplice: fine-tuning.",
        p_body(
            "Codice QR (B4) CONVALIDATO 21/08/2026: scansione Dashboard/Utility; "
            "etichetta V1 (QR + identificativo permanente) con stampa ed export PDF; "
            "id tecnico permanente non visibile. QR batch resta in 4.1.6."
        ),
    )
    xml = replace_once(
        xml,
        "B4 — Codice QR: scansione ed etichetta; il QR contiene solo l'identificativo permanente del contenitore.",
        "B4 — Codice QR CONVALIDATO 21/08/2026: scansione ed etichetta; il QR contiene solo l'identificativo permanente del contenitore.",
    )
    xml = insert_after_text(
        xml,
        "Non in V1 / Motore B: Questo tipo di richiesta non è ancora disponibile.",
        catalog_b4(),
    )
    xml = replace_once(
        xml,
        "Ripristino REPLACE (B2) convalidato. Import e QR: ancora da fare (B5, B4).",
        "Ripristino REPLACE (B2) convalidato. Codice QR (B4) CONVALIDATO 21/08/2026. Import MERGE: ancora da fare (B5).",
    )
    xml = replace_once(
        xml,
        "D0, B1 e B2 convalidati. In corso B3 Motore A secondo Allegato 4, subordinato a sez. 1.11. L'ordine dei blocchi è quello del quadro sintetico in 4.1.",
        "D0, B1, B2, B3 e B4 convalidati. Prossimo: B5 — Importa dati (MERGE), subordinato a sez. 1.11. L'ordine dei blocchi è quello del quadro sintetico in 4.1.",
    )
    xml = replace_once(
        xml,
        "QR V1: il codice contiene solo l'identificativo permanente del contenitore; gli altri dati si leggono dall'archivio.",
        "QR V1 CONVALIDATO 21/08/2026: il codice contiene solo l'identificativo permanente del contenitore; gli altri dati si leggono dall'archivio. "
        "Id tecnico non visibile (non è l'id Room); etichetta rigenerata; stampa ed export PDF dalla stessa anteprima. QR batch in 4.1.6.",
    )
    xml = replace_once(
        xml,
        "B1 Backup ZIP e B2 Ripristino REPLACE: convalidati. B3 Motore A CONVALIDATO 21/08/2026: Pipeline 0–10 unico avvio; lookup reale sui quattro Core; pagina Ricerca avanzata come interlocuzione (R19, messaggi); output = lista Contenitori; R19 sulle chiavi complete di fase 3 (Allegato 4.7). Motore B in V2. Ricerca semplice: fine-tuning.",
        "B1 Backup ZIP e B2 Ripristino REPLACE: convalidati. "
        "B3 Motore A CONVALIDATO 21/08/2026 (Allegato 4.7). "
        "B4 Codice QR CONVALIDATO 21/08/2026 (Allegato 4.8). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
    )
    xml = insert_after_text(
        xml,
        "Aggiornamento documentale sidecar 9.1_B3d 21/08/2026 21:09.",
        section_48(),
    )
    for tag in (
        "goog_rdk_47",
        "goog_rdk_49",
        "goog_rdk_50",
        "goog_rdk_51",
        "goog_rdk_52",
    ):
        xml = flip_sdt_cross(xml, tag)
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
