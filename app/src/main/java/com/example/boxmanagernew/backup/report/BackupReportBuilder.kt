package com.example.boxmanagernew.backup.report

import com.example.boxmanagernew.backup.model.BackupArchive
import java.io.File

/**
 * Costruisce il report finale del Backup.
 */
class BackupReportBuilder {

    fun build(
        archive: BackupArchive,
        backupFile: File
    ): BackupReport {

        return BackupReport(
            boxCount = archive.boxes.size,
            objectCount = archive.objects.size,
            categoryCount = archive.categories.size,
            locationCount = archive.locations.size,
            objectTypeCount = archive.objectTypes.size,
            backupSizeBytes = backupFile.length()
        )
    }
}