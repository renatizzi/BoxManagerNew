package com.example.boxmanagernew.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String
)