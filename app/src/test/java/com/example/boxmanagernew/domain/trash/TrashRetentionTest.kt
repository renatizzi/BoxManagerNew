package com.example.boxmanagernew.domain.trash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrashRetentionTest {

    @Test
    fun retention_is30Days() {
        assertEquals(30, TrashRetention.RETENTION_DAYS)
        assertEquals(
            30L * 24L * 60L * 60L * 1000L,
            TrashRetention.RETENTION_MS
        )
    }

    @Test
    fun isExpired_afterRetentionWindow() {
        val deletedAt = 1_000_000L
        assertFalse(
            TrashRetention.isExpired(
                deletedAt,
                deletedAt + TrashRetention.RETENTION_MS - 1
            )
        )
        assertTrue(
            TrashRetention.isExpired(
                deletedAt,
                deletedAt + TrashRetention.RETENTION_MS
            )
        )
    }
}
