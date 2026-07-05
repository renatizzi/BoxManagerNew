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
}