package com.example.boxmanagernew.family

import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.merge.FamilyMergeWriter
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import com.example.boxmanagernew.family.model.FamilyInventoryBox
import com.example.boxmanagernew.family.model.FamilyInventoryObject
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot
import com.example.boxmanagernew.storage.StorageFolderConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FamilyMergeExportFilterTest {

    @Test
    fun writer_doesNotEmitObjectWithoutBoxPermanentId() {
        val snapshot = FamilyMergeSnapshot(
            catalog = FamilyCatalogSnapshot(
                categories = emptyList(),
                locations = emptyList()
            ),
            inventory = FamilyInventorySnapshot(
                boxes = emptyList(),
                objects = listOf(
                    FamilyInventoryObject(
                        objectPermanentId = "obj-orphan",
                        boxPermanentId = "",
                        typeName = "Tipo",
                        description = null,
                        quantity = 1,
                        lastModified = 1000L
                    )
                )
            )
        )

        val csv = FamilyMergeWriter.toCsvLines(snapshot).joinToString("\n")
        assertEquals(false, csv.contains("obj-orphan"))
    }

    @Test
    fun storageFolders_useDistinctPreferenceKeys() {
        assertNotEquals(
            StorageFolderConfiguration.KEY_BACKUP,
            StorageFolderConfiguration.KEY_IMPORT_EXPORT
        )
        assertNotEquals(
            StorageFolderConfiguration.KEY_IMPORT_EXPORT,
            StorageFolderConfiguration.KEY_FAMILY_SHARE
        )
        assertEquals(
            "Condivisione_Archivio_",
            FamilyMergeConfiguration.FILE_PREFIX
        )
    }
}
