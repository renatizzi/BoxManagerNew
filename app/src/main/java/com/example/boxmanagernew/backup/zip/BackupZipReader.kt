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
                    // Preserve nested paths (A3 photos/objects/…); root JSON stay basename-only keys.
                    val raw = entry.name.removePrefix("./")
                    val name =
                        if (raw.contains('/')) raw
                        else raw.substringAfterLast('/')
                    entries[name] = zip.readBytes()
                }

                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        return entries
    }
}
