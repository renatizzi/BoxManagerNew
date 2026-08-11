package com.example.boxmanagernew.backup.export

import com.example.boxmanagernew.backup.checksum.BackupChecksum
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.serializer.BackupSerializer
import com.example.boxmanagernew.backup.session.BackupSessionBuilder
import com.example.boxmanagernew.backup.validator.BackupValidator
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class BackupExporter(

    private val validator: BackupValidator = BackupValidator(),

    private val serializer: BackupSerializer = BackupSerializer(),

    private val sessionBuilder: BackupSessionBuilder =
        BackupSessionBuilder()

) {

    fun export(
        archive: BackupArchive
    ): String {

        require(validator.validate(archive)) {
            "Archivio non valido."
        }

        return serializer.serialize(archive)
    }

    fun exportPayload(
        archive: BackupArchive,
        applicationVersion: String
    ): Map<String, ByteArray> {

        require(validator.validate(archive)) {
            "Archivio non valido."
        }

        require(applicationVersion.isNotBlank()) {
            "Versione applicazione non valida."
        }

        val archiveBytes =
            serializer
                .serialize(archive)
                .toByteArray(StandardCharsets.UTF_8)

        val checksum =
            ByteArrayInputStream(archiveBytes).use {
                BackupChecksum.calculate(it)
            }

        val session =
            sessionBuilder.build(
                archive = archive,
                applicationVersion = applicationVersion,
                checksum = checksum
            )

        return linkedMapOf(
            BackupConfiguration.ARCHIVE_FILE_NAME to archiveBytes,
            BackupConfiguration.METADATA_FILE_NAME to serializer
                .serialize(session.metadata)
                .toByteArray(StandardCharsets.UTF_8),
            BackupConfiguration.MANIFEST_FILE_NAME to serializer
                .serialize(session.manifest)
                .toByteArray(StandardCharsets.UTF_8)
        )
    }
}
