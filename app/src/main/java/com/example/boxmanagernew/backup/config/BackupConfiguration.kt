package com.example.boxmanagernew.backup.config

/**
 * Configurazione centralizzata del modulo Backup.
 *
 * Tutte le costanti del modulo devono essere dichiarate
 * esclusivamente in questo file.
 */
object BackupConfiguration {

    /**
     * Versione del formato di Backup.
     * È indipendente dalla versione dell'applicazione.
     */
    const val BACKUP_FORMAT_VERSION = 1

    /**
     * Prefisso del nome file proposto all'utente.
     */
    const val BACKUP_FILE_PREFIX = "BCK_"

    /**
     * Estensione del file di Backup.
     */
    const val BACKUP_FILE_EXTENSION = ".zip"

    /**
     * Nome del file contenente i metadati.
     */
    const val METADATA_FILE_NAME = "metadata.json"

    /**
     * Nome del file contenente l'archivio.
     */
    const val ARCHIVE_FILE_NAME = "archive.json"

    /**
     * Nome del file contenente il manifest.
     */
    const val MANIFEST_FILE_NAME = "manifest.json"

    /**
     * Algoritmo utilizzato per il checksum.
     */
    const val CHECKSUM_ALGORITHM = "SHA-256"

    const val PREFS_NAME = "boxmanager_backup"

    const val PREFS_KEY_FOLDER_URI = "folder_uri"

    const val ZIP_MIME_TYPE = "application/zip"

    const val MSG_BACKUP_COMPLETED = "Backup completato"

    const val MSG_FOLDER_INACCESSIBLE =
        "Cartella non accessibile. Scegli di nuovo la cartella."

    const val MSG_WRITE_FAILED =
        "Backup non creato. Riprovare."

    const val MSG_INVALID_ARCHIVE =
        "Backup non creato. Archivio non valido."

    const val MSG_FILE_EXISTS =
        "File già esistente. Sostituirlo?"

    const val PRE_RESTORE_PREFIX = "PRE_RESTORE_"

    const val MSG_RESTORE_REPLACE_WARNING =
        "Il ripristino sostituirà l'archivio attuale."

    const val MSG_RESTORE_CONFIRM =
        "Il ripristino sostituirà tutti i dati. Continuare?"

    const val MSG_RESTORE_COMPLETED = "Ripristino completato"

    const val MSG_RESTORE_INVALID_FILE =
        "File di backup non valido."

    const val MSG_RESTORE_INCOMPATIBLE =
        "Questo backup non è compatibile."

    const val MSG_RESTORE_FAILED =
        "Ripristino non eseguito. Riprovare."
}