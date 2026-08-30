package com.example.boxmanagernew.family.model

data class FamilyInventoryBox(
    val permanentId: String,
    val name: String,
    val category: String,
    val position: String,
    val lastModified: Long
)

data class FamilyInventoryObject(
    val objectPermanentId: String,
    val boxPermanentId: String,
    val typeName: String,
    val description: String?,
    val quantity: Int?,
    val lastModified: Long
)

data class FamilyInventorySnapshot(
    val boxes: List<FamilyInventoryBox>,
    val objects: List<FamilyInventoryObject>
)
