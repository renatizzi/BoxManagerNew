package com.example.boxmanagernew.importdata

import com.example.boxmanagernew.data.photo.ObjectPhotoPaths
import com.example.boxmanagernew.importdata.export.DataExportCsvBuilder
import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import com.example.boxmanagernew.importdata.zip.ImportDataZip
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportDataZipRoundTripTest {

    @Test
    fun exportV2Zip_importRoundTrip_keepsIdsAndPhotos() {
        val builder = DataExportCsvBuilder()
        val csv = builder.build(
            boxes = listOf(
                DataExportCsvBuilder.BoxRow(
                    name = "Box A",
                    category = "Cat",
                    position = "Pos",
                    permanentId = "pid-box"
                )
            ),
            objects = listOf(
                DataExportCsvBuilder.ObjectRow(
                    name = "Obj",
                    boxName = "Box A",
                    description = "d",
                    quantity = "2",
                    objectPermanentId = "pid-obj"
                )
            ),
            withIds = true
        )
        val photos = mapOf(
            ObjectPhotoPaths.zipDisplayEntry("pid-obj") to byteArrayOf(9),
            ObjectPhotoPaths.zipThumbEntry("pid-obj") to byteArrayOf(8)
        )
        val zip = ImportDataZip.pack(csv, photos)
        val ready = ImportFileInspector().inspect(zip)
        assertTrue(ready is ImportFileInspector.Result.Ready)
        val r = ready as ImportFileInspector.Result.Ready
        assertEquals("pid-box", r.boxes.single().permanentId)
        assertEquals("pid-obj", r.objects.single().objectPermanentId)
        assertArrayEquals(
            byteArrayOf(9),
            r.photoEntries[ObjectPhotoPaths.zipDisplayEntry("pid-obj")]
        )
    }

    @Test
    fun csvV1_export_hasNoIds() {
        val csv = DataExportCsvBuilder().build(
            boxes = emptyList(),
            objects = emptyList(),
            withIds = false
        )
        val ready = ImportFileInspector().inspect(csv) as ImportFileInspector.Result.Ready
        assertEquals(1, ready.formatVersion)
        assertTrue(ready.photoEntries.isEmpty())
    }
}
