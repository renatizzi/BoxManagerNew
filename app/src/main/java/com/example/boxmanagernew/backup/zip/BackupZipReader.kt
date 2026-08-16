package com.example.boxmanagernew.backup.zip

import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Legge le voci di un archivio ZIP.
 */
class BackupZipReader {

    fun read(
        source: InputStream
    ): Map<String, ByteArray> {

        val entries = linkedMapOf<String, ByteArray>()

        ZipInputStream(source).use { zip ->

            var entry = zip.nextEntry

            while (entry != null) {

                if (!entry.isDirectory) {
                    val name = entry.name.substringAfterLast('/')
                    entries[name] = zip.readBytes()
                }

                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        return entries
    }
}
