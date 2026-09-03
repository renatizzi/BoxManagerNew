# Due installazioni durante il test — stessa BoxManager

Contesto: sui telefoni di casa c’è **BoxManager 1.2** da Play. Durante il test si può installare **accanto** una seconda copia: la **stessa** BoxManager di sviluppo (funzioni in più). Android le tiene distinte perché il package della build di sviluppo è diverso (`…boxmanager.famiglia` è etichetta tecnica Gradle, **non** un altro nome di app).

Non esistono «l’app famiglia» né «l’app multilingue».

## Regola d’oro

| Installazione | Ruolo | Chi la usa |
|---------------|--------|------------|
| **BoxManager 1.2** (Play) | Test chiuso + uso quotidiano dei tester | **Tutti**, sempre, fino a fine test |
| **BoxManager sviluppo** (installazione locale) | Provare le funzioni in più (archivio condiviso, inglese, …) | Chi sta verificando lo sviluppo (di solito Renato) |

Non disinstallare la 1.2. Non smettere il test Play (conteggio giorni Console).

## Cosa NON fare

- Non sostituire la 1.2 con lo sviluppo su tutti i telefoni “perché è la nuova” **durante** il test.
- Non usare Ripristino ZIP dalla 1.2 verso lo sviluppo pensando che “unisca” (è replace).
- Non caricare la build di sviluppo su Play **durante** il test.
- Non censire lo stesso inventario in **entrambe** le installazioni come se fosse un solo archivio: sono **due database** Android, stessa app.

## Come conviverle sul telefono

1. Icona della **1.2** = aggiornamenti Store; topbar `1.2`.
2. Seconda icona = BoxManager di sviluppo; topbar tipo `1.3-famigliaB…` (etichetta di build). Il titolo in-app è **BoxManager**.
3. Nello sviluppo → Impostazioni: nome utente = nome reale di chi usa il telefono (Marco, Anna, …) se si prova l’archivio condiviso.
4. Dati: se serve copiare categorie/contenitori dalla 1.2 allo sviluppo, usare **export CSV / backup** consapevolmente, non mescolare a caso.

## Piano con più telefoni

**Durante il test Play**  
- Tutti restano sulla **1.2** per il test Store.  
- Renato (eventualmente +1) può avere **anche** la BoxManager di sviluppo.  
- Gli altri **non** devono passare allo sviluppo come se fosse già l’ufficiale.

**A test chiuso**  
- La BoxManager di sviluppo **sostituisce** la 1.2 su Play. Vedi [STRATEGIA_UNIFICAZIONE.md](STRATEGIA_UNIFICAZIONE.md).

## Come riconoscere quale stai aprendo

- Versione in topbar: `1.2` vs etichetta di sviluppo (`1.3-famigliaB…`)
- Utility: solo lo sviluppo ha le card dell’archivio condiviso (Invia/Ricevi Catalogo e Inventario)
- Titolo: **BoxManager** in entrambi i casi
