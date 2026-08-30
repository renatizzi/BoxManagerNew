package com.example.boxmanagernew.family.model

data class FamilyMergeSnapshot(
    val catalog: FamilyCatalogSnapshot,
    val inventory: FamilyInventorySnapshot
)
