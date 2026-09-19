package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.inspect.ImportExtendedValidator
import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportExtendedValidatorTest {

    private val validator = ImportExtendedValidator()

    @Test
    fun validRows_ok() {
        val result = validator.validate(
            boxes = listOf(box("A", "Cat", "Pos")),
            objects = listOf(obj("Vite", "A", "4mm", "10"))
        )
        assertTrue(result is ImportExtendedValidator.Result.Ok)
    }

    @Test
    fun quantityBlank_ok() {
        val result = validator.validate(
            boxes = emptyList(),
            objects = listOf(obj("Vite", "A", null, null))
        )
        assertTrue(result is ImportExtendedValidator.Result.Ok)
    }

    @Test
    fun quantitySheetFloat_ok() {
        assertEquals(12, validator.parseQuantity("12.0"))
        assertEquals(12, validator.parseQuantity("12,0"))
        val result = validator.validate(
            boxes = emptyList(),
            objects = listOf(obj("Vite", "A", null, "12.0"))
        )
        assertTrue(result is ImportExtendedValidator.Result.Ok)
    }

    @Test
    fun quantityInvalid_fails() {
        assertNull(validator.parseQuantity("abc"))
        val result = validator.validate(
            boxes = emptyList(),
            objects = listOf(obj("Vite", "A", null, "abc"))
        )
        assertTrue(result is ImportExtendedValidator.Result.Failed)
        assertTrue(
            (result as ImportExtendedValidator.Result.Failed)
                .message.startsWith(ImportConfiguration.MSG_QUANTITY_INVALID)
        )
    }

    @Test
    fun quantityNegative_fails() {
        val result = validator.validate(
            boxes = emptyList(),
            objects = listOf(obj("Vite", "A", null, "-1"))
        )
        assertTrue(result is ImportExtendedValidator.Result.Failed)
    }

    @Test
    fun fieldTooLong_fails() {
        val long = "x".repeat(ImportConfiguration.MAX_FIELD_CHARS + 1)
        val result = validator.validate(
            boxes = listOf(box(long, "Cat", "Pos")),
            objects = emptyList()
        )
        assertTrue(result is ImportExtendedValidator.Result.Failed)
        assertTrue(
            (result as ImportExtendedValidator.Result.Failed)
                .message.startsWith(ImportConfiguration.MSG_FIELD_TOO_LONG)
        )
    }

    @Test
    fun homonymBoxes_ok() {
        val result = validator.validate(
            boxes = listOf(
                box("Scatola", "Cat", "Soggiorno"),
                box("scatola", "Cat", "Cantina")
            ),
            objects = emptyList()
        )
        assertTrue(result is ImportExtendedValidator.Result.Ok)
    }

    @Test
    fun sameBoxTriple_softOk() {
        // Soft: stesso nome+cat+pos non blocca; merge ignora il doppione.
        val result = validator.validate(
            boxes = listOf(
                box("Scatola", "Cat", "Pos"),
                box("scatola", "Cat", "Pos")
            ),
            objects = emptyList()
        )
        assertTrue(result is ImportExtendedValidator.Result.Ok)
    }

    @Test
    fun duplicateObject_fails() {
        val result = validator.validate(
            boxes = emptyList(),
            objects = listOf(
                obj("Vite", "A", "4mm", "1"),
                obj("Vite", "A", "4mm", "1")
            )
        )
        assertTrue(result is ImportExtendedValidator.Result.Failed)
        assertTrue(
            (result as ImportExtendedValidator.Result.Failed)
                .message.startsWith(ImportConfiguration.MSG_DUPLICATE_OBJECT)
        )
    }

    private fun box(name: String, category: String, position: String) =
        ImportFileInspector.BoxRow(name, category, position)

    private fun obj(
        name: String,
        box: String,
        description: String?,
        quantity: String?
    ) = ImportFileInspector.ObjectRow(name, box, description, quantity)
}
