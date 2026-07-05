package com.example.boxmanagernew.backup.version

import com.example.boxmanagernew.backup.config.BackupConfiguration

/**
 * Informazioni di versione del modulo Backup.
 */
object BackupVersion {

    /**
     * Versione del formato Backup.
     */
    val formatVersion: Int
        get() = BackupConfiguration.BACKUP_FORMAT_VERSION

    /**
     * Versione dell'implementazione del modulo Backup.
     */
    const val MODULE_VERSION = "1.0.0"
}