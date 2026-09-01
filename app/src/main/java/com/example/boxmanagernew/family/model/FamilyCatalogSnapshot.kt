package com.example.boxmanagernew.family.model

data class FamilyCatalogCategory(
    val name: String,
    val icon: String
)

data class FamilyCatalogLocation(
    val name: String
)

data class FamilyCatalogSnapshot(
    val categories: List<FamilyCatalogCategory>,
    val locations: List<FamilyCatalogLocation>
)
