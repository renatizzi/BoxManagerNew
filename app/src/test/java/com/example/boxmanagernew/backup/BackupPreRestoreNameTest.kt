package com.example.boxmanagernew.backup

import com.example.boxmanagernew.backup.config.BackupConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

class BackupPreRestoreNameTest {

    @Test
    fun proposedPreRestoreFileName_usesPrefixStampAndZip() {
        val now = GregorianCalendar(
            2026,
            Calendar.SEPTEMBER,
            1,
            11,
            49
        ).time

        val name = BackupConfiguration.proposedPreRestoreFileName(now)

        assertEquals("PRE_RESTORE_010926_1149.zip", name)
        assertTrue(name.startsWith(BackupConfiguration.PRE_RESTORE_PREFIX))
        assertTrue(
            name.endsWith(BackupConfiguration.BACKUP_FILE_EXTENSION)
        )
    }
}
