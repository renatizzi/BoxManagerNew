package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.template.ImportTemplateBuilder
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportTemplateBuilderTest {

    @Test
    fun build_officialV1Track_utf8BomAndSections() {

        val bytes = ImportTemplateBuilder().build()

        assertTrue(bytes.size > ImportConfiguration.UTF8_BOM.size)
        assertArrayEquals(
            ImportConfiguration.UTF8_BOM,
            bytes.copyOfRange(0, ImportConfiguration.UTF8_BOM.size)
        )

        val text = String(
            bytes,
            ImportConfiguration.UTF8_BOM.size,
            bytes.size - ImportConfiguration.UTF8_BOM.size,
            Charsets.UTF_8
        )

        assertEquals(
            ImportConfiguration.TEMPLATE_LINES.joinToString(
                separator = "\r\n",
                postfix = "\r\n"
            ),
            text
        )
        assertTrue(text.contains("formato;BoxManager_Import;1"))
        assertTrue(text.contains("sezione;CONTENITORI"))
        assertTrue(text.contains("nome;categoria;posizione"))
        assertTrue(text.contains("sezione;OGGETTI"))
        assertTrue(text.contains("nome;contenitore;descrizione;quantita"))
    }
}
