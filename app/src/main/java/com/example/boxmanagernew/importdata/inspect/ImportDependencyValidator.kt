package com.example.boxmanagernew.importdata.inspect

import com.example.boxmanagernew.importdata.config.ImportConfiguration
import java.util.Locale

class ImportDependencyValidator {

    sealed class Result {
        object Ok : Result()

        data class Failed(
            val errors: List<ImportRowError>
        ) : Result() {
            val message: String
                get() = errors.firstOrNull()?.reason.orEmpty()
        }
    }

    fun validate(
        boxes: List<ImportFileInspector.BoxRow>,
        objects: List<ImportFileInspector.ObjectRow>,
        categoryNames: Collection<String>,
        locationNames: Collection<String>,
        archiveBoxNames: Collection<String>
    ): Result {

        val categories = indexed(categoryNames)
        val locations = indexed(locationNames)
        val knownBoxes = indexed(archiveBoxNames) + indexed(boxes.map { it.name })
        val errors = mutableListOf<ImportRowError>()

        for (box in boxes) {
            if (!categories.contains(key(box.category)) ||
                !locations.contains(key(box.position))
            ) {
                errors.add(
                    ImportRowError(
                        section = ImportConfiguration.SECTION_BOXES,
                        line = box.sourceLine,
                        reason = ImportConfiguration.MSG_BOX_DEPENDENCY
                    )
                )
            }
        }

        for (obj in objects) {
            if (!knownBoxes.contains(key(obj.box))) {
                errors.add(
                    ImportRowError(
                        section = ImportConfiguration.SECTION_OBJECTS,
                        line = obj.sourceLine,
                        reason = ImportConfiguration.MSG_OBJECT_DEPENDENCY
                    )
                )
            }
        }

        return if (errors.isEmpty()) {
            Result.Ok
        } else {
            Result.Failed(errors)
        }
    }

    private fun indexed(names: Collection<String>): Set<String> {
        return names.map { key(it) }.toSet()
    }

    private fun key(value: String): String {
        return value.trim().lowercase(Locale.ROOT)
    }
}
