# -*- coding: utf-8 -*-
"""Sidecar 9.1_B7: B7 igiene e governance CONVALIDATO 22/08/2026."""
from __future__ import annotations

import shutil
import zipfile
from datetime import datetime, timezone
from pathlib import Path
from xml.sax.saxutils import escape

ROOT = Path(
    r"C:\Users\Dell XPS 15 7590\AndroidStudioProjects\BoxManagerNew\docs"
)
SRC = ROOT / "Nota_Integrata_9.1_B6b.docx"
DST = ROOT / "Nota_Integrata_9.1_B7.docx"


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


def section_413() -> str:
    stamp = datetime.now(timezone.utc).astimezone().strftime("%d/%m/%Y %H:%M")
    return "".join(
        [
            p_title(
                2,
                "4.13 B7 Igiene e governance (congelato 22/08/2026)",
            ),
            p_body(
                "B7 Igiene e governance è CONVALIDATO il 22/08/2026. "
                "Perimetro: catalogo 2.6, ricerca semplice, scritture file dove ha senso, "
                "ingresso etichetta QR, osservazioni Allegato 3.4 (Room, allowBackup, orfani). "
                "D0–B6, Motore A/B, 3.4.4 stampa/PDF etichetta e swipe (4.1.6) non si riaprono. "
                "box.position resta stringa. "
                "La tabella 4.1.5 (AppShell, freeze, matrice, context card) resta fuori da B7."
            ),
            p_body(
                "Catalogo 2.6: nessuna frase nuova. "
                "Allineati in codice Categoria in uso. Eliminazione non consentita "
                "e Posizione in uso. Eliminazione non consentita."
            ),
            p_body(
                "Ricerca semplice CONVALIDATA: sottostringa inline, minimo 3 caratteri "
                "dopo normalizzazione (maiuscole, accenti, simboli), stesso ago per filtro e giallo. "
                "Contenitori: nome, categoria, posizione, oggetti (nome e descrizione). "
                "Niente plurali né inflessione (restano solo della Ricerca avanzata, 3.3.6 / R19 / 4.7). "
                "Motore A invariato."
            ),
            p_body(
                "Genera Modello: nome fisso Modello_Importazione.csv. "
                "Dopo il primo CONSENTI si riusa la cartella Backup se ancora valida; "
                "il picker solo se manca o non è più accessibile. "
                "File già esistente. Sostituirlo? invariato."
            ),
            p_body(
                "Menu card Contenitore (ordine): Modifica, Elimina, Visualizza etichetta QR. "
                "Il ⋮ della pagina contenuto box è stato rimosso. "
                "3.4.4 anteprima/stampa/PDF invariati. "
                "Scansione Dashboard/Utility invariata."
            ),
            p_body(
                "Room: tolto fallbackToDestructiveMigration(); resta MIGRATION_5_6 (permanentId). "
                "Manifest: android:allowBackup=false (backup automatico Android, distinto da B1). "
                "Orfani rimossi: ViewModel vuoti, TypeObjectEntity non in @Database, "
                "SearchNavigationPlanner, stub ricerca e catena Backup morta. "
                "GlobalSearchDispatcher resta l'unico avvio della Pipeline."
            ),
            p_body(
                f"Aggiornamento documentale sidecar 9.1_B7 {stamp}."
            ),
        ]
    )


def catalog_b7() -> str:
    return p_body(
        "IGIENE B7 (catalogo 22/08/2026): "
        "nessuna frase nuova. "
        "Allineati al 2.6 già presente: "
        "Categoria in uso. Eliminazione non consentita. "
        "Posizione in uso. Eliminazione non consentita."
    )


