# Disco di rete con BoxManager

**A cosa serve:** salvare Backup e altri file di BoxManager su un **disco di rete** — cioè un hard disk o una cartella sul computer, raggiungibile dal telefono via Wi‑Fi di casa.

**Files di Google non basta** per questo uso (va bene per il telefono e per Drive, non per il disco in rete di casa).

**Come funziona in pratica:** una piccola app gratuita collega il telefono al disco. Poi BoxManager sceglie quella cartella come fa già per le altre.

L’app consigliata si chiama **CIFS Documents Provider** (gratuita su Play Store e F‑Droid). BoxManager non la installa al posto tuo: ti accompagna dai passi.

---

## Passo 1 — Da BoxManager (Impostazioni)

1. Apri **BoxManager** → **Impostazioni**.
2. Tocca **Disco di rete**.
3. Se l’app non c’è ancora → **Installa l’app** (si apre lo store) → installa.
4. Torna in BoxManager → **Disco di rete** → **Apri l’app**.

---

## Passo 2 — Nell’app di collegamento (una volta sola)

1. Tocca **Aggiungi** (＋).
2. Inserisci:
   - indirizzo del disco o del computer (spesso tipo `192.168.1.50` — lo trovi nelle impostazioni del disco o te lo dà chi ha messo in rete la cartella)
   - nome della cartella condivisa
   - utente e password, se richiesti
3. Controlla la connessione se c’è il pulsante, poi **Salva**.

Telefono e disco devono essere sulla **stessa Wi‑Fi di casa**.

---

## Passo 3 — Di nuovo in BoxManager

1. **Utility** → **Backup Archivio** (stesso schema per salvare altri file).
2. Tocca **Sfoglia**.
3. Scegli la cartella del disco di rete (compare dopo il passo 2) oppure **Tutte le cartelle…** e poi l’app di collegamento.
4. Conferma la cartella e fai il Backup.

Dalla volta successiva BoxManager può **riusare** la stessa cartella.

---

## Frase corta per l’utente

> Per salvare sul disco di rete: Impostazioni → Disco di rete → installa l’app gratuita → aggiungi il disco → in Backup premi Sfoglia e scegli quella cartella.

---

## Limiti (semplici)

- Serve il Wi‑Fi di casa verso il disco.
- L’app di collegamento è di terzi (gratuita). BoxManager non chiede la password del disco: la chiedono solo in quella app.
- Collegare il disco “dentro” BoxManager senza app esterne = sviluppo futuro.

---

## Riferimento (sviluppo)

Build da **1.3-famigliaB5.18**. Codice: `NetworkDriveAssistant`, card Impostazioni.  
Pacchetto app consigliata: `com.wa2c.android.cifsdocumentsprovider`.
