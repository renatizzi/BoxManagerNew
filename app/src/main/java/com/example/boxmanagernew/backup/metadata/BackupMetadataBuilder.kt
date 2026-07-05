package com.example.boxmanagernew.backup.metadata

import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.model.BackupMetadata
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Costruisce i metadati del Backup.
 */
class BackupMetadataBuilder {

    private val dateFormat =
        SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            Locale.getDefault()
        )

    fun build(
        archive: BackupArchive,
        applicationVersion: String
    ): BackupMetadata {

        return BackupMetadata(
            backupFormatVersion =
                BackupConfiguration.BACKUP_FORMAT_VERSION,
            applicationVersion =
                applicationVersion,
            creationTimestamp =
                dateFormat.format(Date()),
            boxCount =
                archive.boxes.size,
            objectCount =
                archive.objects.size,
            categoryCount =
                archive.categories.size,
            locationCount =
                archive.locations.size,
            objectTypeCount =
                archive.objectTypes.size
        )
    }
}