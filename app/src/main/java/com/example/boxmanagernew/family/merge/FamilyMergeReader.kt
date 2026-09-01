package com.example.boxmanagernew.family.merge

import com.example.boxmanagernew.family.catalog.FamilyCatalogReader
import com.example.boxmanagernew.family.config.FamilyCatalogConfiguration
import com.example.boxmanagernew.family.config.FamilyInventoryConfiguration
import com.example.boxmanagernew.family.config.FamilyMergeConfiguration
import com.example.boxmanagernew.family.inventory.FamilyInventoryReader
import com.example.boxmanagernew.family.model.FamilyCatalogSnapshot
import com.example.boxmanagernew.family.model.FamilyInventorySnapshot
import com.example.boxmanagernew.family.model.FamilyMergeSnapshot

/**
 * Legge unione famiglia unificata o file legacy B1/B2.
 */
class FamilyMergeReader {

    sealed class Result {
        data class Ok(
            val snapshot: FamilyMergeSnapshot,
            val skippedRows: Int = 0
        ) : Result()

        data class Error(val message: String) : Result()
    }

    private val catalogReader = FamilyCatalogReader()
    private val inventoryReader = FamilyInventoryReader()

    fun parse(text: String): Result {
        val raw = text.removePrefix("\uFEFF")
        val firstLine = raw.lineSequence().firstOrNull()?.trim().orEmpty()
        val header = firstLine.split(FamilyMergeConfiguration.SEPARATOR)

        if (header.size >= 2) {
            when (header[1]) {
                FamilyMergeConfiguration.FORMAT_NAME ->
                    return parseUnified(raw)
                FamilyCatalogConfiguration.FORMAT_NAME ->
                    return parseLegacyCatalog(raw)
                FamilyInventoryConfiguration.FORMAT_NAME ->
                    return parseLegacyInventory(raw)
            }
        }

        return Result.Error(MSG_FORMAT)
    }

    private fun parseUnified(text: String): Result {
        val sep = FamilyMergeConfiguration.SEPARATOR
        val inventoryMarker =
            "sezione$sep${FamilyInventoryConfiguration.SECTION_BOXES}"
        val locationsMarker =
            "sezione$sep${FamilyCatalogConfiguration.SECTION_LOCATIONS}"
        val lines = text.lineSequence()
            .map { it.trimEnd('\r').trim() }
            .filter { it.isNotEmpty() }
            .toList()
        if (lines.isEmpty()) {
            return Result.Error(MSG_STRUCTURE)
        }

        val locationsIndex = lines.indexOfFirst { line ->
            line == locationsMarker
        }
        val searchStart = if (locationsIndex >= 0) locationsIndex + 1 else 1
        val inventoryIndex = lines.withIndex()
            .drop(searchStart)
            .firstOrNull { (index, line) ->
                line == inventoryMarker &&
                    isInventoryHeaderLine(lines.getOrNull(index + 1))
            }
            ?.index ?: -1
        if (inventoryIndex <= 1) {
            return Result.Error(MSG_STRUCTURE)
        }

        val catalogBody = lines
            .drop(1)
            .take(inventoryIndex - 1)
            .joinToString("\n")
        val inventoryBody = lines
            .drop(inventoryIndex)
            .joinToString("\n")

        val catalogText = buildString {
            append("formato$sep")
            append(FamilyCatalogConfiguration.FORMAT_NAME)
            append(sep)
            append(FamilyCatalogConfiguration.FORMAT_VERSION)
            append('\n')
            append(catalogBody)
        }

        val inventoryText = buildString {
            append("formato$sep")
            append(FamilyInventoryConfiguration.FORMAT_NAME)
            append(sep)
            append(FamilyInventoryConfiguration.FORMAT_VERSION)
            append('\n')
            append(inventoryBody)
        }

        val catalog = when (val parsed = catalogReader.parse(catalogText)) {
            is FamilyCatalogReader.Result.Ok -> parsed.snapshot
            is FamilyCatalogReader.Result.Error ->
                return Result.Error(parsed.message)
        }

        val inventory = when (val parsed = inventoryReader.parse(inventoryText)) {
            is FamilyInventoryReader.Result.Ok -> parsed
            is FamilyInventoryReader.Result.Error ->
                return Result.Error(parsed.message)
        }

        return Result.Ok(
            FamilyMergeSnapshot(
                catalog = catalog,
                inventory = inventory.snapshot
            ),
            skippedRows = inventory.skippedRows
        )
    }

    private fun isInventoryHeaderLine(line: String?): Boolean {
        if (line.isNullOrBlank()) {
            return false
        }
        val cols = line.split(FamilyMergeConfiguration.SEPARATOR)
        return cols.size >= 5 &&
            cols[0].equals(
                FamilyInventoryConfiguration.COL_PERMANENT_ID,
                ignoreCase = true
            ) &&
            cols[1].equals(
                FamilyInventoryConfiguration.COL_NAME,
                ignoreCase = true
            )
    }

    private fun parseLegacyCatalog(text: String): Result {
        return when (val parsed = catalogReader.parse(text)) {
            is FamilyCatalogReader.Result.Ok -> Result.Ok(
                FamilyMergeSnapshot(
                    catalog = parsed.snapshot,
                    inventory = FamilyInventorySnapshot(
                        boxes = emptyList(),
                        objects = emptyList()
                    )
                )
            )
            is FamilyCatalogReader.Result.Error ->
                Result.Error(parsed.message)
        }
    }

    private fun parseLegacyInventory(text: String): Result {
        return when (val parsed = inventoryReader.parse(text)) {
            is FamilyInventoryReader.Result.Ok -> Result.Ok(
                FamilyMergeSnapshot(
                    catalog = FamilyCatalogSnapshot(
                        categories = emptyList(),
                        locations = emptyList()
                    ),
                    inventory = parsed.snapshot
                ),
                skippedRows = parsed.skippedRows
            )
            is FamilyInventoryReader.Result.Error ->
                Result.Error(parsed.message)
        }
    }

    companion object {
        const val MSG_FORMAT =
            "Formato non riconosciuto. Serve BoxManager_FamilyMerge v1 " +
                "(o file legacy Catalogo/Inventario famiglia)."
        const val MSG_STRUCTURE =
            "File di condivisione archivio incompleto."
    }
}
