package com.example.boxmanagernew.family.catalog

import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.model.FamilyCatalogCategory
import com.example.boxmanagernew.family.model.FamilyCatalogLocation
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import java.util.Locale

/**
 * Legge e valida un file catalogo famiglia.
 */
class FamilyCatalogReader {

    sealed class Result {
        data class Ok(val snapshot: FamilyCatalogSnapshot) : Result()
        data class Error(val message: String) : Result()
    }

    fun parse(text: String): Result {
        val raw = text.removePrefix("\uFEFF")
        val lines = raw.split('\n', '\r')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) {
            return Result.Error(MSG_EMPTY)
        }

        val header = split(lines.first())
        if (
            header.size < 3 ||
            !header[0].equals("formato", ignoreCase = true) ||
            header[1] != FamilyCatalogConfiguration.FORMAT_NAME ||
            header[2].toIntOrNull() != FamilyCatalogConfiguration.FORMAT_VERSION
        ) {
            return Result.Error(MSG_FORMAT)
        }

        var section: String? = null
        val categories = mutableListOf<FamilyCatalogCategory>()
        val locations = mutableListOf<FamilyCatalogLocation>()
        var expectCategoryHeader = false
        var expectLocationHeader = false

        for (line in lines.drop(1)) {
            val cols = split(line)
            if (cols.isEmpty()) {
                continue
            }

            if (cols[0].equals("sezione", ignoreCase = true)) {
                if (cols.size < 2) {
                    return Result.Error(MSG_SECTION)
                }
                section = cols[1].uppercase(Locale.ROOT)
                when (section) {
                    FamilyCatalogConfiguration.SECTION_CATEGORIES -> {
                        expectCategoryHeader = true
                        expectLocationHeader = false
                    }
                    FamilyCatalogConfiguration.SECTION_LOCATIONS -> {
                        expectLocationHeader = true
                        expectCategoryHeader = false
                    }
                    else -> return Result.Error(MSG_SECTION)
                }
                continue
            }

            when (section) {
                FamilyCatalogConfiguration.SECTION_CATEGORIES -> {
                    if (expectCategoryHeader) {
                        if (
                            cols.getOrNull(0).equals(
                                FamilyCatalogConfiguration.COL_NAME,
                                ignoreCase = true
                            ) &&
                            cols.getOrNull(1).equals(
                                FamilyCatalogConfiguration.COL_ICON,
                                ignoreCase = true
                            )
                        ) {
                            expectCategoryHeader = false
                            continue
                        }
                        return Result.Error(MSG_CATEGORY_HEADER)
                    }
                    val name = cols.getOrNull(0)?.trim().orEmpty()
                    if (name.isEmpty()) {
                        return Result.Error(MSG_CATEGORY_NAME)
                    }
                    val icon = cols.getOrNull(1)?.trim().orEmpty().ifEmpty {
                        FamilyCatalogConfiguration.DEFAULT_CATEGORY_ICON
                    }
                    categories += FamilyCatalogCategory(name = name, icon = icon)
                }
                FamilyCatalogConfiguration.SECTION_LOCATIONS -> {
                    if (expectLocationHeader) {
                        if (
                            cols.getOrNull(0).equals(
                                FamilyCatalogConfiguration.COL_NAME,
                                ignoreCase = true
                            )
                        ) {
                            expectLocationHeader = false
                            continue
                        }
                        return Result.Error(MSG_LOCATION_HEADER)
                    }
                    val name = cols.getOrNull(0)?.trim().orEmpty()
                    if (name.isEmpty()) {
                        return Result.Error(MSG_LOCATION_NAME)
                    }
                    locations += FamilyCatalogLocation(name = name)
                }
                else -> return Result.Error(MSG_SECTION_ORDER)
            }
        }

        if (section == null) {
            return Result.Error(MSG_SECTION_ORDER)
        }

        return Result.Ok(
            FamilyCatalogSnapshot(
                categories = categories,
                locations = locations
            )
        )
    }

    private fun split(line: String): List<String> {
        return line.split(FamilyCatalogConfiguration.SEPARATOR)
    }

    companion object {
        const val MSG_EMPTY = "File catalogo vuoto."
        const val MSG_FORMAT =
            "Formato non riconosciuto. Serve BoxManager_FamilyCatalog v1."
        const val MSG_SECTION = "Sezione catalogo non valida."
        const val MSG_SECTION_ORDER =
            "Struttura incompleta: servono sezioni CATEGORIE e/o POSIZIONI."
        const val MSG_CATEGORY_HEADER =
            "Intestazione CATEGORIE non valida (nome;icona)."
        const val MSG_LOCATION_HEADER =
            "Intestazione POSIZIONI non valida (nome)."
        const val MSG_CATEGORY_NAME = "Categoria senza nome."
        const val MSG_LOCATION_NAME = "Posizione senza nome."
    }
}
