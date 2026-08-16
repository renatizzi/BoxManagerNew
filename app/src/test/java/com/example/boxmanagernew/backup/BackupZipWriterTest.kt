package com.example.boxmanagernew.backup

import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.zip.BackupZipWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

class BackupZipWriterTest {

    @Test
    fun write_outputStream_containsAllEntries() {

        val entries = linkedMapOf(
            BackupConfiguration.ARCHIVE_FILE_NAME to "archivio".toByteArray(),
            BackupConfiguration.METADATA_FILE_NAME to "meta".toByteArray(),
            BackupConfiguration.MANIFEST_FILE_NAME to "manifesto".toByteArray()
        )

        val output = ByteArrayOutputStream()

        BackupZipWriter().write(output, entries)

        val read = linkedMapOf<String, String>()

        ZipInputStream(ByteArrayInputStream(output.toByteArray())).use { zip ->

            var entry = zip.nextEntry

            while (entry != null) {

                read[entry.name] = zip.readBytes().toString(Charsets.UTF_8)
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        assertEquals(entries.keys.toList(), read.keys.toList())
        assertEquals("archivio", read[BackupConfiguration.ARCHIVE_FILE_NAME])
        assertTrue(output.size() > 0)
    }
}
