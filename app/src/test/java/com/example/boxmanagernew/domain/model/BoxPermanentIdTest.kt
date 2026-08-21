package com.example.boxmanagernew.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BoxPermanentIdTest {

    @Test
    fun generate_isNotBlankAndUnique() {

        val first = BoxPermanentId.generate()
        val second = BoxPermanentId.generate()

        assertTrue(first.isNotBlank())
        assertTrue(second.isNotBlank())
        assertNotEquals(first, second)
    }

    @Test
    fun fromStored_keepsExistingValue() {

        assertEquals(
            "kept-id",
            BoxPermanentId.fromStored("kept-id")
        )
    }

    @Test
    fun fromStored_blankOrNull_generatesTechnicalId() {

        val fromBlank = BoxPermanentId.fromStored("  ")
        val fromNull = BoxPermanentId.fromStored(null)

        assertTrue(fromBlank.isNotBlank())
        assertTrue(fromNull.isNotBlank())
        assertNotEquals(fromBlank, fromNull)
    }
}
