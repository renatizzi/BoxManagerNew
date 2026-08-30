package com.example.boxmanagernew.family.merge

import com.example.boxmanagernew.family.catalog.FamilyCatalogWriter
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.inventory.FamilyInventoryWriter
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot

/**
 * Serializza la condivisione archivio (tabelle di riferimento + inventario)
 * in un unico CSV.
 */
object FamilyMergeWriter {

    fun toCsvLines(snapshot: FamilyMergeSnapshot): List<String> {
        val sep = FamilyMergeConfiguration.SEPARATOR
        val lines = mutableListOf<String>()

        lines += "formato$sep${FamilyMergeConfiguration.FORMAT_NAME}" +
            "$sep${FamilyMergeConfiguration.FORMAT_VERSION}"

        val catalogLines = FamilyCatalogWriter.toCsvLines(snapshot.catalog)
            .drop(1)
        val inventoryLines = FamilyInventoryWriter.toCsvLines(snapshot.inventory)
            .drop(1)

        lines += catalogLines
        lines += inventoryLines

        return lines
    }

    fun toCsvBytes(snapshot: FamilyMergeSnapshot): ByteArray {
        val body = toCsvLines(snapshot).joinToString("\r\n") + "\r\n"
        return FamilyMergeConfiguration.UTF8_BOM +
            body.toByteArray(Charsets.UTF_8)
    }
}
