package com.example.boxmanagernew.backup.model

/**
 * Metadati del Backup.
 *
 * Rappresenta il contenuto del file metadata.json.
 */
data class BackupMetadata(

    /**
     * Versione del formato Backup.
     */
    val backupFormatVersion: Int,

    /**
     * Versione dell'applicazione.
     */
    val applicationVersion: String,

    /**
     * Data e ora di creazione del Backup (ISO-8601).
     */
    val creationTimestamp: String,

    /**
     * Numero di contenitori.
     */
    val boxCount: Int,

    /**
     * Numero di oggetti.
     */
    val objectCount: Int,

    /**
     * Numero di categorie.
     */
    val categoryCount: Int,

    /**
     * Numero di posizioni.
     */
    val locationCount: Int,

    /**
     * Numero di tipologie oggetto.
     */
    val objectTypeCount: Int
)