package com.example.boxmanagernew.photo

import com.example.boxmanagernew.data.photo.ObjectPhotoPaths
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ObjectPhotoPathsTest {

    @Test
    fun names_followPermanentIdConvention() {
        assertEquals("abc.jpg", ObjectPhotoPaths.displayFileName("abc"))
        assertEquals("abc_thumb.jpg", ObjectPhotoPaths.thumbFileName("abc"))
        assertEquals(
            "photos/objects/abc.jpg",
            ObjectPhotoPaths.zipDisplayEntry("abc")
        )
        assertEquals(
            "photos/objects/abc_thumb.jpg",
            ObjectPhotoPaths.zipThumbEntry("abc")
        )
    }

    @Test
    fun compressor_constants_matchRequirementsT2() {
        assertEquals(800, com.example.boxmanagernew.data.photo.ObjectPhotoCompressor.DISPLAY_MAX_SIDE)
        assertEquals(75, com.example.boxmanagernew.data.photo.ObjectPhotoCompressor.DISPLAY_JPEG_QUALITY)
        assertEquals(160, com.example.boxmanagernew.data.photo.ObjectPhotoCompressor.THUMB_MAX_SIDE)
        assertEquals(70, com.example.boxmanagernew.data.photo.ObjectPhotoCompressor.THUMB_JPEG_QUALITY)
    }

    @Test
    fun strings_objectPhoto_exist() {
        val it = File("src/main/res/values/strings.xml").readText()
        val en = File("src/main/res/values-en/strings.xml").readText()
        assertTrue(it.contains("object_photo_from_gallery"))
        assertTrue(en.contains("object_photo_from_camera"))
        assertTrue(it.contains("premium_feature_object_photo"))
        assertTrue(en.contains("premium_feature_object_photo"))
    }
}
