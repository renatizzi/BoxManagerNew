package com.example.boxmanagernew.backup.validator

import com.example.boxmanagernew.backup.model.BackupArchive

class BackupValidator {

    fun validate(
        archive: BackupArchive
    ): Boolean {

        if (archive.formatVersion <= 0) {
            return false
        }

        if (archive.createdAt.isBlank()) {
            return false
        }

        if (archive.application.name.isBlank()) {
            return false
        }

        if (archive.application.backupType.isBlank()) {
            return false
        }

        return true
    }
}