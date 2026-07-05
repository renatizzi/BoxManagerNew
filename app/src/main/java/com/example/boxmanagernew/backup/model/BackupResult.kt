package com.example.boxmanagernew.backup.model

/**
 * Esito di un'operazione di Backup o Ripristino.
 */
data class BackupResult(

    /**
     * Indica se l'operazione è terminata correttamente.
     */
    val success: Boolean,

    /**
     * Messaggio destinato all'interfaccia utente.
     */
    val message: String,

    /**
     * Percorso del file generato.
     * Valorizzato solo in caso di Backup.
     */
    val filePath: String? = null
)