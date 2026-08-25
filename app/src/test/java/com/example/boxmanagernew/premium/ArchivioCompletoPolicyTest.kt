package com.example.boxmanagernew.premium

import com.example.boxmanagernew.domain.premium.ArchivioCompletoPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchivioCompletoPolicyTest {

    @Test
    fun remaining_neverNegative() {
        assertEquals(0, ArchivioCompletoPolicy.remaining(3, 10))
        assertEquals(2, ArchivioCompletoPolicy.remaining(3, 1))
        assertEquals(0, ArchivioCompletoPolicy.remaining(0, 0))
    }

    @Test
    fun import_hasNoTrial() {
        assertFalse(ArchivioCompletoPolicy.canTrial(0, 0))
    }

    @Test
    fun search_threeTrials() {
        assertTrue(ArchivioCompletoPolicy.canTrial(3, 0))
        assertTrue(ArchivioCompletoPolicy.canTrial(3, 2))
        assertFalse(ArchivioCompletoPolicy.canTrial(3, 3))
    }

    @Test
    fun open_ifPurchasedOrDebug() {
        assertTrue(ArchivioCompletoPolicy.isOpen(true, false))
        assertTrue(ArchivioCompletoPolicy.isOpen(false, true))
        assertFalse(ArchivioCompletoPolicy.isOpen(false, false))
    }
}
