package com.example.boxmanagernew.domain.model

data class Box(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val categoryId: Int,
    val position: String,
    val lastModified: Long,
    val permanentId: String = "",
    val createdBy: String = ""
)