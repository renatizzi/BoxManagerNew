package com.example.boxmanagernew.importdata.merge

import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import java.util.Locale

class ImportMergePlanner {

    data class ArchiveBox(
        val name: String,
        val categoryName: String,
        val position: String
    )

    data class ArchiveObject(
        val typeName: String,
        val boxName: String,
        val description: String?,
        val quantity: Int?
    )

    data class ObjectToInsert(
        val name: String,
        val box: String,
        val description: String?,
        val quantity: Int?
    )

    data class Plan(
        val recordsRead: Int,
        val boxesToInsert: List<ImportFileInspector.BoxRow>,
        val objectsToInsert: List<ObjectToInsert>,
        val ignoredDuplicates: Int,
        val discardedErrors: Int
    ) {
        val imported: Int
            get() = boxesToInsert.size + objectsToInsert.size

        val canApply: Boolean
            get() = discardedErrors == 0
    }

    fun plan(
        fileBoxes: List<ImportFileInspector.BoxRow>,
        fileObjects: List<ImportFileInspector.ObjectRow>,
        archiveBoxes: List<ArchiveBox>,
        archiveObjects: List<ArchiveObject>
    ): Plan {

        val boxKeys = archiveBoxes.map { boxKey(it.name, it.categoryName, it.position) }
            .toMutableSet()
        val boxesToInsert = mutableListOf<ImportFileInspector.BoxRow>()
        var ignored = 0
        var discarded = 0

        for (box in fileBoxes) {
            val key = boxKey(box.name, box.category, box.position)
            if (boxKeys.contains(key)) {
                ignored++
            } else {
                boxKeys.add(key)
                boxesToInsert.add(box)
            }
        }

        val objectKeys = archiveObjects.map {
            objectKey(it.typeName, it.boxName, it.description, it.quantity)
        }.toMutableSet()
        val objectsToInsert = mutableListOf<ObjectToInsert>()

        for (obj in fileObjects) {
            val quantity = parseQuantity(obj.quantity)
            if (obj.quantity != null && quantity == PARSE_FAILED) {
                discarded++
                continue
            }
            val qty = quantity as Int?
            val key = objectKey(obj.name, obj.box, obj.description, qty)
            if (objectKeys.contains(key)) {
                ignored++
            } else {
                objectKeys.add(key)
                objectsToInsert.add(
                    ObjectToInsert(
                        name = obj.name,
                        box = obj.box,
                        description = obj.description,
                        quantity = qty
                    )
                )
            }
        }

        return Plan(
            recordsRead = fileBoxes.size + fileObjects.size,
            boxesToInsert = boxesToInsert,
            objectsToInsert = objectsToInsert,
            ignoredDuplicates = ignored,
            discardedErrors = discarded
        )
    }

    private fun parseQuantity(value: String?): Any? {
        if (value.isNullOrBlank()) {
            return null
        }
        return value.toIntOrNull() ?: PARSE_FAILED
    }

    private fun boxKey(
        name: String,
        category: String,
        position: String
    ): String {
        return listOf(name, category, position).joinToString("|") { key(it) }
    }

    private fun objectKey(
        name: String,
        box: String,
        description: String?,
        quantity: Int?
    ): String {
        return listOf(
            key(name),
            key(box),
            key(description.orEmpty()),
            (quantity ?: "").toString()
        ).joinToString("|")
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }

    private companion object {
        private val PARSE_FAILED = Any()
    }
}
