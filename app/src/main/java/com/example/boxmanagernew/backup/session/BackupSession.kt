package com.example.boxmanagernew.backup.session

import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.model.BackupManifest
import com.example.boxmanagernew.backup.model.BackupMetadata

/**
 * Contesto completo di una sessione di Backup.
 */
data class BackupSession(

    val archive: BackupArchive,

    val metadata: BackupMetadata,

    val manifest: BackupManifest
)