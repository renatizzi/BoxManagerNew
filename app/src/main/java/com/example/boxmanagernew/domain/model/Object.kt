package com.example.boxmanagernew.domain.model

data class Object(
    val id: Int = 0,
    val typeObjectId: Int,
    val boxId: Int,
    val description: String?,
    val quantity: Int?,
    val objectPermanentId: String = "",
    val lastModified: Long = 0L
)