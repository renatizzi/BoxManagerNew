package com.example.boxmanagernew.backup.manifest

import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.model.BackupManifest

/**
 * Costruisce il manifest tecnico del Backup.
 */
class BackupManifestBuilder {

    fun build(
        checksum: String
    ): BackupManifest {

        return BackupManifest(
            checksumAlgorithm =
                BackupConfiguration.CHECKSUM_ALGORITHM,
            checksum =
                checksum,
            manifestVersion =
                BackupConfiguration.BACKUP_FORMAT_VERSION
        )
    }
}