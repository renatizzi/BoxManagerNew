package com.example.boxmanagernew.backup.result

import java.io.File

/**
 * Esito completo dell'esecuzione del Backup.
 */
data class BackupExecutionResult(

    /**
     * Stato finale dell'operazione.
     */
    val status: BackupExecutionStatus,

    /**
     * File prodotto dal Backup.
     * Null in caso di errore.
     */
    val backupFile: File? = null,

    /**
     * Messaggio descrittivo dell'esito.
     */
    val message: String = "",

    /**
     * Durata dell'operazione in millisecondi.
     */
    val elapsedTimeMillis: Long = 0L
)