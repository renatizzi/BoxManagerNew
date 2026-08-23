package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveQuery
import com.example.boxmanagernew.domain.search.model.SearchArchiveQueryOperation
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Motore B: elaborazione della Query Archivistica.
 * F7 / PATTERN_007: oggetti uguali / doppioni.
 * F8 / PATTERN_008: per ogni nome oggetto presente in più box,
 * restano solo i gruppi con almeno due categorie distinte.
 * Chiave oggetto: solo il nome (accenti, caratteri speciali,
 * singolare/plurale se una sola parola). Quantità e descrizione escluse.
 */
class SearchEngineB {

    fun execute(
        query: SearchArchiveQuery,
        index: SearchArchiveIndex
    ): SearchResponse {

        if (
            query.operation !=
            SearchArchiveQueryOperation.COMPARE
        ) {

            return unavailable()
        }

        val f8 =
            query.filters.any { filter ->
                filter == SearchF8Pattern.ID
            }

        val f7 =
            query.filters.any { filter ->
                filter == SearchF7Pattern.ID
            }

        if (!f8 && !f7) {

            return unavailable()
        }

        val boxes =
            if (f8) {
                boxesWithCrossCategoryDuplicates(
                    index
                )
            } else {
                boxesWithDuplicateObjects(
                    index
                )
            }

        if (boxes.isEmpty()) {

            return SearchResponse(
                success = false,
                message =
                    SearchConfiguration.MSG_NO_RESULTS,
                requestType =
                    SearchRequestType.ARCHIVE_QUERY
            )
        }

        val heading =
            if (f8) {
                SearchF8Pattern.VARIANTS[6]
            } else {
                SearchF7Pattern.VARIANTS[2]
            }

        return SearchResponse(
            success = true,
            message =
                heading + "\n" +
                    boxes.joinToString(
                        "\n"
                    ),
            requestType =
                SearchRequestType.ARCHIVE_QUERY
        )
    }

    private fun unavailable():
            SearchResponse =

        SearchResponse(
            success = false,
            message =
                SearchConfiguration.MSG_INTERROGATION_UNAVAILABLE,
            requestType =
                SearchRequestType.ARCHIVE_QUERY
        )

    private fun boxesWithDuplicateObjects(
        index: SearchArchiveIndex
    ): List<String> {

        return boxesFromGroups(
            duplicateNameGroups(
                index
            )
        )
    }

    private fun boxesWithCrossCategoryDuplicates(
        index: SearchArchiveIndex
    ): List<String> {

        val records =
            index.objectRecords.filter { record ->

                record.boxName.isNotBlank() &&
                    record.name.isNotBlank()
            }

        return records
            .groupBy { record ->
                CanonicalNormalizer.canonical(
                    record.name
                )
            }
            .mapNotNull { (_, group) ->

                val boxesByCategory =
                    group
                        .map { record ->
                            record.boxName to
                                categoryKeyOf(
                                    record
                                )
                        }
                        .filter { pair ->
                            pair.second.isNotEmpty()
                        }
                        .distinctBy { pair ->
                            pair.first.lowercase()
                        }

                val categories =
                    boxesByCategory
                        .map { pair ->
                            pair.second
                        }
                        .distinct()

                if (
                    boxesByCategory.size < 2 ||
                    categories.size < 2
                ) {
                    return@mapNotNull null
                }

                val objectName =
                    group
                        .map { record ->
                            record.name
                        }
                        .minWith(
                            String.CASE_INSENSITIVE_ORDER
                        )

                val boxNames =
                    boxesByCategory
                        .map { pair ->
                            pair.first
                        }
                        .sortedWith(
                            String.CASE_INSENSITIVE_ORDER
                        )

                objectName + ": " +
                    boxNames.joinToString(
                        ", "
                    )
            }
            .sortedWith(
                String.CASE_INSENSITIVE_ORDER
            )
    }

    private fun categoryKeyOf(
        record: SearchArchiveObjectRecord
    ): String {

        if (record.categoryId != 0) {

            return "id:" +
                record.categoryId
        }

        return categoryKey(
            record.boxCategory
        )
    }

    private fun boxesFromGroups(
        groups: List<List<SearchArchiveObjectRecord>>
    ): List<String> {

        return groups
            .flatten()
            .map { record ->
                record.boxName
            }
            .distinct()
            .sortedWith(
                String.CASE_INSENSITIVE_ORDER
            )
    }

    private fun duplicateNameGroups(
        index: SearchArchiveIndex
    ): List<List<SearchArchiveObjectRecord>> {

        val records =
            index.objectRecords.filter { record ->

                record.boxName.isNotBlank() &&
                    record.name.isNotBlank()
            }

        val parent =
            IntArray(records.size) { slot ->
                slot
            }

        fun find(
            slot: Int
        ): Int {

            var current =
                slot

            while (parent[current] != current) {

                parent[current] =
                    parent[parent[current]]

                current =
                    parent[current]
            }

            return current
        }

        fun union(
            left: Int,
            right: Int
        ) {

            val rootLeft =
                find(left)

            val rootRight =
                find(right)

            if (rootLeft != rootRight) {

                parent[rootLeft] =
                    rootRight
            }
        }

        for (i in records.indices) {

            for (j in i + 1 until records.size) {

                if (
                    sameSearchName(
                        records[i].name,
                        records[j].name
                    )
                ) {

                    union(i, j)
                }
            }
        }

        return records.indices
            .groupBy { slot ->
                find(slot)
            }
            .values
            .filter { members ->
                members.size >= 2
            }
            .map { members ->
                members.map { slot ->
                    records[slot]
                }
            }
    }

    private fun categoryKey(
        category: String
    ): String {

        return CanonicalNormalizer.canonical(
            category.trim()
        )
    }

    private fun sameSearchName(
        left: String,
        right: String
    ): Boolean {

        if (
            CanonicalNormalizer.canonical(
                left
            ) ==
            CanonicalNormalizer.canonical(
                right
            )
        ) {

            return true
        }

        val leftWords =
            CanonicalNormalizer.wordTokens(
                left
            )

        val rightWords =
            CanonicalNormalizer.wordTokens(
                right
            )

        if (
            leftWords.size != 1 ||
            rightWords.size != 1
        ) {

            return false
        }

        return CanonicalNormalizer.wholeWordMatches(
            left,
            right
        )
    }

    companion object {

        fun f7Query():
                SearchArchiveQuery =

            SearchArchiveQuery(
                operation =
                    SearchArchiveQueryOperation.COMPARE,
                targetEntities =
                    setOf(
                        CoreEntityType.OBJECT,
                        CoreEntityType.BOX
                    ),
                filters =
                    listOf(
                        SearchF7Pattern.ID
                    )
            )

        fun f8Query():
                SearchArchiveQuery =

            SearchArchiveQuery(
                operation =
                    SearchArchiveQueryOperation.COMPARE,
                targetEntities =
                    setOf(
                        CoreEntityType.OBJECT,
                        CoreEntityType.BOX,
                        CoreEntityType.CATEGORY
                    ),
                filters =
                    listOf(
                        SearchF8Pattern.ID
                    )
            )
    }
}
