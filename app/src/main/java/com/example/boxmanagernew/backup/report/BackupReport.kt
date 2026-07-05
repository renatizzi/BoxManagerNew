package com.example.boxmanagernew.backup.report

/**
 * Report finale dell'operazione di Backup.
 */
data class BackupReport(

    /**
     * Numero di contenitori esportati.
     */
    val boxCount: Int,

    /**
     * Numero di oggetti esportati.
     */
    val objectCount: Int,

    /**
     * Numero di categorie esportate.
     */
    val categoryCount: Int,

    /**
     * Numero di posizioni esportate.
     */
    val locationCount: Int,

    /**
     * Numero di tipologie esportate.
     */
    val objectTypeCount: Int,

    /**
     * Dimensione finale del Backup in byte.
     */
    val backupSizeBytes: Long
)