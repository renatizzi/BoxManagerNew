package com.example.boxmanagernew.domain.search.model

data class SearchArchiveIndex(

    val locations: List<String> = emptyList(),

    val categories: List<String> = emptyList(),

    val objects: List<String> = emptyList(),

    val boxes: List<String> = emptyList(),

    val objectRecords:
    List<SearchArchiveObjectRecord> = emptyList()
) {

    fun archivalObjects():
            List<SearchArchiveObjectRecord> {

        if (
            objectRecords.isEmpty()
        ) {

            return objects.map { name ->

                SearchArchiveObjectRecord(
                    name = name
                )
            }
        }

        val extraTypes =
            objects.filter { typeName ->

                objectRecords.none { record ->

                    record.name.equals(
                        typeName,
                        ignoreCase = true
                    )
                }
            }.map { name ->

                SearchArchiveObjectRecord(
                    name = name
                )
            }

        return objectRecords + extraTypes
    }
}
