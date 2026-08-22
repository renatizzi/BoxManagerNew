package com.example.boxmanagernew.viewoutput.model

import com.example.boxmanagernew.domain.model.Box
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
}
