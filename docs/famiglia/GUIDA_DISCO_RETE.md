# Disco di rete con BoxManager

Messaggi in-app (Impostazioni → **Disco di rete**): approccio **minimalista** — solo l’essenziale da sapere. Testi ufficiali in `strings.xml` (`network_drive_dialog_need_app` / `network_drive_dialog_ready`).

---

## Se CIFS non è ancora installata

Per salvare i dati dell’archivio su un disco o pc (server) della tua rete WiFi hai bisogno di una apposita app (es. **CIFS Documents Provider**) grazie alla quale puoi vedere e gestire unità di memoria come se fossero dischi locali. Ciò ti risulterà utile nel caso di uso in condivisione di BoxManager.

Istruzioni:

1. Per installare l’app gratuita proposta utilizza il link a Play Store del tasto **Installa l’app**.
2. Per configurarla correttamente hai bisogno innanzitutto di conoscere:
   - **a.** Nome dell’unità (es. Disco di Rete)
   - **b.** Storage (es. SMB2,3 (jCIFS NG))
   - **c.** Host (es. 192.168.1.1)
   - **d.** Port (es. 445)
   - **e.** User e Password (credenziali di accesso all’unità)
   - **f.** Folder (es. BoxManager(sda1)/)
3. Torna in BoxManager e, salvo errori, da ora potrai vedere — insieme ai dischi locali e cloud — anche l’unità di rete/cartelle dove salvare i file dell’archivio (backup, esporta, invia tabelle, invia archivio).

In Sfoglia, se non compare subito: **Tutte le cartelle…** → menu ☰ → app CIFS / nome unità.

---

## Se CIFS è già installata

L’app CIFS Documents Provider risulta già installata su questo dispositivo.

1. Se vuoi apportare modifiche, tocca **Apri l’app**.
2. Se vuoi usare il disco di rete, torna in BoxManager e scegli la cartella di CIFS dove salvare i file dell’archivio (backup, esporta, invia tabelle, invia archivio).

Pulsanti tipici del dialogo: **Apri l’app** / **Apri nello store** / **Annulla**.

---

## Riferimento (solo sviluppo)

Build da **1.3-famigliaB5.20**. Codice: `NetworkDriveAssistant`, card Impostazioni.  
Package: `com.wa2c.android.cifsdocumentsprovider`.
