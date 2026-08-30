package com.example.boxmanagernew.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "objects",
    indices = [
        Index(value = ["objectPermanentId"], unique = true)
    ]
)
data class ObjectEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val typeObjectId: Int,

    val boxId: Int,

    val description: String?,

    val quantity: Int?,

    val objectPermanentId: String,

    val lastModified: Long
)