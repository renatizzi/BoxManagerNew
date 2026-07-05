package com.example.boxmanagernew.backup.session

import com.example.boxmanagernew.backup.manifest.BackupManifestBuilder
import com.example.boxmanagernew.backup.metadata.BackupMetadataBuilder
import com.example.boxmanagernew.backup.model.BackupArchive

/**
 * Costruisce una sessione completa di Backup.
 */
class BackupSessionBuilder(

    private val metadataBuilder: BackupMetadataBuilder =
        BackupMetadataBuilder(),

    private val manifestBuilder: BackupManifestBuilder =
        BackupManifestBuilder()
) {

    fun build(
        archive: BackupArchive,
        applicationVersion: String,
        checksum: String
    ): BackupSession {

        val metadata =
            metadataBuilder.build(
                archive = archive,
                applicationVersion = applicationVersion
            )

        val manifest =
            manifestBuilder.build(
                checksum = checksum
            )

        return BackupSession(
            archive = archive,
            metadata = metadata,
            manifest = manifest
        )
    }
}