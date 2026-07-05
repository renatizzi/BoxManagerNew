package com.example.boxmanagernew.backup.zip

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Scrive un archivio ZIP.
 */
class BackupZipWriter {

    fun write(
        destination: File,
        entries: Map<String, ByteArray>
    ) {

        ZipOutputStream(
            FileOutputStream(destination)
        ).use { zip ->

            entries.forEach { (name, content) ->

                zip.putNextEntry(
                    ZipEntry(name)
                )

                zip.write(content)

                zip.closeEntry()
            }
        }
    }
}