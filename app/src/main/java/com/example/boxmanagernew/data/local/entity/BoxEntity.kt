package com.example.boxmanagernew.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "box",
    indices = [
        Index(value = ["permanentId"], unique = true)
    ]
)
data class BoxEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val categoryId: Int,
    val position: String,
    val lastModified: Long,
    val permanentId: String
)