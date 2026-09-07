package com.example.boxmanagernew.backup

import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.ui.backup.BackupZipPersister
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupZipFileNameTest {

    @Test
    fun zipFileName_appendsExtensionWhenMissing() {
        assertEquals(
            "BCK_070926_0830.zip",
            BackupZipPersister.zipFileName("BCK_070926_0830")
        )
    }

    @Test
    fun zipFileName_keepsExistingExtension() {
        assertEquals(
            "BCK_070926_0830.zip",
            BackupZipPersister.zipFileName("BCK_070926_0830.zip")
        )
    }

    @Test
    fun backupExtension_isZip() {
        assertEquals(".zip", BackupConfiguration.BACKUP_FILE_EXTENSION)
        assertEquals("application/zip", BackupConfiguration.ZIP_MIME_TYPE)
        assertTrue(
            BackupZipPersister.zipFileName("x")
                .endsWith(BackupConfiguration.BACKUP_FILE_EXTENSION)
        )
        assertFalse(
            BackupZipPersister.zipFileName("x")
                .endsWith(".zip.zip")
        )
    }
}