def patch_document_xml(xml: str) -> str:
    xml = replace_once(
        xml,
        "B7 — Igiene e governance: catalogo messaggi 2.6, frammenti orfani, migrazioni Room. "
        "Solo a V1 funzionante.",
        "B7 — Igiene e governance CONVALIDATO 22/08/2026 (Allegato 4.13): "
        "catalogo 2.6, ricerca semplice, Genera Modello cartella Backup, "
        "menu QR sulla card, Room senza wipe, allowBackup=false, orfani rimossi.",
    )
    xml = replace_once(
        xml,
        "D0, B1, B2, B3, B4, B5 e B6 (stampa/export vista e ricerca vocale) convalidati. "
        "Prossimo: B7 — Igiene e governance, subordinato a sez. 1.11.",
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. "
        "Flusso core V1 chiuso. Motore B resta in V2. 4.1.6 resta NON core.",
    )
    xml = replace_once(
        xml,
        "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 "
        "(lista Contenitori; R19 su chiavi complete di fase 3, Allegato 4.7). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
        "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 "
        "(lista Contenitori; R19 su chiavi complete di fase 3, Allegato 4.7). "
        "Motore B in V2. Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 (vedi Allegato 4.7); "
        "Motore B in V2; ricerca semplice in fine-tuning.",
        "Ricerca avanzata: Motore A CONVALIDATO 21/08/2026 (vedi Allegato 4.7); "
        "Motore B in V2; ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "B1 Backup ZIP e B2 Ripristino REPLACE: convalidati. "
        "B3 Motore A CONVALIDATO 21/08/2026 (Allegato 4.7). "
        "B4 Codice QR CONVALIDATO 21/08/2026 (Allegato 4.8). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
        "B1 Backup ZIP e B2 Ripristino REPLACE: convalidati. "
        "B3 Motore A CONVALIDATO 21/08/2026 (Allegato 4.7). "
        "B4 Codice QR CONVALIDATO 21/08/2026 (Allegato 4.8). "
        "Motore B in V2. Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "B5 Import MERGE CONVALIDATO 22/08/2026 (Allegato 4.10). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
        "B5 Import MERGE CONVALIDATO 22/08/2026 (Allegato 4.10). "
        "Motore B in V2. Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "B6 stampa/export vista CONVALIDATO 22/08/2026 (Allegato 4.11). "
        "B6/2 ricerca vocale CONVALIDATO 22/08/2026 (Allegato 4.12). "
        "Motore B in V2. Ricerca semplice: fine-tuning.",
        "B6 stampa/export vista CONVALIDATO 22/08/2026 (Allegato 4.11). "
        "B6/2 ricerca vocale CONVALIDATO 22/08/2026 (Allegato 4.12). "
        "B7 igiene CONVALIDATO 22/08/2026 (Allegato 4.13). "
        "Motore B in V2. Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "La ricerca semplice (filtro Dashboard / lista) è rimandata a fine-tuning: "
        "non è chiusa in B3.",
        "La ricerca semplice (filtro Dashboard / lista) non è chiusa in B3: "
        "è CONVALIDATA in B7 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "Messaggi utente: allineare i testi sparsi nel codice al catalogo 2.6 (unica fonte).",
        "Messaggi utente: testi sparsi allineati al catalogo 2.6 in B7 (Allegato 4.13). "
        "Nessuna frase nuova.",
    )
    xml = replace_once(
        xml,
        "fallbackToDestructiveMigration() cancella i dati al cambio schema: "
        "da sostituire con migrazioni controllate in B7.",
        "fallbackToDestructiveMigration() rimosso in B7. Resta MIGRATION_5_6.",
    )
    xml = replace_once(
        xml,
        "android:allowBackup=true è il backup automatico Android, "
        "distinto dal modulo Backup utente.",
        "android:allowBackup=false (B7). È il backup automatico Android, "
        "distinto dal modulo Backup utente B1.",
    )
    xml = replace_once(
        xml,
        "Frammenti orfani (ViewModel vuoti, Entity non registrate, classi ricerca non usate) "
        "non si cancellano fino a B7.",
        "Frammenti orfani rimossi in B7 (ViewModel vuoti, Entity non in @Database, "
        "piano-nomi e stub ricerca/Backup morti). GlobalSearchDispatcher resta l'unico avvio.",
    )
    xml = replace_once(
        xml,
        "Due ingressi: Dashboard / Utility → Codice QR → fotocamera → Dettaglio Contenitore; "
        "Dettaglio Contenitore → Visualizza etichetta QR → un layout V1 "
        "(QR + codice identificativo) → Stampa etichetta oppure Esporta PDF.",
        "Due ingressi: Dashboard / Utility → Codice QR → fotocamera → Dettaglio Contenitore; "
        "card Contenitore ⋮ → Visualizza etichetta QR → un layout V1 "
        "(QR + codice identificativo) → Stampa etichetta oppure Esporta PDF.",
    )
    xml = replace_once(
        xml,
        "B6/2 ricerca vocale è CONVALIDATO il 22/08/2026 (Allegato 4.12). "
        "3.4.4 QR non si tocca. B7 igiene resta dopo.",
        "B6/2 ricerca vocale è CONVALIDATO il 22/08/2026 (Allegato 4.12). "
        "3.4.4 QR non si tocca. B7 igiene CONVALIDATO 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "Annulla o riconoscitore assente: nessun inserimento. "
        "3.4.4 QR non si tocca. B7 igiene resta dopo.",
        "Annulla o riconoscitore assente: nessun inserimento. "
        "3.4.4 QR non si tocca. B7 igiene CONVALIDATO 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "3.4.4 QR, Motore B, ricerca semplice, B6 e B7 restano fuori.",
        "3.4.4 QR, Motore B, ricerca semplice, B6 e B7 restavano fuori da B5. "
        "B7 è CONVALIDATO il 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "B7 resta fuori da B5.",
        "B7 resta fuori da B5. B7 è CONVALIDATO il 22/08/2026 (Allegato 4.13).",
    )
    xml = replace_once(
        xml,
        "<w:t>MENU CONTESTUALE</w:t></w:r><w:r><w:rPr><w:color w:val=\"000000\"/>"
        "<w:sz w:val=\"20\"/><w:szCs w:val=\"20\"/></w:rPr><w:br/><w:t>- Modifica</w:t>"
        "</w:r><w:r><w:rPr><w:color w:val=\"000000\"/><w:sz w:val=\"20\"/>"
        "<w:szCs w:val=\"20\"/></w:rPr><w:br/><w:t>- Elimina</w:t>",
        "<w:t>MENU CONTESTUALE</w:t></w:r><w:r><w:rPr><w:color w:val=\"000000\"/>"
        "<w:sz w:val=\"20\"/><w:szCs w:val=\"20\"/></w:rPr><w:br/><w:t>- Modifica</w:t>"
        "</w:r><w:r><w:rPr><w:color w:val=\"000000\"/><w:sz w:val=\"20\"/>"
        "<w:szCs w:val=\"20\"/></w:rPr><w:br/><w:t>- Elimina</w:t>"
        "</w:r><w:r><w:rPr><w:color w:val=\"000000\"/><w:sz w:val=\"20\"/>"
        "<w:szCs w:val=\"20\"/></w:rPr><w:br/><w:t>- Visualizza etichetta QR</w:t>",
    )
    xml = replace_once(
        xml,
        "<w:t>- QR + stampa etichetta</w:t>",
        "<w:t>- etichetta QR: ⋮ della card in lista (B7)</w:t>",
    )
    xml = insert_after_text(
        xml,
        "Nome file: Modello_Importazione.csv.",
        p_body(
            "B7 CONVALIDATO 22/08/2026: Genera Modello riusa la cartella Backup se è ancora valida; "
            "il picker solo se manca o non è più accessibile. Nome file invariato."
        ),
    )
    xml = insert_after_text(
        xml,
        "RICERCA VOCALE (catalogo 22/08/2026 — B6/2): "
        "nessuna frase nuova. "
        "Il dialogo vocale è di sistema. "
        "Annulla o riconoscitore assente: nessun inserimento, nessun messaggio.",
        catalog_b7(),
    )
    xml = insert_after_text(
        xml,
        "Aggiornamento documentale sidecar 9.1_B6b 22/08/2026 17:59.",
        section_413(),
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
