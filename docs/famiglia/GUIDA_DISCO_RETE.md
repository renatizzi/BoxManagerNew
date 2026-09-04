# Disco di rete con BoxManager

**A cosa serve:** salvare Backup e altri file di BoxManager su un **disco di rete** — un hard disk o una cartella sul computer, raggiungibile dal telefono con la Wi‑Fi di casa.

**Nota:** l’app File di Google non basta per questo (va bene per il telefono e per Drive, non per il disco in casa).

**Come funziona:** una piccola app gratuita collega il telefono al disco. BoxManager apre lo store sull’app giusta: basta installarla. Poi scegli quella cartella come per le altre.

---

## Passo 1 — Da BoxManager (Impostazioni)

1. Apri **BoxManager** → **Impostazioni**.
2. Tocca **Disco di rete**.
3. Se l’app non c’è ancora → **Installa l’app** (si apre lo store) → installa.
4. Torna in BoxManager → **Disco di rete** → **Apri l’app**.

---

## Passo 2 — Nell’app gratuita (una volta sola)

1. Tocca **Aggiungi** (＋).
2. Inserisci dove si trova il disco (te lo dà chi ha messo in rete la cartella, oppure lo trovi nelle impostazioni del disco) e, se chiesto, nome utente e password.
3. Controlla la connessione se c’è il pulsante, poi **Salva**.

Telefono e disco devono essere sulla **stessa Wi‑Fi di casa**.

---

## Passo 3 — Di nuovo in BoxManager

1. **Utility** → **Backup Archivio** (stesso schema per salvare altri file).
2. Tocca **Sfoglia**.
3. Scegli la cartella del disco di rete (compare dopo il passo 2) oppure **Tutte le cartelle…** e poi l’app appena installata.
4. Conferma la cartella e fai il Backup.

Dalla volta dopo BoxManager può **riusare** la stessa cartella.

---

## Frase corta

> Per salvare sul disco di rete: Impostazioni → Disco di rete → installa l’app gratuita → aggiungi il disco → in Backup premi Sfoglia e scegli quella cartella.

---

## Limiti (semplici)

- Serve la Wi‑Fi di casa verso il disco.
- L’app gratuita è di terzi. BoxManager non chiede la password del disco: la chiede solo quella app.
- Collegare il disco “dentro” BoxManager senza app esterne = sviluppo futuro.

---

## Riferimento (solo sviluppo — non in guida utente)

Build da **1.3-famigliaB5.19**. Codice: `NetworkDriveAssistant`, card Impostazioni.  
App consigliata (nome store / package): CIFS Documents Provider — `com.wa2c.android.cifsdocumentsprovider`.
