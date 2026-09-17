package com.example.boxmanagernew.photo

import com.example.boxmanagernew.data.photo.ObjectDescriptionOcr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ObjectDescriptionOcrTest {

    @Test
    fun normalize_trimsAndDropsBlankLines() {
        val raw = "  Linea uno  \n\n  Linea due \n"
        assertEquals("Linea uno\nLinea due", ObjectDescriptionOcr.normalize(raw))
    }

    @Test
    fun normalize_blank_returnsNull() {
        assertNull(ObjectDescriptionOcr.normalize("  \n  "))
        assertNull(ObjectDescriptionOcr.normalize(null))
    }

    @Test
    fun gradle_and_strings_wireOcr() {
        val gradle = File("build.gradle.kts").readText()
        assertTrue(gradle.contains("libs.mlkit.text.recognition"))
        val versions = File("../gradle/libs.versions.toml").readText()
        assertTrue(versions.contains("text-recognition"))
        val it = File("src/main/res/values/strings.xml").readText()
        assertTrue(it.contains("object_photo_ocr_replace_description"))
        val en = File("src/main/res/values-en/strings.xml").readText()
        assertTrue(en.contains("object_photo_ocr_replace_description"))
    }
}
