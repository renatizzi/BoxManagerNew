package com.example.boxmanagernew.premium

import com.example.boxmanagernew.domain.premium.ArchivioCompletoPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchivioCompletoPolicyTest {

    @Test
    fun open_ifCodeOrDebug() {
        assertTrue(
            ArchivioCompletoPolicy.isOpen(10L, 0L, true, false)
        )
        assertTrue(
            ArchivioCompletoPolicy.isOpen(10L, 0L, false, true)
        )
        assertFalse(
            ArchivioCompletoPolicy.isOpen(10L, 0L, false, false)
        )
    }

    @Test
    fun open_duringTimeWindow() {
        val now = 1_000_000L
        val until = now + ArchivioCompletoPolicy.DAY_MS
        assertTrue(
            ArchivioCompletoPolicy.isOpen(now, until, false, false)
        )
        assertFalse(
            ArchivioCompletoPolicy.isOpen(until, until, false, false)
        )
    }

    @Test
    fun trial_usesConfiguredDays() {
        val start = 0L
        val end =
            ArchivioCompletoPolicy.trialEnd(start, 14)
        assertEquals(
            14L * ArchivioCompletoPolicy.DAY_MS,
            end
        )
        assertEquals(
            14,
            ArchivioCompletoPolicy.remainingDaysCeil(start, end)
        )
    }

    @Test
    fun share_extendsFromNowIfExpired() {
        val now = 50L
        val until = 10L
        assertEquals(
            now + 7L * ArchivioCompletoPolicy.DAY_MS,
            ArchivioCompletoPolicy.extendAccess(now, until, 7)
        )
    }

    @Test
    fun share_cooldownFortyEightHours() {
        val now = ArchivioCompletoPolicy.SHARE_COOLDOWN_MS
        assertFalse(
            ArchivioCompletoPolicy.canGrantShare(now, 1L)
        )
        assertTrue(
            ArchivioCompletoPolicy.canGrantShare(now, 0L)
        )
        assertTrue(
            ArchivioCompletoPolicy.canGrantShare(
                now,
                now - ArchivioCompletoPolicy.SHARE_COOLDOWN_MS
            )
        )
    }

    @Test
    fun unlock_codeIsNormalized() {
        assertTrue(
            ArchivioCompletoPolicy.isValidUnlockCode(" boxmanager amico ")
        )
        assertTrue(
            ArchivioCompletoPolicy.isValidUnlockCode("BOXMANAGER-AMICO")
        )
        assertTrue(
            ArchivioCompletoPolicy.isValidUnlockCode(" boxmanager tester ")
        )
        assertTrue(
            ArchivioCompletoPolicy.isValidUnlockCode("BOXMANAGER-TESTER")
        )
        assertFalse(
            ArchivioCompletoPolicy.isValidUnlockCode("ALTRO")
        )
    }

    @Test
    fun admin_usernameExact() {
        assertTrue(
            ArchivioCompletoPolicy.isAdminUsername("Renato Stefanizzi")
        )
        assertFalse(
            ArchivioCompletoPolicy.isAdminUsername("renato stefanizzi")
        )
        assertFalse(
            ArchivioCompletoPolicy.isAdminUsername("Altro")
        )
    }

    @Test
    fun params_areClamped() {
        assertEquals(1, ArchivioCompletoPolicy.clampTrialDays(0))
        assertEquals(365, ArchivioCompletoPolicy.clampTrialDays(999))
        assertEquals(1, ArchivioCompletoPolicy.clampShareFriends(0))
        assertEquals(20, ArchivioCompletoPolicy.clampShareFriends(99))
    }
}
