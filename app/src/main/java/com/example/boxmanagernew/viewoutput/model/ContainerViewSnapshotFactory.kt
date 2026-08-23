package com.example.boxmanagernew.viewoutput.model

import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.domain.model.Location
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.domain.model.SearchResult

object ContainerViewSnapshotFactory {

    fun from(
        boxes: List<Box>,
        categoryNameOf: (Int) -> String,
        categoryIconOf: (Int) -> Int,
        objects: List<SearchResult>
    ): ContainerViewSnapshot {

        val byBoxId = objects.groupBy { row ->
            row.boxId
        }

        return ContainerViewSnapshot(
            boxes = boxes.map { box ->
                ViewBoxBlock(
                    name = box.name,
                    category = categoryNameOf(box.categoryId),
                    position = box.position,
                    categoryIconRes = categoryIconOf(box.categoryId),
                    objects = (byBoxId[box.id] ?: emptyList())
                        .sortedBy { row ->
                            row.objectName.lowercase()
                        }
                        .map { row ->
                            ViewObjectLine(
                                name = row.objectName,
                                description = row.description.orEmpty(),
                                quantity = row.quantity?.toString().orEmpty()
                            )
                        }
                )
            }
        )
    }

    fun fromBoxContents(
        box: Box,
        categoryName: String,
        categoryIconRes: Int,
        objects: List<ObjectWithType>
    ): ContainerViewSnapshot {

        return ContainerViewSnapshot(
            boxes = listOf(
                ViewBoxBlock(
                    name = box.name,
                    category = categoryName,
                    position = box.position,
                    categoryIconRes = categoryIconRes,
                    objects = objects.map { row ->
                        ViewObjectLine(
                            name = row.typeName,
                            description = row.obj.description.orEmpty(),
                            quantity = row.obj.quantity?.toString().orEmpty()
                        )
                    }
                )
            )
        )
    }

    fun fromSearchResults(
        results: List<SearchResult>,
        categoryIconOfName: (String) -> Int
    ): ContainerViewSnapshot {

        if (results.isEmpty()) {
            return ContainerViewSnapshot(emptyList())
        }

        val boxes = results
            .sortedBy { row ->
                row.boxName.lowercase()
            }
            .groupBy { row ->
                row.boxId
            }
            .map { (_, items) ->
                val first = items.first()
                val category = first.categoryName.orEmpty()
                ViewBoxBlock(
                    name = first.boxName,
                    category = category.ifBlank { "-" },
                    position = first.boxPosition,
                    categoryIconRes = categoryIconOfName(category),
                    objects = items
                        .sortedBy { row ->
                            row.objectName.lowercase()
                        }
                        .map { row ->
                            ViewObjectLine(
                                name = row.objectName,
                                description = row.description.orEmpty(),
                                quantity = row.quantity?.toString().orEmpty()
                            )
                        }
                )
            }

        return ContainerViewSnapshot(boxes)
    }

    fun fromCategories(
        categories: List<Category>,
        categoryIconOf: (String) -> Int
    ): ContainerViewSnapshot {

        return ContainerViewSnapshot(
            boxes = categories.map { category ->
                ViewBoxBlock(
                    name = category.name,
                    category = "",
                    position = "",
                    categoryIconRes = categoryIconOf(category.icon),
                    objects = emptyList()
                )
            }
        )
    }

    fun fromLocations(
        locations: List<Location>
    ): ContainerViewSnapshot {

        return ContainerViewSnapshot(
            boxes = locations.map { location ->
                ViewBoxBlock(
                    name = location.name,
                    category = "",
                    position = "",
                    objects = emptyList()
                )
            }
        )
    }

    fun fromBoxesGroupedByCategory(
        boxes: List<Box>,
        categoryNameOf: (Int) -> String,
        categoryIconOf: (Int) -> Int
    ): ContainerViewSnapshot {

        val groups =
            boxes
                .groupBy { box ->
                    categoryNameOf(
                        box.categoryId
                    ).ifBlank {
                        "-"
                    }
                }
                .toSortedMap(
                    String.CASE_INSENSITIVE_ORDER
                )

        return ContainerViewSnapshot(
            boxes = groups.map { (category, group) ->

                val iconRes =
                    group
                        .map { box ->
                            categoryIconOf(
                                box.categoryId
                            )
                        }
                        .firstOrNull { res ->
                            res != 0
                        } ?: 0

                ViewBoxBlock(
                    name = category,
                    category = "",
                    position = "",
                    categoryIconRes = iconRes,
                    objects = group
                        .sortedBy { box ->
                            box.name.lowercase()
                        }
                        .map { box ->
                            ViewObjectLine(
                                name = box.name,
                                description = "",
                                quantity = ""
                            )
                        }
                )
            }
        )
    }

    fun fromBoxesGroupedByLocation(
        boxes: List<Box>
    ): ContainerViewSnapshot {

        val groups =
            boxes
                .groupBy { box ->
                    box.position.ifBlank {
                        "-"
                    }
                }
                .toSortedMap(
                    String.CASE_INSENSITIVE_ORDER
                )

        return ContainerViewSnapshot(
            boxes = groups.map { (location, group) ->

                ViewBoxBlock(
                    name = location,
                    category = "",
                    position = "",
                    objects = group
                        .sortedBy { box ->
                            box.name.lowercase()
                        }
                        .map { box ->
                            ViewObjectLine(
                                name = box.name,
                                description = "",
                                quantity = ""
                            )
                        }
                )
            }
        )
    }
}
