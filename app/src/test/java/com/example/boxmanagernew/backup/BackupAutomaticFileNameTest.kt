package com.example.boxmanagernew.backup

import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * B-AUTO-FILES-LIST: file automatici fuori dalla lista Ripristino in-app.
 */
class BackupAutomaticFileNameTest {

    @Test
    fun preRestore_isAutomatic() {
        assertTrue(
            BackupConfiguration.isAutomaticBackupFileName(
                "PRE_RESTORE_140926_1130.zip"
            )
        )
        assertTrue(
            BackupConfiguration.isAutomaticBackupFileName(
                "pre_restore_legacy"
            )
        )
    }

    @Test
    fun preImport_isAutomatic() {
        assertTrue(
            BackupConfiguration.isAutomaticBackupFileName(
                ImportConfiguration.PRE_IMPORT_PREFIX + "140926_1130.zip"
            )
        )
    }

    @Test
    fun userBackup_isNotAutomatic() {
        assertFalse(
            BackupConfiguration.isAutomaticBackupFileName(
                "BCK_140926_1130.zip"
            )
        )
        assertFalse(
            BackupConfiguration.isAutomaticBackupFileName(
                "mio_backup.zip"
            )
        )
    }
}
