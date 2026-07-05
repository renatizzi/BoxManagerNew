package com.example.boxmanagernew.backup.io

import java.io.File

/**
 * Scrive il contenuto del Backup su file.
 */
class BackupFileWriter {

    fun write(
        destination: File,
        content: String
    ) {

        destination.writeText(content)
    }
}