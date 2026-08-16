package com.example.boxmanagernew.backup.zip

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
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

        FileOutputStream(destination).use { stream ->
            write(stream, entries)
        }
    }

    fun write(
        destination: OutputStream,
        entries: Map<String, ByteArray>
    ) {

        ZipOutputStream(destination).use { zip ->

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