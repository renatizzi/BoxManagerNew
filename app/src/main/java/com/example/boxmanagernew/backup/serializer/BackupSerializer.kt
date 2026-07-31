package com.example.boxmanagernew.backup.serializer

import com.example.boxmanagernew.backup.model.BackupArchive

class BackupSerializer {

    fun serialize(
        archive: BackupArchive
    ): String {

        return buildString {

            appendLine("{")

            appendLine("""  "formatVersion": ${archive.formatVersion},""")
            appendLine("""  "createdAt": "${escape(archive.createdAt)}",""")

            appendLine("""  "application": {""")
            appendLine("""    "name": "${escape(archive.application.name)}",""")
            appendLine("""    "backupType": "${escape(archive.application.backupType)}"""")
            appendLine("""  },""")

            appendLine("""  "archive": {""")
            appendLine("""    "boxesCount": ${archive.archive.boxes.size},""")
            appendLine("""    "objectsCount": ${archive.archive.objects.size},""")
            appendLine("""    "categoriesCount": ${archive.archive.categories.size},""")
            appendLine("""    "locationsCount": ${archive.archive.locations.size},""")
            appendLine("""    "objectTypesCount": ${archive.archive.objectTypes.size}""")
            appendLine("""  }""")

            append("}")
        }
    }

    private fun escape(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
}