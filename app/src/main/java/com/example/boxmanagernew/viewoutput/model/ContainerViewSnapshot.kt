package com.example.boxmanagernew.viewoutput.model

data class ViewObjectLine(
    val name: String,
    val description: String,
    val quantity: String
)

data class ViewBoxBlock(
    val name: String,
    val category: String,
    val position: String,
    val categoryIconRes: Int = 0,
    val objects: List<ViewObjectLine>
)

data class ViewPrintHeader(
    val title: String,
    val filterLine: String,
    val countLine: String
)

data class ContainerViewSnapshot(
    val boxes: List<ViewBoxBlock>
) {

    val isEmpty: Boolean
        get() = boxes.isEmpty()

    val objectCount: Int
        get() = boxes.sumOf { box ->
            box.objects.size
        }
}
