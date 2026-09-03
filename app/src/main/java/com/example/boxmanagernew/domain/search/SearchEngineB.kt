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
 * BOX⇄CATEGORY: confronto sulle coppie contenitore-categoria
 * restituite dalla navigazione.
 * F8 / PATTERN_008: stesso tipo di oggetto su quei dati
 * (OBJECT⇄BOX concatenato a BOX⇄CATEGORY).
 * BOX⇄LOCATION: confronto sulle coppie contenitore-posizione
 * restituite dalla navigazione.
 * F6/F9: stesso tipo di oggetto su quei dati
 * (OBJECT⇄BOX concatenato a BOX⇄LOCATION).
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

        val f7 =
            query.filters.any { filter ->
                filter == SearchF7Pattern.ID
            }

        if (f7) {

            return compareResult(
                boxesWithDuplicateObjects(
                    index
                ),
                SearchF7Pattern.HEADING
            )
        }

        val f8 =
            query.targetEntities.contains(
                CoreEntityType.OBJECT
            ) &&
                query.targetEntities.contains(
                    CoreEntityType.BOX
                ) &&
                query.targetEntities.contains(
                    CoreEntityType.CATEGORY
                )

        if (f8) {

            return compareResult(
                boxesWithCrossCategoryDuplicates(
                    index
                ),
                SearchF8Pattern.HEADING
            )
        }

        val objectLocation =
            query.targetEntities.contains(
                CoreEntityType.OBJECT
            ) &&
                query.targetEntities.contains(
                    CoreEntityType.BOX
                ) &&
                query.targetEntities.contains(
                    CoreEntityType.LOCATION
                )

        if (objectLocation) {

            return compareResult(
                boxesWithCrossLocationDuplicates(
                    index
                ),
                heading = null
            )
        }

        if (
            query.targetEntities.contains(
                CoreEntityType.BOX
            ) &&
            query.targetEntities.contains(
                CoreEntityType.CATEGORY
            )
        ) {

            return compareResult(
                boxCategoryCompareLines(
                    index
                ),
                heading = null
            )
        }

        if (
            query.targetEntities.contains(
                CoreEntityType.BOX
            ) &&
            query.targetEntities.contains(
                CoreEntityType.LOCATION
            )
        ) {

            return compareResult(
                boxLocationCompareLines(
                    index
                ),
                heading = null
            )
        }

        return unavailable()
    }

    private fun compareResult(
        payload: ComparePayload,
        heading: String?
    ): SearchResponse {

        if (payload.lines.isEmpty()) {

            return SearchResponse(
                success = false,
                message =
                    SearchConfiguration.MSG_NO_RESULTS,
                requestType =
                    SearchRequestType.ARCHIVE_QUERY
            )
        }

        val body =
            payload.lines.joinToString(
                "\n"
            )

        val message =
            if (heading.isNullOrBlank()) {
                body
            } else {
                heading + "\n" + body
            }

        return SearchResponse(
            success = true,
            message = message,
            requestType =
                SearchRequestType.ARCHIVE_QUERY,
            resultBoxNames =
                payload.boxNames,
            resultObjectNames =
                payload.objectNames
        )
    }

    private fun boxCategoryCompareLines(
        index: SearchArchiveIndex
    ): ComparePayload {

        val pairs =
            boxCategoryPairs(
                index
            )

        val categoryKeys =
            pairs
                .map { pair ->
                    CanonicalNormalizer.canonical(
                        pair.second
                    )
                }
                .distinct()

        if (categoryKeys.size < 2) {
            return ComparePayload(
                emptyList(),
                emptyList()
            )
        }

        val grouped =
            pairs
                .groupBy { pair ->
                    pair.second
                }
                .filter { (_, group) ->
                    group
                        .map { pair ->
                            pair.first
                        }
                        .distinct()
                        .size == 1
                }
                .toSortedMap(
                    String.CASE_INSENSITIVE_ORDER
                )

        val lines =
            grouped.flatMap { (category, group) ->

                val boxes =
                    group
                        .map { pair ->
                            pair.first
                        }
                        .distinct()
                        .sortedWith(
                            String.CASE_INSENSITIVE_ORDER
                        )

                listOf(
                    category
                ) + boxes
            }

        val boxNames =
            grouped.flatMap { (_, group) ->

                group
                    .map { pair ->
                        pair.first
                    }
                    .distinct()
                    .sortedWith(
                        String.CASE_INSENSITIVE_ORDER
                    )
            }

        return ComparePayload(
            lines,
            boxNames
        )
    }

    private fun boxLocationCompareLines(
        index: SearchArchiveIndex
    ): ComparePayload {

        val pairs =
            boxLocationPairs(
                index
            )

        val locationKeys =
            pairs
                .map { pair ->
                    CanonicalNormalizer.canonical(
                        pair.second
                    )
                }
                .distinct()

        if (locationKeys.size < 2) {
            return ComparePayload(
                emptyList(),
                emptyList()
            )
        }

        val grouped =
            pairs
                .groupBy { pair ->
                    pair.second
                }
                .filter { (_, group) ->
                    group
                        .map { pair ->
                            pair.first
                        }
                        .distinct()
                        .size == 1
                }
                .toSortedMap(
                    String.CASE_INSENSITIVE_ORDER
                )

        val lines =
            grouped.flatMap { (location, group) ->

                val boxes =
                    group
                        .map { pair ->
                            pair.first
                        }
                        .distinct()
                        .sortedWith(
                            String.CASE_INSENSITIVE_ORDER
                        )

                listOf(
                    location
                ) + boxes
            }

        val boxNames =
            grouped.flatMap { (_, group) ->

                group
                    .map { pair ->
                        pair.first
                    }
                    .distinct()
                    .sortedWith(
                        String.CASE_INSENSITIVE_ORDER
                    )
            }

        return ComparePayload(
            lines,
            boxNames
        )
    }

    private fun boxLocationPairs(
        index: SearchArchiveIndex
    ): List<Pair<String, String>> {

        val fromBoxes =
            index.boxRecords
                .filter { record ->
                    record.name.isNotBlank() &&
                        record.locationName.isNotBlank()
                }
                .distinctBy { record ->
                    record.name.lowercase()
                }
                .map { record ->
                    record.name to
                        record.locationName
                }

        if (fromBoxes.isNotEmpty()) {
            return fromBoxes
        }

        return index.objectRecords
            .filter { record ->
                record.boxName.isNotBlank() &&
                    record.boxLocation.isNotBlank()
            }
            .distinctBy { record ->
                record.boxName.lowercase()
            }
            .map { record ->
                record.boxName to
                    record.boxLocation
            }
    }

    private fun boxCategoryPairs(
        index: SearchArchiveIndex
    ): List<Pair<String, String>> {

        val fromBoxes =
            index.boxRecords
                .filter { record ->
                    record.name.isNotBlank() &&
                        record.categoryName.isNotBlank()
                }
                .distinctBy { record ->
                    record.name.lowercase()
                }
                .map { record ->
                    record.name to
                        record.categoryName
                }

        if (fromBoxes.isNotEmpty()) {
            return fromBoxes
        }

        return index.objectRecords
            .filter { record ->
                record.boxName.isNotBlank() &&
                    record.boxCategory.isNotBlank()
            }
            .distinctBy { record ->
                record.boxName.lowercase()
            }
            .map { record ->
                record.boxName to
                    record.boxCategory
            }
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
    ): ComparePayload {

        val groups =
            duplicateNameGroups(
                index
            )

        val boxNames =
            boxesFromGroups(
                groups
            )

        val objectNames =
            groups
                .flatten()
                .map { record ->
                    record.name
                }
                .filter { name ->
                    name.isNotBlank()
                }
                .distinctBy { name ->
                    name.lowercase()
                }
                .sortedWith(
                    String.CASE_INSENSITIVE_ORDER
                )

        return ComparePayload(
            boxNames,
            boxNames,
            objectNames
        )
    }

    private fun boxesWithCrossCategoryDuplicates(
        index: SearchArchiveIndex
    ): ComparePayload {

        val categoryByBox =
            boxCategoryPairs(
                index
            ).associate { pair ->
                pair.first.lowercase() to
                    pair.second
            }

        val hits =
            duplicateNameGroups(
                index
            )
                .mapNotNull { group ->

                    val boxesWithCategory =
                        group
                            .map { record ->
                                record.boxName
                            }
                            .filter { boxName ->
                                boxName.isNotBlank()
                            }
                            .distinctBy { boxName ->
                                boxName.lowercase()
                            }
                            .mapNotNull { boxName ->

                                val category =
                                    categoryByBox[
                                        boxName.lowercase()
                                    ].orEmpty()

                                if (category.isBlank()) {
                                    null
                                } else {
                                    boxName to category
                                }
                            }

                    val categories =
                        boxesWithCategory
                            .map { pair ->
                                CanonicalNormalizer.canonical(
                                    pair.second
                                )
                            }
                            .distinct()

                    if (
                        boxesWithCategory.size < 2 ||
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
                        boxesWithCategory
                            .map { pair ->
                                pair.first
                            }
                            .sortedWith(
                                String.CASE_INSENSITIVE_ORDER
                            )

                    val objectNames =
                        group
                            .map { record ->
                                record.name
                            }
                            .filter { name ->
                                name.isNotBlank()
                            }
                            .distinctBy { name ->
                                name.lowercase()
                            }

                    CompareHit(
                        objectName + ": " +
                            boxNames.joinToString(
                                ", "
                            ),
                        boxNames,
                        objectNames
                    )
                }

        return payloadFromHits(
            hits
        )
    }

    private fun boxesWithCrossLocationDuplicates(
        index: SearchArchiveIndex
    ): ComparePayload {

        val locationByBox =
            boxLocationPairs(
                index
            ).associate { pair ->
                pair.first.lowercase() to
                    pair.second
            }

        val hits =
            duplicateNameGroups(
                index
            )
                .mapNotNull { group ->

                    val boxesWithLocation =
                        group
                            .map { record ->
                                record.boxName
                            }
                            .filter { boxName ->
                                boxName.isNotBlank()
                            }
                            .distinctBy { boxName ->
                                boxName.lowercase()
                            }
                            .mapNotNull { boxName ->

                                val location =
                                    locationByBox[
                                        boxName.lowercase()
                                    ].orEmpty()

                                if (location.isBlank()) {
                                    null
                                } else {
                                    boxName to location
                                }
                            }

                    val locations =
                        boxesWithLocation
                            .map { pair ->
                                CanonicalNormalizer.canonical(
                                    pair.second
                                )
                            }
                            .distinct()

                    if (
                        boxesWithLocation.size < 2 ||
                        locations.size < 2
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
                        boxesWithLocation
                            .map { pair ->
                                pair.first
                            }
                            .sortedWith(
                                String.CASE_INSENSITIVE_ORDER
                            )

                    val objectNames =
                        group
                            .map { record ->
                                record.name
                            }
                            .filter { name ->
                                name.isNotBlank()
                            }
                            .distinctBy { name ->
                                name.lowercase()
                            }

                    CompareHit(
                        objectName + ": " +
                            boxNames.joinToString(
                                ", "
                            ),
                        boxNames,
                        objectNames
                    )
                }

        return payloadFromHits(
            hits
        )
    }

    private fun payloadFromHits(
        hits: List<CompareHit>
    ): ComparePayload {

        val lines =
            hits
                .map { hit ->
                    hit.line
                }
                .sortedWith(
                    String.CASE_INSENSITIVE_ORDER
                )

        val boxNames =
            hits
                .flatMap { hit ->
                    hit.boxNames
                }
                .distinctBy { name ->
                    name.lowercase()
                }
                .sortedWith(
                    String.CASE_INSENSITIVE_ORDER
                )

        val objectNames =
            hits
                .flatMap { hit ->
                    hit.objectNames
                }
                .distinctBy { name ->
                    name.lowercase()
                }
                .sortedWith(
                    String.CASE_INSENSITIVE_ORDER
                )

        return ComparePayload(
            lines,
            boxNames,
            objectNames
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

        fun objectLocationQuery():
                SearchArchiveQuery =

            SearchArchiveQuery(
                operation =
                    SearchArchiveQueryOperation.COMPARE,
                targetEntities =
                    setOf(
                        CoreEntityType.OBJECT,
                        CoreEntityType.BOX,
                        CoreEntityType.LOCATION
                    )
            )

        fun boxLocationQuery():
                SearchArchiveQuery =

            SearchArchiveQuery(
                operation =
                    SearchArchiveQueryOperation.COMPARE,
                targetEntities =
                    setOf(
                        CoreEntityType.BOX,
                        CoreEntityType.LOCATION
                    )
            )

        fun boxCategoryQuery():
                SearchArchiveQuery =

            SearchArchiveQuery(
                operation =
                    SearchArchiveQueryOperation.COMPARE,
                targetEntities =
                    setOf(
                        CoreEntityType.BOX,
                        CoreEntityType.CATEGORY
                    )
            )

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

private data class ComparePayload(

    val lines: List<String>,

    val boxNames: List<String>,

    val objectNames: List<String> = emptyList()
)

private data class CompareHit(

    val line: String,

    val boxNames: List<String>,

    val objectNames: List<String>
)
