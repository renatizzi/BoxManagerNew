package com.example.boxmanagernew.viewoutput.model

import com.example.boxmanagernew.domain.model.Box
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
}
