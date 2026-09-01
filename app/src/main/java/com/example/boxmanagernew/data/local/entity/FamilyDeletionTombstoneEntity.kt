package com.example.boxmanagernew.data.local.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * Tombstone per delete esplicito propagabile in merge famiglia (B5 / Nota B0 §3.2).
 */
@Entity(
    tableName = "family_deletion_tombstone",
    primaryKeys = ["entityType", "permanentId"],
    indices = [
        Index(value = ["entityType", "permanentId"], unique = true)
    ]
)
data class FamilyDeletionTombstoneEntity(
    val entityType: String,
    val permanentId: String,
    val deletedAt: Long,
    val deletedBy: String
) {
    companion object {
        const val TYPE_BOX = "BOX"
        const val TYPE_OBJECT = "OBJECT"
    }
}
