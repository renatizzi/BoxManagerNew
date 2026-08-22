package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ImportPreBackupNameTest {

    @Test
    fun preImportFileName_usesDedicatedPrefixAndTimestamp() {
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.AUGUST, 22, 8, 15, 0)

        val name = ImportConfiguration.preImportFileName(calendar.time)

        assertTrue(name.startsWith(ImportConfiguration.PRE_IMPORT_PREFIX))
        assertEquals(
            ImportConfiguration.PRE_IMPORT_PREFIX + "220826_0815",
            name
        )
    }
}
