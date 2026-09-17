package com.example.boxmanagernew.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Metadati foto oggetto (A3). File su disco per [objectPermanentId], non BLOB.
 */
@Entity(tableName = "object_photos")
data class ObjectPhotoEntity(
    @PrimaryKey
    val objectPermanentId: String,
    val displayFileName: String,
    val thumbFileName: String,
    val updatedAt: Long,
    val byteSize: Long = 0L
)
