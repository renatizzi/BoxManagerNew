# -*- coding: utf-8 -*-
"""Aggiorna quadro 4.1 (Roadmap vigente 26/08/2026) e Allegato 4.19."""
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


def replace_startswith(
    children: list[ET.Element],
    start: str,
    new_text: str,
) -> int:
    for el in children:
        if ptext(el).startswith(start):
            replace_p_text(el, new_text)
            return 1
    raise RuntimeError(f"Not found: {start[:80]}")


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

    n += replace_startswith(
        children,
        "Questa è l'unica Roadmap ufficiale. Quadro al ",
        "Questa è l'unica Roadmap ufficiale. Quadro al 26/08/2026. "
        "I paragrafi 4.1.1–4.1.6 restano il dettaglio per area; "
        "in caso di conflitto prevale questo quadro.",
    )

    n += replace_startswith(
        children,
        "ROADMAP DEL PROGETTO AL ",
        "ROADMAP DEL PROGETTO AL 26/08/2026",
    )

    n += replace_startswith(
        children,
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B F7",
        "Stato al 26/08/2026. Flusso core V1 chiuso (D0–B7; Allegati 4.7–4.18; "
        "AppShell 24/08/2026). Google Play — fase binario e accesso Archivio "
        "completo CONVALIDATA (Allegato 4.19): applicationId "
        "it.renatizzi.boxmanager; prova a tempo (default 14 gg); rinnovo via "
        "CONDIVIDI (default +7 gg / N amici); codice locale; parametri admin "
        "solo «Renato Stefanizzi»; Google Billing congelato (vincolo fiscale). "
        "Roadmap vigente ora (Progetto 1 — Play): (1) privacy policy URL + "
        "link in-app; (2) Data safety e permessi fusi ML Kit; (3) keystore + "
        "AAB + icona 512; (4) closed test 12 tester × 14 giorni; (5) dopo "
        "1–2 mesi di utilizzo, rivalutare donazioni (Ko-fi / Buy Me a Coffee) "
        "o micro-AdMob — non IAP finché non c’è quadro fiscale. "
        "Progetto 2 — V2 (dopo Play verde), dettaglio in 4.1.6: QR avanzato / "
        "stampa multipla; cestino; foto miniatura; ricerca data/KPI solo via "
        "pipeline 0–10; storico; condivisione selettiva file. PATTERN_010 "
        "resta SOSPESO. Motore B già in V1 per F7–F9 / CATEGORY / LOCATION "
        "(4.17); evoluzioni ulteriori Motore B restano fuori dal primo Play. "
        "Vietato anticipare un blocco successivo prima della convalida utente "
        "(sez. 1.11).",
    )

    n += replace_startswith(
        children,
        "Ricerca avanzata V1: Motore A CONVALIDATO 21/08/2026",
        "Ricerca avanzata V1: Motore A CONVALIDATO 21/08/2026 (Allegato 4.7). "
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B: F7 / PATTERN_007, CATEGORY Query, F8 / PATTERN_008, "
        "LOCATION Query, F6 / PATTERN_006 e F9 / PATTERN_009 CONVALIDATI "
        "23/08/2026 (Allegato 4.17). Inventario Motore A e stampa contestuale "
        "CONVALIDATI 23/08/2026 (Allegato 4.18). Google Play accesso Archivio "
        "completo (prova a tempo + CONDIVIDI + codice; Billing congelato) "
        "CONVALIDATO 26/08/2026 (Allegato 4.19). PATTERN_010 resta SOSPESO. "
        "Ricerca semplice CONVALIDATA 22/08/2026 (Allegato 4.13). "
        "La tabella TO DO (B3/B4/B5 ante Motore A) e 4.1.2.1 sono storici: "
        "Allegato 4.14, non vigenti.",
    )

    n += replace_startswith(
        children,
        "AppShell globale, context card e navigazione tab CONVALIDATI 24/08/2026",
        "AppShell globale, context card e navigazione tab CONVALIDATI "
        "24/08/2026 (chiusura V1). Google Play — Archivio completo e accesso "
        "a tempo CONVALIDATI 26/08/2026 (Allegato 4.19). Freeze aree mature, "
        "regression scope e matrice impatti restano governance di progetto, "
        "non funzioni utente. Non sono fette V2 applicative.",
    )

    n += replace_startswith(
        children,
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso.",
        "D0, B1, B2, B3, B4, B5, B6 e B7 convalidati. Flusso core V1 chiuso. "
        "Dark e contrasto UI CONVALIDATI 23/08/2026 (Allegato 4.16). "
        "Motore B F7–F9 / CATEGORY / LOCATION CONVALIDATI 23/08/2026 "
        "(Allegato 4.17). Inventario e stampa contestuale Ricerca avanzata "
        "CONVALIDATI 23/08/2026 (Allegato 4.18). Google Play accesso Archivio "
        "completo CONVALIDATO 26/08/2026 (Allegato 4.19). Roadmap vigente: "
        "privacy → Data safety → AAB → closed test; dopo 1–2 mesi "
        "donazioni/AdMob se utile. PATTERN_010 resta SOSPESO. "
        "4.1.6 resta NON core (backlog V2).",
    )

    n += replace_startswith(
        children,
        "4.19 Google Play — Archivio completo e paywall",
        "4.19 Google Play — Archivio completo e accesso (congelato 26/08/2026)",
    )

    n += replace_startswith(
        children,
        "CONVALIDATO il 25/08/2026. Progetto parallelo a V2:",
        "CONVALIDATO il 25/08/2026; modello accesso a tempo e parametri admin "
        "CONVALIDATI il 26/08/2026. Progetto parallelo a V2: preparare il "
        "primo binario Play senza riaprire D0–B7, pipeline 0–10, Motore A/B "
        "né catalogo 2.6. I testi paywall non fanno parte del catalogo 2.6.",
    )

    n += replace_startswith(
        children,
        "Modello commerciale CONVALIDATO: scarico gratis; pacchetto "
        "Archivio completo a pagamento",
        "Modello distribuzione CONVALIDATO (revisione 26/08/2026): scarico "
        "gratis; niente prezzi in-app né Google Billing in questa fase "
        "(vincolo fiscale). Base sempre disponibile: archivio, ricerca "
        "semplice per ambito, backup/ripristino, stampa. Pacchetto Archivio "
        "completo (Ricerca avanzata, Codice QR, Etichetta QR, Importa, "
        "Esporta): prova a tempo poi rinnovo via CONDIVIDI o codice locale. "
        "Il primo AAB su Play nasce già con i lucchetti.",
    )

    n += replace_startswith(
        children,
        "Anteprima paywall: titolo = nome funzione; sottotitolo "
        "«Funzione a pagamento»",
        "Anteprima / gate: titolo = nome funzione; sottotitolo "
        "«Funzione avanzata». Durante la prova a tempo: accesso libero al "
        "pacchetto. Dopo la scadenza: bottone CONDIVIDI (Intent nativo) e "
        "campo codice; niente ACQUISTA né prezzi. Esempi tra parentesi in "
        "corsivo. Importa/Esporta: messaggio incrociato (sistema aperto verso "
        "CSV su foglio elettronico). Bottom bar su ArchivioCompletoActivity, "
        "ancorata alla tab di provenienza.",
    )

    n += replace_startswith(
        children,
        "Prossimo Play (non codice V1):",
        "Prossimo (Roadmap vigente, Progetto 1): privacy policy URL + link "
        "in-app; Data safety e permessi fusi ML Kit; keystore + AAB; "
        "closed test 12×14. Dopo 1–2 mesi di utilizzo: rivalutare donazioni "
        "(Ko-fi / Buy Me a Coffee) o micro-AdMob. Billing IAP solo se/quando "
        "il quadro fiscale lo consente. Motore B evoluzioni oltre 4.17 e "
        "voci 4.1.6 = Progetto 2 dopo Play verde.",
    )

    # stamp on latest 4.19 sidecar line if present
    for el in children:
        t = ptext(el)
        if t.startswith(
            "Aggiornamento documentale sidecar 9.1_B7 Allegato 4.19 "
            "(prova a tempo + admin)"
        ):
            replace_p_text(
                el,
                f"Aggiornamento documentale sidecar 9.1_B7 Allegato 4.19 "
                f"e quadro 4.1 (Roadmap vigente) {stamp}.",
            )
            n += 1
            break

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
