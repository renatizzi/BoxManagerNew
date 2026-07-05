package com.example.boxmanagernew.backup.export

import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.serializer.BackupSerializer
import com.example.boxmanagernew.backup.validator.BackupValidator

/**
 * Coordinatore dell'esportazione Backup.
 */
class BackupExporter(

    private val validator: BackupValidator =
        BackupValidator(),

    private val serializer: BackupSerializer =
        BackupSerializer()
) {

    fun export(
        archive: BackupArchive
    ): String {

        require(
            validator.validate(archive)
        ) {
            "Archivio non valido."
        }

        return serializer.serialize(
            archive
        )
    }
}