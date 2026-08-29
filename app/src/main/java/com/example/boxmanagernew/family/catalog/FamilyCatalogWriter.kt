package com.example.boxmanagernew.family.catalog

import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot

/**
 * Serializza il catalogo famiglia nel tracciato ufficiale B1.
 */
object FamilyCatalogWriter {

    fun toCsvLines(snapshot: FamilyCatalogSnapshot): List<String> {
        val sep = FamilyCatalogConfiguration.SEPARATOR
        val lines = mutableListOf<String>()

        lines += "formato$sep${FamilyCatalogConfiguration.FORMAT_NAME}" +
            "$sep${FamilyCatalogConfiguration.FORMAT_VERSION}"

        lines += "sezione$sep${FamilyCatalogConfiguration.SECTION_CATEGORIES}"
        lines += "${FamilyCatalogConfiguration.COL_NAME}$sep" +
            FamilyCatalogConfiguration.COL_ICON
        for (category in snapshot.categories) {
            lines += escape(category.name) + sep + escape(category.icon)
        }

        lines += "sezione$sep${FamilyCatalogConfiguration.SECTION_LOCATIONS}"
        lines += FamilyCatalogConfiguration.COL_NAME
        for (location in snapshot.locations) {
            lines += escape(location.name)
        }

        return lines
    }

    fun toCsvBytes(snapshot: FamilyCatalogSnapshot): ByteArray {
        val body = toCsvLines(snapshot).joinToString("\r\n") + "\r\n"
        return FamilyCatalogConfiguration.UTF8_BOM +
            body.toByteArray(Charsets.UTF_8)
    }

    private fun escape(value: String): String {
        return value.replace("\r", " ").replace("\n", " ").trim()
    }
}
