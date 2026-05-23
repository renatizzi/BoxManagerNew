package com.example.boxmanagernew.domain.model

data class SearchResult(

    val objectName: String,

    val description: String?,

    val quantity: Int?,

    val boxId: Int,

    val boxName: String,

    val boxPosition: String,

    val categoryName: String? = null,

    val boxDescription: String? = null
)