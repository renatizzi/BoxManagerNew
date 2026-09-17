package com.example.boxmanagernew.family

import com.example.boxmanagernew.data.photo.ObjectPhotoPaths
import com.example.boxmanagernew.family.zip.FamilyArchiveZip
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyArchiveZipTest {

    @Test
    fun packUnpack_roundTripsCsvAndPhotos() {
        val csv = "formato;BoxManager_FamilyMerge;1\r\n".toByteArray(Charsets.UTF_8)
        val display = byteArrayOf(1, 2, 3)
        val thumb = byteArrayOf(4, 5)
        val photos = mapOf(
            ObjectPhotoPaths.zipDisplayEntry("oid-1") to display,
            ObjectPhotoPaths.zipThumbEntry("oid-1") to thumb
        )

        val zip = FamilyArchiveZip.pack(csv, photos)
        assertTrue(FamilyArchiveZip.looksLikeZip(zip))

        val unpacked = FamilyArchiveZip.unpack(zip)
        assertNotNull(unpacked)
        assertArrayEquals(csv, unpacked!!.csvBytes)
        assertEquals(2, unpacked.photoEntries.size)
        assertArrayEquals(display, unpacked.photoEntries[ObjectPhotoPaths.zipDisplayEntry("oid-1")])
        assertArrayEquals(thumb, unpacked.photoEntries[ObjectPhotoPaths.zipThumbEntry("oid-1")])
    }

    @Test
    fun unpack_acceptsRootCsvWithAnyName() {
        val csv = "x".toByteArray()
        val zip = FamilyArchiveZip.pack(csv, emptyMap())
        // Repack already uses archivio.csv; also verify looksLikeZip false for plain csv
        assertFalse(FamilyArchiveZip.looksLikeZip(csv))
        val unpacked = FamilyArchiveZip.unpack(zip)
        assertNotNull(unpacked)
        assertArrayEquals(csv, unpacked!!.csvBytes)
        assertTrue(unpacked.photoEntries.isEmpty())
    }
}
