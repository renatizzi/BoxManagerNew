package com.example.boxmanagernew.backup.validator

import com.example.boxmanagernew.backup.model.BackupArchive

/**
 * Validatore del contenuto del Backup.
 */
class BackupValidator {

    /**
     * Verifica la coerenza minima dell'archivio.
     */
    fun validate(
        archive: BackupArchive
    ): Boolean {

        return archive.boxes.isNotEmpty() ||
                archive.objects.isNotEmpty() ||
                archive.categories.isNotEmpty() ||
                archive.locations.isNotEmpty() ||
                archive.objectTypes.isNotEmpty()
    }
}