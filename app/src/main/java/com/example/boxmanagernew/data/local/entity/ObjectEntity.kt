package com.example.boxmanagernew.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "objects")
data class ObjectEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val typeObjectId: Int,

    val boxId: Int,

    val description: String?,

    val quantity: Int?
)