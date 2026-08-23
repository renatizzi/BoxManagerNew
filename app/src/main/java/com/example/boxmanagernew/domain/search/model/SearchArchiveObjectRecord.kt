package com.example.boxmanagernew.domain.search.model

data class SearchArchiveObjectRecord(

    val name: String,

    val description: String = "",

    val boxName: String = "",

    val boxCategory: String = "",

    val boxLocation: String = "",

    val categoryId: Int = 0
)
