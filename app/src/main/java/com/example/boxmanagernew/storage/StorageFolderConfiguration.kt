package com.example.boxmanagernew.storage

import com.example.boxmanagernew.backup.config.BackupConfiguration

/**
 * Cartelle SAF distinte per area funzionale (stesso schema del Backup:
 * prima scelta esplicita, poi riuso della preferenza dedicata).
 */
object StorageFolderConfiguration {

    const val PREFS_NAME = BackupConfiguration.PREFS_NAME

    /** Backup e Ripristino. */
    const val KEY_BACKUP = BackupConfiguration.PREFS_KEY_FOLDER_URI

    /** Importa dati, Esporta dati e modello importazione. */
    const val KEY_IMPORT_EXPORT = "folder_uri_import_export"

    /** Condivisione archivio (flavor famiglia). */
    const val KEY_FAMILY_SHARE = "folder_uri_family_share"
}
