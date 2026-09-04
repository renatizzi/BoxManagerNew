# Cartella di rete / NAS con BoxManager

**Scopo:** salvare Backup, Esporta, Invia tabelle / Archivio condiviso su un disco di rete (NAS o cartella Windows condivisa), **senza** collegare il NAS dentro BoxManager.

**Perché non basta Files di Google:** Files di Google gestisce telefono e cloud (Drive), **non** aggiunge cartelle SMB/NAS al selettore condiviso di Android. Con solo Files di Google il NAS non compare in BoxManager: è normale.

**Soluzione consigliata (gratuita, open source):** app **CIFS Documents Provider** (autore wa2c).  
Espone la cartella di rete ad Android; BoxManager la sceglie come qualsiasi altra cartella.

| | |
|--|--|
| Play Store | [CIFS Documents Provider](https://play.google.com/store/apps/details?id=com.wa2c.android.cifsdocumentsprovider) |
| F-Droid | [com.wa2c.android.cifsdocumentsprovider](https://f-droid.org/packages/com.wa2c.android.cifsdocumentsprovider/) |
| Codice | [github.com/wa2c/cifs-documents-provider](https://github.com/wa2c/cifs-documents-provider) |
| Android | 8.0 o superiore |

BoxManager **non** dipende da CIFS: se non usi il NAS, non installi nulla. Se usi il NAS, CIFS è il ponte consigliato.

---

## Parte A — Una volta sola: registra il NAS in CIFS

Telefono e NAS (o PC) sulla **stessa rete Wi‑Fi**.

1. Installa **CIFS Documents Provider** da Play Store o F-Droid.
2. Apri l’app → tap sul pulsante **Aggiungi** (＋).
3. Compila la connessione (tipico per un NAS domestico):
   - **Host / indirizzo:** es. `192.168.1.50` oppure il nome del NAS in rete
   - **Cartella / percorso:** nome della condivisione (es. `Backup` o `Documents`)
   - **Utente e password** del NAS (se richiesti)
   - Protocollo: di solito **SMB** (rete Windows / Samba)
4. Usa il controllo di **verifica connessione** se presente, poi **Salva**.
5. Nella lista di CIFS deve comparire la connessione appena creata.

Suggerimento opzionale in CIFS (Impostazioni): attiva la **notifica durante l’uso file** così Android non chiude l’app mentre BoxManager scrive il backup.

---

## Parte B — In BoxManager: scegli quella cartella

1. Apri **BoxManager** → **Utility** → **Backup Archivio** (stesso schema per Esporta / Condivisione).
2. Tap **Sfoglia**.
3. Nell’elenco scegli la voce legata a **CIFS Documents Provider** / al nome che hai dato al NAS  
   **oppure** **Tutte le cartelle…** → menu ☰ del selettore Android → **CIFS Documents Provider**.
4. Entra nella cartella desiderata e conferma (**Usa questa cartella** / Consenti).
5. Esegui il Backup (o l’export). Dalla volta successiva BoxManager **riusa** quella cartella se il permesso resta valido.

---

## Cosa dire all’utente (frase corta)

> Per salvare sul NAS: installa l’app gratuita **CIFS Documents Provider**, registra lì la cartella di rete, poi in BoxManager premi Sfoglia e scegli quella cartella. Files di Google non basta per il NAS.

---

## Limiti (onesti)

- Serve Wi‑Fi verso il NAS; fuori casa di solito non funziona senza VPN.
- CIFS è di terzi (gratuita e open source): BoxManager non la installa al posto tuo.
- Collegamento NAS “nativo” dentro BoxManager = futura versione con backend, non questa.

---

## Riferimento tecnico BoxManager

Da build **1.3-famigliaB5.16** (`B-SEL-CARTELLA`): Sfoglia elenca radici già registrate + selettore SAF.  
Vedi anche [INSTALLA_SENZA_RUN.md](INSTALLA_SENZA_RUN.md).
