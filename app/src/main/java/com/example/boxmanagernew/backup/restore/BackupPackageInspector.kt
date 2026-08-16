package com.example.boxmanagernew.backup.restore

import com.example.boxmanagernew.backup.checksum.BackupChecksum
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.model.BackupMetadata
import com.example.boxmanagernew.backup.serializer.BackupDeserializer
import com.example.boxmanagernew.backup.validator.BackupValidator
import java.io.ByteArrayInputStream

class BackupPackageInspector(
    private val deserializer: BackupDeserializer = BackupDeserializer(),
    private val validator: BackupValidator = BackupValidator()
) {

    sealed class Result {
        data class Ready(
            val archive: BackupArchive,
            val metadata: BackupMetadata
        ) : Result()

        object Invalid : Result()

        object Incompatible : Result()
    }

    fun inspect(
        entries: Map<String, ByteArray>
    ): Result {

        val archiveBytes =
            entries[BackupConfiguration.ARCHIVE_FILE_NAME]
                ?: return Result.Invalid

        val metadataBytes =
            entries[BackupConfiguration.METADATA_FILE_NAME]
                ?: return Result.Invalid

        val manifestBytes =
            entries[BackupConfiguration.MANIFEST_FILE_NAME]
                ?: return Result.Invalid

        return try {

            val archive = deserializer.deserializeArchive(archiveBytes)
            val metadata = deserializer.deserializeMetadata(metadataBytes)
            val manifest = deserializer.deserializeManifest(manifestBytes)

            if (
                archive.formatVersion > BackupConfiguration.BACKUP_FORMAT_VERSION ||
                metadata.backupFormatVersion > BackupConfiguration.BACKUP_FORMAT_VERSION
            ) {
                return Result.Incompatible
            }

            if (
                archive.formatVersion != BackupConfiguration.BACKUP_FORMAT_VERSION ||
                metadata.backupFormatVersion != BackupConfiguration.BACKUP_FORMAT_VERSION
            ) {
                return Result.Invalid
            }

            if (
                !manifest.checksumAlgorithm.equals(
                    BackupConfiguration.CHECKSUM_ALGORITHM,
                    ignoreCase = true
                )
            ) {
                return Result.Invalid
            }

            val checksum = ByteArrayInputStream(archiveBytes).use {
                BackupChecksum.calculate(it)
            }

            if (!checksum.equals(manifest.checksum, ignoreCase = true)) {
                return Result.Invalid
            }

            if (!validator.validate(archive)) {
                return Result.Invalid
            }

            Result.Ready(
                archive = archive,
                metadata = metadata
            )

        } catch (_: Exception) {
            Result.Invalid
        }
    }
}
