package com.example.boxmanagernew.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type_objects")
data class TypeObjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)