package com.example.boxmanagernew.backup.model

/**
 * Manifest tecnico del Backup.
 *
 * Rappresenta il contenuto del file manifest.json.
 */
data class BackupManifest(

    /**
     * Algoritmo utilizzato per il checksum.
     */
    val checksumAlgorithm: String,

    /**
     * Checksum dell'archivio.
     */
    val checksum: String,

    /**
     * Versione del manifest.
     */
    val manifestVersion: Int
)