package com.example.boxmanagernew.family.model

data class FamilyInventoryBox(
    val permanentId: String,
    val name: String,
    val category: String,
    val position: String,
    val lastModified: Long,
    val createdBy: String = ""
)

data class FamilyInventoryObject(
    val objectPermanentId: String,
    val boxPermanentId: String,
    val typeName: String,
    val description: String?,
    val quantity: Int?,
    val lastModified: Long,
    val createdBy: String = ""
)

data class FamilyDeletion(
    val entityType: String,
    val permanentId: String,
    val deletedAt: Long,
    val deletedBy: String = ""
)

data class FamilyInventorySnapshot(
    val boxes: List<FamilyInventoryBox>,
    val objects: List<FamilyInventoryObject>,
    val deletions: List<FamilyDeletion> = emptyList()
)
